#include <assert.h>
#include <cstddef>
#include <cstdint>
#include <cstring>
#include <ctime>
#include <getopt.h>
#include <math.h>
#include <pthread.h>
#include <stdio.h>
#include <unistd.h>
#include <windows.h>
#include "PSXMSFS.h"
#include "SimConnect.h"

int quit = 0;

// indicates whether there is a data of ground elevation received from MSFS in the callback procedure
double MSFS_plane_alt, CG_height;
int ground_elev_avail = 0;
int MSFS_POS_avail = 0;
double latMSFS, longMSFS;
int key_press = 0;

float ground_elev = 0;
int Qi198Sent = 0;
struct Struct_MSFS MSFS_POS;

Target Tmain, Tboost;
pthread_mutex_t mutex;
int updateLights, UTCupdate = 1;
int validtime = 0;
HRESULT hr;
int DEBUG;
int TCAS_INJECT = 1; /*TCAS injection on by default*/
char PSXMainServer[] = "127.0.0.1";
char PSXBoostServer[] = "0.0.0.0";
int PSXPort;
int PSXBoostPort;
int SLAVE = 0; // 0=PSX is master, 1=MSFS is master

FILE *fdebug;

/*
 * Global variables used for TCAS updating
 */

TCAS tcas_acft[7];
double min_dist = 999999;
int nb_acft = 0;

void update_TCAS(AI_TCAS *ai, double d);

void CalcCoord(double bearing, double dist, double lato, double longo, double *latr, double *longr) {

    *latr = asin(sin(lato) * cos(dist * FTM / EARTH_RAD) + cos(lato) * sin(dist * FTM / EARTH_RAD) * cos(bearing));
    *longr = longo + atan2(sin(bearing) * sin(dist * FTM / EARTH_RAD) * cos(lato),
                           cos(dist * FTM / EARTH_RAD) - sin(lato) * sin(*latr));
}

double dist(double lat1, double lat2, double long1, double long2) {
    return 2 * EARTH_RAD *
           (sqrt(pow(sin((lat2 - lat1) / 2), 2) + cos(lat1) * cos(lat2) * pow(sin((long2 - long1) / 2), 2)));
}
void SetUTCTime(void) {

    if (UTCupdate && validtime) {
        SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_ZULU_HOURS, Tmain.hour,
                                       SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
        SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_ZULU_MINUTES, Tmain.minute,
                                       SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
        SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_ZULU_DAY, Tmain.day,
                                       SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
        SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_ZULU_YEAR, Tmain.year,
                                       SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);

        UTCupdate = 1; // continous update of UTC time
    }
}

void SetCOMM(void) {

    SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_XPDR, Tmain.XPDR,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
    SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_XPDR_IDENT, Tmain.IDENT,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
    SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_COM, Tmain.COM1,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
    SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_COM_STDBY, Tmain.COM2,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
}
void SetBARO(void) {

    SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_BARO, Tmain.altimeter * 16.0,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
    if (Tmain.STD) {
        SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_BARO_STD, 1,
                                       SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
    }
}

void IA_update() {

    hr = SimConnect_RequestDataOnSimObjectType(hSimConnect, DATA_REQUEST_TCAS, DATA_TCAS_TRAFFIC, 40 * NM,
                                               SIMCONNECT_SIMOBJECT_TYPE_AIRCRAFT);
    for (int acft_id = 0; acft_id < 7; acft_id++) {
        tcas_acft[acft_id].latitude = 0.0;
        tcas_acft[acft_id].longitude = 0.0;
        tcas_acft[acft_id].altitude = 0;
        tcas_acft[acft_id].heading = 0;
        tcas_acft[acft_id].distance = 0;
    }
    min_dist = 999999;
    nb_acft = 0;
}
void print_MSFS(struct Struct_MSFS *M) {

    printf("Lat: %.15lf\t Long: %.15lf\t head: %.5f\n", M->latitude, M->longitude, M->heading_true);
    printf("Latdeg: %.15lf\t Longdeg: %.15lf\t head: %.5f\n\n", M->latitude * 180 / M_PI, M->longitude * 180 / M_PI,
           M->heading_true * 180 / M_PI);
}

