#include <cstdint>
#include <stdio.h>
#include <string.h>
#include <time.h>
#include <stdarg.h>
#include "PSXMSFSLIB.h"

#define MAXLEN_DEBUG_MSG 8192 // maximum debug message size

static int NB_LOGS = 0;

static inline DWORD elapsedMs(DWORD start_time)
{
    return GetTickCount() - start_time;
}

typedef struct logMessage {
    uint64_t Id;
    int loglevel;
    char message[MAXLEN_DEBUG_MSG];
} logMessage;

logMessage *logBuffer = NULL;

static void logging(LOG_LEVELS level, const char *msg)
{
    static uint64_t nblogs = 0;
    int currCount;

    currCount = nblogs % NB_LOGS;
    if (strlen(msg)) {
        nblogs++;
        logBuffer[currCount].Id = nblogs;
        logBuffer[currCount].loglevel = level;
        strncpy(logBuffer[currCount].message, msg, MAXLEN_DEBUG_MSG - 1);
    }
}

void printDebug(LOG_LEVELS level, const char *debugInfo, ...)
{
    va_list ap;
    char msg[MAXLEN_DEBUG_MSG];
    char timestamp[50];

    time_t t = time(NULL);
    struct tm date = *localtime(&t);
    FILE *fdebug;

    fdebug = fopen("LOG.TXT", "a");
    if (!fdebug)
        return;


    va_start(ap, debugInfo);
    vsnprintf(msg, sizeof(msg), debugInfo, ap);
    va_end(ap);

    strftime(timestamp, 50, "%H:%M:%S", &date);
    if (level >= VERBOSITY) {
        fprintf(fdebug, "%s[+%ld.%.03ds]\t%s", timestamp, (long)elapsedMs(TimeStart) / 1000, (int)elapsedMs(TimeStart) % 1000, msg);
        fprintf(fdebug, "\n");
        fflush(fdebug);

        logging(level, msg);
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

int getLogLevel(logMessage *D, int n)
{
    return D[n].loglevel;
}

logMessage *getLogBuffer(int nbLogs)
{

    logMessage *log = (logMessage *)malloc(sizeof(logMessage) * nbLogs);
    if (log == NULL) {
        printDebug(LL_ERROR, "Could not initialize log buffer. Exiting now...");
        quit = 1;
    }
    bzero(log, sizeof(logMessage) * nbLogs);
    NB_LOGS = nbLogs;
    logBuffer = log;
    return log;
}

void freeLogBuffer(logMessage *Log)
{
    free(Log);
}
