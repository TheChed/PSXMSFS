#include <stdint.h>
#include <stdio.h>
#include <windows.h>
#include "PSXMSFSLIB.h"



monotime getMonotonicTime(void);

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

double dist(double lat1, double lat2, double long1, double long2);
void CalcCoord(double bearing, double lato, double longo, double *latr, double *longr);


void remove_debug(void);

void printDebug(int console, const char *debugInfo, ...);

int init_debug(void);

void SetMSFSPos(void);


/*
 * function used to send variables to PSX
 */
int sendQPSX(const char *s);

/*
 * used to reset internal flags
 * when a situ is reloaded for example
 */
void resetInternalFlags(void);