void Inject_MSFS_PSX(void) {
    char tmpchn[128] = {0};
    char Qs122[200] = {0}; // max lenght = 200
    strcpy(Qs122, "Qs122=1;");
    sprintf(tmpchn, "%d", (int)(-MSFS_POS.pitch * 1000));
    strcat(Qs122, strcat(tmpchn, ";"));
    sprintf(tmpchn, "%d", (int)(MSFS_POS.bank * 1000));
    strcat(Qs122, strcat(tmpchn, ";"));
    sprintf(tmpchn, "%d", (int)(MSFS_POS.heading_true * 1000));
    strcat(Qs122, strcat(tmpchn, ";"));
    sprintf(tmpchn, "%d", (int)MSFS_POS.ground_altitude);
    strcat(Qs122, strcat(tmpchn, ";"));
    sprintf(tmpchn, "%d", (int)MSFS_POS.VS);
    strcat(Qs122, strcat(tmpchn, ";"));
    sprintf(tmpchn, "%d", (int)MSFS_POS.TAS);
    strcat(Qs122, strcat(tmpchn, ";0;"));
    sprintf(tmpchn, "%.15lf", MSFS_POS.latitude);
    strcat(Qs122, strcat(tmpchn, ";"));
    sprintf(tmpchn, "%.15lf", MSFS_POS.longitude);
    strcat(Qs122, strcat(tmpchn, ";"));
    sprintf(tmpchn, "%d", (int)(MSFS_POS.ground_altitude * 10));
    strcat(Qs122, tmpchn);
    if (MSFS_POS_avail) {
        sendQPSX(Qs122);
    }
    MSFS_POS_avail = 0;
}

