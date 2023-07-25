#ifndef __PSXMSFSLIB_H_
#define __PSXMSFSLIB_H_

#define MAX(x, y) ((x) > (y) ? (x) : (y))
#define MSFSHEIGHT 15.13 // offset when on ground compared to PSX

/*Anti-warning macro*/
#define UNUSED(V) ((void)V)

extern HANDLE mutex, mutexsitu;
extern CONDITION_VARIABLE condNewSitu;

extern int quit;
extern HANDLE hSimConnect;

extern struct FLAGS PSXflags;
extern struct INTERNALFLAGS intflags;

/*
 * structure used at initialization
 * to get user info about PSX and
 * MSFS servers
 */
typedef struct server_options {
    char *PSXMainServer;  // IP address of the PSX main server
    char *MSFSServer;     // IP address of the PSX boost server
    char *PSXBoostServer; // IP address of the MSFS server
    size_t PSXPort;       // Main PSX port
    size_t PSXBoostPort;  // PSX boot server port

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

struct INTERNALFLAGS {
    int oldcrz;
    int updateNewSitu;
    int Qi198Sentground;
    int Qi198SentFlight;
};

struct Struct_MSFS {
    double ground_altitude;           // ground altitude above MSL
    double alt_above_ground;          // altitude of MSFS plane above ground
    double alt_above_ground_minus_CG; // altitude of MSFS wheels above ground (not settable in MSFS)
    double indicated_altitude;
    double latitude;
    double longitude;
    double pitch;
    double bank;
    double heading_true;
    double VS;
    double TAS;
    double altitude;    // plane altitude above MSL
    double mmHg;        // ambiant pressure
    double MSL;         // ambiant pressure
    double baro;        // ambiant pressure
    double temperature; // ambiant temperature
};

/* Definition of the structure used to update MSFS
 * It is VERY important that the order this structure elements are defined is the
 * same order as when mapping the variables in PSXMSFS.cpp
 */
struct AcftMSFS {
    // Updated by Boost server
    double altitude;
    double latitude;
    double longitude;
    double heading_true;
    double pitch;
    double bank;
};

struct AcftLight {
    double LandLeftOutboard;  // L Inboard
    double LandLeftInboard;   // L Inboard
    double LandRightInboard;  // R Inboard
    double LandRightOutboard; // R Outboard
    double LeftRwyTurnoff;    // L Runway Turnoff light
    double RightRwyTurnoff;   // R Runway Turnoff light
    double LightTaxi;         // Taxi light
    double LightNav;          // Nav light
    double Strobe;            // Strobe light
    double BeaconLwr;         // Lower Beacon light
    double Beacon;            // Both Beacon light
    double LightWing;         // Wing light
    double LightLogo;         // Wing light
};

typedef struct {

    int altitude;
    double latitude;
    double longitude;
    int heading;
    double distance;
} TCAS;

/*
 * Structure of AI traffic present in MSFS
 */

struct AI_TCAS {
    double altitude;
    double latitude;
    double longitude;
    double heading;
};

/*
 * Storage for Transation Altitude
 * and tranition level
 */

struct TATL {
    int TA;
    int TL;

    // Flight phase as per Qs392
    // 0 : climb
    // 1: cruise
    // 2: descent

    int phase;
};

// Function definitions
DWORD init_param(server_options *ini, flags *flags);
int check_param(const char *);
int init_socket(void);
void init_variables(void);
int close_PSX_socket(SOCKET socket);
int open_connections();
int umain(void);
int umainBoost(void);
double SetAltitude(int onGround);
int init_connect_MSFS(HANDLE *p);

extern "C" __declspec(dllexport) DWORD initialize(server_options *server, flags *flags);
extern "C" __declspec(dllexport) server_options *connectPSXMSFS(void);
extern "C" __declspec(dllexport) DWORD WINAPI main_launch(void);
extern "C" __declspec(dllexport) DWORD cleanup(void);
#endif
