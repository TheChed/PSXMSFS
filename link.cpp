#include <stdio.h>
#include <windows.h>
#include "PSXMSFS.h"

#define NB_LOGS 10

struct resu {
    debugMessage **D;
    char logmsg[8192];
} *resu;

DWORD WINAPI runLink(void *param)
{
    (void)param;
    main_launch();
    return 0;
}

DWORD WINAPI printLogBuffer(void *Param)
{

    debugMessage **D = (debugMessage **)(Param);

    static uint64_t printedLogs = 0;
    while (1) {
        for (size_t i = 0; i < NB_LOGS; i++) {
            if (D[i]->Id > printedLogs) {
                printf("Debug Id: %llu\tLog: %s\n", D[i]->Id, D[i]->message);
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

    debugMessage **debugBuff = initDebugBuff();
    loghandle = CreateThread(NULL, 0, printLogBuffer, debugBuff, 0, &logthread);

    if (initialize("127.0.0.1", "192.168.1.132", 10747, NULL, 10749) != 0) {
        printf("Quitting now\n");
        exit(EXIT_FAILURE);
    }

    if (connectPSXMSFS()== NULL) {

        printf("Could not connect PSX to MSFS. Quitting now\n");
        exit(EXIT_FAILURE);
    }

    mainhandle = CreateThread(NULL, 0, runLink, NULL, 0, &mainthread);

    WaitForSingleObject(loghandle, INFINITE);
    WaitForSingleObject(mainhandle, INFINITE);

    cleanup();

    free(resu);
    printf("Normal exit. See you soon...\n");
    return 0;
}
