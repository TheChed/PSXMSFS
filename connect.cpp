#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <unistd.h>
#include <winsock2.h>
#include <windows.h>
#include "PSXMSFS.h"
#include "SimConnect.h"

// Connection to PSX
//
//
int sPSX, sPSXBOOST;
HANDLE hSimConnect = NULL;


int close_socket(int sockid) {

    if (close(sockid) < 0) {
        err_n_die("Could not close socket");
    }
}

int init_connect_PSX_Boost(const char *hostname, int portboost) {

    struct sockaddr_in server;


    // Create a socket
    if ((sPSXBOOST = socket(AF_INET, SOCK_STREAM, 0)) == INVALID_SOCKET) {
        printf("Could not create socket. Error: %d", WSAGetLastError());
        exit(EXIT_FAILURE);
    }

    // Connect to PSX
    printf("Trying to connect to PSX Boost on %s:%d\n", hostname, portboost);
    server.sin_addr.s_addr = inet_addr(hostname);
    server.sin_family = AF_INET;
    server.sin_port = htons(portboost);

    if (connect(sPSXBOOST, (struct sockaddr *)&server, sizeof(server)) < 0) {
        printf("Connection error to boost server\n");
        exit(EXIT_FAILURE);
    }

    printf("Connected to PSX Boost Server\n");
    return 1;
}
int init_connect_PSX(const char *hostname, int portno) {

    // int sockfd ;
    struct sockaddr_in PSXmainserver;

    // Create a socket
    sPSX = socket(AF_INET, SOCK_STREAM,6 );
    printf("sPSX: %d\n",sPSX);
    printf("errno: %d\n",errno);
    
    //if ((sPSX = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
   //     err_n_die("Error while creating the main PSX socket");
   // }

    // Connect to PSX
    printf("Trying to connect to PSX on %s:%d\n", hostname, portno);
    bzero(&PSXmainserver, sizeof(PSXmainserver));

    PSXmainserver.sin_family = AF_INET;
    PSXmainserver.sin_port = htons(portno);
    PSXmainserver.sin_addr.s_addr = inet_addr(hostname);

    if (connect(sPSX, (struct sockaddr *)&PSXmainserver, sizeof(PSXmainserver)) < 0) {
        err_n_die("Error while connecting to the main PSX socket");
    }

    printf("Connected to PSX on %s:%d\n",hostname, portno);
    return 1;
}

int init_connect_MSFS(HANDLE *p) {

    SimConnect_Open(p, "PSX", NULL, 0, 0, 0);
    if (p) {
        printf("Connected to MSFS\n");
    } else {
        err_n_die("Could not connect to MSFS. Exiting...");
    }
    return 1;
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
