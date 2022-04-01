#ifndef __PSXMSFS_H_
#define __PSXMSFS_H_

#define bzero(b, len) (memset((b), '\0', (len)), (void)0)
#define bcopy(b1, b2, len) (memmove((b2), (b1), (len)), (void)0)
#define position 1 // do we want to print the acft posotion
#define LENGTH(X) (sizeof X / sizeof X[0])

#define NB_Q_VAR 3 // number of PSZ Q Variables to read
#define MAXLEN 8192
#define PRINT 1

extern int sPSX, sPSXBOOST;
extern HANDLE hSimConnect;
extern HRESULT hr;

extern int updateLights ; // has one light been toggled?

struct Struct_MSFS {
    double ground; // ground altitude
    // double  altitude;
    // double  latitude;
    // double  longitude;
    // double heading;
    // double trueheading;
    // double pitch;
    // double bank;
    // double plane_alt; //
    // double plane_alt_above_gnd; //ground altitude
    // double plane_alt_above_gnd_minus_cg; //ground altitude
    // double Flaps;
    // double Speedbrake;
    // double GearDown;
}; //

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

};
enum GROUP_ID {
    GROUP0,
    GROUP1,
};

enum INPUT_ID {
    INPUT_PRINT,
    INPUT_QUIT,
};

enum EVENT_ID {
    EVENT_SIM_START,
    EVENT_ONE_SEC,
    EVENT_6_HZ,
    EVENT_FRAME,
    EVENT_PRINT,
    EVENT_FREEZE_ALT,
    EVENT_FREEZE_ATT,
    EVENT_FREEZE_LAT_LONG,
    EVENT_INIT,
    EVENT_QUIT,
};
enum DATA_DEFINE_ID {
    MSFS_CLIENT_DATA,
    DATA_PSX_TO_MSFS,
};

enum DATA_REQUEST_ID {
    DATA_REQUEST,
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
} Target;

// Function definitions
int check_param(const char *);
void init_socket();
int close_PSX_socket(int socket);
void Decode(int, char, char *, Target *);
void open_connections(char **v);
void state(Target *T); // prints aircraft position
char *convert(double, int);
int umain(Target *T);
int umainBoost(Target *T);
int umainBoost2(Target *T);
void err_n_die(const char *fmt, ...);
int SetMSFSPos(Target *T);

int sendQPSX(const char *s);
#endif
