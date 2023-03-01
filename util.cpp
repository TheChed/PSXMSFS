/* File containing all UTIL functions such as coordinates calculations, time
 * related functions etc.
 */

#include <cmath>
#include <math.h>
#include <stdint.h>
#include <time.h>
#include <getopt.h>

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

    time_t t = time(NULL);
    struct tm date = *localtime(&t);

    char timestamp[50];
    strftime(timestamp, 50, "%H:%M:%S", &date);
    if (DEBUG) {
        fprintf(fdebug, "%s[+%ld.%.03ds]\t%s", timestamp, (long)elapsedMs(TimeStart) / 1000,
                (int)elapsedMs(TimeStart) % 1000, debugInfo);
        fprintf(fdebug, "\n");
        fflush(fdebug);
    }
    if (console) {
        printf("%s\n", debugInfo);
    }
}

void usage() {

    printf("usage: [-N] [-E] [-h] [-v] [-s] [-t][-m IP [-p port]] [-b IP [-q port]]\n");
    printf("\t -h, --help");
    printf("\t Prints this help\n");
    printf("\t -d");
    printf("\t debug. Prints out debug info on console and in file "
           "DEBUG.TXT. Warning: can be very verbose\n");
    printf("\t -m");
    printf("\t Main server IP. Default is 127.0.0.1\n");
    printf("\t -p");
    printf("\t Main server port. Default is 10747\n");
    printf("\t -b");
    printf("\t Boost server IP. Default is main server IP [127.0.0.1] \n");
    printf("\t -q");
    printf("\t Boost server port. Default is 10749\n");
    printf("\t -t");
    printf("\t Disables TCAS injection from MSFS to PSX\n");
    printf("\t -s");
    printf("\t Starts with PSX enslaved to MSFS\n");
    printf("\t -E");
    printf("\t Disables elevation injection into MSFS\n");
    printf("\t -C");
    printf("\t No crash detection during 10 seconds after loading a new situ\n");
    printf("\t -N");
    printf("\t Disables pressure altitude injection (usefull for online networks like VATSIM or IVAO\n");

    exit(EXIT_SUCCESS);
}
int write_ini_file() {
    FILE *f;

    f = fopen("PSXMSFS.ini", "w");
    if (!f) {
        printf("Cannot create PSXMSFS.ini file. Aborting...\n");
        fclose(f);
        return -1;
    }

    /*PSX server addresses and port*/
    fprintf(f, "PSXMainServer=%s\n", PSXMainServer);
    fprintf(f, "PSXBoostServer=%s\n", PSXBoostServer);
    fprintf(f, "PSXPort=%d\n", PSXPort);
    fprintf(f, "PSXBoostPort=%d\n", PSXBoostPort);

    /*MSFS address*/
    fprintf(f, "MSFSServer=%s\n", MSFSServer);

    /* Switches */
    fprintf(f, "DEBUG=%d\n", DEBUG);
    fprintf(f, "TCAS_INJECT=%d\n", TCAS_INJECT);
    fprintf(f, "SLAVE=%d\n", SLAVE);
    fprintf(f, "ELEV_INJECT=%d\n", ELEV_INJECT);
    fprintf(f, "INHIB_CRASH_DETECT=%d\n", INHIB_CRASH_DETECT);
    fprintf(f, "ONLINE=%d\n", ONLINE);

    fclose(f);
    return 0;
}

char *scan_ini(FILE *file, const char *key) {

    char name[64];
    char val[64];
    rewind(file);
    while (fscanf(file, "%63[^=]=%63[^\n]%*c", name, val) == 2) {
        if (0 == strcmp(name, key)) {
            return strdup(val);
        }
    }
    return NULL;
}

