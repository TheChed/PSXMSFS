#include "SimConnect.h"

int init_MS_data(void);
double getGroundAltitude(void);
double getMSL_pressure(void);
double getMSFS_baro(void);
double getMSL_temperature(void);
double getIndAltitude(void);
int isGroundAltitudeAvailable(void);
void CALLBACK SimmConnectProcess(SIMCONNECT_RECV *pData, DWORD cbData, void *pContext);
void freezeMSFS(void);
