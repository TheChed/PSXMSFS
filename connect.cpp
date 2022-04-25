#include <stdio.h>
#include <string.h>
#include <stdarg.h>
#include <stdlib.h>
#include <errno.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <unistd.h>
#include "PSXMSFS.h"
#include <sys/socket.h>
#include <arpa/inet.h>
//HANDLE hSimConnect = NULL;

int sPSX, sPSXBOOST;
int close_PSX_socket(int sockid) { return close(sockid); }

void init_socket() {

    //WSADATA wsa;

    //if (WSAStartup(MAKEWORD(2, 2), &wsa) != 0) {
    //    printf("Initialzation Failed. Error Code : %d. Exiting...\n", WSAGetLastError());
    //    exit(EXIT_FAILURE);
    //}
}

int init_connect_PSX(const char *hostname, int portno) {

    // int sockfd ;
    struct sockaddr_in PSXserver;
    int socketID;

    // Create a socket
    if ((socketID = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
        err_n_die("Error while creating the main PSX socket");
    }

    // Connect to PSX
    bzero(&PSXserver, sizeof(PSXserver));

    PSXserver.sin_family = AF_INET;
    PSXserver.sin_port = htons(portno);
    PSXserver.sin_addr.s_addr = inet_addr(hostname);

    if (connect(socketID, (struct sockaddr *)&PSXserver, sizeof(PSXserver)) < 0) {
        err_n_die("Error while connecting to the PSX socket. Exiting...");
    }

    return socketID;
}

void init_connect_MSFS(HANDLE *p) {

 //   if (SimConnect_Open(p, "PSX", NULL, 0, 0, 0) == S_OK) {
 //       printf("Connected to MSFS\n");
 //   } else {
 //       err_n_die("Could not connect to MSFS. Are you sure it is running? Exiting...");
 //   }
}

void open_connections() {

    // initialise Win32 socket library
    init_socket();

    // connect to PSX main socket
    printf("opening: %s:%d\n", PSXMainServer, PSXPort);
    sPSX = init_connect_PSX(PSXMainServer, PSXPort);
    printf("Connected to PSX main server on %s:%d\n", PSXMainServer, PSXPort);

    // connect to boost socket
    sPSXBOOST = init_connect_PSX(PSXBoostServer, PSXBoostPort);
    printf("Connected to PSX boost server on %s:%d\n", PSXBoostServer, PSXBoostPort);

    // finally connect to MSFS socket via SimConnect
    //init_connect_MSFS(&hSimConnect);
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
