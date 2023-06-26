#include <stdio.h>
#include <windows.h>
#include "PSXMSFS.h"
#include "PSXMSFSLIB.h"
#include "MSFS.h"
#include "SimConnect.h"
#include "util.h"
#include "update.h"

struct PSXMSFSFLAGS flags;
struct INTERNALFLAGS intflags;

HANDLE mutex, mutexsitu;
CONDITION_VARIABLE condNewSitu;
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

DWORD main_launch(void)
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

    CloseHandle(mutex);
    CloseHandle(mutexsitu);
    return 0;
}

DWORD cleanup(void)
{
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

    return 0;
}

DWORD initialize(int argc, char **argv)
{
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
     * only when compiling with MINGW
     * since no getopt.h header in Win32
     */

    if (argc > 1 && argv != NULL) {
        parse_arguments(argc, argv);
    }

    /*
     * version of program
     * And Compiler options used
     */

    printDebug(LL_INFO, "This is PSXMSFS version: %lld", VER);
    printDebug(LL_DEBUG, "Compiled on: %s", COMP);
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
    Initialize position at LFPG*/
    init_pos();


    return 0;
}
