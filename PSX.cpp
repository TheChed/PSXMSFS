#include <cstdlib>
//#include <ctime>
#include <time.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <strings.h>
#include <sys/time.h>
#include <windows.h>
#include "PSXMSFS.h"
// Global variables
int print = 1;

const char delim[2] = ";"; // delimiter for parsing the Q variable strings

void state(Target *T) {

    if (print) {
        //   asctime_s(stime, sizeof(stime),&result);
        printf("PSX:\t  ");
        printf("Alt: %.0f\t", T->altitude);
        printf("Head: %.2f\t", T->heading);
        printf("Lat: %.4f\t", T->latitude);
        printf("Long: %.4f\t", T->longitude);
        printf("Pitch in deg: %.6f\t", T->pitch);
        printf("Bank: %.4f\t", T->bank);
        printf("TAS: %.1f\t", T->TAS);
        printf("\r");
    }
}

// Position of Gear
void H170(char *s, Target *T) {


    T->GearLever = (int)(s[6] - '0');
}

// Flap lever variable Qh389
void H389(char *s, Target *T) {

    T->FlapLever = (int)(s[6] - '0');
}

//Parking break position
void H397(char *s, Target *T) {

    T->parkbreak = (int)(s[6] - '0');
}
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
    token = strtok(s + 6, delim);

    T->SpdBrkLever = strtol(token, &ptr, 10);
}


void S483(char *s, Target *T) {

    char *token, *ptr;

    /* get the first token */
    token = strtok(s + 6, delim);
    T->IAS = strtol(token, &ptr, 10) / 10.0;
}

void S480(char *s, Target *T) {

    int val[10]={0};

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

        if(s==NULL){
            return;
        }
        updateLights = 1;
        for (int i = 0; i < 14; i++) {
            T->light[i] = (int)(s[i + 6] - '0') < 5 ? 0 : 1;
        }
    }

void S122(char *s, Target *T) {

    const char delim[2] = ";";
    char *token, *ptr;

    if(s==NULL){
        return;
    }


    /* get the first token */
    token = strtok(s + 6, delim);
    /* walk through other tokens */

    token = strtok(NULL, delim);
    T->pitch = strtol(token, &ptr, 10) * 180.0 / M_PI / 1000.0;

    token = strtok(NULL, delim);
    T->bank = strtol(token, &ptr, 10) * 180.0 / M_PI / 1000.0;
    ;

    token = strtok(NULL, delim);
    T->heading = strtod(token, &ptr) * 180 / M_PI / 1000.0;

    token = strtok(NULL, delim);
    T->altitude = strtol(token, &ptr, 10);

    token = strtok(NULL, delim);
    T->VerticalSpeed = strtol(token, &ptr, 10);

    token = strtok(NULL, delim);
    T->TAS = strtol(token, &ptr, 10);

    token = strtok(NULL, delim); // YAW is not needed
    token = strtok(NULL, delim);
    T->latitude = strtod(token, &ptr) * 180.0 / M_PI;

    token = strtok(NULL, delim);
    T->longitude = strtod(token, &ptr) * 180 / M_PI;
}

void I257(char *s, Target *T) {

    T->onGround = (int)(s[6] - '0');
}

// Checks validity of input
int check_param(const char *s) {

    return (strlen(s) && (s[0] == 'Q') && ((s[1] == 's') || (s[1] == 'i') || (s[1] == 'h')));
}

char *convert(double d, int Lat) {

    char *resu;
    double dec;
    float min;

    d = d * 180 / M_PI;

    resu = (char *)malloc(11 * sizeof(char));

    if (Lat) {
        resu[0] = (d < 0) ? 'S' : 'N';
    } else {
        resu[0] = (d < 0) ? 'W' : 'E';
    }
    d = fabs(d);

    dec = d - (int)d;

    min = dec * 60.0;

    if (Lat) {
        sprintf(resu + 1, "%.2d %.2f", (int)d, min);
    } else {
        sprintf(resu + 1, "%.3d %.2f", (int)d, min);
    }

    return resu;
}

void Decode_Boost(Target *T, char *s) {

    const char delim[2] = ";";
    char *token, *ptr;
    float flightDeckAlt;

    /* get the first token */
    token = strtok(s, delim);
    T->onGround = (strcmp(token, "G") == 0 ? 2 : 1);

    token = strtok(NULL, delim);
    flightDeckAlt = strtol(token, &ptr, 10) / 100.0;

    token = strtok(NULL, delim);
    T->heading = strtol(token, &ptr, 10) / 100.0;

    token = strtok(NULL, delim);
    T->pitch = strtol(token, &ptr, 10) / 100.0;

    token = strtok(NULL, delim);
    T->bank = strtol(token, &ptr, 10) / 100.0;

    token = strtok(NULL, delim);
    T->latitude = strtod(token, &ptr);

    token = strtok(NULL, delim);
    T->longitude = strtod(token, &ptr);

    T->altitude = flightDeckAlt - 28.412073 - 92.5 * sin(T->pitch / 180.0 * M_PI) + 15.63;
}

