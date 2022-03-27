#ifndef __PSXMSFS_H_
#define __PSXMSFS_H_

#define bzero(b, len) (memset((b), '\0', (len)), (void)0)
#define bcopy(b1, b2, len) (memmove((b2), (b1), (len)), (void)0)
#define position 1 // do we want to print the acft posotion
#define LENGTH(X) (sizeof X / sizeof X[0])

#define NB_Q_VAR 3 // number of PSZ Q Variables to read
#define MAXLEN 8192
#define PRINT 1

typedef struct {
    char type;     // i, s or h
    int index;     // variable index
    char name[30]; // name of variable
    char *Mode;    // Variable mode ECON
    int min, max;  // maximum and minimum values for the Variable
} QPSX;

//extern int sockfdPSX, sockfdFS;
extern SOCKET sPSX, sPSXBOOST;


extern HANDLE hSimConnect ;
extern HRESULT hr;

struct Struct_MSFS
{
    double  altitude;
    double  latitude;
    double  longitude;
    double heading;
    double trueheading;
    double pitch;
    double bank;
    double ground; //ground altitude
    double plane_alt; //
    double plane_alt_above_gnd; //ground altitude
    double plane_alt_above_gnd_minus_cg; //ground altitude
    double Flaps;
}; 


struct AcftPosition
{
    double  altitude;
    double  latitude;
    double  longitude;
    double heading;
    double pitch;
    double bank;
    double tas;
    double vertical_speed;
    int GearDown;
    //int FlapsPosition;
}; 
enum GROUP_ID {
    GROUP0,
    GROUP1,
};

enum INPUT_ID {
    INPUT_INIT,
};

enum EVENT_ID {
    EVENT_SIM_START,
    EVENT_ONE_SEC,
    EVENT_6_HZ,
    EVENT_FREEZE_ALT,
    EVENT_FREEZE_ATT,
    EVENT_FREEZE_LAT_LONG,
    EVENT_QUIT,
    EVENT_INIT,
    EVENT_FRAME,
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
    double VerticalSpeed;
    int onGround;   //1 if PSX is on groud, 0 otherwise
    int GearDown=1; //Gear down =1, gear up =0;
} Target;



// Function definitions
int init_connect_PSX(const char *, int);
int init_connect_PSX_Boost(const char *, int);
int init_connect_MSFS(HANDLE *);
int check_param(const char *);
void Qformat(QPSX *, const char *);
void Decode(int, char, char *, Target *);
void state(Target *T); // prints aircraft position
char *convert(double, int);
int umain(Target *T);
int umainBoost(Target *T);
int umainBoost2(Target *T);
void init_Q_variables(int, QPSX**);
      int SetMSFSPos(Target *T);

#endif
