#ifndef __PSXMSFS_H_
#define __PSXMSFS_H_

#include <pthread.h>
#define bzero(b, len) (memset((b), '\0', (len)), (void)0)
#define MAX(x, y) ((x) > (y) ? (x) : (y))

#define MAXLEN 8192
#define PRINT 1
#define MSFSHEIGHT 15.13   //offset when on ground compared to PSX
#define CONSOLE 1   //print debug info on the console
#define MAXBUFF 165536
#define DELIM ";"

/*Global variable used in readin boost socket*/

extern pthread_mutex_t mutex;

extern int sPSX, sPSXBOOST;
extern HANDLE hSimConnect;
extern HRESULT hr;

extern int PSX_acftelev;
extern int MSFS_on_ground;
extern int PSX_on_ground;

extern int DEBUG;
extern int SLAVE;
extern FILE *fdebug;
extern char PSXMainServer[];
extern char MSFSServer[];
extern char PSXBoostServer[];
extern int PSXPort;
extern int PSXBoostPort;
extern int elevupdated;

extern char debugInfo[256];


struct Struct_MSFS {
    double ground_altitude; // ground altitude above MSL
    double alt_above_ground; // altitude of MSFS plane above ground
    double alt_above_ground_minus_CG; // altitude of MSFS wheels above ground (not settable in MSFS)
    double latitude;
    double longitude;
    double pitch;
    double bank;
    double heading_true;
    double VS;
    double TAS;
    double altitude; //plane altitude above MSL
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
    
    double tas;
    double ias;
    
    double GearDown;
    double FlapsPosition;
    double Speedbrake;
    double rudder;
    double elevator;
    double ailerons;
    
    //Lights

    double LandLeftOutboard; // L Inboard
    double LandLeftInboard; // L Inboard
    double LandRightInboard; // R Inboard
    double LandRightOutboard; // R Outboard
    double LeftRwyTurnoff; // L Runway Turnoff light
    double RightRwyTurnoff; // R Runway Turnoff light
    double LightTaxi; // Taxi light
    double LightNav; // Nav light
    double Strobe; // Strobe light
    double BeaconLwr; // Lower Beacon light
    double Beacon; // Both Beacon light
    double LightWing; // Wing light
    double LightLogo; // Wing light

};

struct PSXBOOST {
    // Updated by Boost server
    double flightDeckAlt;
    double latitude;
    double longitude;
    double heading_true;
    double pitch;
    double bank;
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
    DATA_MSFS,
    MSFS_CLIENT_DATA,
    MSFS_FREEZE,
    TCAS_TRAFFIC_DATA  //This is the DATA to be returned for the aircraft in the vicinity
};

enum DATA_REQUEST_ID {
    DATA_REQUEST,
    DATA_REQUEST_FREEZE,
    DATA_REQUEST_TCAS,
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
struct PSXTIME{
    int year;
    int day;
    int hour;
    int minute;
};

/* 
 * Structure storing the Freeze state of MSFS
 */ 

struct MSFSFREEZE{
    double altfreeze;
    double attfreeze;
    double latlongfreeze;
};

/*
 * Structure containing all PSX instruments we
 * want to update in MSFS
 */

struct PSXINST{
    //COMMS & XPDR
    int XPDR;
    int IDENT;
    int COM1;
    int COM2;

    //Altimeter
    double altimeter;
    int STD ;

    //Speed
    double IAS;
    double GS;
    double TAS;

    //local QNH on the weather zone
    int weather_zone;
    double QNH[7];

    //Acft elevation
    //
    double acftelev;

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
extern int light[14]; // In that order: lights Outboard landing L, outboard landing R, inboard landing L, inboard landing
                   //
                   // R, Rwy turnoff L, Rwy turnoff R, taxi, beacon upper, beacon lower, nav L, nav R, strobe, wing,
                   // logo

extern AcftMSFS APos;
extern struct PSXINST PSXDATA;
extern struct PSXBOOST PSXBoost;
extern struct TATL PSXTATL;


typedef struct {
    double pitch;
    double bank;
    double heading_true;
    float altitude;
    double latitude, longitude;

    double TAS;
    double IAS;
    int onGround;          // 1 if PSX is on groud, 0 otherwise
    double GearDown ; // Gear down =1, gear up =0;
    int GearLever  ;   // 1=up, 2=off, 3=down
    int FlapLever ;   // 0 (up) to 6 (30)
    int SpdBrkLever ; // 0 (up) to 800 max deployed
    //Moving surfaces
    double elevator, aileron, rudder;
    double parkbreak;
    double steering;


} Target;


// Function definitions
int check_param(const char *);
int init_socket(void);
void init_variables(void);
int close_PSX_socket(int socket);
int open_connections();
int umain(Target *T);
int umainBoost(Target *T);
void SetUTCTime(PSXTIME *P);
void SetCOMM(void);
void SetBARO(void);
void SetXPDR(void);
double SetAltitude(int onGround); 
int sendQPSX(const char *s);
int init_connect_MSFS(HANDLE *p);
#endif