void CALLBACK ReadPositionFromMSFS(SIMCONNECT_RECV *pData, DWORD cbData, void *pContext) {

    (void)(cbData);
    (void)(&pContext);

    switch (pData->dwID) {

    case SIMCONNECT_RECV_ID_OPEN: {

        SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_FREEZE_ALT, 1,
                                       SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
        SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_FREEZE_ATT, 1,
                                       SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
        SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_FREEZE_LAT_LONG, 1,
                                       SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
    } break;

    case SIMCONNECT_RECV_ID_EVENT: {
        SIMCONNECT_RECV_EVENT *evt = (SIMCONNECT_RECV_EVENT *)pData;

        switch (evt->uEventID) {
        case EVENT_SIM_START: {
        } break;

        case EVENT_ONE_SEC: {

            key_press = 0;
        } break;

        case EVENT_6_HZ: {
            if (SLAVE) {

                SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_FREEZE_ALT, 0,
                                               SIMCONNECT_GROUP_PRIORITY_HIGHEST,
                                               SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
                SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_FREEZE_ATT, 0,
                                               SIMCONNECT_GROUP_PRIORITY_HIGHEST,
                                               SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
                SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_FREEZE_LAT_LONG, 0,
                                               SIMCONNECT_GROUP_PRIORITY_HIGHEST,
                                               SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
                Inject_MSFS_PSX();
            }
        } break;

        case EVENT_4_SEC: {
            /*
             * TCAS injection every 4 seconds but only if TCAS switch is on
             */
            if (TCAS_INJECT) {
                IA_update();
            }

        } break;

        case EVENT_FREEZE_ALT: {

        } break;

        case EVENT_P_PRESS: {
            if (!key_press) {
                SLAVE = !SLAVE;
                key_press = 1;

                if (!SLAVE) {
                    printf("Injecting position to MSFS from PSX\n");
                } else {
                    printf("Injection position to PSX from MSFS\n");
                }
            }
        } break;

        case EVENT_QUIT: {
            quit = 1;

        } break;

        default:
            printf("Default Event\n");
        }
        break;
    }

    case SIMCONNECT_RECV_ID_SIMOBJECT_DATA: {
        SIMCONNECT_RECV_SIMOBJECT_DATA *pObjData = (SIMCONNECT_RECV_SIMOBJECT_DATA *)pData;

        switch (pObjData->dwRequestID) {

        case MSFS_CLIENT_DATA: {
            Struct_MSFS *pS = (Struct_MSFS *)&pObjData->dwData;
            MSFS_POS_avail = 1;
            MSFS_POS.ground_altitude = pS->ground_altitude;
            MSFS_POS.alt_above_ground = pS->alt_above_ground;
            MSFS_POS.alt_above_ground_minus_CG = pS->alt_above_ground_minus_CG;
            MSFS_POS.pitch = pS->pitch;
            MSFS_POS.bank = pS->bank;
            MSFS_POS.heading_true = pS->heading_true;
            MSFS_POS.VS = pS->VS;
            MSFS_POS.TAS = pS->TAS;
            MSFS_POS.altitude = pS->altitude;

            MSFS_POS.latitude = pS->latitude;
            MSFS_POS.longitude = pS->longitude;

            MSFS_plane_alt = pS->alt_above_ground;
            CG_height = pS->alt_above_ground_minus_CG;
            ground_elev = pS->ground_altitude;
            ground_elev_avail = (ground_elev != 0);
        } break;

        default:
            break;
        }
        break;
    }
    case SIMCONNECT_RECV_ID_SIMOBJECT_DATA_BYTYPE: {
        SIMCONNECT_RECV_SIMOBJECT_DATA_BYTYPE *pObjData = (SIMCONNECT_RECV_SIMOBJECT_DATA_BYTYPE *)pData;

        switch (pObjData->dwRequestID) {
        case DATA_REQUEST_TCAS: {
            AI_TCAS *ai = (AI_TCAS *)&pObjData->dwData;
            double d;
            char tmpchn[128] = {0};
            char QsTfcPos[999] = {0}; // max lenght = 999

            if (pObjData->dwentrynumber > 1) {
                d = dist(ai->latitude, Tboost.latitude, ai->longitude, Tboost.longitude) / NM;
                if ((d < 40) &&                                   // less than 40 NM away
                    abs(ai->altitude - Tboost.altitude) < 2700 && // below or above 2700 feet
                    (!(Tboost.onGround == 2) ||
                     ((Tboost.onGround == 2) &&
                      abs(ai->altitude - Tboost.altitude) > 500))) { // onground dont show acft below 500 above us

                    update_TCAS(ai, d);
                }

                /*
                 * We have scanned all the planes in the vicinity{
                 */
                if (pObjData->dwentrynumber == pObjData->dwoutof) {
                    strcpy(QsTfcPos, "Qs450=");
                    for (int i = 0; i < 7; i++) {
                        sprintf(tmpchn, "%lf", tcas_acft[i].latitude);
                        strcat(strcat(QsTfcPos, tmpchn), ";");
                        sprintf(tmpchn, "%lf", tcas_acft[i].longitude);
                        strcat(strcat(QsTfcPos, tmpchn), ";");
                        sprintf(tmpchn, "%d", tcas_acft[i].altitude);
                        strcat(strcat(QsTfcPos, tmpchn), ";");
                        sprintf(tmpchn, "%d", tcas_acft[i].heading);
                        strcat(strcat(QsTfcPos, tmpchn), ";");
                    }

                    /* and now we can send the string to PSX */
                    sendQPSX("Qi201=1");
                    sendQPSX(QsTfcPos);
                }
            }

        } break;
        }
        break;
    }

    case SIMCONNECT_RECV_ID_QUIT: {

        printf("\nSIMCONNECT_RECV_ID_QUIT received and data sent");
    } break;

    default:
        break;
    }
}

