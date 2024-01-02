#include <cstdlib>
#include <stdio.h>
#include <windows.h>
#include "PSXMSFSLIB.h"
#include "MSFS.h"
#include "util.h"
#include "update.h"
#include "log.h"
#include "connect.h"

/*----------------------------------------------
 * Main thread functions used to get data from
 * PSX and Boost servers.
 * Defined in PSXDATA.cpp
 * --------------------------------------------*/
int getDataFromPSX(void);
int getDataFromBoost(void);

/*----------------------------------------
 * State flags read from PSXMSFS.ini file
 *---------------------------------------*/
FLAGS PSXflags;

/*--------------------------------------------
 * Handles for mutexes used in
 * various threads
 *-------------------------------------------*/
HANDLE mutex, mutexsitu;
CONDITION_VARIABLE condNewSitu;

int quit = 0;
DWORD TimeStart;

DWORD WINAPI ptDataFromMSFS(void *thread_param)
{
    UNUSED(thread_param);

    while (!quit) {
        SimConnect_CallDispatch(hSimConnect, SimmConnectProcess, NULL);
        Sleep(1); // We sleep for 1 ms to avoid heavy polling
    }

    return 0;
}

DWORD WINAPI ptDataFromBoost(void *)
{
    while (!quit) {
        getDataFromBoost();
        Sleep(1); // We sleep for 1 ms to avoid heavy polling
    }
    return 0;
}

DWORD WINAPI ptDataFromPSX(void *)
{

    while (!quit) {
        getDataFromPSX();
        Sleep(1); // We sleep for 1 ms to avoid heavy polling
    }
    return 0;
}

void thread_launch(void)
{
    DWORD t1, t2, t3;
    HANDLE h1, h2, h3;

    /*----------------------------------------------------
     * Create a thread mutex so that two threads cannot
     * change simulataneously the position of the aircraft
     * And create a condition mutex while loading a situ
     *---------------------------------------------------*/
    mutex = CreateMutex(NULL, FALSE, NULL);
    mutexsitu = CreateMutex(NULL, FALSE, NULL);
    InitializeConditionVariable(&condNewSitu);

    /*---------------------------------------
     * Creating the 3 threads:
     * Thread 1: main server PSX
     * Thread 2: boost server
     * Thread 3: callback function in MSFS
     *---------------------------------------*/

    h1 = CreateThread(NULL, 0, ptDataFromPSX, NULL, 0, &t1);
    if (h1 == NULL) {
        printDebug(LL_ERROR, "Error creating PSX main server thread. Quitting now.");
        quit = 1;
    }

    h2 = CreateThread(NULL, 0, ptDataFromBoost, NULL, 0, &t2);
    if (h2 == NULL) {
        printDebug(LL_ERROR, "Error creating boost server thread. Quitting now.");
        quit = 1;
    }
    h3 = CreateThread(NULL, 0, ptDataFromMSFS, NULL, 0, &t3);
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

int cleanup(void)
{
    quit = 1; // To force threads to close if not yet done

    printDebug(LL_INFO, "Closing MSFS connection...");
    SimConnect_Close(hSimConnect);

    sendQPSX("exit"); // Signal PSX that we are quitting

    /*-------------------------------------------------------------------------------
     * and gracefully try to close main and boost sockets
     * -----------------------------------------------------------------------------*/
    printDebug(LL_INFO, "Closing PSX boost connection...");
    if (close_PSX_socket(sPSXBOOST)) {
        printDebug(LL_ERROR, "Could not close boost PSX socket... You might want to check PSX");
    }
    printDebug(LL_INFO, "Closing PSX main connection...\n");
    if (close_PSX_socket(sPSX)) {
        printDebug(LL_ERROR, "Could not close main PSX socket...But does it matter now?...");
    }

    WSACleanup();   // CLose the Win32 sockets
    remove_debug(); // Remove DEBUG file if in DEBUG mode

    return 0;
}

int initialize(const char *MSFSServer, const char *PSXMainIP, int PSXMainPort, const char *PSXBoostIP, int PSXBoostPort)
{

    int result_init;

    TimeStart = GetTickCount(); // Initialize the timer

    /*---------------------------------------------
     * Initialize internal flags and
     * create default PSXMSFS.ini file if not present
     * -------------------------------------------*/
    result_init = init_param(MSFSServer, PSXMainIP, PSXMainPort, PSXBoostIP, PSXBoostPort);

    if (result_init == 1) {
        printDebug(LL_ERROR, "Could not initialize various PSX internal flags. Exiting now...");
        quit = 1;
    }
    if (result_init == 2) {
        printDebug(LL_ERROR, "Could not open config file, will try to create one with educated guesses... Please restart the program");
        quit = 1;
    }

    return result_init;
}

FLAGS *connectPSXMSFS(void)
{

    /*
     * Initialise and connect all sockets: PSX, PSX Boost and Simconnect
     */
    if (!open_connections()) {
        return NULL;
    }

    // initialize the data to be received as well as all EVENTS
    init_MS_data();

    /*
    Initialize position at LFPG*/
    init_pos();

    //printDebug(LL_INFO, "This is PSXMSFS version: %lld", VER);
    printDebug(LL_INFO, "DLL built on %s",__DATE__);
    printDebug(LL_INFO, "Please disable all crash detection in MSFS");

    return &PSXflags;
}

DWORD WINAPI main_launch()
{
    thread_launch();
    return 0;
}
