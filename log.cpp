#include <cstdint>
#include <stdio.h>
#include <string.h>
#include <time.h>
#include <stdarg.h>
#include "PSXMSFSLIB.h"
#include "log.h"

#define MAXLEN_DEBUG_MSG 8192 // maximum debug message size
#define NB_LOGS 20


static inline DWORD elapsedMs(DWORD start_time)
{
    return GetTickCount() - start_time;
}

typedef struct logMessage {
    uint64_t Id;
    char message[MAXLEN_DEBUG_MSG];
} logMessage;

logMessage logBuffer[NB_LOGS];

static void logging(const char *msg)
{
    static int nblogs = 0;
    int currCount;

    currCount = nblogs % NB_LOGS;

    logBuffer[currCount].Id = nblogs;
    strncpy(logBuffer[currCount].message, msg, MAXLEN_DEBUG_MSG - 1);
    nblogs++;
    printf("Current log ID: %d\n", nblogs);
}

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
        // if INFO or above level
        printf("%s\n", msg);
        if (level >= LL_INFO) {
            logging(msg);
        }
    }

    fclose(fdebug);
}

char *getLogMessage(logMessage *D, int n)
{
    return D[n].message;
}

uint64_t getLogID(logMessage *D, int n)
{
    return D[n].Id;
}

logMessage *getLogBuffer(void)
{
    return logBuffer;
}
