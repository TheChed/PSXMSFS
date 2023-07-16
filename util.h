#include <stdint.h>
#include <stdio.h>
#include <windows.h>
#include "PSXMSFSLIB.h"

#define M_PI 3.14159265358979323846
#define NM 1852           // meters in a nm
#define EARTH_RAD 6371008 // earth radius in meters
#define FTM 0.3048        // feet to meters
#define DEG2RAD (M_PI / 180.0)
#define LMB (-0.0065)  // temperature gradient per meters
#define GACCEL 9.80655 // gravitation acceleration at sea level
#define ALPHA -5.255822518257

/*
 *
 *
 * Various functions and variables used for time management
 *
 *
 * */

typedef uint64_t monotime; // long unsigned 64 bit integer used to store time related variables
extern monotime TimeStart; // Timestamp when the simulation is started.

extern monotime getMonotonicTime(void);

static inline void elapsedStart(monotime *start_time)
{
    *start_time = getMonotonicTime();
}

static inline uint64_t elapsedUs(monotime start_time)
{
    return getMonotonicTime() - start_time;
}

static inline uint64_t elapsedMs(monotime start_time)
{
    return elapsedUs(start_time) / 1000;
}

/* Geographical functions retunring distance between two points
 * and the coordinates given a bearing, distance and initial location
 */

extern double dist(double lat1, double lat2, double long1, double long2);
extern void CalcCoord(double bearing, double lato, double longo, double *latr, double *longr);

/* To calculate the altitude difference between local pressure and PSX
 * in order to inject this correction to IVAO/VATSIM who model
 * theirt QNH differently
 * https://en.wikipedia.org/wiki/Pressure_altitude
 */
double pressure_altitude(double T0, double P0, double H);
double altitude_pressure(double T0, double P);

double getISAdev(double T, double H);

void state(AcftMSFS *T, FILE *fdebug, int console);                   // prints PSX information
void stateMSFS(struct AcftPosition *APos, FILE *fdebug, int console); // prints MSFS information

void remove_debug(void);

char *convert(double, int);
void printDebug(int console, const char *debugInfo, ...);

int init_debug(void);

void SetMSFSPos(void);

// void err_n_die(const char *fmt, ...);

void parse_arguments(int argc, char **argv);

/*
 * function used to send variables to PSX
 */
int sendQPSX(const char *s);

/*
 * used to reset internal flags
 * when a situ is reloaded for example
 */
void resetInternalFlags(void);

/* 
 * Function used to create and allocate the flags
 * structre
 */
static FLAGS *create_flags_struct(void);
