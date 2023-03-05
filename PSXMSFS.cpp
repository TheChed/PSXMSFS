#include <assert.h>
#include <cstddef>
#include <cstdint>
#include <cstdlib>
#include <cstring>
#include <ctime>
#include <math.h>
#include <pthread.h>
#include <stdio.h>
#include <time.h>
#include <unistd.h>
#include <windows.h>
#include "SimConnect.h"
#include "PSXMSFS.h"
#include "util.h"
#include "MSFS.h"
#include "update.h"

struct PSXTIME PSXtime;

int quit = 0;

pthread_mutex_t mutex;
int DEBUG = 1;
int TCAS_INJECT = 1; /*TCAS injection on by default*/
int ELEV_INJECT = 1; /*elevation injection on by default below 300 ft AGL */
int ONLINE = 1;		 /* injecting pressure altitude for online networks like VATSIM and IVAO */
int INHIB_CRASH_DETECT = 0;
int SLAVE = 0; // 0=PSX is master, 1=MSFS is master
char debugInfo[256] = {0};

char PSXMainServer[] = "999.999.999.999";
char MSFSServer[] = "999.999.999.999";
char PSXBoostServer[] = "999.999.999.999";
int PSXPort = 10747;
int PSXBoostPort = 10749;

void update_TCAS(AI_TCAS *ai, double d);

void *ptDatafromMSFS(void *thread_param)
{
	(void)(&thread_param);
	while (!quit) {
		SimConnect_CallDispatch(hSimConnect, SimmConnectProcess, NULL);

		Sleep(1); // We sleep for 1 ms (Sleep is a Win32 API with parameter in ms)
				  // to avoid heavy polling
	}
	return NULL;
}

void *ptUmainboost(void *)
{
	while (!quit) {
		umainBoost();
	}
	return NULL;
}

void *ptUmain(void *)
{

	while (!quit) {
		umain();
	}
	return NULL;
}

int main(int argc, char **argv)
{
	pthread_t t1, t2, t3;

	/* Initialise the timer */
	elapsedStart(&TimeStart);

	/*
	 * open debug file
	 */
	if (init_debug()) {
		printf("Error creating debug file...\n");
		exit(EXIT_FAILURE);
	}

	/* Read from .ini file the various values
	 * used in the program
	 */
	if (init_param()) {
		printDebug("Could not initialize default parameters... Quitting", 1);
		exit(EXIT_FAILURE);
	}

	/*
	 * check command line arguments
	 */
	parse_arguments(argc, argv);

	/*
	 * Initialise and connect to all sockets: PSX, PSX Boost and Simconnect
	 */
	if (!open_connections()) {
		printDebug("Could not initialize all connections. Exiting...", 1);
		exit(EXIT_FAILURE);
	}

	// initialize the data to be received as well as all EVENTS
	init_MS_data();

	/*
	 * Sending Q423 DEMAND variable to PSX for the winds
	 * Sending Q480 DEMAND variable to get aileron, rudder and elevator position
	 */

	sendQPSX("demand=Qs483");
	sendQPSX("demand=Qs480");

	/*
	 * Initializing position of the plane
	 * as boost and main threads are not yet available
	 */

	printDebug("Initializing position", DEBUG);
	init_pos();
	printDebug("Initializing done", DEBUG);

	/*
	 * Create a thread mutex so that two threads cannot change simulataneously
	 * the position of the aircraft
	 */

	pthread_mutex_init(&mutex, NULL);

	/*
	 * Creating the 3 threads:
	 * Thread 1: main server PSX
	 * Thread 2: boost server
	 * Thread 3: callback function in MSFS
	 */

	if (pthread_create(&t1, NULL, &ptUmain, NULL) != 0) {
		printDebug("Error creating thread Umain", 1);
		quit = 1;
	}

	if (pthread_create(&t2, NULL, &ptUmainboost, NULL) != 0) {
		printDebug("Error creating thread Umainboost", 1);
		quit = 1;
	}

	if (pthread_create(&t3, NULL, &ptDatafromMSFS, NULL) != 0) {
		printDebug("Error creating thread DatafromMSFS", 1);
		quit = 1;
	}
	if (pthread_join(t1, NULL) != 0) {
		printDebug("Failed to join Main thread", 1);
	}
	if (pthread_join(t2, NULL) != 0) {
		printDebug("Failed to join Boost thread", 1);
	}
	if (pthread_join(t3, NULL) != 0) {
		printDebug("Failed to join MSFS thread", 1);
	}
	pthread_mutex_destroy(&mutex);

	printf("Closing MSFS connection...\n");
	SimConnect_Close(hSimConnect);

	// Signaling PSX that we are quitting
	sendQPSX("exit");

	// and gracefully close main + boost sockets
	printf("Closing PSX boost connection...\n");
	if (close_PSX_socket(sPSXBOOST)) {
		printf("Could not close boost PSX socket...\n");
	}
	printf("Closing PSX main connection...\n");
	if (close_PSX_socket(sPSX)) {
		printf("Could not close main PSX socket...\n");
	}

	// Finally clean up the Win32 sockets
	WSACleanup();

	/* and clean up the debug file
	 * deleting it if not in DEBUG mode
	 */
	remove_debug();

	printf("Normal exit. See you soon...\n");
	return 0;
}
