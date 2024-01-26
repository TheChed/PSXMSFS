/*----------------------------------------------
 * Header file to be included
 * in all clients using the DLL
 * --------------------------------------------*/
#ifndef __PSXMSFS_H_
#define __PSXMSFS_H_
#include <windows.h>
#include <cstdint>

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

extern "C" __declspec(dllimport) DWORD initialize(const char *MSFSIP, const char *PSXIP, int PSXPort, const char *BoostIP, int BoostPort);
extern "C" __declspec(dllimport) FLAGS *connectPSXMSFS(void);
extern "C" __declspec(dllimport) DWORD WINAPI main_launch(void);
extern "C" __declspec(dllimport) DWORD cleanup(void);

/*----------------------------------
 * Log related functions
 * ---------------------------------*/
typedef struct logMessage logMessage;

extern "C" __declspec(dllimport) logMessage *getLogBuffer(void);
extern "C" __declspec(dllimport) char *getLogMessage(logMessage *D, int n);
extern "C" __declspec(dllimport) uint64_t getLogID(logMessage *D, int n);
#endif