void update_TCAS(AI_TCAS *ai, double d) {

    if (d <= min_dist || nb_acft < 7) { // we found a closer aircraft or less than 7 aircrafts
        for (int i = 6; i > 0; i--) {
            tcas_acft[i].latitude = tcas_acft[i - 1].latitude;
            tcas_acft[i].longitude = tcas_acft[i - 1].longitude;
            tcas_acft[i].altitude = tcas_acft[i - 1].altitude;
            tcas_acft[i].heading = tcas_acft[i - 1].heading;
            tcas_acft[i].distance = tcas_acft[i - 1].distance;
            min_dist = MAX(d, tcas_acft[i].distance);
        }
        tcas_acft[0].latitude = ai->latitude;
        tcas_acft[0].longitude = ai->longitude;
        tcas_acft[0].altitude = (int)(ai->altitude * 10);
        tcas_acft[0].heading = (int)(ai->heading * 100);
        tcas_acft[0].distance = d;
        nb_acft++;
    }
    if (d < min_dist) {
        min_dist = d;
    }

    return;
}

int init_MS_data(void) {

    /* Here we map all the variables that are used to update the 747 in MSFS.
     * It is VERY important that the order of those variables matches the order in with the structure AcftPosition is
     * defined in PSXMSFS.h
     */

    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "PLANE ALTITUDE", "feet");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "PLANE LATITUDE", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "PLANE LONGITUDE", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "PLANE HEADING DEGREES TRUE", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "PLANE PITCH DEGREES", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "PLANE BANK DEGREES", "radians");

    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "AIRSPEED TRUE", "knot");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "AIRSPEED INDICATED", "knot");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "VERTICAL SPEED", "feet per minute");

    /*Surfaces attributes*/

    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "GEAR HANDLE POSITION", "percent over 100");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "FLAPS HANDLE INDEX", "number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "SPOILERS HANDLE POSITION", "position");

    /*
     * Data definition for lights. Even though in the SDK documentation they are defined as non settable,
     *  Setting them like this works just fine. Alternative is to use EVENTS, but in that case all 4 landing
     *  light switches cannot be synchronised.
     */

    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "LIGHT LANDING:1", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "LIGHT LANDING:2", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "LIGHT LANDING:3", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "LIGHT LANDING:4", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "LIGHT TAXI:1", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "LIGHT TAXI:2", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "LIGHT TAXI:3", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "LIGHT NAV", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "LIGHT STROBE", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "LIGHT BEACON:1", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "LIGHT BEACON:2", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "LIGHT WING", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "LIGHT LOGO", "Number");

    /*
     * Moving Surfaces: Ailerons, rudder , elevator
     *
     */

    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "RUDDER POSITION", "position 16K");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "ELEVATOR POSITION", "position 16K");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "AILERON POSITION", "position 16K");

    /* This is to get the ground altitude when positionning the aircraft at initialization or once on ground */
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "GROUND ALTITUDE", "feet");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE ALT ABOVE GROUND", "feet");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE ALT ABOVE GROUND MINUS CG", "feet");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE LATITUDE", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE LONGITUDE", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE PITCH DEGREES", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE BANK DEGREES", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE HEADING DEGREES TRUE", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "VERTICAL SPEED", "feet per second");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "AIRSPEED TRUE", "knots");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE ALTITUDE", "feet");

    hr = SimConnect_RequestDataOnSimObject(hSimConnect, DATA_REQUEST, MSFS_CLIENT_DATA, SIMCONNECT_OBJECT_ID_USER,
                                           SIMCONNECT_PERIOD_SECOND);

    /*
     * This is the data that will be fetched from the aircraft in vicinity of PSX
     * And will be used in the PSX TCAS
     */

    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_TCAS_TRAFFIC, "PLANE ALTITUDE", "feet");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_TCAS_TRAFFIC, "PLANE LATITUDE", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_TCAS_TRAFFIC, "PLANE LONGITUDE", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_TCAS_TRAFFIC, "PLANE HEADING DEGREES MAGNETIC", "radians");

    hr = SimConnect_RequestDataOnSimObject(hSimConnect, DATA_REQUEST_TCAS, DATA_TCAS_TRAFFIC, SIMCONNECT_OBJECT_ID_USER,
                                           SIMCONNECT_PERIOD_SECOND);

    // Request a simulation start event

    hr = SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_SIM_START, "SimStart");
    hr = SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_4_SEC, "4sec");
    hr = SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_ONE_SEC, "1sec");
    hr = SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_6_HZ, "6Hz");

    /* Mapping Events to the client*/

    /*Events used to freeze the internal MSFS engine and allow injection of positionning
     * from PSX
     */

    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_FREEZE_ALT, "FREEZE_ALTITUDE_SET");
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_FREEZE_ATT, "FREEZE_ATTITUDE_SET");
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_FREEZE_LAT_LONG, "FREEZE_LATITUDE_LONGITUDE_SET");

    /*
     * EVENTS used to set the time
     */

    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_ZULU_DAY, "ZULU_DAY_SET");
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_ZULU_HOURS, "ZULU_HOURS_SET");
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_ZULU_MINUTES, "ZULU_MINUTES_SET");
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_ZULU_YEAR, "ZULU_YEAR_SET");

    /*
     * EVENT used to set the parking break
     */
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_PARKING, "PARKING_BRAKE_SET");

    /*
     * EVENT used for steering wheel
     */
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_STEERING, "STEERING_SET");

    /*
     * EVENT used for XPDR
     */
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_XPDR, "XPNDR_SET");
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_XPDR_IDENT, "XPNDR_IDENT_SET");

    /*
     * EVENT used for COMM & stdy COMM
     */
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_COM, "COM_RADIO_SET_HZ");
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_COM_STDBY, "COM_STBY_RADIO_SET_HZ");

    /*
     * EVENT Barometer settings
     *
     */

    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_BARO, "KOHLSMAN_SET");
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_BARO_STD, "BAROMETRIC");

    /* Custom EVENTS
     *
     * Here pressing the P or Q key in MSFS
     * Note: the name of the event shall have a "."
     *
     */

    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_P_PRESS, "My.CTRLP");
    hr = SimConnect_MapInputEventToClientEvent(hSimConnect, INPUT_P_PRESS, "p", EVENT_P_PRESS);
    hr = SimConnect_AddClientEventToNotificationGroup(hSimConnect, GROUP0, EVENT_P_PRESS);

    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_QUIT, "My.CTRLQ");
    hr = SimConnect_MapInputEventToClientEvent(hSimConnect, INPUT_QUIT, "q", EVENT_QUIT);
    hr = SimConnect_AddClientEventToNotificationGroup(hSimConnect, GROUP0, EVENT_QUIT);

    return hr;
}

