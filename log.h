#include <stdio.h>
#include <stdint.h>
#include <windows.h>

#define MAXLEN_DEBUG_MSG 8192 // maximum debug message size
#define NB_LOGS 20

/*Log levels*/
#define LL_DEBUG 1
#define LL_VERBOSE 2
#define LL_INFO 3
#define LL_ERROR 4

void printDebug(int level, const char *debugInfo, ...);

struct logMessage {
    uint64_t Id;
    char message[MAXLEN_DEBUG_MSG];
};

void cleanupDebugBuffer(logMessage **D);
void logging(logMessage **D, const char *msg);
