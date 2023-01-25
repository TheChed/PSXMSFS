/* File containing all UTIL functions such as coordinates calculations, time related functions
 * etc.
 */

#include <cmath>
#include <stdint.h>
#include <time.h>
#include <math.h>
#include "util.h"
#include "PSXMSFS.h"
#include "SimConnect.h"

monotime TimeStart;

monotime getMonotonicTime(void) {
    struct timespec ts;
    clock_gettime(CLOCK_MONOTONIC, &ts);
    return ((uint64_t)ts.tv_sec) * 1000000 + ts.tv_nsec / 1000;
}

void CalcCoord(double heading, double lato, double longo, double *latr, double *longr) {

    double bearing, dist;

    bearing = heading + M_PI;
    dist = 92.5;

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

double pressure_altitude(double mmhg) { return 145366.45 * (1 - pow(mmhg / 100.0 * 33.8638 / 1013.25, 0.190284)); }

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

void state(AcftMSFS *T, FILE *fd, int console) {

    if (console) {
        printf("PSX:\t  ");
        printf("Alt: %.0f\t", T->altitude);
        printf("Lat: %.8f\t", T->latitude);
        printf("Long: %.8f\t", T->longitude);
        printf("Head: %.1f\t", T->heading_true);
        printf("Pitch: %.2f\t", T->pitch);
        printf("Bank: %.2f\t", T->bank);
        printf("TAS: %.1f\t", T->tas);
        printf("IAS: %.1f\t", T->ias);
        printf("\n");
    }
    fprintf(fd, "PSX:\t  ");
    fprintf(fd, "Alt: %.0f\t", T->altitude);
    fprintf(fd, "Lat: %.8f\t", T->latitude);
    fprintf(fd, "Long: %.8f\t", T->longitude);
    fprintf(fd, "Head: %.1f\t", T->heading_true);
    fprintf(fd, "Pitch: %.2f\t", T->pitch);
    fprintf(fd, "Bank: %.2f\t", T->bank);
    fprintf(fd, "TAS: %.1f\t", T->tas);
    fprintf(fd, "IAS: %.1f\t", T->ias);
    fprintf(fd, "\n");
    fflush(fd);
}
void stateMSFS(struct AcftMSFS *A, FILE *fd, int console) {

    // printing to debug file
    fprintf(fd, "MSFS:\t  ");
    // fprintf(fd, "Alt: %.0f\t", A->altitude);
    // fprintf(fd, "Lat: %.3f\t", A->latitude);
    // fprintf(fd, "Long: %.3f\t", A->longitude);
    // fprintf(fd, "Head: %.1f\t", A->heading_true);
    // fprintf(fd, "Pitch: %.2f\t", -A->pitch);
    // fprintf(fd, "Bank: %.2f\t", A->bank);
    // fprintf(fd, "TAS: %.1f\t", A->tas);
    // fprintf(fd, "IAS: %.1f\t", A->ias);
    // fprintf(fd, "VS: %.1f\t", A->vertical_speed);
    fprintf(fd, "GearDown: %.1f\t", A->GearDown);
    fprintf(fd, "FlapsPosition: %.1f\t", A->FlapsPosition);
    fprintf(fd, "Speedbrake: %.1f\t", A->Speedbrake);
    // Lights
    fprintf(fd, "Lights: ");
    for (int i = 0; i < 14; ++i)
        fprintf(fd, "%d", light[i]);
    fprintf(fd, "\t");
    // moving surfaces
    fprintf(fd, "rudder: %.1f\t", A->rudder);
    fprintf(fd, "elevator: %.1f\t", A->elevator);
    fprintf(fd, "ailerons: %.1f\t", A->ailerons);
    fprintf(fd, "\n");
    fflush(fd);
    // And printing to stdout if console is set
    if (console) {
        printf("MSFS:\t  ");
        // printf("Alt: %.0f\t", A->altitude);
        // printf("Lat: %.3f\t", A->latitude);
        // printf("Long: %.3f\t", A->longitude);
        // printf("Head: %.1f\t", A->heading_true);
        // printf("Pitch: %.2f\t", -A->pitch);
        // printf("Bank: %.2f\t", A->bank);
        // printf("TAS: %.1f\t", A->tas);
        // printf("IAS: %.1f\t", A->ias);
        // printf("VS: %.1f\t", A->vertical_speed);
        printf("GearDown: %.1f\t", A->GearDown);
        printf("FlapsPosition: %.1f\t", A->FlapsPosition);
        printf("Speedbrake: %.1f\t", A->Speedbrake);
        // Lights
        printf("Lights: ");
        for (int i = 0; i < 14; ++i)
            printf("%d", light[i]);
        printf("\t");
        // moving surfaces
        printf("rudder: %.1f\t", A->rudder);
        printf("elevator: %.1f\t", A->elevator);
        printf("ailerons: %.1f\t", A->ailerons);
        printf("\n");
    }
}
