#ifndef __PSXMSFS_H_
#define __PSXMSFS_H_

#define bzero(b, len) (memset((b), '\0', (len)), (void)0)
#define MAX(x, y) ((x) > (y) ? (x) : (y))

#define MAXLEN 8192
#define PRINT 1
#define NM 1852     //meters in a nm
#define EARTH_RAD   6371008  //earth radius in meters

extern int sPSX, sPSXBOOST;
extern HANDLE hSimConnect;
extern HRESULT hr;

extern int updateLights;
extern int validtime ; // has one light been toggled?

extern int DEBUG;
extern int SLAVE;
extern FILE *fdebug;
extern char PSXMainServer[];
extern char PSXBoostServer[];
extern int PSXPort;
extern int PSXBoostPort;

struct Struct_MSFS {
    double ground_altitude; // ground altitude
    double alt_above_ground; // altitude of MSFS plane above ground
    double alt_above_ground_minus_CG; // altitude of MSFS wheels above ground (not settable in MSFS)
    double latitude;
    double longitude;
    double pitch;
    double bank;
    double heading;
    double VS;
    double TAS;
}; 




/* Definition of the structure used to update MSFS
 * It is VERY important that the order this structure elements are defined is the
 * same order as when mapping the variables in PSXMSFS.cpp
 */
struct AcftPosition {
    double altitude;
    double latitude;
    double longitude;
    double heading;
    double pitch;
    double bank;
    double tas;
    double ias;
    double vertical_speed;
    double GearDown;
    double FlapsPosition;
    double Speedbrake;
    //Lights
    double LandLeftOutboard; // L Outboard
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
    //moving surfaces
    double rudder;
    double elevator;
    double ailerons;

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
    EVENT_FREEZE_ATT,
    EVENT_FREEZE_LAT_LONG,
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
    MSFS_CLIENT_DATA,
    DATA_PSX_TO_MSFS,
    DATA_TCAS_TRAFFIC,  //This is the DATA to be returned for the aircraft in the vicinity
};

enum DATA_REQUEST_ID {
    DATA_REQUEST,
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


typedef struct {
    double pitch;
    double bank;
    double heading;
    float altitude;
    double latitude, longitude;

    double TAS;
    double IAS;
    double VerticalSpeed;
    int onGround;          // 1 if PSX is on groud, 0 otherwise
    double GearDown = 0.0; // Gear down =1, gear up =0;
    int GearLever = 3.0;   // 1=up, 2=off, 3=down
    int FlapLever = 0.0;   // 0 (up) to 6 (30)
    int SpdBrkLever = 0.0; // 0 (up) to 800 max deployed
    int light[14]; // In that order: lights Outboard landing L, outboard landing R, inboard landing L, inboard landing
                   // R, Rwy turnoff L, Rwy turnoff R, taxi, beacon upper, beacon lower, nav L, nav R, strobe, wing,
                   // logo
    //Time Z
    int year,month,day,hour,minute;
    //Moving surfaces
    double elevator, aileron, rudder;
    double parkbreak;
    double steering;

    //COMMS & XPDR
    int XPDR=0;
    int IDENT;
    int COM1;
    int COM2;

    //Altimeter
    double altimeter=1013.25;
    int STD = 1;

} Target;

// Function definitions
int check_param(const char *);
void init_socket();
int close_PSX_socket(int socket);
void Decode(int, char, char *, Target *);
void open_connections();
void state(Target *T, FILE *fdebug, int console); // prints PSX information 
void stateMSFS(struct AcftPosition *APos, FILE *fdebug, int console); // prints MSFS information 
char *convert(double, int);
int umain(Target *T);
int umainBoost(Target *T);
int umainBoost2(Target *T);
void err_n_die(const char *fmt, ...);
void SetMSFSPos(void);
void SetUTCTime(Target *T);
int sendQPSX(const char *s);
void init_connect_MSFS(HANDLE *p);
#endif
