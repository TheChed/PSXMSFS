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

    struct timeval start;
    double msecs = 0;

    gettimeofday(&start, NULL);

    // Do stuff  here

    msecs = (double)start.tv_usec / 1000;

    if (print) {
        //   asctime_s(stime, sizeof(stime),&result);
        printf("PSX: |%f|\t  ", msecs);
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

    if (strlen(s) != 7) {
        printf("Wrong Qh170 code\n");
        exit(-1);
    }

    T->GearLever = (int)(s[6] - '0');
    printf("s: %s\t T->GearLever: %d\n", s, T->GearLever);
}

// Flap lever variable Qh389
void H389(char *s, Target *T) {

    if (strlen(s) != 7) {
        printf("Wrong Qh389 code\n");
        exit(-1);
    }

    T->FlapLever = (int)(s[6] - '0');
    printf("s: %s\t T->FlapLever: %d\n", s, T->FlapLever);
}

// Speedbrake lever variable Qh389
void H388(char *s, Target *T) {

    char *token, *ptr;
    token = strtok(s + 6, delim);

    T->SpdBrkLever = strtol(token, &ptr, 10);
    printf("s: %s\t T->SpdBrkLever: %d\n", s, T->SpdBrkLever);
}

void S121(char *s, Target *T) {

    char *token, *ptr;

    /* get the first token */
    token = strtok(s + 6, delim);
    /* walk through other tokens */
    T->pitch = strtol(token, &ptr, 10) * 180.0 / M_PI / 100000.0;
    token = strtok(NULL, delim);

    T->bank = strtol(token, &ptr, 10) * 180.0 / M_PI / 100000.0;
    ;
    token = strtok(NULL, delim);

    T->heading = strtod(token, &ptr);
    token = strtok(NULL, delim);

    T->altitude = strtol(token, &ptr, 10) / 1000.0;
    token = strtok(NULL, delim);

    T->TAS = strtol(token, &ptr, 10) / 1000.0;
    token = strtok(NULL, delim);

    T->latitude = strtod(token, &ptr);
    token = strtok(NULL, delim);

    T->longitude = strtod(token, &ptr);
    token = strtok(NULL, delim);
}

void S483(char *s, Target *T) {

    char *token, *ptr;

    /* get the first token */
    token = strtok(s + 6, delim);
    
    /* walk through other tokens */
    T->IAS= strtol(token, &ptr, 10) / 10.0;
}
void S122(char *s, Target *T) {

    const char delim[2] = ";";
    char *token, *ptr;
    int PosUpdated;
    /* get the first token */
    token = strtok(s + 6, delim);
    PosUpdated = (int)(token[0] - '0');
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

    if (strlen(s) != 7) {
        printf("Wrong Qi257 format\n");
        exit(-1);
    }
    T->onGround = (int)(s[6] - '0');
    printf("s: %s\t T->onground: %d\n", s, T->onGround);
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
    char chaine[MAXLEN];
    char cBuf;
    int boucle = 1;
    int pos = 0;
    int update = 0; // shall we update the aircraft in MSFS ?

    while (boucle) {
        int nbread = recv(sPSX, &cBuf, 1, 0);
        if (nbread > 0) {
            if (cBuf == '\n' || cBuf == '\r' || cBuf == 10 || cBuf == 0) {
                chaine[pos] = '\0';
                boucle = 0;
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

    //  We found the Variable in the stream
    // if (strstr(chaine, "Qs121=")) {
    //    Decode(VarDecoded, 121, 's', chaine, T);
    //}
    if (strstr(chaine, "Qs122=")) {
        S122(chaine, T);
        update = 1;
    }

    // PSX on groud
    if (strstr(chaine, "Qi257=")) {
        I257(chaine, T);
        update = 1;
    }

    // Update Gear position
    if (strstr(chaine, "Qh170=")) {
        H170(chaine, T);
        update = 1;
    }
    // Update Flap position
    if (strstr(chaine, "Qh389=")) {
        H389(chaine, T);
        update = 1;
    }
    // Speedbrake
    if (strstr(chaine, "Qh388=")) {
        H388(chaine, T);
        update = 1;
    }
    if (strstr(chaine, "Qs482=")) {
    //    printf("Got Qs482%s\n", chaine);
    //    update = 1;
    }
    if (strstr(chaine, "Qs483=")) {
        S483(chaine,T);
        update = 1;
    }

    if (strstr(chaine, "Qi214=")) {
    //    printf("Got Qi214:%s\n", chaine);
    //    update = 1;
    }
    return update;
}
