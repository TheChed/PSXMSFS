#include <cstdint>
#include <cstdlib>
#include <cassert>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <strings.h>
#include <pthread.h>
#include <sys/time.h>
#include <sys/types.h>
#include <time.h>
#include <unistd.h>
#include <windows.h>
#include "PSXMSFS.h"
#include "util.h"
#include "SimConnect.h"

const char delim[2] = ";"; // delimiter for parsing the Q variable strings

size_t bufboost_used = 0;
size_t bufmain_used = 0;
char bufboost[256];
// char bufmain[MAXBUFF];
char bufmain[4096];

// Position of Gear

void H170(char *s, Target *T) { T->GearLever = (int)(s[6] - '0'); }

// Flap lever variable Qh389
void H389(char *s, Target *T) { T->FlapLever = (int)(s[6] - '0'); }

// Parking break
void H397(char *s, Target *T) {
    T->parkbreak = (int)(s[6] - '0');
    SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_PARKING, T->parkbreak,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
}

// Steering wheel
void H426(char *s, Target *T) {
    double pos;

    pos = strtol(s + 6, NULL, 10) / 999.0 * 16384.0;
    if (abs(pos) > 16385) {
        pos = 0;
    }

    T->steering = -pos;
    SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_STEERING, T->steering,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
}

// Speedbrake lever variable Qh389
void H388(char *s, Target *T) {

    char *token, *ptr, *savptr;
    if ((token = strtok_r(s + 6, delim, &savptr)) != NULL) {
        T->SpdBrkLever = strtol(token, &ptr, 10);
    }
}

void S121(char *s, Target *T) {

    char *token, *ptr;
    assert(strlen(s) >= 9 && strlen(s) <= 200);

    if ((token = strtok(s + 6, delim)) != NULL) {
        T->pitch = strtol(token, &ptr, 10) / 100000.0;
    }

    if ((token = strtok(NULL, delim)) != NULL) {
        T->bank = strtol(token, &ptr, 10) / 100000.0;
    }

    if ((token = strtok(NULL, delim)) != NULL) {
        T->heading_true = strtod(token, &ptr);
    }

    if ((token = strtok(NULL, delim)) != NULL) {
        T->altitude = strtol(token, &ptr, 10) / 1000.0;
    }

    if ((token = strtok(NULL, delim)) != NULL) {
        T->TAS = strtol(token, &ptr, 10) / 1000.0;
    }

    if ((token = strtok(NULL, delim)) != NULL) {
        T->latitude = strtod(token, &ptr);
    }

    if ((token = strtok(NULL, delim)) != NULL) {
        T->longitude = strtod(token, &ptr);
    }

    if ((token = strtok(NULL, delim)) != NULL) {
    }
}
void S483(char *s, Target *T) {

    char *token, *ptr;

    if ((token = strtok(s + 6, delim)) != NULL) {
        T->IAS = strtol(token, &ptr, 10) / 10.0;
    }
}

void S78(const char *s) {

    if (strstr(s, "MSFS")) {
        SLAVE = 1;
    } else {
        if (strstr(s, "PSX")) {
            SLAVE = 0;
        }
    }
}

void S448(char *s, Target *T) {

    char *token, *ptr, *savptr;
    int stdbar;

    /* get the first token
     * Altimeter setting is the 4th token
     * 5th token is STD setting
     */

    token = strtok_r(s + 6, delim, &savptr);
    token = strtok_r(NULL, delim, &savptr);
    token = strtok_r(NULL, delim, &savptr);

    if ((token = strtok_r(NULL, delim, &savptr)) != NULL) {
        T->altimeter = strtol(token, &ptr, 10) / 100.0;
    }
    /* STD setting*/
    if ((token = strtok_r(NULL, delim, &savptr)) != NULL) {
        stdbar = strtod(token, NULL);
        T->STD = (abs(stdbar) == 1) ? 0 : 1;
    }
    SetBARO();
}

