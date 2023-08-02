/* File containing all UTIL functions such as coordinates calculations, time
 * related functions etc.
 */

#include <cmath>
#include <math.h>
#include <stdint.h>
#include <time.h>
#include "util.h"
#include "PSXMSFSLIB.h"
#include "SimConnect.h"
#include "log.h"

#ifdef __MINGW__
#include <getopt.h>
#endif

#define IP_LENGTH 128 // maximum lenght of IP address

monotime TimeStart;
FILE *fdebug;

double getTime()
{
    LARGE_INTEGER freq, val;
    QueryPerformanceFrequency(&freq);
    QueryPerformanceCounter(&val);
    return (double)val.QuadPart / (double)freq.QuadPart;
}

int sendQPSX(const char *s)
{

    char *dem = (char *)malloc((strlen(s) + 1) * sizeof(char));
    if (dem == NULL) {
        printDebug(LL_ERROR, "Could not create PSX variable. I have to exit now.");
        exit(EXIT_FAILURE);
    }

    strncpy(dem, s, strlen(s));
    dem[strlen(s)] = 10;

    int nbsend = send(PSXflags.sPSX, dem, (int)(strlen(s) + 1), 0);

    if (nbsend == 0) {
        printDebug(LL_VERBOSE, "Error sending variable %s to PSX\n", s);
    }

    free(dem);
    return nbsend;
}
monotime getMonotonicTime(void)
{
    // struct timespec ts;
    // clock_gettime(CLOCK_MONOTONIC, &ts);
    // return ((uint64_t)ts.tv_sec) * 1000000 + ts.tv_nsec / 1000;
    return (monotime)(getTime());
}

void CalcCoord(double heading, double lato, double longo, double *latr, double *longr)
{

    double bearing, dist;

    bearing = heading + M_PI;
    dist = 92.5;

    *latr = asin(sin(lato) * cos(dist * FTM / EARTH_RAD) +
                 cos(lato) * sin(dist * FTM / EARTH_RAD) * cos(bearing));
    *longr = longo + atan2(sin(bearing) * sin(dist * FTM / EARTH_RAD) * cos(lato),
                           cos(dist * FTM / EARTH_RAD) - sin(lato) * sin(*latr));
}

double dist(double lat1, double lat2, double long1, double long2)
{
    return 2 * EARTH_RAD *
           (sqrt(pow(sin((lat2 - lat1) / 2), 2) +
                 cos(lat1) * cos(lat2) * pow(sin((long2 - long1) / 2), 2)));
}

double pressure_altitude(double T0, double P0, double H)
{
    return P0 * pow((T0 + 273.15) / (T0 + 273.15 + LMB * H * FTM), ALPHA);
}
double altitude_pressure(double T0, double P)
{
    return (T0 + 273.15) / LMB * (pow(1013.25 / P, 1 / ALPHA) - 1) / FTM;
}

double getISAdev(double T, double H)
{
    return T - (15 + H * LMB * FTM);
}

