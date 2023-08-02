#include <cstdlib>
#include <stdio.h>
#include <windows.h>
#include "PSXMSFSLIB.h"
#include "MSFS.h"
#include "util.h"
#include "update.h"
#include "log.h"

/*
 * State flags from either:
 * ini file, command line options or default flags
 */
FLAGS PSXflags;

struct INTERNALFLAGS intflags;

/*
 * Handles for mutexes used in
 * various threads
 */
HANDLE mutex, mutexsitu;
CONDITION_VARIABLE condNewSitu;

/*
 * Global variable used in the main 3
 * thread loops
 * 0: continues the program
 * 1: quits
 */
int quit = 0;

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

void thread_launch(void)
{
    DWORD t1, t2, t3;
    HANDLE h1, h2, h3;
    /*
     * Create a thread mutex so that two threads cannot change simulataneously
     * the position of the aircraft
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
        printDebug(LL_ERROR, "Error creating PSX main server thread. Quitting now.");
        quit = 1;
    }

    h2 = CreateThread(NULL, 0, ptUmainboost, NULL, 0, &t2);
    if (h2 == NULL) {
        printDebug(LL_ERROR, "Error creating boost server thread. Quitting now.");
        quit = 1;
    }
    h3 = CreateThread(NULL, 0, ptDatafromMSFS, NULL, 0, &t3);
    if (h3 == NULL) {
        printDebug(LL_ERROR, "Error creating MSFS server thread. Quitting now.");
        quit = 1;
    }

    WaitForSingleObject(h1, INFINITE);
    WaitForSingleObject(h2, INFINITE);
    WaitForSingleObject(h3, INFINITE);

    CloseHandle(mutex);
    CloseHandle(mutexsitu);
}

DWORD cleanup(void)
{
    // To force threads to close if not yet done
    quit = 1;

    printDebug(LL_INFO, "Closing MSFS connection...");
    SimConnect_Close(hSimConnect);

    // Signaling PSX that we are quitting
    sendQPSX("exit");

    // and gracefully close main + boost sockets
    printDebug(LL_INFO, "Closing PSX boost connection...");
    if (close_PSX_socket(PSXflags.sPSXBOOST)) {
        printDebug(LL_ERROR, "Could not close boost PSX socket... You might want to check PSX");
    }
    printDebug(LL_INFO, "Closing PSX main connection...\n");
    if (close_PSX_socket(PSXflags.sPSX)) {
        printDebug(LL_ERROR, "Could not close main PSX socket...But does it matter now?...");
    }

    // Finally clean up the Win32 sockets
    WSACleanup();

    /* and clean up the debug file
     * deleting it if not in DEBUG mode
     */
    remove_debug();

    /*
     * and free the used memory
     */

    // free(&PSXflags);
    return 0;
}

DWORD initialize(const char *MSFSServer, const char *PSXMainIP, int PSXMainPort, const char *PSXBoostIP, int PSXBoostPort)
{

    int result_init;

    /* Initialise the timer */
    elapsedStart(&TimeStart);

    result_init = init_param(MSFSServer, PSXMainIP, PSXMainPort, PSXBoostIP, PSXBoostPort);

    if(result_init == 1){ 
        printDebug(LL_ERROR, "Could not initialize various PSX internal flags. Exiting now...");
        quit=1;
    }
    if(result_init == 2){ 
        printDebug(LL_ERROR, "Could not open config file, will try to create one with educated guesses... Please restart the program");
        quit=1;
    }

    return result_init;
}

FLAGS *connectPSXMSFS(void)
{

    /*
     * Initialise and connect to all sockets: PSX, PSX Boost and Simconnect
     */
    if (!open_connections()) {
        return NULL;
    }

    // initialize the data to be received as well as all EVENTS
    init_MS_data();

    /*
    Initialize position at LFPG*/
    init_pos();

    printDebug(LL_INFO, "This is PSXMSFS version: %lld", VER);
    printDebug(LL_DEBUG, "Compiled on: %s", COMP);
    printDebug(LL_INFO, "Please disable all crash detection in MSFS");

     return &PSXflags;
}

DWORD WINAPI main_launch()
{
    thread_launch();
    return 0;
}
