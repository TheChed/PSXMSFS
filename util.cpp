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
FILE *fdebug;

monotime getMonotonicTime(void)
{
	struct timespec ts;
	clock_gettime(CLOCK_MONOTONIC, &ts);
	return ((uint64_t)ts.tv_sec) * 1000000 + ts.tv_nsec / 1000;
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

double pressure_altitude(double mmhg)
{
	return 145366.45 * (1 - pow(mmhg / 100.0 * 33.8638 / 1013.25, 0.190284));
}

void printDebug(int level, const char *debugInfo, ...)
{

	va_list ap;
	char msg[MAXLEN];
	char timestamp[50];

	time_t t = time(NULL);
	struct tm date = *localtime(&t);
	FILE *fdebug;

	fdebug = fopen("DEBUG.TXT", "a");
	if (!fdebug)
		return;

	va_start(ap, debugInfo);

	vsnprintf(msg, sizeof(msg), debugInfo, ap);
	va_end(ap);

	strftime(timestamp, 50, "%H:%M:%S", &date);
	if (level >= flags.LOG_VERBOSITY) {
		fprintf(fdebug, "%s[+%ld.%.03ds]\t%s", timestamp, (long)elapsedMs(TimeStart) / 1000, (int)elapsedMs(TimeStart) % 1000, msg);
		fprintf(fdebug, "\n");
		fflush(fdebug);

		// and also print on the console
		printf("%s\n", msg);
	}
	fclose(fdebug);
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
void write_ini_file()
{
	FILE *f;

	f = fopen("PSXMSFS.ini", "w");
	if (!f) {
		printDebug(LL_ERROR,"Cannot create PSXMSFS.ini file. Something is seriously wrong!");
		return ;
	}

	/*PSX server addresses and port*/
	fprintf(f, "PSXMainServer=%s\n", "127.0.0.1" );
	fprintf(f, "PSXBoostServer=%s\n","127.0.0.1");
	fprintf(f, "PSXPort=%d\n", 10747);
	fprintf(f, "PSXBoostPort=%d\n", 10749);

	/*MSFS address*/
	fprintf(f, "MSFSServer=%s\n", "127.0.0.1");

	/* Switches */
	fprintf(f, "LOG_VERBOSITY=%d\n", flags.LOG_VERBOSITY);
	fprintf(f, "TCAS_INJECT=%d\n", flags.TCAS_INJECT);
	fprintf(f, "SLAVE=%d\n", flags.SLAVE);
	fprintf(f, "ELEV_INJECT=%d\n", flags.ELEV_INJECT);
	fprintf(f, "INHIB_CRASH_DETECT=%d\n", flags.INHIB_CRASH_DETECT);
	fprintf(f, "ONLINE=%d\n", flags.ONLINE);

	fclose(f);
	return ;
}

char *scan_ini(FILE *file, const char *key)
{

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

int init_param()
{
	FILE *fini;
	char *value;
	char *stop;

	/* Sensible default values*/
//	strcpy(flags.PSXMainServer, "127.0.0.1");
	//strcpy(flags.PSXBoostServer, "127.0.0.1");
//	strcpy(flags.MSFSServer, "127.0.0.1");
	flags.PSXPort = 10747;
	flags.PSXBoostPort = 10749;
	flags.SLAVE = 0;
	flags.LOG_VERBOSITY = LL_INFO;
	flags.TCAS_INJECT = 1;
	flags.ELEV_INJECT = 1;
	flags.INHIB_CRASH_DETECT = 0;
	flags.ONLINE = 0;

	fini = fopen("PSXMSFS.ini", "r");
	if (!fini) {
		printDebug(LL_ERROR,"Cannot open config file: trying to create one with educated "
			   "guesses... Please restart PSXMSFS");
		write_ini_file();
		quit=1;
		return 1;
	} else {
		flags.PSXMainServer = scan_ini(fini, "PSXMainServer");
		flags.PSXBoostServer = scan_ini(fini, "PSXBoostServer");
		flags.MSFSServer = scan_ini(fini, "MSFSServer");

		value = scan_ini(fini, "SLAVE");
		flags.SLAVE = strtol(value, &stop, 10);
		value = scan_ini(fini, "TCAS_INJECT");
		flags.TCAS_INJECT = strtol(value, &stop, 10);
		value = scan_ini(fini, "LOG_VERBOSITY");
			flags.LOG_VERBOSITY= (int)strtol(value, &stop, 10);
		value = scan_ini(fini, "ELEV_INJECT");
		flags.ELEV_INJECT = strtol(value, &stop, 10);
		value = scan_ini(fini, "INHIB_CRASH_DETECT");
		flags.INHIB_CRASH_DETECT = strtol(value, &stop, 10);
		value = scan_ini(fini, "ONLINE");
		flags.ONLINE = strtol(value, &stop, 10);
		free(value);
		fclose(fini);
	}

	return 0;
}
void remove_debug()
{
	remove("DEBUG.TXT");
}

void parse_arguments(int argc, char **argv)
{

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
			flags.PSXBoostServer = optarg;
			break;
		case 'E':
			flags.ELEV_INJECT = 0;
			break;
		case 'N':
			flags.ONLINE = 0;
			break;
		case 'C':
			flags.INHIB_CRASH_DETECT = 0;
			break;
		case 't':
			flags.TCAS_INJECT = 0;
			break;
		case 'h':
			usage();
			break;
		case 'm':
			flags.PSXMainServer = optarg;
			break;
		case 'q':
			flags.PSXBoostPort = (int)strtol(optarg, NULL, 10);
			break;
		case 'p':
			flags.PSXPort = (int)strtol(optarg, NULL, 10);
			break;
		case 'd':
			flags.LOG_VERBOSITY = LL_ERROR;
			break;
		case 's':
			flags.SLAVE = 1;
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