void usage()
{

    printf("usage: [-N] [-E] [-h] [-d] [-v] [-s] [-t][-m IP [-p port]] [-b IP [-q port]]\n");
    printf("\t -h, --help");
    printf("\t Prints this help\n");
    printf("\t -d");
    printf("\t debug. Prints out debug info on console and in file "
           "DEBUG.TXT. Warning: can be very verbose. Adjust verbosity level in the ini file\n");
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
    printf("\t Disables pressure altitude injection (usefull for online networks like VATSIM or "
           "IVAO\n");

    exit(EXIT_SUCCESS);
}
int write_ini_file(FLAGS *ini)
{
    FILE *f;

    f = fopen("PSXMSFS.ini", "w");
    if (!f) {
        printDebug(LL_ERROR, "Cannot create PSXMSFS.ini file. Something is seriously wrong! No choice but to exit.");
        quit = 1;
        return 1;
    }

    /*PSX server addresses and port*/
    fprintf(f, "PSXMainServer=%s\n", ini->server.PSXMainServer);
    fprintf(f, "PSXBoostServer=%s\n", ini->server.PSXBoostServer);
    fprintf(f, "PSXPort=%d\n", ini->server.PSXPort);
    fprintf(f, "PSXBoostPort=%d\n", ini->server.PSXBoostPort);

    /*MSFS address*/
    fprintf(f, "MSFSServer=%s\n", ini->server.MSFSServer);

    /* Switches */
    fprintf(f, "LOG_VERBOSITY=%d\n", ini->flags.LOG_VERBOSITY);
    fprintf(f, "TCAS_INJECT=%d\n", ini->flags.TCAS_INJECT);
    fprintf(f, "SLAVE=%d\n", ini->flags.SLAVE);
    fprintf(f, "ELEV_INJECT=%d\n", ini->flags.ELEV_INJECT);
    fprintf(f, "INHIB_CRASH_DETECT=%d\n", ini->flags.INHIB_CRASH_DETECT);
    fprintf(f, "ONLINE=%d\n", ini->flags.ONLINE);

    fclose(f);
    return 0;
}

char *scan_ini(FILE *file, const char *key)
{

    char name[64];
    char val[64];
    rewind(file);
    while (fscanf(file, "%63[^=]=%63[^\n]%*c", name, val) == 2) {
        if (0 == strcmp(name, key)) {
            return _strdup(val);
        }
    }
    return NULL;
}

void resetInternalFlags(void)
{
    /* same for internal flags */
    intflags.oldcrz = 0;
    intflags.updateNewSitu = 1;
}

FLAGS *create_flags_struct()
{

    FLAGS *result = (FLAGS *)malloc(sizeof(FLAGS));

    if (result == NULL) {
        quit = 1;
        return NULL;
    }

    result->server.PSXMainServer = (char *)malloc(IP_LENGTH);
    result->server.PSXBoostServer = (char *)malloc(IP_LENGTH);
    result->server.MSFSServer = (char *)malloc(IP_LENGTH);

    if ((result->server.PSXMainServer == NULL) || (result->server.PSXBoostServer == NULL) || (result->server.MSFSServer == NULL)) {
        quit = 1;
        return NULL;
    }

    return result;
}

void init_servers(FLAGS *ini, const char *MSFSServerIP, const char *PSXMainIP, int PSXMainPort, const char *PSXBoostIP, int PSXBoostPort)
{
    /* Sensible default values for Main PSX server*/
    if (PSXMainIP == NULL || strlen(PSXMainIP) > 15) {

        strcpy(ini->server.PSXMainServer, "127.0.0.1");
        ini->server.PSXPort = 10747;
    } else {
        strcpy(ini->server.PSXMainServer, PSXMainIP);
        ini->server.PSXPort = PSXMainPort;
    }

    /* Sensible default values for PSX Boost server*/
    if (PSXBoostIP == NULL || strlen(PSXBoostIP) > 15) {

        strcpy(ini->server.PSXBoostServer, ini->server.PSXMainServer);
        ini->server.PSXBoostPort = 10749;
    } else {
        strcpy(ini->server.PSXBoostServer, PSXBoostIP);
        ini->server.PSXBoostPort = PSXBoostPort;
    }

    /* Sensible default values for MSFS server*/
    if (MSFSServerIP == NULL || strlen(MSFSServerIP) > 15) {
        strcpy(ini->server.MSFSServer, "127.0.0.1");
    } else {
        strcpy(ini->server.MSFSServer, MSFSServerIP);
    }
}

