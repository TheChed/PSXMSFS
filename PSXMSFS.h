/*----------------------------------------------
 * Header file to be included
 * in all clients using the DLL
 * --------------------------------------------*/
#ifndef __PSXMSFS_H_
#define __PSXMSFS_H_

#include <cstdint>
#include <windows.h>
#define LIBIMPORT extern "C" __declspec(dllimport)

#define IP_LENGTH 15

typedef struct flags {
    char PSXMainServer[IP_LENGTH];  // IP address of the PSX main server
    char MSFSServer[IP_LENGTH];     // IP address of the PSX boost server
    char PSXBoostServer[IP_LENGTH]; // IP address of the MSFS server
    int PSXPort;          // Main PSX port
    int PSXBoostPort;     // PSX boot server port

    int TCAS_INJECT;        // 1 if TCAS is injected to PSX, 0 otherwise
    int ELEV_INJECT;        // 1 if MSFS elevation is injected into PSX. 0 otherwise
    int INHIB_CRASH_DETECT; // 1 if no crash detection in PSX when loading new situ. 0 otherwise
    int ONLINE;             // 1 if PSXMSFS is used on online on VATSIM,IVAO etc, 0 otherwise
    int LOG_VERBOSITY;      // verbosity of the logs: 1 very verbose and 4 minimum verbosity
    int SLAVE;              // 0 if PSX is slave, 1 if MSFS is slave
} FLAGS;

/*---------------------------------
 * Functions imported from the PSXMSFS DLL
 *--------------------------------*/

LIBIMPORT int initialize(const char *MSFSIP, const char *PSXIP, int PSXPort, const char *BoostIP, int BoostPort);
LIBIMPORT FLAGS *connectPSXMSFS(void);
LIBIMPORT int main_launch(void);
LIBIMPORT int cleanup(void);

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
typedef struct logMessage logMessage;

LIBIMPORT logMessage *initLogBuffer(void);
LIBIMPORT char *getLogMessage(logMessage *D, int n);
LIBIMPORT uint64_t getLogID(logMessage *D, int n);
#endif
