#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include "log.h"
#include "util.h"

int currCount = 0;

logMessage **D = NULL;

void printDebug(int level, const char *debugInfo, ...)
{
    va_list ap;
    char msg[MAXLEN_DEBUG_MSG];
    char timestamp[50];

    time_t t = time(NULL);
    struct tm date = *localtime(&t);
    FILE *fdebug;

    fdebug = fopen("DEBUG.TXT", "a");
    if (!fdebug)
        return;

    va_start(ap, debugInfo);
    vsnprintf(msg, sizeof(msg), debugInfo, ap);
    va_end(ap);

    strftime(timestamp, 50, "%H:%M:%S", &date);
    if (level >= PSXflags.LOG_VERBOSITY) {
        fprintf(fdebug, "%s[+%ld.%.03ds]\t%s", timestamp, (long)elapsedMs(TimeStart) / 1000, (int)elapsedMs(TimeStart) % 1000, msg);
        fprintf(fdebug, "\n");
        fflush(fdebug);

        // and also print on the console or in buffer
        printf("%s\n", msg);
        if (D != NULL && level >= LL_INFO) {
            logging(D, msg);
        }
    }

    fclose(fdebug);
}

logMessage **initDebugBuff(void)
{

    logMessage **buffer = NULL;

    buffer = (logMessage **)malloc(sizeof(logMessage *) * NB_LOGS);
    if (buffer == NULL) {
        return NULL;
    }

    for (int i = 0; i < NB_LOGS; i++) {
        buffer[i] = (logMessage *)malloc(sizeof(logMessage));
        buffer[i]->Id = 0;
    }

    D = buffer;

    return buffer;
}

void logging(logMessage **D, const char *msg)
{
    static int nblogs = 1;

    D[currCount]->Id = nblogs;
    strncpy(D[currCount]->message, msg, MAXLEN_DEBUG_MSG - 1);
    nblogs++;
    currCount++;
    currCount = currCount % NB_LOGS;
}

void cleanupDebugBuffer(logMessage **D)
{
    for (int i = 0; i < NB_LOGS; i++) {
        free(D[i]);
    }
    free(D);
}
