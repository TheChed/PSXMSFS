#include <stdio.h>
#include <string.h>
#include <pthread.h>
#include <stdint.h>
#include <stdlib.h>
#include "PSXMSFS.h"

#define NB_LOGS 20

void *runLink(void *param)
{
    (void)param;
    main_launch();
    return NULL;
}

void *printLogBuffer(void *Param)
{

    logMessage *D = (logMessage *)Param;

    static uint64_t printedLogs = 0;

    uint64_t ID;
    char mess[128];

    while (1) {
        for (int i = 0; i < NB_LOGS; i++) {
            ID = getLogID(D, i);
            if (ID > printedLogs) {
                strncpy(mess, getLogMessage(D, i), 128);
                printf("Debug Id: %lld\tLog: %s\n", ID, mess);
                printedLogs++;
            }
        }
    }
}

int main(void)
{

    pthread_t Tmain, Tlog;
    logMessage *D = initLogBuffer();

    if (initialize("127.0.0.1", "192.168.1.132", 10747, NULL, 10749) != 0) {
        printf("Quitting now\n");
        exit(EXIT_FAILURE);
    }

    if (connectPSXMSFS() == NULL) {

        printf("Could not connect PSX to MSFS. Quitting now\n");
        exit(EXIT_FAILURE);
    }

    pthread_create(&Tmain, NULL, runLink,NULL);
    pthread_create(&Tlog, NULL, printLogBuffer,NULL);

    pthread_join(Tmain,NULL);

    cleanup();

    printf("Normal exit. See you soon...\n");
    return 0;
}
