#include <stdint.h>
#include <stdio.h>
#include <windows.h>
#include "PSXMSFSLIB.h"



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
FLAGS *create_flags_struct(void);
