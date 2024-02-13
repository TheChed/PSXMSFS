#ifndef __PSXMSFSLIB_H_
#define __PSXMSFSLIB_H_

#include <cstdint>
#include <windows.h>
#define LIBEXPORT extern "C" __declspec(dllexport)

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
#define M_PI 3.14159265358979323846f
#define NM 1852           // meters in a nm
#define EARTH_RAD 6371008 // earth radius in meters
#define FTM 0.3048        // feet to meters
#define DEG2RAD (M_PI / 180.0)
#define LMB (-0.0065)         // temperature gradient per meters
#define ALPHA -5.255822518257 // used in the atmosphere modelization
#define VSSAMPLE 50           // number of samples used from boost string to calculate the vertical speed
#define DELIM ";"

#define IP_LENGTH 15

/*-------------------------------------
 * Internal switches
 * -----------------------------------*/

#define F_TCAS (1 << 0)
#define F_INJECT (1 << 1)
#define F_ONLINE (1 << 2)
#define F_INHIB (1 << 3)
#define F_SLAVE (1 << 4)


/*-----------------------------------------
 * structure used at initialization
 * to get user info about PSX servers, MSFS
 * and to store various user defined flags
 *----------------------------------------*/
typedef enum {
    LL_DEBUG = 1,
    LL_VERBOSE,
    LL_INFO,
    LL_ERROR
} LOG_LEVELS;


typedef struct flags {
    char PSXMainServer[IP_LENGTH];  // IP address of the PSX main server
    char MSFSServer[IP_LENGTH];     // IP address of the PSX boost server
    char PSXBoostServer[IP_LENGTH]; // IP address of the MSFS server
    int PSXPort;          // Main PSX port
    int PSXBoostPort;     // PSX boot server port

    int LOG_VERBOSITY;      // verbosity of the logs: 1 very verbose and 4 minimum verbosity

   /* -----------------------------------------------
    * Internal switched combination of:
    * F_TCAS, F_INJECT, F_ONLINE, F_INHIB and F_SLAVE
    *-----------------------------------------------*/
    unsigned int switches;
} FLAGS;

typedef struct servers {
    char PSXMainServer[IP_LENGTH];  // IP address of the PSX main server
    char MSFSServer[IP_LENGTH];     // IP address of the PSX boost server
    char PSXBoostServer[IP_LENGTH]; // IP address of the MSFS server
    int PSXPort;          // Main PSX port
    int PSXBoostPort;     // PSX boot server port
} servers;


typedef struct BOOST {
    // Updated by Boost server
    double flightDeckAlt;
    double latitude;
    double longitude;
    double heading_true;
    double pitch;
    double bank;
    int onGround;
} BOOST;

struct INTERNALPSXflags {
    int oldcrz;
    int updateNewSitu;
    int Qi198Sentground;
    int Qi198SentFlight;
    DWORD NewSituTimeLoad;
};

/*---------------------------------
 * Global variables
 *--------------------------------*/
extern HANDLE mutex;
extern HANDLE hSimConnect;

extern int quit;

extern struct INTERNALPSXflags intflags;

extern SOCKET sPSX;      // main PSX socket id
extern SOCKET sPSXBOOST; // PSX boost socket id
extern FLAGS PSXflags;

extern DWORD TimeStart; // Timestamp when the simulation is started.


void printDebug(LOG_LEVELS level, const char *debugInfo, ...);
/*--------------------------------------------------------
 * Functions to be exported in the DLL
 *------------------------------------------------------*/

LIBEXPORT int initialize(FLAGS *flags);
LIBEXPORT int connectPSXMSFS(FLAGS *F);
LIBEXPORT int main_launch(void);
LIBEXPORT int cleanup(void);
LIBEXPORT FLAGS* initFlags(void);
LIBEXPORT int updateFromIni(FLAGS *flags);

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

LIBEXPORT logMessage *getLogBuffer(int nblogs);
LIBEXPORT char *getLogMessage(logMessage *D, int n);
LIBEXPORT uint64_t getLogID(logMessage *D, int n);
LIBEXPORT int getLogLevel(logMessage *D, int n);
LIBEXPORT void freeLogBuffer(logMessage *D);


/*----------------------------------------
* Functions used to manipulate internal flags.
* Setting and reading values
* ---------------------------------------*/ 

LIBEXPORT unsigned int getSwitch(FLAGS *f);
LIBEXPORT void setSwitch(FLAGS *f, unsigned int flagvalue);
LIBEXPORT int getLogVerbosity(FLAGS *f);
LIBEXPORT void setLogVerbosity(FLAGS *f, LOG_LEVELS level);
LIBEXPORT servers getServersInfo(FLAGS *f); 
LIBEXPORT  void setServersInfo(servers *S);
LIBEXPORT BOOST getACFTInfo(void); 
#endif


