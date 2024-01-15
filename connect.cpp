#include <stdio.h>
#include <string.h>
#include <winsock.h>
#include <windows.h>
#include "SimConnect.h"
#include "PSXMSFSLIB.h"
#include "log.h"

#ifndef __MINGW__
#pragma comment(lib, "Ws2_32.lib")
#endif // !__MINGW__

HANDLE hSimConnect = NULL;
SOCKET sPSX;      // main PSX socket id
SOCKET sPSXBOOST; // PSX boost socket id

int close_PSX_socket(SOCKET sockid)
{
    return closesocket(sockid);
}

int init_socket()
{

    WSADATA wsa;

    /* WSAStartup returns 0 in case of success*/
    return !WSAStartup(MAKEWORD(2, 2), &wsa);
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
    memset(&PSXmainserver,0, sizeof(PSXmainserver));

    PSXmainserver.sin_family = AF_INET;
    PSXmainserver.sin_port = htons(portno);
    PSXmainserver.sin_addr.s_addr = inet_addr(hostname);

    if (connect(socketID, (struct sockaddr *)&PSXmainserver, sizeof(PSXmainserver)) < 0) {
        return -1;
    } else {
        return socketID;
    }
}

int init_connect_MSFS(void)
{
    HRESULT hr;
    hr = SimConnect_Open(&hSimConnect, "PSX", NULL, 0, 0, 0);
    printDebug(LL_DEBUG, "Connected to MSFS Simconnect with return code %d",hr);
    return (SUCCEEDED(hr== S_OK));
}

int open_connections(void)
{

    // initialise Win32 socket library
    if (!init_socket()) {
        printDebug(LL_ERROR, "Could not initialize Windows sockets. Exiting...");
        return 0;
    }

    // connect to PSX main socket

    printDebug(LL_INFO, "Connecting to PSX main server on: %s:%d", PSXflags.PSXMainServer, PSXflags.PSXPort);

    sPSX = init_connect_PSX(PSXflags.PSXMainServer, PSXflags.PSXPort);
    if (sPSX == INVALID_SOCKET) {
        printDebug(LL_ERROR, "Error connecting to the PSX socket. Exiting...");
        quit=1;
        return 0;
    } else {
        printDebug(LL_INFO, "Connected to PSX main server.");
    }

    // connect to boost socket
    printDebug(LL_INFO, "Connecting to PSX boost server on: %s:%d", PSXflags.PSXBoostServer, PSXflags.PSXBoostPort);

    sPSXBOOST = init_connect_PSX(PSXflags.PSXBoostServer, PSXflags.PSXBoostPort);
    if (sPSXBOOST == INVALID_SOCKET) {
        printDebug(LL_ERROR, "Error connecting to the PSX boost socket. Are you sure it is "
                             "running? Exiting...");
        return 0;
    } else {
        printDebug(LL_INFO, "Connected to PSX boost server.");
    }

    // finally connect to MSFS socket via SimConnect
    if (!init_connect_MSFS()) {
        printDebug(LL_ERROR, "Could not connect to Simconnect.dll. Is MSFS running?");
        return 0;
    } else {
        printDebug(LL_INFO, "Connected to MSFS.");
        return 1;
    }
}
