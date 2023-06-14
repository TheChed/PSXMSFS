#ifndef __PSXMSFS_H_
#define __PSXMSFS_H_

#define bzero(b, len) (memset((b), '\0', (len)), (void)0)
#define MAX(x, y) ((x) > (y) ? (x) : (y))
#define MAXLEN 8192
#define MSFSHEIGHT 15.13 // offset when on ground compared to PSX
#define MAXBUFF 165536
#define DELIM ";"
#define PSXMSFS_VERSION 2

/*Log levels*/
#define LL_DEBUG 1
#define LL_VERBOSE 2
#define LL_INFO 3
#define LL_ERROR 4



//extern pthread_mutex_t mutex, mutexsitu;
extern HANDLE mutex, mutexsitu;
//extern pthread_cond_t condNewSitu;
extern CONDITION_VARIABLE condNewSitu;

extern int quit;
extern HANDLE hSimConnect;

extern struct PSXMSFSFLAGS flags;
extern struct INTERNALFLAGS intflags;


struct PSXMSFSFLAGS{
  
	char *PSXMainServer; //IP address of the PSX main server
	char *MSFSServer; //IP address of the PSX boost server
  char *PSXBoostServer; // IP address of the MSFS server
  int PSXPort; //Main PSX port
  int PSXBoostPort;

	int TCAS_INJECT; //1 if TCAS is injected to PSX, 0 otherwise
	int ELEV_INJECT; //1 if MSFS elevation is injected into PSX. 0 otherwise
	int INHIB_CRASH_DETECT; //1 if no crash detection in PSX when loading new situ. 0 otherwise
	
	int ONLINE; //1 if PSXMSFS is used on online on VATSIM,IVAO etc, 0 otherwise
	
	int LOG_VERBOSITY; //verbosity of the logs
  
	int SLAVE; //0 if PSX is slave, 1 if MSFS is slave
 
	SOCKET sPSX; //main PSX socket id
	SOCKET sPSXBOOST; //PSX boost socket id
};

struct INTERNALFLAGS {
	int oldcrz;
	int updateNewSitu;
	int Qi198Sentground;
	int Qi198SentFlight;
};


/*Anti-warning macro*/
#define UNUSED(V) ((void) V)

struct Struct_MSFS {
	double ground_altitude;			  // ground altitude above MSL
	double alt_above_ground;		  // altitude of MSFS plane above ground
	double alt_above_ground_minus_CG; // altitude of MSFS wheels above ground (not settable in MSFS)
	double indicated_altitude; 
	double latitude;
	double longitude;
	double pitch;
	double bank;
	double heading_true;
	double VS;
	double TAS;
	double altitude; // plane altitude above MSL
	double mmHg; // ambiant pressure
	double MSL; // ambiant pressure
	double baro; // ambiant pressure
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
int init_connect_MSFS(HANDLE *p);
#endif
