#include <windows.h>
#include "SimConnect.h"

int init_MS_data(void) ;
double getGroundAltitude(void);
int isGroundAltitudeAvailable(void);
void CALLBACK SimmConnectProcess(SIMCONNECT_RECV *pData, DWORD cbData, void *pContext) ;
