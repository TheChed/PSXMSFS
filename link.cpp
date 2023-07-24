#include <stdio.h>
#include "PSXMSFS.h"

/* void printDebug(debugMessage **D)
{
    static uint64_t printedLogs = 0;
    for (size_t i = 0; i < NB_LOGS; i++) {
        if (D[i]->Id > printedLogs) {
            printf("Debug Id: %llu\tLog: %s\n", D[i]->Id, D[i]->message);
            printedLogs++;
        }
    }

}

*/
int main(void)
{
    if (initialize(NULL, NULL) != 0) {
        printf("Could not initialize various parameters. Quitting now\n");
        exit(EXIT_FAILURE);
    }

    if (connectPSXMSFS() != 0) {

        printf("Could not connect PSX to MSFS. Quitting now\n");
        exit(EXIT_FAILURE);
    }

    debugMessage **debugBuff = initDebugBuff(); 
    (void) debugBuff;
    main_launch();
    cleanup();


    printf("Normal exit. See you soon...\n");
    return 0;
}
