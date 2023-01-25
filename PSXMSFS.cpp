#include <assert.h>
#include <cstddef>
#include <cstdint>
#include <cstdlib>
#include <cstring>
#include <ctime>
#include <getopt.h>
#include <math.h>
#include <pthread.h>
#include <stdio.h>
#include <time.h>
#include <unistd.h>
#include <windows.h>
#include <imagehlp.h>
#include "PSXMSFS.h"
#include "util.h"
#include "SimConnect.h"

int quit = 0;
DWORD dwLastID;

PSXTIME PSXtime;

AcftMSFS APos;
struct PSXINST PSXDATA;

// indicates whether there is a data of ground elevation received from MSFS in
// the callback procedure
double MSFS_plane_alt, CG_height;
int ground_altitude_avail = 0;
int MSFS_on_ground = 0;
int PSX_on_ground = 1;
int MSFS_POS_avail = 0;
double latMSFS, longMSFS;
int key_press = 0;

float ground_altitude = 0;
int Qi198SentLand = 0;
int Qi198SentAirborne = 0;
struct Struct_MSFS MSFS_POS;

Target Tmain, Tboost;
pthread_mutex_t mutex;
int UTCupdate = 1;
int validtime = 0;
HRESULT hr;
int DEBUG;
int TCAS_INJECT = 1; /*TCAS injection on by default*/
int SLAVE = 0;       // 0=PSX is master, 1=MSFS is master
char debugInfo[256] = {0};
FILE *fdebug;

/*
 * Global variables used for TCAS updating
 */

TCAS tcas_acft[7];
double min_dist = 999999;
static int nb_acft = 0;

char PSXMainServer[] = "999.999.999.999";
char MSFSServer[] = "999.999.999.999";
char PSXBoostServer[] = "999.999.999.999";
int PSXPort = 10747;
int PSXBoostPort = 10749;

void update_TCAS(AI_TCAS *ai, double d);

void SetUTCTime(struct PSXTIME *P) {

    SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_ZULU_HOURS, P->hour,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
    SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_ZULU_MINUTES, P->minute,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
    SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_ZULU_DAY, P->day,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
    SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_ZULU_YEAR, P->year,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
}

void SetXPDR(void) {

    SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_XPDR, PSXDATA.XPDR,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
    SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_XPDR_IDENT, PSXDATA.IDENT,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
}

void SetCOMM(void) {

    SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_COM, PSXDATA.COM1,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
    SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_COM_STDBY, PSXDATA.COM2,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
}
void SetBARO(void) {

    SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_BARO, PSXDATA.altimeter * 16.0,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
    if (PSXDATA.STD) {
        SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_BARO_STD, 1,
                                       SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
    }
}

