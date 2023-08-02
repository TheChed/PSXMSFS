#ifndef __PSXMSFS_H_
#define __PSXMSFS_H_
#include <windows.h>
#include <cstdint>

#define MAXLEN_DEBUG_MSG 8192

typedef struct flags flags_options;
typedef struct FLAGS PSXMSFSFLAGS;

typedef struct server_options {
    char *PSXMainServer;  // IP address of the PSX main server
    char *MSFSServer;     // IP address of the PSX boost server
    char *PSXBoostServer; // IP address of the MSFS server
    int PSXPort;          // Main PSX port
    int PSXBoostPort;     // PSX boot server port

} server_options;

typedef struct flags {
    int TCAS_INJECT;        // 1 if TCAS is injected to PSX, 0 otherwise
    int ELEV_INJECT;        // 1 if MSFS elevation is injected into PSX. 0 otherwise
    int INHIB_CRASH_DETECT; // 1 if no crash detection in PSX when loading new situ. 0 otherwise

    int ONLINE; // 1 if PSXMSFS is used on online on VATSIM,IVAO etc, 0 otherwise

    int LOG_VERBOSITY; // verbosity of the logs

    int SLAVE; // 0 if PSX is slave, 1 if MSFS is slave
} flags;

typedef struct FLAGS {

    server_options server;
    flags flags;

    SOCKET sPSX;      // main PSX socket id
    SOCKET sPSXBOOST; // PSX boost socket id
} FLAGS;

struct debugMessage {
    uint64_t Id;
    char message[MAXLEN_DEBUG_MSG];
};

typedef struct debugMessage debugMessage;

extern "C" __declspec(dllimport) DWORD initialize(const char *MSFSIP, const char *PSXIP, int PSXPort, const char *BoostIP, int BoostPort);
extern "C" __declspec(dllimport) FLAGS *connectPSXMSFS(void);
extern "C" __declspec(dllimport) DWORD WINAPI main_launch(void);
extern "C" __declspec(dllimport) DWORD cleanup(void);
extern "C" __declspec(dllimport) debugMessage **initDebugBuff(void);
#endif
