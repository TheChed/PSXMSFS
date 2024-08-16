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

/*----------------------------------------
 * State flags read from PSXMSFS.ini file
 * or input by client
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
    FLAGS *flags = (FLAGS *)thread_param;

    while (!quit) {
        SimConnect_CallDispatch(flags->hSimConnect, SimConnectProcess,flags);
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

    /*----------------------------------------------------
     * Create a thread mutex so that two threads cannot
     * change simulataneously the position of the aircraft
     * And create a condition mutex while loading a situ
     *---------------------------------------------------*/
    mutex = CreateMutex(NULL, FALSE, NULL);
    mutexsitu = CreateMutex(NULL, FALSE, NULL);

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

    h2 = CreateThread(NULL, 0, ptDataFromBoost,flags, 0, &t2);
    if (h2 == NULL) {
        printDebug(LL_ERROR, "Error creating boost server thread. Quitting now.");
        quit = 1;
    }
    h3 = CreateThread(NULL, 0, ptDataFromMSFS, flags, 0, &t3);
    if (h3 == NULL) {
        printDebug(LL_ERROR, "Error creating MSFS server thread. Quitting now.");
        quit = 1;
    }
}

void cleanup(FLAGS *flags)
{

    quit = 1; // To force threads to close if not yet done
    printDebug(LL_INFO, "Closing MSFS connection...");
    SimConnect_Close(flags->hSimConnect);

    printDebug(LL_INFO, "MSFS closed.");
    sendQPSX("exit"); // Signal PSX that we are quitting

    /*-----------------------------------------------------*
     * and gracefully try to close main and boost sockets  *
     * ----------------------------------------------------*/
    printDebug(LL_INFO, "Closing PSX boost connection...");
    if (close_PSX_socket(flags->BOOSTsocket)) {
        printDebug(LL_ERROR, "Could not close boost PSX socket... You might want to check PSX");
    }

    printDebug(LL_INFO, "Closing PSX main connection...");
    if (close_PSX_socket(flags->PSXsocket)) {
        printDebug(LL_ERROR, "Could not close main PSX socket...But does it matter now?");
    }

    WSACleanup(); // CLose the Win32 sockets

    // Remove DEBUG file if not in DEBUG mode
    if (flags->LOG_VERBOSITY > 1)
        remove("DEBUG.TXT");

    // bye bye
    printDebug(LL_INFO, "See you next time!");
}

int initialize(FLAGS *flags)
{

    /*---------------------------
     * resetting to 0 in case
     * we quit in a previous call
     * -------------------------*/
    quit = 0;

    TimeStart = GetTickCount(); // Initialize the timer
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

    if (flags != NULL)
        PSXflags = *flags;

    return 0;
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

    /*
    Initialize position at LFPG*/
    init_pos();

    printDebug(LL_INFO, "This is PSXMSFS version: %lld", VER);
    printDebug(LL_INFO, "Please disable all crash detection in MSFS");

    return 0;
}

int main_launch(FLAGS *flags)
{
    quit = 0;
    thread_launch(flags);
    return 0;
}

void disconnect(FLAGS *flags)
{
    CloseHandle(mutex);
    CloseHandle(mutexsitu);
    cleanup(flags);
    quit = 1;
}
