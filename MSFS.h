#ifndef __MSFS_H_
#define __MSFS_H_
#include "SimConnect.h"

enum GROUP_ID {
    GROUP0,
    GROUP1,
};

enum INPUT_ID {
    INPUT_P_PRESS,
    INPUT_QUIT,
};

enum EVENT_ID {
    EVENT_SIM_START,
    EVENT_ONE_SEC,
    EVENT_6_HZ,
    EVENT_4_SEC,
    EVENT_FRAME,
    EVENT_P_PRESS,
    EVENT_FREEZE_ALT,
    EVENT_FREEZE_ALT_TOGGLE,
    EVENT_FREEZE_ATT,
    EVENT_FREEZE_ATT_TOGGLE,
    EVENT_FREEZE_LAT_LONG,
    EVENT_FREEZE_LAT_LONG_TOGGLE,
    EVENT_INIT,
    EVENT_QUIT,
    EVENT_ZULU_DAY,
    EVENT_ZULU_HOURS,
    EVENT_ZULU_MINUTES,
    EVENT_ZULU_YEAR,
    EVENT_PARKING,
    EVENT_STEERING,
    EVENT_XPDR,
    EVENT_XPDR_IDENT,
    EVENT_COM,
    EVENT_COM_STDBY,
    EVENT_BARO,
    EVENT_BARO_STD,
};

enum DATA_DEFINE_ID {
    BOOST_TO_MSFS,
    MSFS_CLIENT_DATA,
    MSFS_FREEZE,
    TCAS_TRAFFIC_DATA, // This is the DATA to be returned for the aircraft in the vicinity
    DATA_LIGHT,        // This is the DATA to be sent to MSFS to update the lights
    DATA_MOVING_SURFACES,
    DATA_SPEED,
    BOOST_TO_MSFS_STD_ALT,
    BOOST_TO_MSFS_ALT,
};

enum DATA_REQUEST_ID {
    DATA_REQUEST,
    DATA_REQUEST_FREEZE,
    DATA_REQUEST_TCAS,
};

int init_MS_data(void);
double getGroundAltitude(void);
double getMSL_pressure(void);
double getMSFS_baro(void);
double getMSL_temperature(void);
double getIndAltitude(void);
int isGroundAltitudeAvailable(void);
void CALLBACK SimmConnectProcess(SIMCONNECT_RECV *pData, DWORD cbData, void *pContext);
void freezeMSFS(void);
void init_variables(void);

#endif