void IA_update() {

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
    sprintf(tmpchn, "%d", (int)(MSFS_POS.altitude - MSFSHEIGHT));
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

void CALLBACK SimmConnectProcess(SIMCONNECT_RECV *pData, DWORD cbData, void *pContext) {
    (void)(cbData);
    (void)(pContext);

    switch (pData->dwID) {

    case SIMCONNECT_RECV_ID_EXCEPTION: {
        SIMCONNECT_RECV_EXCEPTION *evt = (SIMCONNECT_RECV_EXCEPTION *)pData;
        snprintf(debugInfo, sizeof(debugInfo), "Exception risen: %ld, by sender: %ld at index: %ld, dwLastID: %ld",
                 evt->dwException, evt->dwSendID, evt->dwIndex, dwLastID);
        printDebug(debugInfo, 1);
        printDebug("RECV_ID_EXCEPTION", CONSOLE);
    } break;

    case SIMCONNECT_RECV_ID_OPEN: {

        /* Structure received containing some info about the version
         * of MSFS and Simconnect.
         * Just usefull for debugging and info purposes
         */

        SIMCONNECT_RECV_OPEN *evt = (SIMCONNECT_RECV_OPEN *)pData;

        sprintf(debugInfo, "MSFS %ld.%ld (build %ld.%ld) Simconnect %ld.%ld (build %ld.%ld)\n",
                evt->dwApplicationVersionMajor, evt->dwApplicationVersionMinor, evt->dwApplicationBuildMajor,
                evt->dwApplicationBuildMinor, evt->dwSimConnectVersionMajor, evt->dwSimConnectVersionMinor,
                evt->dwSimConnectBuildMajor, evt->dwSimConnectBuildMinor);

        printDebug(debugInfo, CONSOLE);

        SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_FREEZE_ALT, 0,
                                       SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
        SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_FREEZE_ATT, 0,
                                       SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
        SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_FREEZE_LAT_LONG, 0,
                                       SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
    } break;

    case SIMCONNECT_RECV_ID_EVENT: {
        SIMCONNECT_RECV_EVENT *evt = (SIMCONNECT_RECV_EVENT *)pData;

        switch (evt->uEventID) {

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
                if (SUCCEEDED(SimConnect_RequestDataOnSimObjectType(hSimConnect, DATA_REQUEST_TCAS, TCAS_TRAFFIC_DATA,
                                                                    40 * NM, SIMCONNECT_SIMOBJECT_TYPE_AIRCRAFT))) {
                    IA_update();
                }
            }

        } break;

        case EVENT_P_PRESS: {
            printDebug("EVET_P_PRESS", CONSOLE);
            if (!key_press) {
                SLAVE = !SLAVE;
                key_press = 1;
                if (DEBUG) {
                    if (!SLAVE) {
                        sendQPSX("Qs419=");
                        printf("Injecting position to MSFS from PSX\n");
                    } else {
                        sendQPSX("Qs419=>MSFS MASTER");
                        printf("Injecting position to PSX from MSFS\n");
                    }
                }
            }
        } break;

        case EVENT_QUIT: {
            printDebug("EVENT_QUIT", CONSOLE);
            quit = 1;

        } break;

        default:
            printf("Default Event\n");
        }
    } break;

    case SIMCONNECT_RECV_ID_SIMOBJECT_DATA: {
        SIMCONNECT_RECV_SIMOBJECT_DATA *pObjData = (SIMCONNECT_RECV_SIMOBJECT_DATA *)pData;

        switch (pObjData->dwRequestID) {

        case MSFS_CLIENT_DATA: {

            Struct_MSFS *pS = (Struct_MSFS *)&pObjData->dwData;
            MSFS_POS_avail = 1;
            MSFS_POS.ground_altitude = pS->ground_altitude;
            MSFS_POS.alt_above_ground = pS->alt_above_ground;
            MSFS_POS.alt_above_ground_minus_CG = pS->alt_above_ground_minus_CG;
            MSFS_on_ground = (MSFS_POS.alt_above_ground_minus_CG < 1);
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
            ground_altitude = pS->ground_altitude;
            ground_altitude_avail = 1;
        } break;
        }
    } break;
    case SIMCONNECT_RECV_ID_SIMOBJECT_DATA_BYTYPE: {
        SIMCONNECT_RECV_SIMOBJECT_DATA_BYTYPE *pObjData = (SIMCONNECT_RECV_SIMOBJECT_DATA_BYTYPE *)pData;
        switch (pObjData->dwRequestID) {
        case DATA_REQUEST_TCAS: {
            AI_TCAS *ai = (AI_TCAS *)&pObjData->dwData;
            double d, lat, lon, alt;
            char tmpchn[128] = {0};
            char QsTfcPos[999] = {0}; // max lenght = 999

            lat = MSFS_POS.latitude;
            lon = MSFS_POS.longitude;
            alt = MSFS_POS.altitude;

            if (pObjData->dwentrynumber > 1) {
                d = dist(ai->latitude, lat, ai->longitude, lon) / NM;
                //   if (d < 40) {                             // show only aircraft less than 40NM away from us
                if (abs(ai->altitude - alt) < 7000) { // show only aircraft 2700 above or below us
                    if (Tboost.onGround == 2) {
                        if (abs(ai->altitude - alt) < 500) { // on the ground only update if 500 above
                                                             // us
                            update_TCAS(ai, d);
                            // printf("Acft[%ld/%ld]:\tai.lat:%lf\t ai.long:%lf\tlat:%lf\tlon:%lf\tAlt:%lf\t",
                            //        pObjData->dwentrynumber, pObjData->dwoutof, ai->latitude, ai->longitude, lat, lon,
                            //        alt);
                            // printf("Distance: %lf\n", d);

                        } else {
                            //  printf("Acft[%ld/%ld]:\tai.lat:%lf\t ai.long:%lf\tlat:%lf\tlon:%lf\tAlt:%lf\t",
                            //         pObjData->dwentrynumber, pObjData->dwoutof, ai->latitude, ai->longitude, lat,
                            //         lon, alt);
                            //  printf("Distance: %lf\n", d);
                            update_TCAS(ai, d);
                        }
                    }
                }
                // }
            }

            /*
             * We have scanned all the planes in the vicinity{
             */
            if (pObjData->dwentrynumber == pObjData->dwoutof) {
                strcpy(QsTfcPos, "Qs450=");
                for (int i = 0; i < 7; i++) {
                    if (i < nb_acft) {
                        sprintf(tmpchn, "%lf", tcas_acft[i].latitude);
                        strcat(strcat(QsTfcPos, tmpchn), ";");
                        sprintf(tmpchn, "%lf", tcas_acft[i].longitude);
                        strcat(strcat(QsTfcPos, tmpchn), ";");
                        sprintf(tmpchn, "%d", tcas_acft[i].altitude);
                        strcat(strcat(QsTfcPos, tmpchn), ";");
                        sprintf(tmpchn, "%d", tcas_acft[i].heading);
                        strcat(strcat(QsTfcPos, tmpchn), ";");
                    } else {
                        strcat(QsTfcPos, "0;0;0;0;");
                    }
                }

                /* and now we can send the string to PSX */
                sendQPSX("Qi201=1");
                sendQPSX(QsTfcPos);
            }
        } break;
        }
    } break;
    case SIMCONNECT_RECV_ID_QUIT: {
        printDebug("RECV_ID_QUIT", CONSOLE);
        quit = 1;
        printDebug("MSFS was exited. I guess I should do the same...", 1);
    } break;

    case SIMCONNECT_RECV_ID_EVENT_FRAME: {
        pthread_mutex_lock(&mutex);
        hr =
            SimConnect_SetDataOnSimObject(hSimConnect, DATA_MSFS, SIMCONNECT_OBJECT_ID_USER, 0, 0, sizeof(APos), &APos);
        pthread_mutex_unlock(&mutex);

    }

    break;
    default:
        sprintf(debugInfo, "In Callbackfunction default case: nothing was done. Event: %ld\n", pData->dwID);
        printDebug(debugInfo, 1);
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
        tcas_acft[0].heading = (int)(ai->heading / M_PI * 180 * 100);
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
     * It is VERY important that the order of those variables matches the order
     * in with the structure AcftMSFS is defined in PSXMSFS.h
     */

    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_MSFS, "PLANE ALTITUDE", "feet");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_MSFS, "PLANE LATITUDE", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_MSFS, "PLANE LONGITUDE", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_MSFS, "PLANE HEADING DEGREES TRUE", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_MSFS, "PLANE PITCH DEGREES", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_MSFS, "PLANE BANK DEGREES", "radians");

    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_MSFS, "AIRSPEED TRUE", "knot");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_MSFS, "AIRSPEED INDICATED", "knot");

    /*
     * Moving Surfaces: Ailerons, rudder , elevator
     *
     */
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_MSFS, "GEAR HANDLE POSITION", "percent over 100");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_MSFS, "FLAPS HANDLE INDEX", "number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_MSFS, "SPOILERS HANDLE POSITION", "position");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_MSFS, "RUDDER POSITION", "position 16K");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_MSFS, "ELEVATOR POSITION", "position 16K");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_MSFS, "AILERON POSITION", "position 16K");

    /*
     * Data definition for lights. Even though in the SDK documentation they are
     * defined as non settable, Setting them like this works just fine.
     * Alternative is to use EVENTS, but in that case all 4 landing light
     * switches cannot be synchronised.
     */

    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_MSFS, "LIGHT LANDING:1", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_MSFS, "LIGHT LANDING:2", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_MSFS, "LIGHT LANDING:3", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_MSFS, "LIGHT LANDING:4", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_MSFS, "LIGHT TAXI:1", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_MSFS, "LIGHT TAXI:2", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_MSFS, "LIGHT TAXI:3", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_MSFS, "LIGHT NAV", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_MSFS, "LIGHT STROBE", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_MSFS, "LIGHT BEACON:1", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_MSFS, "LIGHT BEACON:2", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_MSFS, "LIGHT WING", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_MSFS, "LIGHT LOGO", "Number");

    /* This is to get the ground altitude when positionning the aircraft at
     * initialization or once on ground */
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "GROUND ALTITUDE", "feet");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE ALT ABOVE GROUND", "feet");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE ALT ABOVE GROUND MINUS CG", "feet");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE LATITUDE", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE LONGITUDE", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE PITCH DEGREES", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE BANK DEGREES", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE HEADING DEGREES TRUE", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "AIRSPEED TRUE", "knots");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE ALTITUDE", "feet");

    hr = SimConnect_RequestDataOnSimObject(hSimConnect, DATA_REQUEST, MSFS_CLIENT_DATA, SIMCONNECT_OBJECT_ID_USER,
                                           SIMCONNECT_PERIOD_SECOND);

    // Request a simulation start event

    hr = SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_SIM_START, "SimStart");
    hr = SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_4_SEC, "4sec");
    hr = SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_ONE_SEC, "1sec");
    hr = SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_6_HZ, "6Hz");
    hr = SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_FRAME, "Frame");

    /* Mapping Events to the client*/

    /*Events used to freeze the internal MSFS engine and allow injection of
     * positionning from PSX
     */

    /*
     * EVENTS used to set the time
     */

    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_ZULU_DAY, "ZULU_DAY_SET");
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_ZULU_HOURS, "ZULU_HOURS_SET");
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_ZULU_MINUTES, "ZULU_MINUTES_SET");
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_ZULU_YEAR, "ZULU_YEAR_SET");

    /* Eventys used to freeze altitude, longitude, latitude and attitude*/

    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_FREEZE_ALT, "FREEZE_ALTITUDE_SET");
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_FREEZE_ATT, "FREEZE_ATTITUDE_SET");
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_FREEZE_LAT_LONG, "FREEZE_LATITUDE_LONGITUDE_SET");

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

    /*
     * TCAS EVENT INITIALIZATION
     */

    /*
     * This is the data that will be fetched from the aircraft in vicinity of
     * PSX And will be used in the PSX TCAS
     */

    hr = SimConnect_AddToDataDefinition(hSimConnect, TCAS_TRAFFIC_DATA, "PLANE ALTITUDE", "feet");
    hr = SimConnect_AddToDataDefinition(hSimConnect, TCAS_TRAFFIC_DATA, "PLANE LATITUDE", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, TCAS_TRAFFIC_DATA, "PLANE LONGITUDE", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, TCAS_TRAFFIC_DATA, "PLANE HEADING DEGREES MAGNETIC", "radians");
    return hr;
}

