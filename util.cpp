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
        printDebug(LL_ERROR, "Could not create PSX variable: PSX will not be updated.");
        return -1;
    }

    strncpy(dem, s, strlen(s));
    dem[strlen(s)] = 10;

    int nbsend = send(sPSX, dem, (int)(strlen(s) + 1), 0);

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

int write_ini_file(FLAGS *ini)
{
    FILE *f;

    f = fopen("PSXMSFS.ini", "w");
    if (!f) {
        printDebug(LL_ERROR, "Cannot create PSXMSFS.ini file. Exiting now.");
        quit = 1;
        return 1;
    }

    /*PSX server addresses and port*/
    fprintf(f, "PSXMainServer=%s\n", ini->PSXMainServer);
    fprintf(f, "PSXBoostServer=%s\n", ini->PSXBoostServer);
    fprintf(f, "PSXPort=%d\n", ini->PSXPort);
    fprintf(f, "PSXBoostPort=%d\n", ini->PSXBoostPort);

    /*MSFS address*/
    fprintf(f, "MSFSServer=%s\n", ini->MSFSServer);

    /* Switches */
    fprintf(f, "LOG_VERBOSITY=%d\n", ini->LOG_VERBOSITY);
    fprintf(f, "TCAS_INJECT=%d\n", ini->TCAS_INJECT);
    fprintf(f, "SLAVE=%d\n", ini->SLAVE);
    fprintf(f, "ELEV_INJECT=%d\n", ini->ELEV_INJECT);
    fprintf(f, "INHIB_CRASH_DETECT=%d\n", ini->INHIB_CRASH_DETECT);
    fprintf(f, "ONLINE=%d\n", ini->ONLINE);

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

    result->PSXMainServer = (char *)malloc(IP_LENGTH);
    result->PSXBoostServer = (char *)malloc(IP_LENGTH);
    result->MSFSServer = (char *)malloc(IP_LENGTH);

    if ((result->PSXMainServer == NULL) || (result->PSXBoostServer == NULL) || (result->MSFSServer == NULL)) {
        quit = 1;
        return NULL;
    }

    return result;
}

void init_servers(FLAGS *ini, const char *MSFSServerIP, const char *PSXMainIP, int PSXMainPort, const char *PSXBoostIP, int PSXBoostPort)
{
    /* Sensible default values for Main PSX server*/
    if (PSXMainIP == NULL || strlen(PSXMainIP) > 15) {

        strcpy(ini->PSXMainServer, "127.0.0.1");
        ini->PSXPort = 10747;
    } else {
        strcpy(ini->PSXMainServer, PSXMainIP);
        ini->PSXPort = PSXMainPort;
    }

    /* Sensible default values for PSX Boost server*/
    if (PSXBoostIP == NULL || strlen(PSXBoostIP) > 15) {

        strcpy(ini->PSXBoostServer, ini->PSXMainServer);
        ini->PSXBoostPort = 10749;
    } else {
        strcpy(ini->PSXBoostServer, PSXBoostIP);
        ini->PSXBoostPort = PSXBoostPort;
    }

    /* Sensible default values for MSFS server*/
    if (MSFSServerIP == NULL || strlen(MSFSServerIP) > 15) {
        strcpy(ini->MSFSServer, "127.0.0.1");
    } else {
        strcpy(ini->MSFSServer, MSFSServerIP);
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
    ini->SLAVE = 0;
    ini->LOG_VERBOSITY = LL_INFO;
    ini->TCAS_INJECT = 1;
    ini->ELEV_INJECT = 1;
    ini->INHIB_CRASH_DETECT = 0;
    ini->ONLINE = 0;

    fini = fopen("PSXMSFS.ini", "r");
    if (!fini) {
        write_ini_file(ini);
        quit = 1;
        return 2;
    } else {
        strcpy(ini->PSXMainServer, scan_ini(fini, "PSXMainServer"));
        strcpy(ini->PSXBoostServer, scan_ini(fini, "PSXBoostServer"));
        strcpy(ini->MSFSServer, scan_ini(fini, "MSFSServer"));

        value = scan_ini(fini, "SLAVE");
        ini->SLAVE = strtol(value, &stop, 10);
        value = scan_ini(fini, "TCAS_INJECT");
        ini->TCAS_INJECT = strtol(value, &stop, 10);
        value = scan_ini(fini, "LOG_VERBOSITY");
        ini->LOG_VERBOSITY = (int)strtol(value, &stop, 10);
        value = scan_ini(fini, "ELEV_INJECT");
        ini->ELEV_INJECT = strtol(value, &stop, 10);
        value = scan_ini(fini, "INHIB_CRASH_DETECT");
        ini->INHIB_CRASH_DETECT = strtol(value, &stop, 10);
        value = scan_ini(fini, "ONLINE");
        ini->ONLINE = strtol(value, &stop, 10);
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
        printDebug(LL_ERROR, "Failed to initialize various server values.Exiting now...");
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

