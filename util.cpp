/* File containing all UTIL functions such as coordinates calculations, time related functions
 * etc.
 */

#include <stdint.h>
#include <time.h>
#include <math.h>
#include "util.h"
#include "PSXMSFS.h"

monotime TimeStart;

monotime getMonotonicTime(void) {
    struct timespec ts;
    clock_gettime(CLOCK_MONOTONIC, &ts);
    return ((uint64_t)ts.tv_sec) * 1000000 + ts.tv_nsec / 1000;
}

void CalcCoord(double bearing, double dist, double lato, double longo, double *latr, double *longr) {

    *latr = asin(sin(lato) * cos(dist * FTM / EARTH_RAD) + cos(lato) * sin(dist * FTM / EARTH_RAD) * cos(bearing));
    *longr = longo + atan2(sin(bearing) * sin(dist * FTM / EARTH_RAD) * cos(lato),
                           cos(dist * FTM / EARTH_RAD) - sin(lato) * sin(*latr));
}

double dist(double lat1, double lat2, double long1, double long2) {
    return 2 * EARTH_RAD *
           (sqrt(pow(sin((lat2 - lat1) / 2), 2) + cos(lat1) * cos(lat2) * pow(sin((long2 - long1) / 2), 2)));
}

void err_n_die(const char *fmt, ...) {
    int errno_save;
    va_list ap;

    errno_save = errno;

    va_start(ap, fmt);
    vfprintf(stdout, fmt, ap);
    fprintf(stdout, "\n");
    fflush(stdout);

    if (errno_save != 0) {
        fprintf(stdout, "(errno= %d) : %s\n", errno_save, strerror(errno_save));
        fprintf(stdout, "\n");
        fflush(stdout);
    }
    va_end(ap);
    exit(1);
}

void printDebug(const char *debugInfo, int console) {

    if (DEBUG) {
        fprintf(fdebug, "[%ld.%.03d]\t%s", (long)elapsedMs(TimeStart) / 1000, (int)elapsedMs(TimeStart) % 1000,
                debugInfo);
        fprintf(fdebug, "\n");
        fflush(fdebug);
    }
    if (console) {
        printf("%s\n", debugInfo);
    }
}
