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

// struct PSXTIME PSXtime;

struct PSXMSFSFLAGS flags;
struct INTERNALFLAGS intflags;

pthread_mutex_t mutex;
int quit = 0;

// char PSXMainServer[] = "999.999.999.999";
// char MSFSServer[] = "999.999.999.999";
// char PSXBoostServer[] = "999.999.999.999";
// int PSXPort = 10747;
// int PSXBoostPort = 10749;

void *ptDatafromMSFS(void *thread_param)
{
	(void)(thread_param);
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

	/* Read from .ini file the various values
	 * used in the program
	 */
	if (init_param()) {
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
	sendQPSX("demand=Qs562");

	/*
	 * Initializing position of the plane
	 * as boost and main threads are not yet available
	 */

	init_pos();

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

	if (pthread_create(&t1, NULL, ptUmain, NULL) != 0) {
		printDebug(LL_ERROR, "Error creating thread Umain");
		quit = 1;
	}

	if (pthread_create(&t2, NULL, ptUmainboost, NULL) != 0) {
		printDebug(LL_ERROR, "Error creating thread Umainboost");
		quit = 1;
	}

	if (pthread_create(&t3, NULL, ptDatafromMSFS, NULL) != 0) {
		printDebug(LL_ERROR, "Error creating thread DatafromMSFS");
		quit = 1;
	}
	if (pthread_join(t1, NULL) != 0) {
		printDebug(LL_ERROR, "Failed to join Main thread");
	}
	if (pthread_join(t2, NULL) != 0) {
		printDebug(LL_ERROR, "Failed to join Boost thread");
	}
	if (pthread_join(t3, NULL) != 0) {
		printDebug(LL_ERROR, "Failed to join MSFS thread");
	}
	pthread_mutex_destroy(&mutex);

	printf("Closing MSFS connection...\n");
	SimConnect_Close(hSimConnect);

	// Signaling PSX that we are quitting
	sendQPSX("exit");

	// and gracefully close main + boost sockets
	printf("Closing PSX boost connection...\n");
	if (close_PSX_socket(flags.sPSXBOOST)) {
		printDebug(LL_ERROR, "Could not close boost PSX socket... You might want to check PSX");
	}
	printf("Closing PSX main connection...\n");
	if (close_PSX_socket(flags.sPSX)) {
		printDebug(LL_ERROR, "Could not close main PSX socket...But does it matter now?...");
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
