#ifndef __PSXMSFSLIB_H_
#define __PSXMSFSLIB_H_
#include <stdint.h>

/*---------------------------------
 * usefull macros
 *-------------------------------*/
#define UNUSED(V) ((void)V)
#define MAX(x, y) ((x) > (y) ? (x) : (y))
#define bzero(b, len) (memset((b), '\0', (len)), (void)0)

/*---------------------------------
 * various constant definitions
 * used in the variosu modules
 *-------------------------------*/
#define MSFSHEIGHT 15.13 // altitude offset in feet of the default 747 in MSFS when on ground
#define M_PI 3.14159265358979323846
#define NM 1852           // meters in a nm
#define EARTH_RAD 6371008 // earth radius in meters
#define FTM 0.3048        // feet to meters
#define DEG2RAD (M_PI / 180.0)
#define LMB (-0.0065)         // temperature gradient per meters
#define ALPHA -5.255822518257 // used in the atmosphere modelization
#define VSSAMPLE 50           // number of samples used from boost string to calculate the vertical speed
#define DELIM ";"

/*-----------------------------------------
 * structure used at initialization
 * to get user info about PSX servers, MSFS
 * and to store various user defined flags
 *----------------------------------------*/
typedef struct flags {
    char *PSXMainServer;  // IP address of the PSX main server
    char *MSFSServer;     // IP address of the PSX boost server
    char *PSXBoostServer; // IP address of the MSFS server
    int PSXPort;          // Main PSX port
    int PSXBoostPort;     // PSX boot server port

    int TCAS_INJECT;        // 1 if TCAS is injected to PSX, 0 otherwise
    int ELEV_INJECT;        // 1 if MSFS elevation is injected into PSX. 0 otherwise
    int INHIB_CRASH_DETECT; // 1 if no crash detection in PSX when loading new situ. 0 otherwise
    int ONLINE;             // 1 if PSXMSFS is used on online on VATSIM,IVAO etc, 0 otherwise
    int LOG_VERBOSITY;      // verbosity of the logs: 1 very verbose and 4 minimum verbosity
    int SLAVE;              // 0 if PSX is slave, 1 if MSFS is slave
} FLAGS;

struct INTERNALPSXflags {
    int oldcrz;
    int updateNewSitu;
    int Qi198Sentground;
    int Qi198SentFlight;
};

/*---------------------------------
 * Global variables
 *--------------------------------*/
extern HANDLE mutex, mutexsitu;
extern CONDITION_VARIABLE condNewSitu;
extern HANDLE hSimConnect;

extern int quit;

extern struct INTERNALPSXflags intflags;

extern SOCKET sPSX;      // main PSX socket id
extern SOCKET sPSXBOOST; // PSX boost socket id
extern FLAGS PSXflags;

extern DWORD TimeStart; // Timestamp when the simulation is started.

/*--------------------------------------------------------
 * Functions to be exported in the DLL
 *------------------------------------------------------*/

extern "C" __declspec(dllexport) int initialize(const char *MSFSIP, const char *PSXIP, int PSXPort, const char *BoostIP, int BoostPort);
extern "C" __declspec(dllexport) FLAGS *connectPSXMSFS(void);
extern "C" __declspec(dllexport) DWORD WINAPI main_launch(void);
extern "C" __declspec(dllexport) int cleanup(void);
#endif
