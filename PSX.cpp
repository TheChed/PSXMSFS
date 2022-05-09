#include <cstdlib>
#include <time.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <strings.h>
#include <unistd.h>
#include <sys/time.h>
#include <windows.h>
#include "PSXMSFS.h"

const char delim[2] = ";"; // delimiter for parsing the Q variable strings

void state(Target *T, FILE *fd, int console) {

    time_t result = time(NULL);
    if (console) {
        printf("PSX:\t  ");
        printf("Alt: %.0f\t", T->altitude);
        printf("Lat: %.3f\t", T->latitude);
        printf("Long: %.3f\t", T->longitude);
        printf("Head: %.1f\t", T->heading_true);
        printf("Pitch: %.2f\t", T->pitch);
        printf("Bank: %.2f\t", T->bank);
        printf("TAS: %.1f\t", T->TAS);
        printf("IAS: %.1f\t", T->IAS);
        printf("VS: %.1f\t", T->VerticalSpeed);
        printf("\n");
    }
    fprintf(fd, "%s", asctime(gmtime(&result)));
    fprintf(fd, "PSX:\t  ");
    fprintf(fd, "Alt: %.0f\t", T->altitude);
    fprintf(fd, "Lat: %.3f\t", T->latitude);
    fprintf(fd, "Long: %.3f\t", T->longitude);
    fprintf(fd, "Head: %.1f\t", T->heading_true);
    fprintf(fd, "Pitch: %.2f\t", T->pitch);
    fprintf(fd, "Bank: %.2f\t", T->bank);
    fprintf(fd, "TAS: %.1f\t", T->TAS);
    fprintf(fd, "IAS: %.1f\t", T->IAS);
    fprintf(fd, "VS: %.1f\t", T->VerticalSpeed);
    fprintf(fd, "\n");
}
void stateMSFS(struct AcftPosition *A, FILE *fd, int console) {

    time_t result = time(NULL);
    // printing to debug file
    fprintf(fd, "%s", asctime(gmtime(&result)));
    fprintf(fd, "MSFS:\t  ");
    fprintf(fd, "Alt: %.0f\t", A->altitude);
    fprintf(fd, "Lat: %.3f\t", A->latitude);
    fprintf(fd, "Long: %.3f\t", A->longitude);
    fprintf(fd, "Head: %.1f\t", A->heading_true);
    fprintf(fd, "Pitch: %.2f\t", -A->pitch);
    fprintf(fd, "Bank: %.2f\t", A->bank);
    fprintf(fd, "TAS: %.1f\t", A->tas);
    fprintf(fd, "IAS: %.1f\t", A->ias);
    fprintf(fd, "VS: %.1f\t", A->vertical_speed);
    fprintf(fd, "GearDown: %.1f\t", A->GearDown);
    fprintf(fd, "FlapsPosition: %.1f\t", A->FlapsPosition);
    fprintf(fd, "Speedbrake: %.1f\t", A->Speedbrake);
    // Lights
    fprintf(fd, "Lights: %.0f%.0f%.0f%.0f%.0f%.0f%.0f%.0f%.0f%.0f%.0f%.0f%.0f\t", A->LandLeftOutboard, // L Outboard
            A->LandLeftInboard,                                                                        // L Inboard
            A->LandRightInboard,                                                                       // R Inboard
            A->LandRightOutboard,                                                                      // R Outboard
            A->LeftRwyTurnoff,  // L Runway Turnoff light
            A->RightRwyTurnoff, // R Runway Turnoff light
            A->LightTaxi,       // Taxi light
            A->LightNav,        // Nav light
            A->Strobe,          // Strobe light
            A->BeaconLwr,       // Lower Beacon light
            A->Beacon,          // Both Beacon light
            A->LightWing,       // Wing light
            A->LightLogo);      // Wing light
    // moving surfaces
    fprintf(fd, "rudder: %.1f\t", A->rudder);
    fprintf(fd, "elevator: %.1f\t", A->elevator);
    fprintf(fd, "ailerons: %.1f\t", A->ailerons);
    fprintf(fd, "\n");
    fflush(NULL);
    // And printing to stdout if console is set
    if (console) {
        printf("%s", asctime(gmtime(&result)));
        printf("MSFS:\t  ");
        printf("Alt: %.0f\t", A->altitude);
        printf("Lat: %.3f\t", A->latitude);
        printf("Long: %.3f\t", A->longitude);
        printf("Head: %.1f\t", A->heading_true);
        printf("Pitch: %.2f\t", -A->pitch);
        printf("Bank: %.2f\t", A->bank);
        printf("TAS: %.1f\t", A->tas);
        printf("IAS: %.1f\t", A->ias);
        printf("VS: %.1f\t", A->vertical_speed);
        printf("GearDown: %.1f\t", A->GearDown);
        printf("FlapsPosition: %.1f\t", A->FlapsPosition);
        printf("Speedbrake: %.1f\t", A->Speedbrake);
        // Lights
        printf("Lights: %.0f%.0f%.0f%.0f%.0f%.0f%.0f%.0f%.0f%.0f%.0f%.0f%.0f\t", A->LandLeftOutboard, // L Outboard
               A->LandLeftInboard,                                                                    // L Inboard
               A->LandRightInboard,                                                                   // R Inboard
               A->LandRightOutboard,                                                                  // R Outboard
               A->LeftRwyTurnoff,  // L Runway Turnoff light
               A->RightRwyTurnoff, // R Runway Turnoff light
               A->LightTaxi,       // Taxi light
               A->LightNav,        // Nav light
               A->Strobe,          // Strobe light
               A->BeaconLwr,       // Lower Beacon light
               A->Beacon,          // Both Beacon light
               A->LightWing,       // Wing light
               A->LightLogo);      // Wing light
        // moving surfaces
        printf("rudder: %.1f\t", A->rudder);
        printf("elevator: %.1f\t", A->elevator);
        printf("ailerons: %.1f\t", A->ailerons);
        printf("\n");
    }
}

