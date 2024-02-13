int init_param(const char *MSFSServerIP, const char *PSXMainIP, int PSXMainPort, const char *PSXBoostIP, int PSXBoostPort);
int init_socket(void);
int close_PSX_socket(SOCKET socket);
int open_connections(FLAGS *f);
int init_connect_MSFS(HANDLE *p);
