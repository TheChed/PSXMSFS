#include <cstdint>
#include <string.h>
#include <stdio.h>
#include <windows.h>
#include "PSXMSFS.h"

#define NB_LOGS 20

DWORD WINAPI runLink(void *param)
{
    (void)param;
    main_launch();
    return 0;
}

DWORD WINAPI printLogBuffer(void *Param)
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
    return 0;
}

int main(void)
{
    DWORD logthread, mainthread;
    HANDLE loghandle, mainhandle;

    logMessage *D = getLogBuffer();
    loghandle = CreateThread(NULL, 0, printLogBuffer, D, 0, &logthread);

    if (initialize("127.0.0.1", "192.168.1.132", 10747, NULL, 10749) != 0) {
        printf("Quitting now\n");
        exit(EXIT_FAILURE);
    }

    if (connectPSXMSFS() == NULL) {

        printf("Could not connect PSX to MSFS. Quitting now\n");
        exit(EXIT_FAILURE);
    }

    mainhandle = CreateThread(NULL, 0, runLink, NULL, 0, &mainthread);

    WaitForSingleObject(loghandle, INFINITE);
    WaitForSingleObject(mainhandle, INFINITE);

    cleanup();

    printf("Normal exit. See you soon...\n");
    return 0;
}
