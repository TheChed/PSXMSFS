#include <stdio.h>
#include <stdlib.h>
#include <winsock.h>
#include <windows.h>
#include "PSXMSFS.h"
#include "SimConnect.h"
#include "util.h"

#ifndef __MINGW__
#pragma comment(lib, "Ws2_32.lib")
#endif // !__MINGW__



HANDLE hSimConnect = NULL;

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
    bzero(&PSXmainserver, sizeof(PSXmainserver));

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
    return (SUCCEEDED(SimConnect_Open(&hSimConnect, "PSX", NULL, 0, 0, 0) == S_OK));
}

int open_connections()
{

    // initialise Win32 socket library
    if (!init_socket()) {
        printDebug(LL_ERROR, "Could not initialize Windows sockets. Exiting...");
        return 0;
    }

    // connect to PSX main socket

    printDebug(LL_INFO, "Connecting to PSX main server on: %s:%d", flags.PSXMainServer, flags.PSXPort);

    flags.sPSX = init_connect_PSX(flags.PSXMainServer, flags.PSXPort);
    if (flags.sPSX == INVALID_SOCKET) {
        printDebug(LL_ERROR, "Error connecting to the PSX socket. Exiting...");
        return 0;
    } else {
        printDebug(LL_INFO, "Connected to PSX main server.");
    }

    // connect to boost socket
    printDebug(LL_INFO, "Connecting to PSX boost server on: %s:%d", flags.PSXBoostServer, flags.PSXBoostPort);

    flags.sPSXBOOST = init_connect_PSX(flags.PSXBoostServer, flags.PSXBoostPort);
    if (flags.sPSXBOOST == INVALID_SOCKET) {
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
