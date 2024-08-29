/* File containing all UTIL functions such as coordinates calculations, time
 * related functions etc.
 */
#include <cstring>
#include <stdio.h>
#include <cmath>
#include <stdint.h>
#include <time.h>
// #include "handleapi.h"
#include "util.h"
#include "PSXMSFSLIB.h"
#include "SimConnect.h"

LOG_LEVELS VERBOSITY = LL_INFO;

/*----------------------------------------
 * State flags read from PSXMSFS.ini file
 * or input by client
 *---------------------------------------*/
FLAGS PSXflags;

/*--------------------------------------------
 * Handles for mutexes used in
 * various threads
 *-------------------------------------------*/
HANDLE mutex;

static int write_ini_file(FLAGS *flags)
{
    FILE *f;

    f = fopen("PSXMSFS.ini", "w");
    if (!f) {
        printDebug(LL_ERROR, "Cannot create PSXMSFS.ini file. Exiting now.");
        quit = 1;
        return 1;
    }
    printDebug(LL_INFO, "Creating PSXMSFS.ini file with default values.");
    printDebug(LL_INFO, "Trying to connect...");

    /*PSX server addresses and port*/
    fprintf(f, "#Self Explanatory IP and port variables\n");
    fprintf(f, "PSXMainServer=%s\n", flags->PSXMainServer);
    fprintf(f, "PSXBoostServer=%s\n", flags->PSXBoostServer);
    fprintf(f, "PSXPort=%d\n", flags->PSXPort);
    fprintf(f, "PSXBoostPort=%d\n", flags->PSXBoostPort);

    /*MSFS address*/
    fprintf(f, "MSFSServer=%s\n", flags->MSFSServer);
    fprintf(f, "\n");

    /* Switches */
    fprintf(f, "#How much debug you want. DEBUG = 1, VERBOSE = 2, INFO = 3, ERROR = 4\n");
    fprintf(f, "LOG_VERBOSITY=%d\n", flags->LOG_VERBOSITY);
    fprintf(f, "\n#Inject MSFS TCAS in PSX if equal to 1.\n");
    fprintf(f, "TCAS_INJECT=%d\n", flags->switches & F_TCAS);
    fprintf(f, "\n#If 0 then MSFS slave to PSX. If 1 then PSX slave to MSFS\n");
    fprintf(f, "SLAVE=%d\n", (flags->switches & F_SLAVE) >> 4);
    fprintf(f, "\n#If 1 then inject PSX elevations to MSFS. If 0 the other way round. Best results with 1\n");
    fprintf(f, "ELEV_INJECT=%d\n", (flags->switches & F_INJECT) >> 1);
    fprintf(f, "\n#If 1 inhibits PSX crash detections for 10 seconds when loading a situ. If 0 crashes are not inhibited\n");
    fprintf(f, "INHIB_CRASH_DETECT=%d\n", (flags->switches & F_INHIB) >> 3);
    fprintf(f, "\n#If 1 reports proper FL on networks such as IVAO, VATSIM, etc. If 0 no correction is made\n");
    fprintf(f, "ONLINE=%d\n", (flags->switches & F_ONLINE) >> 2);
    fprintf(f, "\n#################################################################################\n");
    fprintf(f, "#You don't want to mess with the following switches. Change at your own risk\n");
    fprintf(f, "#################################################################################\n");
    fprintf(f, "\n#OK, this one should be harmless and self explanatory\n");
    fprintf(f, "DELETELOGFILE=%d\n", flags->deleteLogFile);
    fprintf(f, "\n#MSFS delay. Amount of seconds before we send the elevation to PSX, to account for the fact that MSFS takes\n");
    fprintf(f, "#around 10 seconds to load the current elevation\n");
    fprintf(f, "MSFSDELAY=%d\n", flags->MSFSdelay);

    fclose(f);
    return 0;
}
void cleanup(FLAGS *flags)
{

    quit = 1; // To force threads to close if not yet done
    if (flags->hSimConnect != NULL) {
        SimConnect_Close(flags->hSimConnect);

        printDebug(LL_INFO, "MSFS connection closed.");
    }

    sendQPSX("exit"); // Signal PSX that we are quitting

    /*-----------------------------------------------------*
     * and gracefully try to close main and boost sockets  *
     * ----------------------------------------------------*/
    if (flags->BOOSTsocket != static_cast<SOCKET>(-1)) {
        close_PSX_socket(flags->BOOSTsocket);
        printDebug(LL_INFO, "PSX boost connection closed.");
    }

    if (flags->PSXsocket != static_cast<SOCKET>(-1)) {
        close_PSX_socket(flags->PSXsocket);
        printDebug(LL_INFO, "PSX connection closed.");
    }

    WSACleanup(); // CLose the Win32 sockets

    // bye bye
    printDebug(LL_INFO, "See you next time!\n");

    // Remove LOG file if not in DEBUG mode
    if (flags->deleteLogFile)
        remove("LOG.TXT");
}