void *ptDatafromMSFS(void *thread_param) {
    (void)(&thread_param);
    while (!quit) {
        hr = SimConnect_CallDispatch(hSimConnect, ReadPositionFromMSFS, NULL);
        // sleep(1);
    }
    return NULL;
}

void *ptUmainboost(void *thread_param) {
    (void)(&thread_param);

    while (!quit) {
        if (umainBoost(&Tboost)) {

            if (!SLAVE) {
                SetMSFSPos();
            }
        }
    }

    return NULL;
}

void *ptUmain(void *thread_param) {
    (void)(&thread_param);

    while (!quit) {
        if (umain(&Tmain)) {
            if (!SLAVE) {
                SetMSFSPos();
            }
        }
    }

    return NULL;
}

void init_pos() {

    // Setting the aircraft at LFPG gate
    AcftPosition APos;

    /*
     * Setting initial position at LFPG"
     */
    APos.altitude = 360.5; // 358 + 15.6;
    APos.latitude = 49.0012;
    APos.longitude = 2.57728;
    APos.heading_true = 356.0;
    APos.pitch = -1.36;
    APos.bank = 0.0;
    APos.tas = 0.0;
    APos.ias = 0.0;
    APos.vertical_speed = 0.0;
    APos.FlapsPosition = 0.0; // Flaps down
    APos.Speedbrake = 0.0;    // Spoilers down
    APos.GearDown = 1.0;

    // All lights off
    APos.LandLeftOutboard = 0.0;
    APos.LandLeftInboard = 0.0;
    APos.LandRightInboard = 0.0;
    APos.LandRightOutboard = 0.0;
    APos.LeftRwyTurnoff = 0.0;
    APos.RightRwyTurnoff = 0.0;
    APos.LightTaxi = 0.0;
    APos.Strobe = 0.0;
    APos.LightNav = 0.0;
    APos.Beacon = 0.0;
    APos.BeaconLwr = 0.0;
    APos.LightWing = 0.0;
    APos.LightLogo = 0.0;

    // All surfaces centered (ailerons, rudder, elevator

    APos.rudder = 0.0;
    APos.ailerons = 0.0;
    APos.elevator = 0.0;

    if (SimConnect_SetDataOnSimObject(hSimConnect, DATA_PSX_TO_MSFS, SIMCONNECT_OBJECT_ID_USER, 0, 0, sizeof(APos),
                                      &APos) != S_OK) {
        err_n_die("Could not update position");
    };
}