void *ptDatafromMSFS(void *thread_param) {
    (void)(&thread_param);
    while (!quit) {
        int hr = SimConnect_CallDispatch(hSimConnect, SimmConnectProcess, NULL);
        if (hr == E_FAIL) {
            printf("Error: %ld\r", (long)elapsedMs(TimeStart) / 1000);
            printf("Sleeping 5 seconds and trying to reinitialize everything\n");
            sleep(5);

            SimConnect_Close(hSimConnect);

            /*
             * First start by clearing the data definition, in case we call this
             * function after an error
             */

            hr = SimConnect_ClearDataDefinition(hSimConnect, TCAS_TRAFFIC_DATA);
            hr = SimConnect_ClearDataDefinition(hSimConnect, MSFS_CLIENT_DATA);
            fprintf(fdebug, "\tOpening new connection.....\n");

            init_connect_MSFS(&hSimConnect);
            if (init_MS_data() < 0) {
                fprintf(fdebug, "\tUnable to reinitilize....Sorry folks, quitting now\n");
                fflush(NULL);
                quit = 1;
            }
        }

        Sleep(15); // We sleep for 15 ms (Sleep is a Win32 API with parameter in ms) to avoid heavy polling
    }
    return NULL;
}

void *ptUmainboost(void *) {
    while (!quit) {
        umainBoost(&Tboost);
    }
    return NULL;
}

