#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <winsock2.h>
#include <windows.h>
#include "PSXMSFS.h"
#include "SimConnect.h"
#include "util.h"

HANDLE hSimConnect = NULL;

int sPSX, sPSXBOOST;
int close_PSX_socket(int sockid) { return closesocket(sockid); }

int init_socket()
{

  WSADATA wsa;

  /* WSAStartup returns 0 in case of success*/
  return !WSAStartup(MAKEWORD(2, 2), &wsa);
}

int init_connect_PSX(const char *hostname, int portno)
{

  struct sockaddr_in PSXmainserver;
  int socketID;

  // Create a socket
  if ((socketID = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
    printDebug("Error while creating the main PSX socket", CONSOLE);
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

int init_connect_MSFS(HANDLE *p) { return (SimConnect_Open(p, "PSX", NULL, 0, 0, 0) == S_OK); }

int open_connections()
{
  char debugInfo[256];

  // initialise Win32 socket library
  if (!init_socket()) {
    printDebug("Could not initialize Windows sockets. Exiting...\n", 1);
    return 0;
  }

  // connect to PSX main socket
  sprintf(debugInfo, "Connecting to PSX main server on: %s:%d", PSXMainServer, PSXPort);
  printDebug(debugInfo, CONSOLE);

  sPSX = init_connect_PSX(PSXMainServer, PSXPort);
  if (sPSX < 0) {
    printDebug("Error connecting to the PSX socket. Exiting...", 1);
    return 0;
  } else {
    printDebug("Connected to PSX main server.", CONSOLE);
  }

  // connect to boost socket
  sprintf(debugInfo, "Connecting to PSX boost server on: %s:%d", PSXBoostServer, PSXBoostPort);
  printDebug(debugInfo, CONSOLE);

  sPSXBOOST = init_connect_PSX(PSXBoostServer, PSXBoostPort);
  if (sPSXBOOST < 0) {
    printDebug("Error connecting to the PSX boost socket. Are you sure it is "
	       "running? Exiting...",
	       1);
    return 0;
  } else {
    printDebug("Connected to PSX boost server.", CONSOLE);
  }

  // finally connect to MSFS socket via SimConnect
  if (!init_connect_MSFS(&hSimConnect)) {
    printDebug("Could not connect to Simconnect.dll. Is MSFS running?", 1);
    return 0;
  } else {
    printDebug("Connected to MSFS.", CONSOLE);
    return 1;
  }
}
