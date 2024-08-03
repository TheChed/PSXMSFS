#include <cstdlib>
#include <stdio.h>
#include <windows.h>
#include "PSXMSFSLIB.h"
#include "MSFS.h"
#include "util.h"
#include "update.h"
#include "connect.h"
#include "winnt.h"

/*----------------------------------------------
 * Main thread functions used to get data from
 * PSX and Boost servers.
 * Defined in PSXDATA.cpp
 * --------------------------------------------*/
int getDataFromPSX(void);
int getDataFromBoost(void);

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
    HRESULT hr;
    UNUSED(thread_param);

    while (!quit) {
        hr = SimConnect_CallDispatch(hSimConnect, SimmConnectProcess, NULL);
        if (hr == E_FAIL) {
            exit(1);
        }
        if (!intflags.updateNewSitu) {
            WaitForSingleObject(mutex, INFINITE);
            SetMSFSPos();
            ReleaseMutex(mutex);
        }
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
        if (!intflags.updateNewSitu)
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

    // WaitForSingleObject(h1, INFINITE);
    // WaitForSingleObject(h2, INFINITE);
    // WaitForSingleObject(h3, INFINITE);
}

int cleanup(void)
{

    quit = 1; // To force threads to close if not yet done
    printDebug(LL_INFO, "Closing MSFS connection...");
    SimConnect_Close(hSimConnect);

    printDebug(LL_INFO, "MSFS closed.");
    sendQPSX("exit"); // Signal PSX that we are quitting

    /*-------------------------------------------------------------------------------
     * and gracefully try to close main and boost sockets
     * -----------------------------------------------------------------------------*/
    printDebug(LL_INFO, "Closing PSX boost connection...");
    if (close_PSX_socket(sPSXBOOST)) {
        printDebug(LL_ERROR, "Could not close boost PSX socket... You might want to check PSX");
    }
    printDebug(LL_INFO, "Closing PSX main connection...");
    if (close_PSX_socket(sPSX)) {
        printDebug(LL_ERROR, "Could not close main PSX socket...But does it matter now?...");
    }

    WSACleanup(); // CLose the Win32 sockets
    printDebug(LL_INFO, "See you next time!");
    remove_debug(); // Remove DEBUG file if not in DEBUG mode

    return 0;
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

void disconnect(void)
{
    CloseHandle(mutex);
    CloseHandle(mutexsitu);
    cleanup();
    quit = 1;
}

int connectPSXMSFS(FLAGS *flags)
{

    /*
     * Initialise and connect all sockets: PSX, PSX Boost and Simconnect
     */
    if (open_connections(flags)) {
        return 1;
    }

    // initialize the data to be received as well as all EVENTS
    init_MS_data();

    /*
    Initialize position at LFPG*/
    init_pos();

    printDebug(LL_INFO, "This is PSXMSFS version: %lld", VER);
    printDebug(LL_INFO, "Please disable all crash detection in MSFS");

    return 0;
}

int main_launch()
{
    quit = 0;
    thread_launch();
    return 0;
}