void *ptUmain(void *) {

    while (!quit) {
        umain(&Tmain);
    }
    return NULL;
}

double SetAltitude(int onGround) {

    double FinalAltitude;
    double ctrAltitude; // altitude of Aircraft centre
                        //
    char sQi198[128];

    /*
     * Before touching landing of after take off
     * switch from MSFS elevation to PSX elevation
     * and vice versa
     */

    /*
     * Boost servers gives altitude of flight deck
     */
    ctrAltitude = APos.altitude - (28.412073 + 92.5 * sin(-APos.pitch));
    /*
     * Calculate the altitude if PSX is on the ground
     * or in flight
     */

    if (ground_altitude_avail) {
        if (onGround || (ctrAltitude - ground_altitude < 300)) {
            if (!Qi198SentLand) {
                printDebug("Below 300 ft AGL => using MSFS elevation", CONSOLE);
                Qi198SentLand = 1;
            }
            Qi198SentAirborne = 0;
            sprintf(sQi198, "Qi198=%d", (int)(ground_altitude * 100));
            sendQPSX(sQi198);
        } else {

            if (!Qi198SentAirborne) {

                printDebug("Above 300 ft AGL => using PSX elevation.", CONSOLE);
                sendQPSX("Qi198=-999999"); // if airborne, use PSX elevation data
                Qi198SentAirborne = 1;
            }
            Qi198SentLand = 0;
        }
    } else {

        Qi198SentLand = 0;
        Qi198SentAirborne = 0;
    }
    // printf("onground :%d\t Apos: %.4f\tground: %.4f\t MSFS: %.4f\t ctrAlt:
    // %.4f\n",onGround,APos.altitude,MSFS_POS.ground_altitude, MSFSHEIGHT, ctrAltitude);
    //   assert(onGround);
    if (onGround) {
        FinalAltitude = ground_altitude + MSFSHEIGHT;
    } else {
        if (ctrAltitude > PSXDATA.TA)
            
            FinalAltitude = pressure_altitude(PSXDATA.QNH[PSXDATA.weather_zone]) + ctrAltitude;
        else
            FinalAltitude = ctrAltitude;
    }

    return FinalAltitude;
}

