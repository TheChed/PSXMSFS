#include <stdio.h>
#include <string.h>
#include <winsock.h>
#include <windows.h>
#include "SimConnect.h"
#include "PSXMSFSLIB.h"

#ifndef __MINGW__
#pragma comment(lib, "Ws2_32.lib")
#endif // !__MINGW__

static SOCKET sPSX;
HANDLE hSimConnect;

int close_PSX_socket(SOCKET sockid)
{
    return closesocket(sockid);
}

int init_socket()
{

    WSADATA wsa;
    return WSAStartup(MAKEWORD(2, 2), &wsa);
}

SOCKET init_connect_PSX(const char *hostname, int portno)
{

    struct sockaddr_in PSXmainserver;
    SOCKET socketID;

    // Create a socket
    if ((socketID = socket(AF_INET, SOCK_STREAM, 0)) == INVALID_SOCKET) {
        printDebug(LL_ERROR, "Error while creating the main PSX socket");
        return -1;
    }

    // Connect to PSX
    memset(&PSXmainserver, 0, sizeof(PSXmainserver));

    PSXmainserver.sin_family = AF_INET;
    PSXmainserver.sin_port = htons(portno);
    PSXmainserver.sin_addr.s_addr = inet_addr(hostname);

    if (connect(socketID, (struct sockaddr *)&PSXmainserver, sizeof(PSXmainserver)) < 0) {
        return -1;
    } else {
        return socketID;
    }
}

HANDLE init_connect_MSFS(void)
{
    HANDLE hSimConnect = NULL;

    if (SUCCEEDED(SimConnect_Open(&hSimConnect, "PSX", NULL, 0, 0, 0))) {
        printDebug(LL_DEBUG, "Connected to MSFS Simconnect");
    } else {
        printDebug(LL_ERROR, "Could not connect to MSFS Simconnect. Exiting now...");
    }

    return hSimConnect;
}

SOCKET getPSXsocket(FLAGS *f)
{
    return f->PSXsocket;
}

int open_connections(FLAGS *f)
{

    // initialise Win32 socket library
    if (init_socket()!=0) {
        printDebug(LL_ERROR, "Could not initialize Windows sockets. Exiting...");
        return 1;
    }

    // connect to PSX main socket

    printDebug(LL_INFO, "Connecting to PSX main server on: %s:%d", f->PSXMainServer, f->PSXPort);

    f->PSXsocket = init_connect_PSX(f->PSXMainServer, f->PSXPort);
    sPSX=f->PSXsocket;

    if (f->PSXsocket == INVALID_SOCKET) {
        printDebug(LL_ERROR, "Error connecting to the PSX socket. Exiting...");
        quit = 1;
        return 1;
    } else {
        printDebug(LL_INFO, "Connected to PSX main server.");
    }

    // connect to boost socket
    printDebug(LL_INFO, "Connecting to PSX boost server on: %s:%d", f->PSXBoostServer, f->PSXBoostPort);

    f->BOOSTsocket = init_connect_PSX(f->PSXBoostServer, f->PSXBoostPort);
    if (f->BOOSTsocket== INVALID_SOCKET) {
        printDebug(LL_ERROR, "Error connecting to the PSX boost socket. Are you sure it is "
                             "running? Exiting...");
        quit=1;
        return 1;
    } else {
        printDebug(LL_INFO, "Connected to PSX boost server.");
    }

    // finally connect to MSFS socket via SimConnect
    if ((f->hSimConnect=init_connect_MSFS())==NULL) {
        printDebug(LL_ERROR, "Could not connect to Simconnect.dll. Is MSFS running?");
        quit = 1;
        return 1;
    } else {
        printDebug(LL_INFO, "Connected to MSFS.");
        hSimConnect=f->hSimConnect;
        return 0;
    }
}
int sendQPSX(const char *s)
{

    int nbsend = 0;
    char *dem = (char *)malloc((1 + strlen(s)) * sizeof(char));

    if (dem == NULL) {
        printDebug(LL_ERROR, "Could not create PSX variable: PSX will not be updated.");
        return -1;
    }

    strncpy(dem, s, strlen(s));
    dem[strlen(s)] = 10;

    /*-------------------------------------
     * Send a Q variable to PSX if we are not
     * just reloading a situ.
     * ------------------------------------*/
    if (!intflags.updateNewSitu) {
        printDebug(LL_DEBUG, "Sending %s to PSX", s);
        nbsend = send(sPSX, dem, (int)(strlen(s) + 1), 0);
        if (nbsend == 0) {
            printDebug(LL_ERROR, "Error sending variable %s to PSX", s);
        }
    }
    free(dem);
    return nbsend;
}
