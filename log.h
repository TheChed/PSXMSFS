#include <stdio.h>
#include <stdint.h>

#define MAXLEN_DEBUG_MSG 8192 // maximum debug message size
#define NB_LOGS 20

/*Log levels*/
#define LL_DEBUG 1
#define LL_VERBOSE 2
#define LL_INFO 3
#define LL_ERROR 4

void printDebug(int level, const char *debugInfo, ...);
void printPSX(int boost, const char *debugInfo, ...);

struct debugMessage {
    uint64_t Id;
    char message[MAXLEN_DEBUG_MSG];
};

typedef struct debugMessage debugMessage;

void cleanupDebugBuffer(debugMessage **D);
void logging(debugMessage **D, const char *msg);
extern "C" __declspec(dllexport) debugMessage **initDebugBuff(void);
