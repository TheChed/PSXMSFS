/* Geographical functions retunring distance between two points
 * and the coordinates given a bearing, distance and initial location
 */

#include "PSXMSFSLIB.h"

double dist(double lat1, double lat2, double long1, double long2);
void CalcCoord(double bearing, double lato, double longo, double *latr, double *longr);

/*
 * function used to send variables to PSX
 */
int sendQPSX(const char *s);

/*
 * used to reset internal flags
 * when a situ is reloaded for example
 */
void resetInternalFlags(void);

unsigned int getSwitch(FLAGS *f);
unsigned int getVerbosityLevel(FLAGS *f);

void setSwitch(FLAGS *f, unsigned int flagvalue);
