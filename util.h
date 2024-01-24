/* Geographical functions retunring distance between two points
 * and the coordinates given a bearing, distance and initial location
 */

double dist(double lat1, double lat2, double long1, double long2);
void CalcCoord(double bearing, double lato, double longo, double *latr, double *longr);
void remove_debug(void);
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