int init_param() {
    FILE *fini;
    char *value;
    char *stop;

    /* Sensible default values*/
    strcpy(PSXMainServer, "127.0.0.1");
    strcpy(PSXBoostServer, "127.0.0.1");
    strcpy(MSFSServer, "127.0.0.1");
    PSXPort = 10747;
    PSXBoostPort = 10749;
    SLAVE = 0;
    DEBUG = 0;
    TCAS_INJECT = 1;
    ELEV_INJECT = 1;
    INHIB_CRASH_DETECT = 0;
    ONLINE = 0;

    fini = fopen("PSXMSFS.ini", "r");
    if (!fini) {
        printf("Cannot open config file.\nTrying to create one with educated "
               "guesses...\n");
        write_ini_file();
    } else {
        strcpy(PSXMainServer, scan_ini(fini, "PSXMainServer"));
        strcpy(PSXBoostServer, scan_ini(fini, "PSXBoostServer"));
        strcpy(MSFSServer, scan_ini(fini, "MSFSServer"));

        value = scan_ini(fini, "SLAVE");
        SLAVE = strtol(value, &stop, 10);
        value = scan_ini(fini, "TCAS_INJECT");
        TCAS_INJECT = strtol(value, &stop, 10);
        value = scan_ini(fini, "DEBUG");
        DEBUG = strtol(value, &stop, 10);
        value = scan_ini(fini, "ELEV_INJECT");
        ELEV_INJECT = strtol(value, &stop, 10);
        value = scan_ini(fini, "INHIB_CRASH_DETECT");
        INHIB_CRASH_DETECT = strtol(value, &stop, 10);
        value = scan_ini(fini, "ONLINE");
        ONLINE = strtol(value, &stop, 10);
        free(value);
    }

    return 1;
}

void parse_arguments(int argc, char **argv) {

    int c;
    while (1) {
        static struct option long_options[] = {/* These options set a flag. */
                                               {"verbose", no_argument, &DEBUG, 1},
                                               /* These options don’t set a flag.
                                                  We distinguish them by their indices. */
                                               {"boost", required_argument, 0, 'b'},
                                               {"help", no_argument, 0, 'h'},
                                               {"main", required_argument, 0, 'm'},
                                               {"boost-port", required_argument, 0, 'c'},
                                               {"main-port", required_argument, 0, 'p'},
                                               {"slave", required_argument, 0, 's'},
                                               {0, 0, 0, 0}};
        /* getopt_long stores the option index here. */
        int option_index = 0;

        c = getopt_long(argc, argv, "CEthvsm:b:c:p:f:", long_options, &option_index);

        /* Detect the end of the options. */
        if (c == -1)
            break;

        switch (c) {
        case 0:
            /* If this option set a flag, do nothing else now. */
            if (long_options[option_index].flag != 0)
                break;
            printf("option %s", long_options[option_index].name);
            if (optarg)
                printf(" with arg %s", optarg);
            printf("\n");
            break;

        case 'b':
            strcpy(PSXBoostServer, optarg);
            break;
        case 'E':
            ELEV_INJECT = 0;
            break;
        case 'N':
            ONLINE = 0;
            break;
        case 'C':
            INHIB_CRASH_DETECT = 0;
            break;
        case 't':
            TCAS_INJECT = 0;
            break;
        case 'h':
            usage();
            break;
        case 'm':
            strcpy(PSXMainServer, optarg);
            break;
        case 'q':
            PSXBoostPort = (int)strtol(optarg, NULL, 10);
            break;
        case 'p':
            PSXPort = (int)strtol(optarg, NULL, 10);
            break;
        case 'd':
            DEBUG = 1;
            break;
        case 's':
            SLAVE = 1;
            break;

        case '?':
            /* getopt_long already printDebug an error message. */
            usage();
            break;

        default:
            abort();
        }
    }

    /* Print any remaining command line arguments (not options). */
    if (optind < argc) {
        // printf("non-option ARGV-elements: ");
        while (optind < argc)
            optind++;
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
        printf("\t");
        // moving surfaces
        printf("rudder: %.1f\t", A->rudder);
        printf("elevator: %.1f\t", A->elevator);
        printf("ailerons: %.1f\t", A->ailerons);
        printf("\n");
    }
}
