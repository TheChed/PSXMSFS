#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <unistd.h>
#include <winsock2.h>
#include <windows.h>
#include "PSXMSFS.h"
#include "SimConnect.h"

HANDLE hSimConnect = NULL;

int sPSX, sPSXBOOST;
int close_PSX_socket(int sockid) { return closesocket(sockid); }

void init_socket() {

    WSADATA wsa;

    if (WSAStartup(MAKEWORD(2, 2), &wsa) != 0) {
        printf("Initialzation Failed. Error Code : %d. Exiting...\n", WSAGetLastError());
        exit(EXIT_FAILURE);
    }
}

int init_connect_PSX(const char *hostname, int portno) {

    // int sockfd ;
    struct sockaddr_in PSXmainserver;
    int socketID;

    // Create a socket
    if ((socketID = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
        err_n_die("Error while creating the main PSX socket");
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

void init_connect_MSFS(HANDLE *p) {

    if (SimConnect_Open(p, "PSX", NULL, 0, 0, 0) == S_OK) {
        printf("Connected to MSFS\n");
    } else {
        err_n_die("Could not connect to MSFS. Are you sure it is running? Exiting...");
    }
}

void open_connections() {

    // initialise Win32 socket library
    init_socket();

    // connect to PSX main socket
    printf("Connecting to PSX main server on: %s:%d\n", PSXMainServer, PSXPort);
    sPSX = init_connect_PSX(PSXMainServer, PSXPort);
    if (sPSX < 0) {
        err_n_die("Error connecting to the PSX socket. Exiting...");
    } else {
        printf("Connected to PSX main server.\n\n");
    }

    // connect to boost socket
    printf("Connecting to PSX boost server on: %s:%d\n", PSXBoostServer, PSXBoostPort);
    sPSXBOOST = init_connect_PSX(PSXBoostServer, PSXBoostPort);
    if (sPSXBOOST < 0) {
        err_n_die("Error connecting to the PSX boost socket. Are you sure it is running? Exiting...");
    } else {
        printf("Connected to PSX boost server.\n\n");
    }

    // finally connect to MSFS socket via SimConnect
    init_connect_MSFS(&hSimConnect);
}

void err_n_die(const char *fmt, ...) {
    int errno_save;
    va_list ap;

    errno_save = errno;

    va_start(ap, fmt);
    vfprintf(stdout, fmt, ap);
    fprintf(stdout, "\n");
    fflush(stdout);

    if (errno_save != 0) {
        fprintf(stdout, "(errno= %d) : %s\n", errno_save, strerror(errno_save));
        fprintf(stdout, "\n");
        fflush(stdout);
    }
    va_end(ap);
    exit(1);
}
