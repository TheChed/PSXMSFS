#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <strings.h>
#include <time.h>
#include <windows.h>
#include "PSXMSFS.h"
// Global variables

void state(Target *T) {
    char *Lat, *Long;

     Lat = convert(T->latitude, 1);
      Long = convert(T->longitude, 0);
if(PRINT) {   printf("PSX:  ");
    printf("Alt: %.0f\t", T->altitude / 1000.0);
    printf("Head: %.2f\t", T->heading * 180.0 / M_PI);
    printf("Lat: %.4f\t", T->latitude * 180 / M_PI);
    printf("Long: %.4f\t", T->longitude * 180 / M_PI);
    printf("Pitch in deg: %.6f\t", T->pitch * 180 / M_PI / 100000.0);
    printf("Bank: %.4f\t", T->bank * 180.0 / M_PI / 100000.0);
    printf("TAS: %.1f\t", T->TAS / 1000.0);
}
      free(Lat);
      free(Long);
}

void S121(char *s, Target *T) {

    const char delim[2] = ";";
    char *token, *ptr;

    /* get the first token */
    token = strtok(s + 6, delim);
    /* walk through other tokens */
    while (token != NULL) {

        T->pitch = strtol(token, &ptr, 10);
        token = strtok(NULL, delim);

        T->bank = strtol(token, &ptr, 10);
        token = strtok(NULL, delim);

        T->heading = strtod(token, &ptr);
        token = strtok(NULL, delim);

        T->altitude = strtol(token, &ptr, 10);
        token = strtok(NULL, delim);

        T->TAS = strtol(token, &ptr, 10);
        token = strtok(NULL, delim);

        T->latitude = strtod(token, &ptr);
        token = strtok(NULL, delim);

        T->longitude = strtod(token, &ptr);
        token = strtok(NULL, delim);
    }
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
        default:
            strcpy(resu, buffer);
            printf("Variable Qs%d not decoded.\n", prefix);
        };
        break;
    case 'h':
    case 'i':
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

int umain(Target *T) {
    char chaine[MAXLEN] = {0};
    char cBuf;
    char VarDecoded[MAXLEN];
    int boucle = 1;
    int pos = 0;

    while (boucle) {
        int nbread = recv(sPSX, &cBuf, 1, 0);
        if (nbread > 0) {
            if (pos > MAXLEN) {
                boucle = 0;
                printf("Lenght >MAXLEN\n");
            }

            if (cBuf == '\n' || cBuf == '\r' || cBuf == 10) {
                boucle = 0;
            } else {
                chaine[pos] = cBuf;
                pos++;
            }
        }
    }

    chaine[pos] = '\0';

   //  We found the Variable in the stream
          if (strstr(chaine, "Qs121=")) {
              Decode(VarDecoded,121, 's', chaine, T);
      }
    return 0;
}