void S458(char *s, Target *T) {
    int C1, C2;
    char COM1[9] = {0}, COM2[9] = {0};
    /*
     * discard the last digit from the Qs string as it is not taken into MSFS.
     * and start at second digit, as first one is always 1
     */
    strncpy(COM1, s + 6, 3);
    strncat(COM1, s + 10, 3);
    strcat(COM1, "000");

    C1 = strtol(COM1, NULL, 10);

    if (C1 < 118000000 || C1 > 136990000) {
        C1 = 122800000;
    }
    T->COM1 = C1;

    strncpy(COM2, s + 13, 3);
    strncat(COM2, s + 17, 3);
    strcat(COM2, "000");

    C2 = strtol(COM2, NULL, 10);
    if (C2 < 118000000 || C2 > 136990000) {
        C2 = 122800000;
    }
    T->COM2 = C2;
    SetCOMM();
}
void S480(char *s, Target *T) {

    int val[10];
    for (int i = 0; i < 10; i++) {
        val[i] = (s[2 * i + 6] - '0') * 10 + (s[2 * i + 1 + 6] - '0');
    }

    T->rudder = 16384 * ((val[8] + val[9]) / 2 - 32) / 32.0; // maximum deflection = 64
    T->aileron = -16384 * (val[0] - 20) / 20.0;              // maximum deflection in PSX  = 40
    T->elevator = 16384 * (val[6] - 21) / 21.0;              // maximum deflection in PSX = 42
}

void S124(char *s, Target *T) {

    struct tm *time_PSX;
    time_t timeUTC;
    timeUTC = strtoll(s + 6, NULL, 10) / 1000;

    if ((time_PSX = gmtime(&timeUTC)) == NULL) {
        printf("Error creating timePSX\n");
        return;
    }

    T->year = time_PSX->tm_year + 1900; // year starts in 1900
    T->day = time_PSX->tm_yday + 1;     // nb days since January 1st, starts at 0
    T->hour = time_PSX->tm_hour;
    T->minute = time_PSX->tm_min;

    SetUTCTime();
    return;
}

void S443(char *s, Target *T) {

    updateLights = 1;
    for (int i = 0; i < 14; i++) {
        T->light[i] = (int)(s[i + 6] - '0') < 5 ? 0 : 1;
    }
}

void S122(char *s, Target *T) {

    const char delim[2] = ";";
    char *token, *ptr;
    assert(strlen(s) >= 9 && strlen(s) <= 200);
    /* get the first token */
    token = strtok(s + 6, delim);
    /* walk through other tokens */
    if (strcmp(token, "1") == 1) {
        MSFS_on_ground = 0;
    }

    if ((token = strtok(NULL, delim)) != NULL) {
        T->pitch = strtol(token, &ptr, 10) / 1000.0;
    }
    if ((token = strtok(NULL, delim)) != NULL) {
        T->pitch = strtol(token, &ptr, 10) / 1000.0;
    }

    if ((token = strtok(NULL, delim)) != NULL) {
        T->bank = strtol(token, &ptr, 10) / 1000.0;
    }

    if ((token = strtok(NULL, delim)) != NULL) {
        T->heading_true = strtod(token, &ptr) / 1000.0;
    }

    if ((token = strtok(NULL, delim)) != NULL) {
        T->altitude = strtol(token, &ptr, 10);
    }

    if ((token = strtok(NULL, delim)) != NULL) {
        T->VerticalSpeed = strtol(token, &ptr, 10);
    }

    if ((token = strtok(NULL, delim)) != NULL) {
        T->TAS = strtol(token, &ptr, 10);
    }

    token = strtok(NULL, delim); // YAW is not needed
    if ((token = strtok(NULL, delim)) != NULL) {
        token = strtok(NULL, delim);
        T->latitude = strtod(token, &ptr);
    }

    if ((token = strtok(NULL, delim)) != NULL) {
        T->longitude = strtod(token, &ptr);
    }
}

void I204(char *s, Target *T) {

    T->XPDR = strtol(s + 8, NULL, 16);
    T->IDENT = (int)(s[7] - '0');

    SetCOMM();
}
void I257(char *s, Target *T) { T->onGround = (int)(s[6] - '0'); }
void I219(char *s) { MSFS_on_ground = (strtol(s + 6, NULL, 10) < 10); }

