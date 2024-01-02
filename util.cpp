/* File containing all UTIL functions such as coordinates calculations, time
 * related functions etc.
 */

#include <cmath>
#include <math.h>
#include <stdint.h>
#include <time.h>
#include "util.h"
#include "PSXMSFSLIB.h"
#include "log.h"

#define IP_LENGTH 128 // maximum lenght of IP address

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

    int nbsend = 0;
    char *dem = (char *)malloc((1 + strlen(s)) * sizeof(char));

    if (dem == NULL) {
        printDebug(LL_ERROR, "Could not create PSX variable: PSX will not be updated.");
        return -1;
    }

    strncpy(dem, s, strlen(s));
    dem[strlen(s)] = 10;

    /*-------------------------------------
     * Send a Q variable to PSX if we are not
     * just reloading a situ.
     * ------------------------------------*/
    if (!intflags.updateNewSitu) {
        printDebug(LL_DEBUG, "Sending '%s' to PSX", s);
        nbsend = send(sPSX, dem, (int)(strlen(s) + 1), 0);
        if (nbsend == 0) {
            printDebug(LL_ERROR, "Error sending variable %s to PSX", s);
        }
    }
    free(dem);
    return nbsend;
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
    return 2 * EARTH_RAD * asin((sqrt(pow(sin((lat2 - lat1) / 2), 2) + cos(lat1) * cos(lat2) * pow(sin((long2 - long1) / 2), 2))));
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
    fprintf(f, "#Self Explanatory IP and port variables\n");
    fprintf(f, "PSXMainServer=%s\n", ini->PSXMainServer);
    fprintf(f, "PSXBoostServer=%s\n", ini->PSXBoostServer);
    fprintf(f, "PSXPort=%d\n", ini->PSXPort);
    fprintf(f, "PSXBoostPort=%d\n", ini->PSXBoostPort);

    /*MSFS address*/
    fprintf(f, "MSFSServer=%s\n", ini->MSFSServer);
    fprintf(f, "\n");

    /* Switches */
    fprintf(f, "#How much debug you want. DEBUG = 1, VERBOSE = 2, INFO = 3, ERROR = 4\n");
    fprintf(f, "LOG_VERBOSITY=%d\n", ini->LOG_VERBOSITY);
    fprintf(f, "\n#Inject MSFS TCAS in PSX\n");
    fprintf(f, "TCAS_INJECT=%d\n", ini->TCAS_INJECT);
    fprintf(f, "\n#If 0 then MSFS slave to PSX. If 1 then PSX slave to MSFS\n");
    fprintf(f, "SLAVE=%d\n", ini->SLAVE);
    fprintf(f, "\n#If 1 then inject PSX elevations to MSFS. If 0 the other way round. Best results with 1\n");
    fprintf(f, "ELEV_INJECT=%d\n", ini->ELEV_INJECT);
    fprintf(f, "\n#If 1 inhibits PSX crash detections for 10 seconds when loading a situ. If 0 crashes are not inhibited\n");
    fprintf(f, "INHIB_CRASH_DETECT=%d\n", ini->INHIB_CRASH_DETECT);
    fprintf(f, "\n#If 1 reports proper FL on networks such as IVAO, VATSIM, etc. If 0 no correction is made\n");
    fprintf(f, "ONLINE=%d\n", ini->ONLINE);

    fclose(f);
    return 0;
}

char *scan_ini(FILE *file, const char *key)
{

    char name[64];
    char val[64];
    char buffer[128];

    rewind(file);
    while (fgets(buffer, sizeof(buffer), file) != NULL) {
        int n = sscanf(buffer, "%63[^=]=%63[^\n]%*c", name, val);
        if (n == 2) {
            if (0 == strcmp(name, key)) {
                return _strdup(val);
            }
        }
    }

    return NULL;
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

        if ((value = scan_ini(fini, "SLAVE")))
            ini->SLAVE = strtol(value, &stop, 10);
        if ((value = scan_ini(fini, "TCAS_INJECT")))
            ini->TCAS_INJECT = strtol(value, &stop, 10);
        if ((value = scan_ini(fini, "ELEV_INJECT")))
            ini->ELEV_INJECT = strtol(value, &stop, 10);
        if ((value = scan_ini(fini, "INHIB_CRASH_DETECT")))
            ini->INHIB_CRASH_DETECT = strtol(value, &stop, 10);
        if ((value = scan_ini(fini, "ONLINE")))
            ini->ONLINE = strtol(value, &stop, 10);
        if ((value = scan_ini(fini, "LOG_VERBOSITY")))
            ini->LOG_VERBOSITY = (int)strtol(value, &stop, 10);
        free(value);
        fclose(fini);
    }

    return 0;
}

int init_param(const char *MSFSServerIP, const char *PSXMainIP, int PSXMainPort, const char *PSXBoostIP, int PSXBoostPort)
{

    int flags_ok = 0;
    FLAGS *flags = create_flags_struct();

    if (flags == NULL) {
        quit = 1;
        return 1;
    }
    PSXflags = *flags;
    /*
     * Initialise server addresses from user input parameters
     * or default values
     */
    init_servers(&PSXflags, MSFSServerIP, PSXMainIP, PSXMainPort, PSXBoostIP, PSXBoostPort);

    /*--------------------------------------------------
     * Flags are directly read from the ini file
     * or defaulted to what is in the flags structure
     *-------------------------------------------------*/

    flags_ok = init_flags(&PSXflags);

    free(flags);

    return flags_ok;
}

void remove_debug()
{
    if (PSXflags.LOG_VERBOSITY > 1)
        remove("DEBUG.TXT");
}
