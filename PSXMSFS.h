#ifndef __PSXMSFS_H_
#define __PSXMSFS_H_

#define bzero(b, len) (memset((b), '\0', (len)), (void)0)
#define MAX(x, y) ((x) > (y) ? (x) : (y))
#include <pthread.h>
#define MAXLEN 8192
#define PRINT 1
#define MSFSHEIGHT 15.13 // offset when on ground compared to PSX
#define MAXBUFF 165536
#define DELIM ";"
#define CONSOLE 1

/*Global variable used in readin boost socket*/

extern pthread_mutex_t mutex;
extern int quit;
extern int sPSX, sPSXBOOST;
extern HANDLE hSimConnect;

extern int SLAVE;
extern char PSXMainServer[];
extern char MSFSServer[];
extern char PSXBoostServer[];
extern int PSXPort;
extern int PSXBoostPort;
extern int elevupdated;

extern int TCAS_INJECT;
extern int ELEV_INJECT;
extern int INHIB_CRASH_DETECT;
extern int ONLINE;

extern char debugInfo[256];

/*Log levels*/
#define LL_DEBUG 0
#define LL_VERBOSE 1
#define LL_INFO 2

/*Anti-warning macro*/
#define UNUSED(V) ((void) V)

struct Struct_MSFS {
	double ground_altitude;			  // ground altitude above MSL
	double alt_above_ground;		  // altitude of MSFS plane above ground
	double alt_above_ground_minus_CG; // altitude of MSFS wheels above ground (not settable in MSFS)
	double latitude;
	double longitude;
	double pitch;
	double bank;
	double heading_true;
	double VS;
	double TAS;
	double altitude; // plane altitude above MSL
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
	// Lights

	double LandLeftOutboard;  // L Inboard
	double LandLeftInboard;	  // L Inboard
	double LandRightInboard;  // R Inboard
	double LandRightOutboard; // R Outboard
	double LeftRwyTurnoff;	  // L Runway Turnoff light
	double RightRwyTurnoff;	  // R Runway Turnoff light
	double LightTaxi;		  // Taxi light
	double LightNav;		  // Nav light
	double Strobe;			  // Strobe light
	double BeaconLwr;		  // Lower Beacon light
	double Beacon;			  // Both Beacon light
	double LightWing;		  // Wing light
	double LightLogo;		  // Wing light
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
 * Structure to store the date+time from PSX
 */
struct PSXTIME {
	int year;
	int day;
	int hour;
	int minute;
};

struct TATL {
	// TA & TL
	int TA;
	int TL;

	// Flight phase as per Qs392
	// 0 : climb
	// 1: cruise
	// 2: descent

	int phase;
};
enum GROUP_ID {
	GROUP0,
	GROUP1,
};

enum INPUT_ID {
	INPUT_P_PRESS,
	INPUT_QUIT,
};

enum EVENT_ID {
	EVENT_SIM_START,
	EVENT_ONE_SEC,
	EVENT_6_HZ,
	EVENT_4_SEC,
	EVENT_FRAME,
	EVENT_P_PRESS,
	EVENT_FREEZE_ALT,
	EVENT_FREEZE_ALT_TOGGLE,
	EVENT_FREEZE_ATT,
	EVENT_FREEZE_ATT_TOGGLE,
	EVENT_FREEZE_LAT_LONG,
	EVENT_FREEZE_LAT_LONG_TOGGLE,
	EVENT_INIT,
	EVENT_QUIT,
	EVENT_ZULU_DAY,
	EVENT_ZULU_HOURS,
	EVENT_ZULU_MINUTES,
	EVENT_ZULU_YEAR,
	EVENT_PARKING,
	EVENT_STEERING,
	EVENT_XPDR,
	EVENT_XPDR_IDENT,
	EVENT_COM,
	EVENT_COM_STDBY,
	EVENT_BARO,
	EVENT_BARO_STD,
};

enum DATA_DEFINE_ID {
	BOOST_TO_MSFS,
	MSFS_CLIENT_DATA,
	MSFS_FREEZE,
	TCAS_TRAFFIC_DATA, // This is the DATA to be returned for the aircraft in the vicinity
	DATA_LIGHT,		   // This is the DATA to be sent to MSFS to update the lights
	DATA_MOVING_SURFACES,
	DATA_SPEED,
	BOOST_TO_MSFS_STD_ALT,
	BOOST_TO_MSFS_ALT,
};

enum DATA_REQUEST_ID {
	DATA_REQUEST,
	DATA_REQUEST_FREEZE,
	DATA_REQUEST_TCAS,
};

typedef struct {
	double pitch;
	double bank;
	double heading_true;
	float altitude;
	double latitude, longitude;

	double TAS;
	double IAS;
	int onGround;	 // 1 if PSX is on groud, 0 otherwise
	double GearDown; // Gear down =1, gear up =0;
	int GearLever;	 // 1=up, 2=off, 3=down
	int FlapLever;	 // 0 (up) to 6 (30)
	int SpdBrkLever; // 0 (up) to 800 max deployed
	// Moving surfaces
	double elevator, aileron, rudder;
	double parkbreak;
	double steering;

} Target;

// Function definitions
int init_param(void);
int check_param(const char *);
int init_socket(void);
void init_variables(void);
int close_PSX_socket(int socket);
int open_connections();
int umain(void);
int umainBoost(void);
double SetAltitude(int onGround);
int sendQPSX(const char *s);
int init_connect_MSFS(HANDLE *p);
#endif