void SetMSFSPos(void) {

    pthread_mutex_lock(&mutex);
    AcftPosition APos;
    double lat, longi;
    HRESULT hr = 0;

    if (Tboost.onGround == 2) {
        APos.altitude = ground_elev + 15.13; // magic number to have the default 747-8 from MSFS touch the gound
        APos.GearDown = 1.0;
        // send to PSX the ground_elev
        //
        if (ground_elev_avail) {
            char tmpchn[128] = {0};
            char sQi198[128] = "Qi198=";
            if (!Qi198Sent) {
                Qi198Sent = 1;              // No need to resend this variable
                sendQPSX("Qi198=-9999920"); // Allow (9999xx) seconds with no crash, no inertia
            }
            sprintf(tmpchn, "%d", (int)(ground_elev * 100));
            strcat(sQi198, tmpchn);
            sendQPSX(sQi198);
            ground_elev_avail = 0;
        }
    } else {

        //        APos.altitude = Tboost.altitude;

        /*
         * Boost servers gives altitude of flight deck
         */

        APos.altitude = Tboost.altitude - (28.412073 + 92.5 * sin(Tboost.pitch));
        APos.GearDown = ((Tmain.GearLever == 3) ? 1.0 : 0.0);
        sendQPSX("Qi198=-9999999"); // if airborne, use PSX elevation data
        Qi198Sent = 0;
    }

    // Calculate coordinates from centre aircraft;
    CalcCoord(Tboost.heading_true + M_PI, 92.5, Tboost.latitude, Tboost.longitude, &lat, &longi);

    APos.latitude = lat;
    APos.longitude = longi;
    APos.heading_true = Tboost.heading_true;
    APos.pitch = -Tboost.pitch;
    APos.bank = Tboost.bank;
    APos.tas = Tmain.TAS;
    APos.ias = Tmain.IAS;
    APos.vertical_speed = Tmain.VerticalSpeed;
    APos.FlapsPosition = Tmain.FlapLever;
    APos.Speedbrake = Tmain.SpdBrkLever / 800.0;

    // Update lights
    APos.LandLeftOutboard = Tmain.light[0];
    APos.LandLeftInboard = Tmain.light[2];
    APos.LandRightInboard = Tmain.light[3];
    APos.LandRightOutboard = Tmain.light[1];
    APos.LeftRwyTurnoff = Tmain.light[4];
    APos.RightRwyTurnoff = Tmain.light[5];
    APos.LightTaxi = Tmain.light[6];
    APos.Strobe = Tmain.light[11];
    APos.LightNav = Tmain.light[9] || Tmain.light[10];
    APos.Beacon = Tmain.light[7];
    APos.BeaconLwr = Tmain.light[8];
    APos.LightWing = Tmain.light[12];
    APos.LightLogo = Tmain.light[13];

    // Taxi lights disabled airborne
    if (Tboost.onGround != 2) {
        APos.LeftRwyTurnoff = 0.0;
        APos.RightRwyTurnoff = 0.0;
    }

    // Set the UTC time
    SetUTCTime();

    // Set the XPDR and COMMS
    SetCOMM();

    // Set the altimeter
    SetBARO();

    /*
     * Set the moving surfaces: aileron, rudder, elevator
     */

    APos.rudder = Tmain.rudder;
    APos.ailerons = Tmain.aileron;
    APos.elevator = Tmain.elevator;

    /*
     * finally update everything
     */

    hr = SimConnect_SetDataOnSimObject(hSimConnect, DATA_PSX_TO_MSFS, SIMCONNECT_OBJECT_ID_USER, 0, 0, sizeof(APos),
                                       &APos);
    if (hr < 0) {

        time_t result = time(NULL);
        fprintf(fdebug, "On:%s", asctime(gmtime(&result)));
        fprintf(fdebug, "ERROR in SimConnect_SetDataOnSimObject\n");
        fprintf(fdebug, "\tTrying to reinitialize the connection to Simconnect.....\n");
        fprintf(fdebug, "\tClosing faulty connection.....\n");
        SimConnect_Close(hSimConnect);

        /*
         * First start by clearing the data definition, in case we call this function after an error
         */

        hr = SimConnect_ClearDataDefinition(hSimConnect, DATA_PSX_TO_MSFS);
        hr = SimConnect_ClearDataDefinition(hSimConnect, DATA_TCAS_TRAFFIC);
        hr = SimConnect_ClearDataDefinition(hSimConnect, MSFS_CLIENT_DATA);
        fprintf(fdebug, "\tOpening new connection.....\n");

        init_connect_MSFS(&hSimConnect);
        if (init_MS_data() < 0) {
            fprintf(fdebug, "\tUnable to reinitilize....Sorry folks, quitting now\n");
            fflush(NULL);
            quit = 1;
        };
    }

    /*
     * PArking break and Steering wheel are updated via events ant not via the APos structure
     */

    SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_PARKING, Tmain.parkbreak,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
    SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_STEERING, Tmain.steering,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
    pthread_mutex_unlock(&mutex);
}