void Decode(Target *T, char *s, int boost) {

    const char delim[2] = ";";
    static float Tms = 0;
    static float alt = 0;
    float vs, tvs, altdiff;
    char *token, *ptr, *savptr;
    float flightDeckAlt = 0.0;

    if (boost) {
        /* get the first token */
        if ((token = strtok_r(s, delim, &savptr)) != NULL) {
            T->onGround = (strcmp(token, "G") == 0 ? 2 : 1);
        }

        if ((token = strtok_r(NULL, delim, &savptr)) != NULL) {

            flightDeckAlt = strtol(token, &ptr, 10);
            T->altitude = flightDeckAlt / 100.0;
            altdiff = flightDeckAlt - alt;
            alt = flightDeckAlt;
        }

        if ((token = strtok_r(NULL, delim, &savptr)) != NULL) {
            T->heading_true = strtol(token, &ptr, 10) / 100.0 * DEG2RAD;
        }

        if ((token = strtok_r(NULL, delim, &savptr)) != NULL) {
            T->pitch = strtol(token, &ptr, 10) / 100.0 * DEG2RAD;
        }

        if ((token = strtok_r(NULL, delim, &savptr)) != NULL) {
            T->bank = strtol(token, &ptr, 10) / 100.0 * DEG2RAD;
        }

        if ((token = strtok_r(NULL, delim, &savptr)) != NULL) {
            T->latitude = strtod(token, &ptr) * DEG2RAD; // B)oost gives lat & long in degrees
        }

        if ((token = strtok_r(NULL, delim, &savptr)) != NULL) {
            T->longitude = strtod(token, &ptr) * DEG2RAD; // Boost gives lat & long in degrees;
        }
        if ((token = strtok_r(NULL, delim, &savptr)) != NULL) {
            vs = strtol(token, NULL, 10);
            if (vs <= Tms) {
                tvs = vs - Tms + 1000;
            } else {
                tvs = vs - Tms;
            }
            if (tvs)
                T->VerticalSpeed = 10 * (altdiff / tvs) * 60.0;
            Tms = vs;
        }

        /*Put main vairables in Boost structure
         *So that we can update MSFS on high frequency
         */
        ABoost = {.altitude = T->altitude,
                  .latitude = T->latitude,
                  .longitude = T->longitude,
                  .heading_true = T->heading_true,
                  .pitch = -T->pitch,
                  .bank = T->bank};

    } else {

        if (strstr(s, "Qs122=")) {
            S122(strstr(s, "Qs122="), T);
        }

        // ExtLts : External lights, Mode=XECON
        if (strstr(s, "Qs443")) {
            S443(strstr(s, "Qs443="), T);
        }

        //// PSX on groud
        if (strstr(s, "Qi257")) {
            I257(strstr(s, "Qi257"), T);
        }

        //// Update Gear position
        if (strstr(s, "Qh170")) {
            H170(strstr(s, "Qh170"), T);
        }

        //// Update PArking break
        if (strstr(s, "Qh397")) {
            H397(strstr(s, "Qh397"), T);
        }

        //// Update Flap position
        if (strstr(s, "Qh389")) {
            H389(strstr(s, "Qh389"), T);
        }
        //// Speedbrake
        if (strstr(s, "Qh388")) {
            H388(strstr(s, "Qh388"), T);
        }

        // Update Time
        if (strstr(s, "Qs124")) {
            S124(strstr(s, "Qs124"), T);
        }

        // Indicated Airspeed IAS
        if (strstr(s, "Qs483")) {
            S483(strstr(s, "Qs483"), T);
        }

        // Rudder+aileron+elevator
        if (strstr(s, "Qs480")) {
            S480(strstr(s, "Qs480"), T);
        }

        // COMMS
        if (strstr(s, "Qs458")) {
            S458(strstr(s, "Qs458"), T);
        }

        // Steering wheel
        if (strstr(s, "Qh426")) {
            H426(strstr(s, "Qh426"), T);
        }

        // Steering wheel
        if (strstr(s, "Qi204")) {
            I204(strstr(s, "Qi204"), T);
        }
        // Altimeter
        if (strstr(s, "Qs448")) {
            S448(strstr(s, "Qs448"), T);
        }
        // MSFS slave-Master
        if (strstr(s, "Qs78")) {
            S78(strstr(s, "Qs78"));
        }
    }
}
int sendQPSX(const char *s) {

    char *dem;
    dem = (char *)malloc((strlen(s) + 1) * sizeof(char));

    strncpy(dem, s, strlen(s));
    dem[strlen(s)] = 10;

    int nbsend = send(sPSX, dem, strlen(s) + 1, 0);

    if (nbsend == 0) {
        printf("Error sending variable %s to PSX\n", s);
    }

    free(dem);
    return nbsend;
}