int init_flags(FLAGS *ini)
{

    FILE *fini;
    char *value;
    char *stop;

    /*
     * Default values for the flags
     */
    ini->flags.SLAVE = 0;
    ini->flags.LOG_VERBOSITY = LL_INFO;
    ini->flags.TCAS_INJECT = 1;
    ini->flags.ELEV_INJECT = 1;
    ini->flags.INHIB_CRASH_DETECT = 0;
    ini->flags.ONLINE = 0;

    fini = fopen("PSXMSFS.ini", "r");
    if (!fini) {
        write_ini_file(ini);
        quit = 1;
        return 2;
    } else {
        strcpy(ini->server.PSXMainServer, scan_ini(fini, "PSXMainServer"));
        strcpy(ini->server.PSXBoostServer, scan_ini(fini, "PSXBoostServer"));
        strcpy(ini->server.MSFSServer, scan_ini(fini, "MSFSServer"));

        value = scan_ini(fini, "SLAVE");
        ini->flags.SLAVE = strtol(value, &stop, 10);
        value = scan_ini(fini, "TCAS_INJECT");
        ini->flags.TCAS_INJECT = strtol(value, &stop, 10);
        value = scan_ini(fini, "LOG_VERBOSITY");
        ini->flags.LOG_VERBOSITY = (int)strtol(value, &stop, 10);
        value = scan_ini(fini, "ELEV_INJECT");
        ini->flags.ELEV_INJECT = strtol(value, &stop, 10);
        value = scan_ini(fini, "INHIB_CRASH_DETECT");
        ini->flags.INHIB_CRASH_DETECT = strtol(value, &stop, 10);
        value = scan_ini(fini, "ONLINE");
        ini->flags.ONLINE = strtol(value, &stop, 10);
        free(value);
        fclose(fini);
    }

    return 0;
}

int init_param(const char *MSFSServerIP, const char *PSXMainIP, int PSXMainPort, const char *PSXBoostIP, int PSXBoostPort)
{

    int flags_ok = 0;
    FLAGS *ini = create_flags_struct();

    if (ini == NULL) {
        printDebug(LL_ERROR, "Could not initialize PSX, PSXBoost or MSFS server values.Exiting now...");
        quit = 1;
        return 1;
    }

    /*
     * Initialise server addresses from user input parameters
     * or default values
     */
    init_servers(ini, MSFSServerIP, PSXMainIP, PSXMainPort, PSXBoostIP, PSXBoostPort);

    /*
     * Flags are directly read from the ini file
     * or defaulted to what is in the ini structure
     */

    flags_ok = init_flags(ini);

    PSXflags = *ini;

    free(ini);
    return flags_ok;
}

void remove_debug()
{
    remove("DEBUG.TXT");
}

void parse_arguments(int argc, char **argv)
{

#ifdef __MINGW__
    int c;
    while (1) {
        static struct option long_options[] = {/* These options set a flag. */
                                               {"debug", no_argument, NULL, 'd'},
                                               /* These options don’t set a flag.
                                              We distinguish them by their indices. */
                                               {"boost", required_argument, NULL, 'b'},
                                               {"help", no_argument, NULL, 'h'},
                                               {"main", required_argument, NULL, 'm'},
                                               {"boost-port", required_argument, NULL, 'c'},
                                               {"main-port", required_argument, NULL, 'p'},
                                               {"slave", required_argument, NULL, 's'},
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
            PSXflags.server.PSXBoostServer = optarg;
            break;
        case 'E':
            PSXflags.flags.ELEV_INJECT = 0;
            break;
        case 'N':
            PSXflags.flags.ONLINE = 0;
            break;
        case 'C':
            PSXflags.flags.INHIB_CRASH_DETECT = 0;
            break;
        case 't':
            PSXflags.flags.TCAS_INJECT = 0;
            break;
        case 'h':
            usage();
            break;
        case 'm':
            PSXflags.server.PSXMainServer = optarg;
            break;
        case 'q':
            PSXflags.server.PSXBoostPort = (int)strtol(optarg, NULL, 10);
            break;
        case 'p':
            PSXflags.server.PSXPort = (int)strtol(optarg, NULL, 10);
            break;
        case 'd':
            PSXflags.flags.LOG_VERBOSITY = LL_ERROR;
            break;
        case 's':
            PSXflags.flags.SLAVE = 1;
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
#else
    printDebug(LL_INFO, "Parsing of arguments not available on native MSVS C++");
#endif
}
