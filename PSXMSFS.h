/*----------------------------------------------
 * Header file to be included
 * in all clients using the DLL
 * --------------------------------------------*/
#ifndef __PSXMSFS_H_
#define __PSXMSFS_H_

#ifdef __cplusplus
#include <cstdint>
#define LIBIMPORT extern "C" __declspec(dllimport)
#else
#include <stdint.h>
#define LIBIMPORT
#endif

#define IP_LENGTH 15

/*-------------------------------------
 * Internal switches
 * -----------------------------------*/

#define F_TCAS (1 << 0)
#define F_INJECT (1 << 1)
#define F_ONLINE (1 << 2)
#define F_INHIB (1 << 3)
#define F_SLAVE (1 << 4)

typedef struct flags FLAGS;
typedef struct logMessage logMessage;

typedef enum {
    LL_DEBUG = 1,
    LL_VERBOSE,
    LL_INFO,
    LL_ERROR
} LOG_LEVELS;

/*---------------------------------------
 * Various structures that can be used
 * by the client
 * ------------------------------------*/

typedef struct {
    double Alt;
    double latitude;
    double longitude;
    double heading;
    double pitch;
    double bank;
} ACFT;

typedef struct servers {
    char PSXIP[IP_LENGTH];   // IP address of the PSX main server
    char MSFSIP[IP_LENGTH];  // IP address of the PSX boost server
    char BOOSTIP[IP_LENGTH]; // IP address of the MSFS server
    int PSXPORT;             // Main PSX port
    int BOOSTPORT;           // PSX boot server port
} servers;

/*---------------------------------
 * Functions imported from the PSXMSFS DLL
 *--------------------------------*/

LIBIMPORT FLAGS *createFlagsPSXMSFS(void);
LIBIMPORT int initializePSXMSFS(FLAGS *flags);
LIBIMPORT int connectPSXMSFS(FLAGS *flags);
LIBIMPORT int launchPSXMSFS(FLAGS *flags);
LIBIMPORT int disconnectPSXMSFS(FLAGS *flags);

/*----------------------------------
 * Log related functions
 * logMessage is a buffer of 20 logmessages.
 *
 * CLient needs to initialize the buffer via:
 * logMessage *D = getLogBuffer();
 *
 * Each message has a unique ID and new
 * log messages keep been pushed on that buffer.
 *
 * Unique IDs can be retrieved with getLogID.
 * For example, getLogId(D, 6) will retrieve the unique ID of the 7th current log message.
 * This can be used in a loop in the client to check whether a new log has been received
 * ---------------------------------*/

LIBIMPORT logMessage *getLogBuffer(int nblogs);
LIBIMPORT char *getLogMessage(logMessage *D, int n);
LIBIMPORT uint64_t getLogID(logMessage *D, int n);
LIBIMPORT int getLogLevel(logMessage *D, int n);
LIBIMPORT void freeLogBuffer(logMessage *D);

/*----------------------------------------
 * Functions used to manipulate internal flags.
 * Setting and reading values
 * ---------------------------------------*/

LIBIMPORT unsigned int getSwitch(FLAGS *f);
LIBIMPORT void setSwitch(FLAGS *f, unsigned int flagvalue);
LIBIMPORT int getLogVerbosity(FLAGS *f);
LIBIMPORT void setLogVerbosity(FLAGS *f, LOG_LEVELS level);
LIBIMPORT unsigned int getSwitch(FLAGS *f);
LIBIMPORT ACFT getACFTInfo(void);
LIBIMPORT servers getServersInfo(FLAGS *f);
LIBIMPORT int setServersInfo(servers *S, FLAGS *f);
#endif