// Position of Gear
void H170(char *s, Target *T) { T->GearLever = (int)(s[6] - '0'); }

// Flap lever variable Qh389
void H389(char *s, Target *T) { T->FlapLever = (int)(s[6] - '0'); }

// Parking break
void H397(char *s, Target *T) { T->parkbreak = (int)(s[6] - '0'); }

// Steering wheel
void H426(char *s, Target *T) {
    double pos;

    pos = strtol(s + 6, NULL, 10) / 999.0 * 16384.0;
    if (abs(pos) > 16385) {
        pos = 0;
    }

    T->steering = -pos;
}

// Speedbrake lever variable Qh389
void H388(char *s, Target *T) {

    char *token, *ptr;
    if ((token = strtok(s + 6, delim)) != NULL) {
        T->SpdBrkLever = strtol(token, &ptr, 10);
    }
}

void S121(char *s, Target *T) {

    char *token, *ptr;

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

    char *token, *ptr;
    int stdbar;

    /* get the first token
     * Altimeter setting is the 4th token
     * 5th token is STD setting
     */

    token = strtok(s + 6, delim);
    token = strtok(NULL, delim);
    token = strtok(NULL, delim);

    if ((token = strtok(NULL, delim)) != NULL) {
        T->altimeter = strtol(token, &ptr, 10) / 100.0;
    }
    /* STD setting*/
    if ((token = strtok(NULL, delim)) != NULL) {
        stdbar = strtod(token, NULL);
        T->STD = (abs(stdbar) == 1) ? 0 : 1;
    }
}

void S458(char *s, Target *T) {
    char COM1[9] = {0}, COM2[9] = {0};

    /*
     * discard the last digit from the Qs string as it is not taken into MSFS. and start at second digit, as first
     * one is always 1
     */
    strncpy(COM1, s + 6, 3);
    strncat(COM1, s + 10, 3);
    strcat(COM1, "000");

    T->COM1 = strtol(COM1, NULL, 10);

    strncpy(COM2, s + 13, 3);
    strncat(COM2, s + 17, 3);
    strcat(COM2, "000");

    T->COM2 = strtol(COM2, NULL, 10);
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

    validtime = 1; // we have read a valid time from PSX
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
}
void I257(char *s, Target *T) { T->onGround = (int)(s[6] - '0'); }
void I219(char *s) { MSFS_on_ground = (strtol(s + 6, NULL, 10) < 10); }

