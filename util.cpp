/* File containing all UTIL functions such as coordinates calculations, time
 * related functions etc.
 */
#include <cstring>
#include <stdio.h>
#include <cmath>
#include <stdint.h>
#include <time.h>
#include "util.h"
#include "PSXMSFSLIB.h"

//FLAGS PSXMSFS;

LOG_LEVELS VERBOSITY = LL_INFO; 

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

int write_ini_file(FLAGS *flags)
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

int updateFromIni(FLAGS *flags)
{
    char *value;
    char *stop;
    unsigned int switches = 0;

    FILE *fini = fopen("PSXMSFS.ini", "r");

    if (!fini)
        return 1;

    strncpy(flags->PSXMainServer, scan_ini(fini, "PSXMainServer"),IP_LENGTH);
    strncpy(flags->PSXBoostServer, scan_ini(fini, "PSXBoostServer"),IP_LENGTH);
    strncpy(flags->MSFSServer, scan_ini(fini, "MSFSServer"),IP_LENGTH);

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

    flags->switches = switches;
    VERBOSITY=flags->LOG_VERBOSITY;

    free(value);
    fclose(fini);
    return 0;
}

FLAGS *initFlags(void)
{

    /*-------------------------------------------*
     * Assign default values to the flags        *
     * ------------------------------------------*/

    FLAGS *PSXMSFS =(FLAGS*) malloc(sizeof(FLAGS));
    if(PSXMSFS==NULL){
        printDebug(LL_ERROR,"Error creating flags structure. Exiting now!");
        quit=1;
    }
        
    strcpy(PSXMSFS->PSXMainServer, "127.0.0.1");
    strcpy(PSXMSFS->PSXBoostServer, "127.0.0.1");
    strcpy(PSXMSFS->MSFSServer, "127.0.0.1");
    PSXMSFS->PSXPort = 10747;
    PSXMSFS->PSXBoostPort = 10749;
    PSXMSFS->switches = (F_TCAS | F_INJECT);
    PSXMSFS->LOG_VERBOSITY = LL_INFO;


    return PSXMSFS;
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
int getLogVerbosity(FLAGS *f)
{
    return f->LOG_VERBOSITY;
}

void setLogVerbosity(FLAGS *f, LOG_LEVELS level){
    f->LOG_VERBOSITY=level;
}
void setServersInfo(servers *S, FLAGS *f)
{

    strncpy(f->PSXMainServer, S->PSXMainServer, IP_LENGTH);
    strncpy(f->PSXBoostServer, S->PSXBoostServer, IP_LENGTH);
    strncpy(f->MSFSServer, S->MSFSServer, IP_LENGTH);
    f->PSXPort = S->PSXPort;
    f->PSXBoostPort = S->PSXBoostPort;

}
