#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <strings.h>
#include <time.h>
#include <windows.h>
#include "PSXMSFS.h"
// Global variables

char *Qvariables[NB_Q_VAR] = {"Qs121", "Qs123", "Qs145"};

void Qformat(QPSX *Q, const char *s) {
    char *ptr;

    Q->type = s[1];
    Q->index = strtol(s + 2, &ptr, 10);
    strcpy(Q->name, s);
}

void state(Target *T) {
    char *Lat, *Long;

    Lat = convert(T->latitude, 1);
    Long = convert(T->longitude, 0);
    printf("PSX:  ");
    printf("Alt: %.0f ", T->altitude / 1000.0);
    printf("Head: %.2f ", T->heading * 180.0 / M_PI);
    printf("Lat: %.4f ",  T->latitude *180 / M_PI);
    printf("Long: %.4f ", T->longitude*180 / M_PI);
    printf("Pitch: %.4f ", T->pitch * 180 / M_PI / 100000.0);
    printf("Bank: %.4f ", T->bank * 180.0 / M_PI / 100000.0);
    printf("TAS: %.1f\n", T->TAS / 1000.0);
    printf("\n");

    free(Lat);
    free(Long);
}

void S121(char *s) {

    const char delim[2] = ";";
    char *token, *ptr;
    /* get the first token */
    token = strtok(s + 6, delim);
    /* walk through other tokens */
    while (token != NULL) {

        T.pitch = strtol(token, &ptr, 10);
        token = strtok(NULL, delim);

        T.bank = strtol(token, &ptr, 10);
        token = strtok(NULL, delim);

        T.heading = strtod(token, &ptr);
        token = strtok(NULL, delim);

        T.altitude = strtol(token, &ptr, 10);
        token = strtok(NULL, delim);

        T.TAS = strtol(token, &ptr, 10);
        token = strtok(NULL, delim);

        T.latitude = strtod(token, &ptr);
        token = strtok(NULL, delim);

        T.longitude = strtod(token, &ptr);
        token = strtok(NULL, delim);
    }
}

char *S123(const char *s) {
    char *ptr;
    static char res[20];

    // Qs123= => s+6th position of the string
    //time_t t = strtol(s + 6, &ptr, 10) / 1000;

  //  struct tm lt;

    //    localtime_r(&t, &lt);

    //strftime(res, sizeof(res), "%X", &lt);
    strcpy(ptr,"not implemented");
    return ptr;

}

char *Decode(int prefix, char type, char *buffer) {
    char resu[50];
    char tmp[MAXLEN];
    strcpy(tmp, buffer);

    switch (type) {
    case 's':
        switch (prefix) {
        case 1:
            strcpy(resu, buffer);
            break;
        case 123:
            strcpy(resu, S123(tmp));
            break;
        case 124:
            strcpy(resu, S123(tmp));
            break;
        case 121:
            S121(tmp);
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
    return resu;
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

void init_Q_variables(int nb, QPSX **p) {

    QPSX **tmp;
    // Store the Variables Q conveniently
    tmp = p;
    if (tmp == NULL) {
        printf("Error allocation memory to QPSX**\n");
        exit(EXIT_FAILURE);
    }

    // Putting the variables in structures
    for (int i = 0; i < nb; i++) {
        p[i] = (QPSX *)malloc(sizeof(QPSX));
        Qformat(p[i], Qvariables[i]);
    }
}
int umain(void) {
    char chaine[MAXLEN]={0};
    char cBuf;
    int boucle = 1;
    int pos=0;

       // chaine = (char *)calloc(MAXLEN , sizeof(char));

    while (boucle) {
        int nbread = recv(sPSX, &cBuf, 1, 0);
        if (nbread > 0) {
            if (cBuf == '\n' || cBuf == '\r')  {
                boucle = 0;
            } else {
              //  strcat(chaine, &cBuf);
              chaine[pos]=cBuf;
              pos++;
            }
        }
    }
    chaine[pos]='\0';            
   // printf("chaine: %s\n", chaine);
    for (int i = 0; i < NB_Q_VAR; i++) {

        //strcpy(ptr, Qvariables[i]);
       // strcat(ptr, "=");

        // We found the Variable in the stream
        if (strstr(chaine, "Qs121=")) {
     //       printf("Variable %s: %s\n", Q[i]->name, Decode(Q[i]->index, Q[i]->type, chaine));
            //Decode(Q[i]->index, Q[i]->type, chaine);
            Decode(121, 's', chaine);
//     printf("PSX chaine: %s\n",chaine);
        }
    }
    //free(chaine);
    return 0;
}
