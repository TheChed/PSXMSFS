#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <strings.h>
#include <time.h>
#include <windows.h>
#include "PSXMSFS.h"
// Global variables
int print=1;

void state(Target *T) {

    if (print) {
        printf("PSX:  ");
        printf("Alt: %.0f\t", T->altitude );
        printf("Head: %.2f\t", T->heading );
        printf("Lat: %.4f\t", T->latitude );
        printf("Long: %.4f\t", T->longitude );
        printf("Pitch in deg: %.6f\t", T->pitch );
        printf("Bank: %.4f\t", T->bank );
        printf("TAS: %.1f\t", T->TAS);
        printf("\r");
    }
}

void S121(char *s, Target *T) {

    const char delim[2] = ";";
    char *token, *ptr;



    /* get the first token */
    token = strtok(s + 6, delim);
    /* walk through other tokens */
        T->pitch = strtol(token, &ptr, 10)*180.0/M_PI/100000.0;
        token = strtok(NULL, delim);

        T->bank = strtol(token, &ptr, 10)*180.0/M_PI/100000.0;;
        token = strtok(NULL, delim);

        T->heading = strtod(token, &ptr);
        token = strtok(NULL, delim);

        T->altitude = strtol(token, &ptr, 10)/1000.0;
        token = strtok(NULL, delim);

        T->TAS = strtol(token, &ptr, 10)/1000.0;
        token = strtok(NULL, delim);

        T->latitude = strtod(token, &ptr);
        token = strtok(NULL, delim);

        T->longitude = strtod(token, &ptr);
        token = strtok(NULL, delim);
}

void S122(char *s, Target *T) {

    const char delim[2] = ";";
    char *token, *ptr;
    int PosUpdated;
    /* get the first token */
    token = strtok(s + 6, delim);
    PosUpdated=(int)(token[0]-'0');
    /* walk through other tokens */
    
        token = strtok(NULL, delim);
        T->pitch = strtol(token, &ptr, 10)*180.0/M_PI/1000.0;
        
        token = strtok(NULL, delim);
        T->bank = strtol(token, &ptr, 10)*180.0/M_PI/1000.0;;

        token = strtok(NULL, delim);
        T->heading = strtod(token, &ptr)*180/M_PI/1000.0;

        token = strtok(NULL, delim);
        T->altitude = strtol(token, &ptr, 10);

        token = strtok(NULL, delim);
        T->VerticalSpeed=strtol(token, &ptr,10);
        
        token = strtok(NULL, delim);
        T->TAS = strtol(token, &ptr, 10);
        
        token = strtok(NULL, delim); //YAW is not needed
        token = strtok(NULL, delim);
        T->latitude = strtod(token, &ptr)*180.0/M_PI;

        token = strtok(NULL, delim);
        T->longitude = strtod(token, &ptr)*180/M_PI;

}

void I257(char *s, Target *T) {

    if (strlen(s) != 7) {
        printf("Wrong Qi257 format\n");
        exit(-1);
    }

    T->onGround = (int)(s[6] - '0');
}
void I129(char *s, Target *T) {

    if (strlen(s) != 7) {
        printf("Wrong Qi129 format\n");
    }

    // T->notpaused = (int)(s[6] - '0');
}

void Decode(char *resultat, int prefix, char type, char *buffer, Target *T) {
    char resu[50] = {0};

    switch (type) {
    case 's':
        switch (prefix) {
        case 1:
            strcpy(resu, buffer);
            break;
        case 121:
            S121(buffer, T);
            strcpy(resu, "121 done");
            break;
        case 122:
            S122(buffer, T);
            strcpy(resu, "121 done");
            break;
        default:
            strcpy(resu, buffer);
            printf("Variable Qs%d not decoded.\n", prefix);
        };
        break;
    case 'h':
    case 'i':
        switch (prefix) {

        case 257:
            I257(buffer, T);
            strcpy(resu, "Qi257 done");
            break;
        case 129:
            I129(buffer, T);
            strcpy(resu, "Qi129 done");
            break;
        default:
            strcpy(resu, buffer);
            printf("Variable Qi%d not decoded.\n", prefix);
        };
        break;
    default:
        strcpy(resu, "Variable not found");
    }
    strcpy(resultat, resu);
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
    T->onGround = strcmp(token, "F");

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

    T->altitude = flightDeckAlt -  28.412073 - 92.5 * sin( T->pitch /180.0*M_PI ) +15.63;

    state(T);
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
                } 
                else {
                    boucle=0; //too much data read, exiting
                }
            }
        } else boucle =0;
    }
    return pos;
}
int umainBoost2(Target *T) {
    char chaine[MAXLEN];

        int nbread = recv(sPSXBOOST, chaine,MAXLEN, 0);
        if (nbread > 0) {
           // printf("chaine: %s\n",chaine);
            Decode_Boost(T, chaine);
        } else 
        {
            printf("Nothing received\n");
        }
    return nbread;
}

int umain(Target *T) {
    char chaine[MAXLEN];
    char cBuf;
    char VarDecoded[MAXLEN];
    int boucle = 1;
    int pos = 0;

    while (boucle) {
        int nbread = recv(sPSX, &cBuf, 1, 0);
        if (nbread > 0) {
            if (cBuf == '\n' || cBuf == '\r' || cBuf == 10 || cBuf == 0) {
                chaine[pos] = '\0';
                boucle = 0;
            } else {
                if (pos < MAXLEN) {
                    chaine[pos++] = cBuf;
                } 
                else {
                    boucle=0; //too much data read, exiting
                }
            }
        } else boucle =0;
    }

    //  We found the Variable in the stream
    //if (strstr(chaine, "Qs121=")) {
    //    Decode(VarDecoded, 121, 's', chaine, T);
    //}
    if (strstr(chaine, "Qs122=")) {
        Decode(VarDecoded, 122, 's', chaine, T);
    }
    if (strstr(chaine, "Qi257=")) {
        Decode(VarDecoded, 257, 'i', chaine, T);
    }
    if (strstr(chaine, "Qi129=")) {
        Decode(VarDecoded, 129, 'i', chaine, T);
    }

    return pos;
}