void Decode_Boost(Target *T, char *s) {

    const char delim[2] = ";";
    char *token, *ptr;
    float flightDeckAlt;

    /* get the first token */
    if ((token = strtok(s, delim)) != NULL) {
        T->onGround = (strcmp(token, "G") == 0 ? 2 : 1);
        // MSFS_on_ground=(T->onGround==2);
    }

    if ((token = strtok(NULL, delim)) != NULL) {
        flightDeckAlt = strtol(token, &ptr, 10) / 100.0;
    }

    if ((token = strtok(NULL, delim)) != NULL) {
        T->heading_true = strtol(token, &ptr, 10) / 100.0 * DEG2RAD;
    }

    if ((token = strtok(NULL, delim)) != NULL) {
        T->pitch = strtol(token, &ptr, 10) / 100.0 * DEG2RAD;
    }

    if ((token = strtok(NULL, delim)) != NULL) {
        T->bank = strtol(token, &ptr, 10) / 100.0 * DEG2RAD;
    }

    if ((token = strtok(NULL, delim)) != NULL) {
        T->latitude = strtod(token, &ptr) * DEG2RAD; // Boost gives lat & long in degrees
    }

    if ((token = strtok(NULL, delim)) != NULL) {
        T->longitude = strtod(token, &ptr) * DEG2RAD; // Boost gives lat & long in degrees;
    }

    // T->altitude = flightDeckAlt - 28.412073 - 92.5 * sin(T->pitch);
    T->altitude = flightDeckAlt;
}

int umainBoost(Target *T) {
    char chaine[128] = {0};

    int nbread = recv(sPSXBOOST, chaine, 127, 0);
    if (nbread > 0) {
        if (chaine[0] == 'F' || chaine[0] == 'G') {
            Decode_Boost(T, chaine);
        } else {
            if (DEBUG) {
                printf("Wrong boost string received: %s", chaine);
                fprintf(fdebug, chaine);
            }
        }
    } else {
        //  printf("Boost connection lost.... We have to exit now. Sorry Folks\n");
    }

    return nbread;
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
    char cBuf[MAXLEN] = {0};
    int update = 0; // shall we update the aircraft in MSFS ?

    bzero(cBuf, MAXLEN);
    int nbread = recv(sPSX, cBuf, MAXLEN - 1, 0);

    if (nbread == 0) {
        // Socket is closed
        printf("Error in main PSX socket. Unfortunately we are closing....\n");
        exit(-1);
    }

    if (strstr(cBuf, "Qs122=")) {
        S122(strstr(cBuf, "Qs122="), T);
        update = 1;
    }

    // New situ loaded
    if (strstr(cBuf, "load3")) {
        sendQPSX("Qi198=-999920"); // no crash detection fort 10 seconds
        sleep(1);                 // let's wait a few seconds to get everyone ready
        MSFS_on_ground = 0;
    }

    // ExtLts : External lights, Mode=XECON
    if (strstr(cBuf, "Qs443")) {
        S443(strstr(cBuf, "Qs443="), T);
        update = 1;
    }

    //// PSX on groud
    if (strstr(cBuf, "Qi257")) {
        I257(strstr(cBuf, "Qi257"), T);
        update = 1;
    }

    //// Update Gear position
    if (strstr(cBuf, "Qh170")) {
        H170(strstr(cBuf, "Qh170"), T);
        update = 1;
    }
    //// Update PArking break
    if (strstr(cBuf, "Qh397")) {
        H397(strstr(cBuf, "Qh397"), T);
        update = 1;
    }

    //// Update Flap position
    if (strstr(cBuf, "Qh389")) {
        H389(strstr(cBuf, "Qh389"), T);
        update = 1;
    }
    //// Speedbrake
    if (strstr(cBuf, "Qh388")) {
        H388(strstr(cBuf, "Qh388"), T);
        update = 1;
    }

    // Update Time
    if (strstr(cBuf, "Qs124")) {
        S124(strstr(cBuf, "Qs124"), T);
        update = 1;
    }

    // Indicated Airspeed IAS
    if (strstr(cBuf, "Qs483")) {
        S483(strstr(cBuf, "Qs483"), T);
        update = 1;
    }

    // Rudder+aileron+elevator
    if (strstr(cBuf, "Qs480")) {
        S480(strstr(cBuf, "Qs480"), T);
        update = 1;
    }

    // COMMS
    if (strstr(cBuf, "Qs458")) {
        S458(strstr(cBuf, "Qs458"), T);
        update = 1;
    }

    // Steering wheel
    if (strstr(cBuf, "Qh426")) {
        H426(strstr(cBuf, "Qh426"), T);
        update = 1;
    }

    // Steering wheel
    if (strstr(cBuf, "Qi204")) {
        I204(strstr(cBuf, "Qi204"), T);
        update = 1;
    }
    // Altimeter
    if (strstr(cBuf, "Qs448")) {
        S448(strstr(cBuf, "Qs448"), T);
        update = 1;
    }
    // MSFS slave-Master
    if (strstr(cBuf, "Qs78")) {
        S78(strstr(cBuf, "Qs78"));
        update = 1;
    }

    return update;
}