int initializePSXMSFS(FLAGS *flags)
{

    if (flags == NULL) {
        printDebug(LL_ERROR, "Cannot initialize values, no flags provided");
        return 1;
    }

    /*---------------------------
     * resetting to 0 in case
     * we quit in a previous call
     * -------------------------*/
    quit = 0;

    /*---------------------------
     * Initialize the timer
     * ------------------------*/
    TimeStart = GetTickCount64();

    /*---------------------------
     * creating a PSXMSFS.ini file
     * if it is non existant, potentially
     * with values passed by client in
     * the flags structure
     * -------------------------*/
    FILE *fini;
    fini = fopen("PSXMSFS.ini", "r");
    if (!fini) {
        write_ini_file(flags);
    } else
        fclose(fini);

    /*----------------------------
     * if we get flags passed as arguments,
     * check if it is valid
      ---------------------------*/

    PSXflags = *flags;

    /*----------------------------------------------------
     * Create a thread mutex so that two threads cannot
     * change simulataneously the position of the aircraft
     *---------------------------------------------------*/
    mutex = CreateMutex(NULL, FALSE, NULL);

    return 0;
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

char *scan_ini(FILE *file, const char *key)
{

    char name[64];
    char val[64];
    char buffer[128];

    rewind(file);
    while (fgets(buffer, sizeof(buffer), file) != NULL) {
        memset(name, 0, 63);
        int n = sscanf(buffer, "%63[^=]=%63[^\n]%*c", name, val);
        if (n == 2) {
            if (0 == strcmp(name, key)) {
                return _strdup(val);
            }
        }
    }

    return NULL;
}

static int updateFromIni(FLAGS *flags)
{
    char *value, *stop;
    unsigned int switches = 0;

    FILE *fini = fopen("PSXMSFS.ini", "r");

    if (!fini)
        return 1;

    strncpy(flags->PSXMainServer, scan_ini(fini, "PSXMainServer"), IP_LENGTH);
    strncpy(flags->PSXBoostServer, scan_ini(fini, "PSXBoostServer"), IP_LENGTH);
    strncpy(flags->MSFSServer, scan_ini(fini, "MSFSServer"), IP_LENGTH);

    if ((value = scan_ini(fini, "SLAVE")))
        switches = switches | (strtol(value, &stop, 10) << 4);
    if ((value = scan_ini(fini, "TCAS_INJECT")))
        switches = switches | (strtol(value, &stop, 10) << 0);
    if ((value = scan_ini(fini, "ELEV_INJECT")))
        switches = switches | (strtol(value, &stop, 10) << 1);
    if ((value = scan_ini(fini, "INHIB_CRASH_DETECT")))
        switches = switches | (strtol(value, &stop, 10) << 3);
    if ((value = scan_ini(fini, "ONLINE")))
        switches = switches | (strtol(value, &stop, 10) << 2);
    if ((value = scan_ini(fini, "LOG_VERBOSITY")))
        flags->LOG_VERBOSITY = (LOG_LEVELS)strtol(value, &stop, 10);
    if ((value = scan_ini(fini, "DELETELOGFILE")))
        flags->deleteLogFile = strtol(value, &stop, 10);
    if ((value = scan_ini(fini, "MSFSDELAY")))
        flags->MSFSdelay = strtol(value, &stop, 10);

    flags->switches = switches;
    VERBOSITY = flags->LOG_VERBOSITY;

    free(value);
    fclose(fini);
    return 0;
}

void deleteFlagsPSXMSFS(FLAGS *flags)
{

    free(flags);
}

FLAGS *createFlagsPSXMSFS(void)
{

    /*-------------------------------------------*
     * Create flags structure and                *
     * Assign default values                     *
     * ------------------------------------------*/

    FLAGS *f = (FLAGS *)malloc(sizeof(FLAGS));
    if (f == NULL) {
        printDebug(LL_ERROR, "Error creating flags structure. Exiting now!");
        return NULL;
    }

    strcpy(f->PSXMainServer, "127.0.0.1");
    strcpy(f->PSXBoostServer, "127.0.0.1");
    strcpy(f->MSFSServer, "127.0.0.1");
    f->PSXPort = 10747;
    f->PSXBoostPort = 10749;
    f->switches = (F_TCAS | F_INJECT);
    f->LOG_VERBOSITY = LL_INFO;
    f->PSXsocket = -1;
    f->BOOSTsocket = -1;
    f->hSimConnect = NULL;
    f->connected = 0;
    f->deleteLogFile = 1;
    f->MSFSdelay = 10;

    updateFromIni(f);

    return f;
}

servers getServersInfo(FLAGS *f)
{

    servers S;

    strncpy(S.PSXMainServer, f->PSXMainServer, IP_LENGTH);
    strncpy(S.PSXBoostServer, f->PSXBoostServer, IP_LENGTH);
    strncpy(S.MSFSServer, f->MSFSServer, IP_LENGTH);
    S.PSXPort = f->PSXPort;
    S.PSXBoostPort = f->PSXBoostPort;

    return S;
}

unsigned int getSwitch(FLAGS *f)
{
    return f->switches;
}
int getMSFSdelay(FLAGS *f)
{
    return f->MSFSdelay;
}
void setMSFSdelay(FLAGS *f, int delay)
{
    if (delay < 0)
        delay = 0;
    f->MSFSdelay = delay;
    return;
}
int deleteLogFile(FLAGS *f)
{
    return f->deleteLogFile;
}
int getLogVerbosity(FLAGS *f)
{
    return f->LOG_VERBOSITY;
}

void setOnlineHack(FLAGS *f, unsigned int hack)
{
    f->switches = (f->switches & ~F_ONLINE) | (hack << 2);
}
void setPSXslave(FLAGS *f, unsigned int slave)
{
    f->switches = (f->switches & ~F_SLAVE) | (slave << 4);
}
void setCrashInhib(FLAGS *f, unsigned int crash)
{
    f->switches = (f->switches & ~F_INHIB) | (crash << 3);
}
void setElevationInject(FLAGS *f, unsigned int elev)
{
    f->switches = (f->switches & ~F_INJECT) | (elev << 1);
}
void setTCASinject(FLAGS *f, unsigned int TCAS)
{
    f->switches = (f->switches & ~F_TCAS) | (TCAS << 0);
}
void setLogVerbosity(FLAGS *f, LOG_LEVELS level)
{
    f->LOG_VERBOSITY = level;
    VERBOSITY = level;
}
int setServersInfo(servers *S, FLAGS *f)
{

    if (f == NULL) {
        printDebug(LL_ERROR, "Could not initialize servers. Exiting");
        return 1;
    }

    strncpy(f->PSXMainServer, S->PSXMainServer, IP_LENGTH);
    strncpy(f->PSXBoostServer, S->PSXBoostServer, IP_LENGTH);
    strncpy(f->MSFSServer, S->MSFSServer, IP_LENGTH);
    f->PSXPort = S->PSXPort;
    f->PSXBoostPort = S->PSXBoostPort;

    return 0;
}

int disconnectPSXMSFS(FLAGS *flags)
{
    CloseHandle(mutex);
    cleanup(flags);
    quit = 1;
    return 0;
}