void usage() {

    printf("usage: [-h] [-v] [-m IP [-p port]] [-b IP [-c port]]\n");
    printf("\t -h, --help");
    printf("\t Prints this help\n");
    printf("\t -v");
    printf("\t verbose. Prints out debug into on console and in file DEBUG.TXT. Warning: can be very verbose\n");
    printf("\t -m");
    printf("\t Main server IP. Default is 127.0.0.1\n");
    printf("\t -p");
    printf("\t Main server port. Default is 10747\n");
    printf("\t -b");
    printf("\t Boost server IP. Default is main server IP [127.0.0.1] \n");
    printf("\t -c");
    printf("\t Boost server port. Default is 10749\n");
    printf("\t -t");
    printf("\t Disables TCAS injection from MSFS to PSX\n");
    printf("\t -s");
    printf("\t Starts with PSX enslaved to MSFS\n");

    exit(-1);
}

int main(int argc, char **argv) {
    pthread_t t1, t2, t3;

    int c;

    strcpy(PSXMainServer, "127.0.0.1");
    PSXPort = 10747;
    PSXBoostPort = 10749;
    while (1) {
        static struct option long_options[] = {/* These options set a flag. */
                                               {"verbose", no_argument, &DEBUG, 1},
                                               /* These options don’t set a flag.
                                                  We distinguish them by their indices. */
                                               {"boost", required_argument, 0, 'b'},
                                               {"help", no_argument, 0, 'h'},
                                               {"main", required_argument, 0, 'm'},
                                               {"boost-port", required_argument, 0, 'c'},
                                               {"main-port", required_argument, 0, 'p'},
                                               {"slave", required_argument, 0, 's'},
                                               {0, 0, 0, 0}};
        /* getopt_long stores the option index here. */
        int option_index = 0;

        c = getopt_long(argc, argv, "thvsm:b:c:p:f:", long_options, &option_index);

        /* Detect the end of the options. */
        if (c == -1)
            break;

        switch (c) {
        case 0:
            /* If this option set a flag, do nothing else now. */
            if (long_options[option_index].flag != 0)
                break;
            printf("option %s", long_options[option_index].name);
            if (optarg)
                printf(" with arg %s", optarg);
            printf("\n");
            break;

        case 'b':
            strcpy(PSXBoostServer, optarg);
            break;
        case 't':
            TCAS_INJECT = 0;
            break;
        case 'h':
            usage();
            break;
        case 'm':
            strcpy(PSXMainServer, optarg);
            break;
        case 'c':
            PSXBoostPort = (int)strtol(optarg, NULL, 10);
            break;
        case 'p':
            PSXPort = (int)strtol(optarg, NULL, 10);
            break;
        case 'v':
            DEBUG = 1;
            break;
        case 's':
            SLAVE = 1;
            break;

        case '?':
            /* getopt_long already printed an error message. */
            usage();
            break;

        default:
            abort();
        }
    }

    /* Print any remaining command line arguments (not options). */
    if (optind < argc) {
        // printf("non-option ARGV-elements: ");
        while (optind < argc)
            optind++;
    }

    if (strcmp(PSXBoostServer, "0.0.0.0") == 0) {
        strcpy(PSXBoostServer, PSXMainServer);
    }
    /*
     * open debug file
     */
    fdebug = fopen("DEBUG.TXT", "w");
    if (!fdebug) {
        err_n_die("Error creating debug file...");
        exit(-1);
    }
    /*
     * Initialise and connect to all sockets: PSX, PSX Boost and Simconnect
     */
    open_connections();

    // initialize the data to be received as well as all EVENTS
    init_MS_data();

    // set a default location for the plane
    // Here at LFPG stand E22 if PSX is not enslaved to MSFS
    if (!SLAVE) {
        init_pos();
    }

    /*
     * Sending Q423 DEMAND variable to PSX for the winds
     * Sending Q480 DEMAND variable to get aileron, rudder and elevator position
     */

    sendQPSX("demand=Qs483");
    sendQPSX("demand=Qs480");

    /*
     * Create a thread mutex so that two threads cannot change simulataneously the
     * position of the aircraft
     */

    pthread_mutex_init(&mutex, NULL);

    /*
     * Creating the 3 threads:
     * Thread 1: main server PSX
     * Thread 2: boost server
     * Thread 3: callback function in MSFS
     */

    if (pthread_create(&t1, NULL, &ptUmain, &Tmain) != 0) {
        err_n_die("Error creating thread Umain");
    }

    if (pthread_create(&t2, NULL, &ptUmainboost, &Tboost) != 0) {
        err_n_die("Error creating thread Umainboost");
    }

    if (pthread_create(&t3, NULL, &ptDatafromMSFS, NULL) != 0) {
        err_n_die("Error creating thread DatafromMSFS");
    }

    pthread_join(t1, NULL);
    pthread_join(t2, NULL);
    pthread_join(t3, NULL);
    pthread_mutex_destroy(&mutex);

    printf("Closing MSFS connection...\n");
    SimConnect_Close(hSimConnect);

    // and gracefully close main + boost sockets
    printf("Closing PSX boost connection...\n");
    if (close_PSX_socket(sPSXBOOST)) {
        printf("Could not close boost PSX socket...\n");
    }
    printf("Closing PSX main connection...\n");
    if (close_PSX_socket(sPSX)) {
        printf("Could not close main PSX socket...\n");
    }

    // Finally clean up the Win32 sockets
    WSACleanup();

    // and close the debug file
    fclose(fdebug);

    printf("Normal exit. See you soon...\n");
    return 0;
}