void init_pos(PSXTIME *P) {

    /*
     * Setting initial position at LFPG"
     */

    APos.rudder = 0.0;
    APos.ailerons = 0.0;
    APos.elevator = 0.0;
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

    P->year = 2022;
    P->day = 1;
    P->hour = 12;
    P->minute = 0;
}

void SetMSFSPos(void) {

    APos.altitude = SetAltitude(PSX_on_ground);

    //  Update lights
    APos.LandLeftOutboard = light[0];
    APos.LandLeftInboard = light[2];
    APos.LandRightInboard = light[3];
    APos.LandRightOutboard = light[1];
    APos.LeftRwyTurnoff = light[4];
    APos.RightRwyTurnoff = light[5];
    APos.LightTaxi = light[6];
    APos.Strobe = light[11];
    APos.LightNav = light[9] || light[10];
    APos.Beacon = light[7];
    APos.BeaconLwr = light[8];
    APos.LightWing = light[12];
    APos.LightLogo = light[13];
    // Taxi lights disabled airborne
    if (PSX_on_ground) {
        APos.LeftRwyTurnoff = 0.0;
        APos.RightRwyTurnoff = 0.0;
    }

    /*
     * Set the moving surfaces: aileron, rudder, elevator
     */

    APos.FlapsPosition = Tmain.FlapLever;
    APos.Speedbrake = Tmain.SpdBrkLever / 800.0;

    APos.rudder = Tmain.rudder;
    APos.ailerons = Tmain.aileron;
    APos.elevator = Tmain.elevator;
}

void usage() {

    printf("usage: [-h] [-v] [-m IP [-p port]] [-b IP [-c port]]\n");
    printf("\t -h, --help");
    printf("\t Prints this help\n");
    printf("\t -d");
    printf("\t debug. Prints out debug info on console and in file "
           "DEBUG.TXT. Warning: can be very verbose\n");
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

    exit(EXIT_SUCCESS);
}

int write_ini_file() {
    FILE *f;

    f = fopen("PSXMSFS.ini", "w");
    if (!f) {
        printf("Cannot create PSXMSFS.ini file. Aborting...\n");
        fclose(f);
        return -1;
    }

    /*PSX server addresses and port*/
    fprintf(f, "PSXMainServer=%s\n", PSXMainServer);
    fprintf(f, "PSXBoostServer=%s\n", PSXBoostServer);
    fprintf(f, "PSXPort=%d\n", PSXPort);
    fprintf(f, "PSXBoostPort=%d\n", PSXBoostPort);

    /*MSFS address*/
    fprintf(f, "MSFSServer=%s\n", MSFSServer);

    /* Switches */
    fprintf(f, "DEBUG=%d\n", DEBUG);
    fprintf(f, "TCAS_INJECT=%d\n", TCAS_INJECT);
    fprintf(f, "SLAVE=%d\n", SLAVE);

    fclose(f);
    return 0;
}

