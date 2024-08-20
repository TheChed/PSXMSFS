#include <cstdlib>
#include <stdio.h>
#include <windows.h>
#include "PSXMSFSLIB.h"
#include "MSFS.h"
#include "util.h"
#include "update.h"

/*----------------------------------------------
 * Main thread functions used to get data from
 * PSX and Boost servers.
 * Defined in PSXDATA.cpp
 * --------------------------------------------*/
int getDataFromPSX(FLAGS *flags);
int getDataFromBoost(SOCKET sPSXBOOST);



int quit = 0;
DWORD TimeStart;

DWORD WINAPI ptDataFromMSFS(void *thread_param)
{
    FLAGS *flags = (FLAGS *)thread_param;

    while (!quit) {
        SimConnect_CallDispatch(flags->hSimConnect, SimConnectProcess, flags);
        WaitForSingleObject(mutex, INFINITE);
        SetMSFSPos(flags);
        ReleaseMutex(mutex);
        Sleep(1); // We sleep for 1 ms to avoid heavy polling
    }

    return 0;
}

DWORD WINAPI ptDataFromBoost(void *param)
{
    FLAGS *flags = (FLAGS *)param;

    while (!quit) {
        getDataFromBoost(flags->BOOSTsocket);
        Sleep(1); // We sleep for 1 ms to avoid heavy polling
    }
    return 0;
}

DWORD WINAPI ptDataFromPSX(void *param)
{
    FLAGS *f = (FLAGS *)(param);
    while (!quit) {
        getDataFromPSX(f);
        Sleep(1); // We sleep for 1 ms to avoid heavy polling
    }
    return 0;
}

void thread_launch(FLAGS *flags)
{
    DWORD t1, t2, t3;
    HANDLE h1, h2, h3;

    /*
    Initialize position at LFPG*/
    resetPSXDATA();

    /*---------------------------------------
     * Creating the 3 threads:
     * Thread 1: main server PSX
     * Thread 2: boost server
     * Thread 3: callback function in MSFS
     *---------------------------------------*/

    h1 = CreateThread(NULL, 0, ptDataFromPSX, flags, 0, &t1);
    if (h1 == NULL) {
        printDebug(LL_ERROR, "Error creating PSX main server thread. Quitting now.");
        quit = 1;
    }

    h2 = CreateThread(NULL, 0, ptDataFromBoost, flags, 0, &t2);
    if (h2 == NULL) {
        printDebug(LL_ERROR, "Error creating boost server thread. Quitting now.");
        quit = 1;
    }

    /*----------------------------------------------------
     * with 3 seconds before launching the MSMS thread
     * --------------------------------------------------*/
    Sleep(3000);
    h3 = CreateThread(NULL, 0, ptDataFromMSFS, flags, 0, &t3);
    if (h3 == NULL) {
        printDebug(LL_ERROR, "Error creating MSFS server thread. Quitting now.");
        quit = 1;
    }
}


int connectPSXMSFS(FLAGS *flags)
{

    /*
     * Initialise and connect all sockets: PSX, PSX Boost and Simconnect
     */
    if (open_connections(flags) != 0) {
        return 1;
    }

    // initialize the data to be received as well as all EVENTS
    init_MS_data(flags->hSimConnect);


    printDebug(LL_INFO, "This is PSXMSFS version: %lld", VER);
    printDebug(LL_INFO, "Please disable all crash detection in MSFS");

    return 0;
}

int launchPSXMSFS(FLAGS *flags)
{
    quit = 0;
    thread_launch(flags);
    return 0;
}