int umain(Target *T) {
    size_t bufmain_remain = sizeof(bufmain) - bufmain_used;

    if (bufmain_remain == 0) {
        printDebug("Main socket line exceeded buffer length! Discarding input", 1);
        bufmain_used = 0;
        printDebug(bufmain, 0);
        return 0;
    }

    int nbread = recv(sPSX, (char *)&bufmain[bufmain_used], bufmain_remain, 0);

    if (nbread == 0) {
        printDebug("Main socket connection closed.", 1);
        return 0;
    }
    if (nbread < 0 && errno == EAGAIN) {
        printDebug("No data received.", 1);
        /* no data for now, call back when the socket is readable */
        return 0;
    }
    if (nbread < 0) {
        printDebug("Main socket Connection error", 1);
        return 0;
    }
    bufmain_used += nbread;

    /* Scan for newlines in the line buffer; we're careful here to deal with
     * embedded \0s an evil server may send, as well as only processing lines
     * that are complete.
     */
    char *line_start = bufmain;
    char *line_end;
    while ((line_end = (char *)memchr((void *)line_start, '\n', bufmain_used - (line_start - bufmain)))) {
        *line_end = 0;

        // New situ loaded
        if (strstr(line_start, "load3")) {
            printDebug("New situ loaded: no crash detection for 10 seconds", CONSOLE);
            printDebug("Let's wait a few seconds to get everyone ready.", CONSOLE);
            sendQPSX("Qi198=-9999910"); // no crash detection fort 20 seconds
            // sleep(5);                   // let's wait a few seconds to get everyone ready
            printDebug("Resuming normal operations.", CONSOLE);
            MSFS_on_ground = 0;
        }
        pthread_mutex_lock(&mutex);
        if (line_start[0] == 'Q') {
            Decode(T, line_start, 0);
            if (!SLAVE) {
                SetMSFSPos();
            }
        }
        pthread_mutex_unlock(&mutex);
        line_start = line_end + 1;
    }
    /* Shift buffer down so the unprocessed data is at the start */
    bufmain_used -= (line_start - bufmain);
    memmove(bufmain, line_start, bufmain_used);
    return nbread;
}

int umainBoost(Target *T) {

    size_t bufboost_remain = sizeof(bufboost) - bufboost_used;

    if (bufboost_remain == 0) {
        printDebug("Boost Line exceeded buffer length!", 1);
        return 0;
    }

    int nbread = recv(sPSXBOOST, (char *)&bufboost[bufboost_used], bufboost_remain, 0);
    if (nbread == 0) {
        printDebug("Boost connection closed.", 1);
        return 0;
    }
    if (nbread < 0 && errno == EAGAIN) {
        printDebug("no data for now, call back when the socket is readable", 1);
        return 0;
    }
    if (nbread < 0) {
        printDebug("Boost Connection error", 1);
        return 0;
    }
    bufboost_used += nbread;

    /* Scan for newlines in the line buffer; we're careful here to deal with
     * embedded \0s an evil server may send, as well as only processing lines
     * that are complete.
     */
    char *line_start = bufboost;
    char *line_end;
    while ((line_end = (char *)memchr((void *)line_start, '\n', bufboost_used - (line_start - bufboost)))) {
        *line_end = 0;

        if (line_start[0] != 'F' && line_start[0] != 'G') {
            printDebug(line_start, 1);
        }
        if (line_start[0] == 'F' || line_start[0] == 'G') {
            pthread_mutex_lock(&mutex);
            Decode(T, line_start, 1);
            if (!SLAVE) {
                SetMSFSPos();
            }
            pthread_mutex_unlock(&mutex);
        } else {
            sprintf(debugInfo, "Wrong boost string received: %s", line_start);
            printDebug(debugInfo, CONSOLE);
        }
        line_start = line_end + 1;
    }
    /* Shift buffer down so the unprocessed data is at the start */
    bufboost_used -= (line_start - bufboost);
    memmove(bufboost, line_start, bufboost_used);
    return nbread;
}