int umainBoost(Target *T) {
    int boucle = 1;
    char cBuf;
    int pos = 0;
    char chaine[MAXLEN];

    while (boucle) {
        int nbread = recv(sPSXBOOST, &cBuf, 1, 0);
        if (nbread > 0) {
            if (cBuf == '\n' || cBuf == '\r' || cBuf == 10 || cBuf == 0) {
                chaine[pos] = '\0';
                boucle = 0;
                Decode_Boost(T, chaine);
            } else {
                if (pos < MAXLEN) {
                    chaine[pos++] = cBuf;
                } else {
                    boucle = 0; // too much data read, exiting
                }
            }
        } else
            boucle = 0;
    }
    return pos;
}
int umainBoost2(Target *T) {
    char chaine[MAXLEN];

    int nbread = recv(sPSXBOOST, chaine, MAXLEN, 0);
    if (nbread > 0) {
        // printf("chaine: %s\n",chaine);
        Decode_Boost(T, chaine);
    } else {
        printf("Nothing received\n");
    }
    return nbread;
}

int sendQPSX(const char *s) {

    char *dem;
    dem = (char *)malloc((strlen(s) + 1) * sizeof(char));

    strncpy(dem, s, strlen(s));
    dem[strlen(s)] = 10;

    int nbsend = send(sPSX, dem, strlen(dem) + 1, 0);

    if (nbsend == 0) {
        printf("Error sending variable %s to PSX\n", s);
    }

    free(dem);
    return nbsend;
}

int umain(Target *T) {
    char cBuf[MAXLEN]={0};
    int update = 0; // shall we update the aircraft in MSFS ?

    // while (boucle) {
    //     int nbread = recv(sPSX, &cBuf, 1, 0);
    //     if (nbread > 0) {
    //         if (cBuf == '\n' || cBuf == '\r' || cBuf == 10 || cBuf == 0) {
    //             chaine[pos] = '\0';
    //             boucle = 0;
    //         } else {
    //             if (pos < MAXLEN) {
    //                 chaine[pos++] = cBuf;
    //             } else {
    //                 boucle = 0; // too much data read, exiting
    //             }
    //         }
    //     } else
    //         boucle = 0;
    // }

    bzero(cBuf, MAXLEN);
    int nbread = recv(sPSX, cBuf, MAXLEN-1, 0);

    if (nbread == 0){
        //Socket is closed
        printf("Error in main PSX socket. Unfortunately we are closing....\n" );
        exit(-1);
    }


    //  We found the Variable in the stream
    // if (strstr(chaine, "Qs121=")) {
    //    Decode(VarDecoded, 121, 's', chaine, T);
    //}
    if (strstr(cBuf, "Qs122=")) {
        S122(strstr(cBuf,"Qs122="),T);
        update = 1;
    }

    // ExtLts : External lights, Mode=XECON
    if (strstr(cBuf, "Qs443")) {
        S443(strstr(cBuf,"Qs443="),T);
        update = 1;
    }

    //// PSX on groud
    if (strstr(cBuf, "Qi257")) {
        I257(strstr(cBuf,"Qi257"),T);
        update = 1;
    }

    //// Update Gear position
    if (strstr(cBuf, "Qh170")) {
        H170(strstr(cBuf,"Qh170"),T);
        update = 1;
    }
    //// Update PArking break
    if (strstr(cBuf, "Qh397")) {
        H397(strstr(cBuf,"Qh397"),T);
        update = 1;
    }
    
    //// Update Flap position
    if (strstr(cBuf, "Qh389")) {
        H389(strstr(cBuf,"Qh389"),T);
        update = 1;
    }
    //// Speedbrake
    if (strstr(cBuf, "Qh388")) {
        H388(strstr(cBuf,"Qh388"),T);
        update = 1;
    }

    // Update Time
    if (strstr(cBuf, "Qs124")) {
        S124(strstr(cBuf,"Qs124"),T);
        update = 1;
    }
    
    // Indicated Airspeed IAS
    if (strstr(cBuf, "Qs483")) {
        S483(strstr(cBuf,"Qs483"),T);
        update = 1;
    }
    
    // Rudder+aileron+elevator
    if (strstr(cBuf, "Qs480")) {
        S480(strstr(cBuf,"Qs480"),T);
        update = 1;
    }

    // Steering wheel
    if (strstr(cBuf, "Qh426")) {
        H426(strstr(cBuf,"Qh426"),T);
        update = 1;
    }


    return update;
}
