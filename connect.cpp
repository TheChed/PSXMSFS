#include <stdio.h>
#include <stdlib.h>
#include <winsock2.h>
#include <windows.h>
#include "PSXMSFS.h"
#include "SimConnect.h"

// Connection to PSX
//
//

SOCKET sPSX;
HANDLE hSimConnect = NULL;

int init_connect_PSX(const char *hostname, int portno) {

    WSADATA wsa;
    struct sockaddr_in server;

    if (WSAStartup(MAKEWORD(2, 2), &wsa) != 0) {
        printf("Initialzation Failed. Error Code : %d. Exiting...\n", WSAGetLastError());
        exit(EXIT_FAILURE);
    }

    // Create a socket
    if ((sPSX = socket(AF_INET, SOCK_STREAM, 0)) == INVALID_SOCKET) {
        printf("Could not create socket. Error: %d", WSAGetLastError());
        exit(EXIT_FAILURE);
    }

    // Connect to PSX
    printf("Trying to connect to PSX on %s:%d\n", hostname, portno);
    server.sin_addr.s_addr = inet_addr(hostname);
    server.sin_family = AF_INET;
    server.sin_port = htons(portno);

    if (connect(sPSX, (struct sockaddr *)&server, sizeof(server)) < 0) {
        printf("Connection error\n");
        exit(EXIT_FAILURE);
    }

    printf("Connected to PSX\n");
    return 1;
}

int init_connect_MSFS(HANDLE *p) {

    SimConnect_Open(p, "PSX", NULL, 0, 0, 0);
    if (p) {
        printf("Connected to MSFS\n");
    } else {
        printf("Could not connect to MSFS. Exiting...\n");
        exit(EXIT_FAILURE);
    }
    return 1;
}
