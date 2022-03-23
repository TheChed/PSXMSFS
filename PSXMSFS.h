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

extern int sockfdPSX, sockfdFS;
extern SOCKET sPSX;


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
}; 


struct SimResponse
{
    double  altitude;
    double  latitude;
    double  longitude;
    double heading;
    double pitch;
    double bank;
    double tas;
    double altitude_cg;
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
};
 enum DATA_DEFINE_ID {
     MSFS_CLIENT_DATA,
     DATA_PSX_TO_MSFS,
 };

enum DATA_REQUEST_ID {
     DATA_REQUEST,
 };


typedef struct {
    int pitch; // x 100 000
    int bank;  // x 100 000
    double heading;
    int altitude; // x 1000
    int TAS;      // x1000
    double latitude, longitude;
} Target;



// Function definitions
int init_connect_PSX(const char *, int);
int init_connect_MSFS(HANDLE *);
int check_param(const char *);
void Qformat(QPSX *, const char *);
void Decode(int, char, char *, Target *);
void state(Target *T); // prints aircraft position
char *convert(double, int);
int umain(Target *T);
void init_Q_variables(int, QPSX**);

#endif