char *scan_ini(FILE *file, const char *key) {

    char name[64];
    char val[64];
    rewind(file);
    while (fscanf(file, "%63[^=]=%63[^\n]%*c", name, val) == 2) {
        if (0 == strcmp(name, key)) {
            return strdup(val);
        }
    }
    return NULL;
}

int init_param() {
    FILE *fini;
    char *value;
    char *stop;

    /* Sensible default values*/
    strcpy(PSXMainServer, "127.0.0.1");
    strcpy(PSXBoostServer, "127.0.0.1");
    strcpy(MSFSServer, "127.0.0.1");
    PSXPort = 10747;
    PSXBoostPort = 10749;
    SLAVE = 0;
    DEBUG = 0;
    TCAS_INJECT = 1;

    fini = fopen("PSXMSFS.ini", "r");
    if (!fini) {
        printf("Cannot open config file.\nTrying to create one with educated "
               "guesses...\n");
        write_ini_file();
    } else {
        strcpy(PSXMainServer, scan_ini(fini, "PSXMainServer"));
        strcpy(PSXBoostServer, scan_ini(fini, "PSXBoostServer"));
        strcpy(MSFSServer, scan_ini(fini, "MSFSServer"));

        value = scan_ini(fini, "SLAVE");
        SLAVE = strtol(value, &stop, 10);
        value = scan_ini(fini, "TCAS_INJECT");
        TCAS_INJECT = strtol(value, &stop, 10);
        value = scan_ini(fini, "DEBUG");
        DEBUG = strtol(value, &stop, 10);
        free(value);
    }

    return 1;
}

void parse_arguments(int argc, char **argv) {

    int c;
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
        case 'd':
            DEBUG = 1;
            break;
        case 's':
            SLAVE = 1;
            break;

        case '?':
            /* getopt_long already printDebug an error message. */
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
}

int main(int argc, char **argv) {
    pthread_t t1, t2, t3;
    PSXTIME P;

    /* Initialise the timer */
    elapsedStart(&TimeStart);

    /* Read from .ini file the various values
     * used in the program
     */
    if (!init_param()) {
        printf("Could not initialize default parameters... Quitting\n");
        exit(EXIT_FAILURE);
    }
    /* Override those values by arguments taken
     * from command line
     */
    parse_arguments(argc, argv);

    /*
     * open debug file
     */
    if (DEBUG) {
        fdebug = fopen("DEBUG.TXT", "w");
        if (!fdebug) {
            printf("Error creating debug file...\n");
            exit(EXIT_FAILURE);
        }
    }
    /*
     * Initialise and connect to all sockets: PSX, PSX Boost and Simconnect
     */
    if (!open_connections()) {
        printDebug("Could not initialize all connections. Exiting...", 1);
        exit(EXIT_FAILURE);
    }

    // initialize the data to be received as well as all EVENTS
    init_MS_data();

    /*
     * Sending Q423 DEMAND variable to PSX for the winds
     * Sending Q480 DEMAND variable to get aileron, rudder and elevator position
     */

    sendQPSX("demand=Qs483");
    sendQPSX("demand=Qs480");

    /*
     * Initializing position of the plane
     * as boost and main threads are not yet available
     */

    printDebug("Initializing position", CONSOLE);
    init_pos(&P);

    /*
     * Create a thread mutex so that two threads cannot change simulataneously
     * the position of the aircraft
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

    if (pthread_create(&t2, NULL, &ptUmainboost, NULL) != 0) {
        err_n_die("Error creating thread Umainboost");
    }

    if (pthread_create(&t3, NULL, &ptDatafromMSFS, NULL) != 0) {
        err_n_die("Error creating thread DatafromMSFS");
    }
    if (pthread_join(t1, NULL) != 0) {
        printDebug("Failed to join Main thread", 1);
    }
    if (pthread_join(t2, NULL) != 0) {
        printDebug("Failed to join Boost thread", 1);
    }
    if (pthread_join(t3, NULL) != 0) {
        printDebug("Failed to join MSFS thread", 1);
    }
    pthread_mutex_destroy(&mutex);

    printf("Closing MSFS connection...\n");
    SimConnect_Close(hSimConnect);

    // Signaling PSX that we are quitting
    sendQPSX("exit");

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

    /* and clean up the debug file
     * deleting it if not in DEBUG mode
     */
    fclose(fdebug);
    if (!DEBUG)
        remove("DEBUG.TXT");

    printf("Normal exit. See you soon...\n");
    return 0;
}
