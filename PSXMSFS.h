#ifndef __PSXMSFS_H_
#define __PSXMSFS_H_

#define bzero(b, len) (memset((b), '\0', (len)), (void)0)
#define bcopy(b1, b2, len) (memmove((b2), (b1), (len)), (void)0)
#define position 1 // do we want to print the acft posotion

#define LENGTH(X) (sizeof X / sizeof X[0])

#define NB_Q_VAR 3 // number of PSZ Q Variables to read
#define MAXLEN 4096

typedef struct {
    char type;     // i, s or h
    int index;     // variable index
    char name[30]; // name of variable
    char *Mode;    // Variable mode ECON
    int min, max;  // maximum and minimum values for the Variable
} QPSX;

extern int sockfdPSX, sockfdFS;
extern SOCKET sPSX;

extern int boucle;
extern char *Qvariables[NB_Q_VAR];
// functions to read PSX info
//
//

extern QPSX **Q;

extern HANDLE hSimConnect ;
extern HRESULT hr;

struct Struct_MSFS
{
    double  kohlsmann;
    double  altitude;
    double  latitude;
    double  longitude;
    double heading;
}; 


struct SimResponse {
    double altitude;
    double heading;
    double latitude;
    double longitude;
    double pitch;
    double bank;
    double TAS;
    double IAS;
    double VS;
//    double mach;
};

enum GROUP_ID {
    GROUP0,
};

enum INPUT_ID {
    INPUT_QUIT,
    INPUT_INIT,
};

enum EVENT_ID {
    EVENT_SIM_START,
    EVENT_ONE_SEC,
    EVENT_QUIT,
    EVENT_INIT,
};
 enum DATA_DEFINE_ID {
     DATA_PSX_TO_MSFS,
     DEFINITION_INIT,
     DATA_DEFINITION,
 };

enum DATA_REQUEST_ID {
     DATA_REQUEST,
     DATA_REQUEST_2,
     DATA_REQUEST_3
 };


typedef struct {
    int pitch; // x 100 000
    int bank;  // x 100 000
    double heading;
    int altitude; // x 1000
    int TAS;      // x1000
    double latitude, longitude;
} target;


extern target T;

// Function definitions
int init_connect_PSX(const char *, int);
int init_connect_MSFS(HANDLE *);
int check_param(const char *);
void Qformat(QPSX *, const char *);
char *Decode(int, char, char *);
void state(); // prints aircraft position
char *convert(double, int);
int umain(void);
void init_Q_variables(int, QPSX**);

#endif
