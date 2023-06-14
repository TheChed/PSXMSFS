#include <assert.h>
#include <cstddef>
#include <cstdint>
#include <cstdlib>
#include <cstring>
#include <ctime>
#include <math.h>
#include <stdio.h>
#include <time.h>
#include <windows.h>
#include "SimConnect.h"
#include "PSXMSFS.h"
#include "util.h"
#include "MSFS.h"
#include "update.h"

// struct PSXTIME PSXtime;

struct PSXMSFSFLAGS flags;
struct INTERNALFLAGS intflags;

// pthread_mutex_t mutex, mutexsitu;
HANDLE mutex, mutexsitu;
CONDITION_VARIABLE condNewSitu;
// pthread_cond_t condNewSitu;
int quit = 0;

// char PSXMainServer[] = "999.999.999.999";
// char MSFSServer[] = "999.999.999.999";
// char PSXBoostServer[] = "999.999.999.999";
// int PSXPort = 10747;
// int PSXBoostPort = 10749;

DWORD WINAPI ptDatafromMSFS(void *thread_param)
{
    
    (void)(thread_param);
    while (!quit) {
       
        SimConnect_CallDispatch(hSimConnect, SimmConnectProcess, NULL);
              Sleep(1); // We sleep for 1 ms (Sleep is a Win32 API with parameter in ms)
                  // to avoid heavy polling
    }
    return 0;
}

DWORD WINAPI ptUmainboost(void *)
{
    while (!quit) {
        umainBoost();
    }
    return 0;
}

DWORD WINAPI ptUmain(void *)
{

    while (!quit) {
        umain();
    }
    return 0;
}

int main(int argc, char **argv)
{
    DWORD t1, t2, t3;
    HANDLE h1, h2, h3;

    UNUSED(argc);
    UNUSED(argv);

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
   //  parse_arguments(argc, argv);

    /*
     * version of program
     * And Compiler options used
     */

  //  printDebug(LL_INFO, "This is PSXMSFS version: %lld", VER);
 //   printDebug(LL_DEBUG, "Compiler options: %s", COMP);
    printDebug(LL_INFO, "Please disable all crash detection in MSFS");

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
    /*
        pthread_mutex_init(&mutex, NULL);
        pthread_mutex_init(&mutexsitu, NULL);
        pthread_cond_init(&condNewSitu, NULL);
    */
    mutex = CreateMutex(NULL, FALSE, NULL);
    mutexsitu = CreateMutex(NULL, FALSE, NULL);
    InitializeConditionVariable(&condNewSitu);
    /*
     * Creating the 3 threads:
     * Thread 1: main server PSX
     * Thread 2: boost server
     * Thread 3: callback function in MSFS
     */

    h1 = CreateThread(NULL, 0, ptUmain, NULL, 0, &t1);
    if (h1 == NULL) {
        printDebug(LL_ERROR, "Error creating thread Umain");
        quit = 1;
    }

    h2 = CreateThread(NULL, 0, ptUmainboost, NULL, 0, &t2);
    if (h2 == NULL) {
        printDebug(LL_ERROR, "Error creating thread Umain");
        quit = 1;
    }
    h3 = CreateThread(NULL, 0, ptDatafromMSFS, NULL, 0, &t3);
    if (h3 == NULL) {
        printDebug(LL_ERROR, "Error creating thread Umain");
        quit = 1;
    }

    WaitForSingleObject(h1, INFINITE);
    WaitForSingleObject(h2, INFINITE);
    WaitForSingleObject(h3, INFINITE);
    /*
        if (pthread_join(t1, NULL) != 0) {
            printDebug(LL_ERROR, "Failed to join Main thread");
        }
        if (pthread_join(t2, NULL) != 0) {
            printDebug(LL_ERROR, "Failed to join Boost thread");
        }
        if (pthread_join(t3, NULL) != 0) {
            printDebug(LL_ERROR, "Failed to join MSFS thread");
        }
    */
    /*
     * Cleaning thread related tools
     */
    // pthread_mutex_destroy(&mutex);
    // pthread_mutex_destroy(&mutexsitu);

    CloseHandle(mutex);
    CloseHandle(mutexsitu);
    //   pthread_cond_destroy(&condNewSitu);

    printDebug(LL_INFO, "Closing MSFS connection...");
    SimConnect_Close(hSimConnect);

    // Signaling PSX that we are quitting
    sendQPSX("exit");

    // and gracefully close main + boost sockets
    printDebug(LL_INFO, "Closing PSX boost connection...");
    if (close_PSX_socket(flags.sPSXBOOST)) {
        printDebug(LL_ERROR, "Could not close boost PSX socket... You might want to check PSX");
    }
    printDebug(LL_INFO, "Closing PSX main connection...\n");
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
