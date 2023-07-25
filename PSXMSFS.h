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
    size_t PSXPort;       // Main PSX port
    size_t PSXBoostPort;  // PSX boot server port
} server_options;

struct debugMessage {
    uint64_t Id;
    char message[MAXLEN_DEBUG_MSG];
};

typedef struct debugMessage debugMessage;

extern "C" __declspec(dllimport) DWORD initialize(server_options *server, flags *flags);
extern "C" __declspec(dllimport) server_options *connectPSXMSFS(void);
extern "C" __declspec(dllimport) DWORD WINAPI main_launch(void);
extern "C" __declspec(dllimport) DWORD cleanup(void);
extern "C" __declspec(dllimport) debugMessage **initDebugBuff(void);
#endif
