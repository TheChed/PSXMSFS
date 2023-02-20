#include <assert.h>
#include <cstddef>
#include <cstdint>
#include <cstdlib>
#include <cstring>
#include <ctime>
// #include <imagehlp.h>
#include <math.h>
#include <pthread.h>
#include <stdio.h>
#include <time.h>
#include <unistd.h>
#include <windows.h>
#include "SimConnect.h"
#include "PSXMSFS.h"
#include "util.h"

int quit = 0;
DWORD dwLastID;

PSXTIME PSXtime;

AcftMSFS APos;
struct PSXINST PSXDATA;
struct PSXBOOST PSXBoost;
struct TATL PSXTATL;

// indicates whether there is a data of ground elevation received from MSFS in
// the callback procedure
double MSFS_plane_alt, CG_height;
int ground_altitude_avail = 0;
int MSFS_on_ground = 0;
int PSX_on_ground = 0;
int MSFS_POS_avail = 0;
int elevupdated = 0;
static int landing, takingoff;

int alt_freezed = 1;

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
int ELEV_INJECT = 1; /*elevation injection on by default below 300 ft AGL */
int INHIB_CRASH_DETECT = 0;
int SLAVE = 0; // 0=PSX is master, 1=MSFS is master
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

void init_variables(void) {

    ground_altitude_avail = 0;
    MSFS_on_ground = 0;
    PSX_on_ground = 0;
    MSFS_POS_avail = 0;
    elevupdated = 0;
    landing = 0;
    takingoff = 0;

    SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_FREEZE_ALT, 1,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
    SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_FREEZE_ATT, 1,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
    SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_FREEZE_LAT_LONG, 1,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
}

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
        printDebug("RECV_ID_EXCEPTION", DEBUG);
    } break;

    case SIMCONNECT_RECV_ID_OPEN: {

        /* Structure received containing some info about the version
         * of MSFS and Simconnect.
         * Just usefull for debugging and info purposes
         */

        SIMCONNECT_RECV_OPEN *evt = (SIMCONNECT_RECV_OPEN *)pData;

        sprintf(debugInfo, "MSFS %ld.%ld (build %ld.%ld) Simconnect %ld.%ld (build %ld.%ld)",
                evt->dwApplicationVersionMajor, evt->dwApplicationVersionMinor, evt->dwApplicationBuildMajor,
                evt->dwApplicationBuildMinor, evt->dwSimConnectVersionMajor, evt->dwSimConnectVersionMinor,
                evt->dwSimConnectBuildMajor, evt->dwSimConnectBuildMinor);

        printDebug(debugInfo, DEBUG);

        /*
         * In this event, received when MSFS is opened
         * we freeze the altitude, attitude and coordinates
         * so that there is no stuttering and conflict with MSFS's
         * engines.
         */

        printDebug("Freezing Altitude, Attitude and Coordinates in MSFS.", DEBUG);
        alt_freezed = 1;
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
                SimConnect_RequestDataOnSimObjectType(hSimConnect, DATA_REQUEST_TCAS, TCAS_TRAFFIC_DATA, 40 * NM,
                                                      SIMCONNECT_SIMOBJECT_TYPE_AIRCRAFT);
                IA_update();
            }

        } break;

        case EVENT_P_PRESS: {
            if (!key_press) {
                key_press = 1;
                SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_FREEZE_ALT_TOGGLE, 0,
                                               SIMCONNECT_GROUP_PRIORITY_HIGHEST,
                                               SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
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
            quit = 1;

        } break;

        default:
            printDebug("Event not captured", DEBUG);
        }
    } break;

    case SIMCONNECT_RECV_ID_SIMOBJECT_DATA: {
        SIMCONNECT_RECV_SIMOBJECT_DATA *pObjData = (SIMCONNECT_RECV_SIMOBJECT_DATA *)pData;

        switch (pObjData->dwRequestID) {

        case DATA_REQUEST: {

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
            break;
        }

        case DATA_REQUEST_FREEZE: {
            break;
        }

        default:
            sprintf(debugInfo, "Did not process request ID: %lu\n", pObjData->dwRequestID);
            printDebug(debugInfo, 1);
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
                //   if (d < 40) {                             // show only aircraft
                //   less than 40NM away from us
                if (abs(ai->altitude - alt) < 7000) { // show only aircraft 2700 above or below us
                    if (PSX_on_ground) {
                            update_TCAS(ai, d);
                    }
                }
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
        quit = 1;
        printDebug("MSFS was exited. I guess I should do the same...", 1);
    } break;

    case SIMCONNECT_RECV_ID_EVENT_FRAME: {
        pthread_mutex_lock(&mutex);

        /*
         * Only update the position if we have enough info
         */
        if (ground_altitude_avail) {

            SetMSFSPos();
            SimConnect_SetDataOnSimObject(hSimConnect, DATA_MSFS, SIMCONNECT_OBJECT_ID_USER, 0, 0, sizeof(APos), &APos);
        }

        pthread_mutex_unlock(&mutex);

    }

    break;
    default:
        sprintf(debugInfo, "In Callbackfunction default case: nothing was done. Event: %ld", pData->dwID);
        printDebug(debugInfo, DEBUG);
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
     * in with the structures defined in PSXMSFS.h
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
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "VERTICAL SPEED", "feet per minute");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "AIRSPEED TRUE", "knots");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE ALTITUDE", "feet");

    hr = SimConnect_RequestDataOnSimObject(hSimConnect, DATA_REQUEST, MSFS_CLIENT_DATA, SIMCONNECT_OBJECT_ID_USER,
                                           SIMCONNECT_PERIOD_VISUAL_FRAME);

    /*
     * This is to get have a state of MSFS being freezed or nothing
     * On severla instances the freeze flag was not properly set and
     * we need to make sure it is  before injecting altitude, attitude
     * and coordinates to MSFS.
     */
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_FREEZE, "IS ALTITUDE FREEZE ON", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_FREEZE, "IS ATTITUDE FREEZE ON", "Boolean");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_FREEZE, "IS LATITUDE LONGITUDE FREEZE ON", "Boolean");
    hr = SimConnect_RequestDataOnSimObject(hSimConnect, DATA_REQUEST_FREEZE, MSFS_FREEZE, SIMCONNECT_OBJECT_ID_USER,
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
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_FREEZE_ALT_TOGGLE, "FREEZE_ALTITUDE_TOGGLE");
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_FREEZE_ATT, "FREEZE_ATTITUDE_SET");
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_FREEZE_ATT_TOGGLE, "FREEZE_ATTITUDE_TOGGLE");
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_FREEZE_LAT_LONG, "FREEZE_LATITUDE_LONGITUDE_SET");
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_FREEZE_LAT_LONG_TOGGLE,
                                             "FREEZE_LATITUDE_LONGITUDE_TOGGLE");

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
        SimConnect_CallDispatch(hSimConnect, SimmConnectProcess, NULL);

        Sleep(1); // We sleep for 1 ms (Sleep is a Win32 API with parameter in ms)
                  // to avoid heavy polling
    }
    return NULL;
}

void *ptUmainboost(void *) {
    while (!quit) {
        if (!umainBoost(&Tboost))
            pthread_exit(NULL);
    }
    return NULL;
}

void *ptUmain(void *) {

    while (!quit) {
        umain(&Tmain);
    }
    return NULL;
}

double SetAltitude(int onGround, double altfltdeck, double pitch, double PSXELEV, double groundalt) {

    double FinalAltitude;
    double ctrAltitude;   // altitude of Aircraft centre
    static double oldctr; // to keep track of last good altitude
    static double delta = 0;
    static double inc = 0;
    static int initalt = 0;
    static double incland = 0;

    char sQi198[128];

    /*
     * PSXELEV = -999 when we just launched PSXMSFS
     */

    if (PSXELEV == -999) {
        return altfltdeck;
    }

    /*
     * Boost servers gives altitude of flight deck
     * Need to get the altitude of the Aircraft centre first
     */
    ctrAltitude = altfltdeck - (28.412073 + 92.5 * sin(pitch));

    /*
     * Now check if we are close to the ground or not
     * by checking the Variable Qi219 from PSX
     * that give the acft height above ground
     * We assume that below 50 feet we are close
     */
    landing = (PSXELEV < 50);

    if (initalt) {
        delta = ctrAltitude - oldctr;
    }
    initalt = 1;
    oldctr = ctrAltitude;
    inc += delta;

    /*
     * we received a new elevation from PSX
     * therefore we can reset the decrement (incland)
     */

    if (elevupdated) {
        incland = 0;
        elevupdated = 0;
    } else {
        incland += delta;
    }

    if (ELEV_INJECT) {
        if (onGround || (PSXELEV < 300)) {
            if (!Qi198SentLand) {
                printDebug("Below 300 ft AGL => using MSFS elevation", DEBUG);
                Qi198SentLand = 1;
            }
            Qi198SentAirborne = 0;
            sprintf(sQi198, "Qi198=%d", (int)(ground_altitude * 100));
            sendQPSX(sQi198);
        } else {

            if (!Qi198SentAirborne) {

                printDebug("Above 300 ft AGL => using PSX elevation.", DEBUG);
                sendQPSX("Qi198=-999999"); // if airborne, use PSX elevation data
                Qi198SentAirborne = 1;
            }
            Qi198SentLand = 0;
        }
    }

    /*
     * by default the altitude is the ctrAltitude given by boost
     * But we will adjust that depeding on the flight phase
     * to cater for MSFS discrepencies with PSX
     */

    FinalAltitude = ctrAltitude;

    /*
     * If we are crusing, return the pressure altitude to have it correcly
     * displayed in VATSIM or IVAO
     */

    if ((PSXTATL.phase == 0 && ctrAltitude > PSXTATL.TA) || (PSXTATL.phase == 2 && ctrAltitude > PSXTATL.TL) ||
        PSXTATL.phase == 1) {

        FinalAltitude = pressure_altitude(PSXDATA.QNH[PSXDATA.weather_zone]) + ctrAltitude;
        takingoff = 0;
        landing = 1; // only choice now is to land !
        return FinalAltitude;
    }

    if (onGround || (PSXELEV + incland < MSFSHEIGHT)) {
        FinalAltitude = groundalt + MSFSHEIGHT;
        inc = 0;
        landing = 0;
        takingoff = 1; // what else can we do except to take off ?

    } else {
        if (takingoff && inc < 300) {
            if (ground_altitude_avail) {
                FinalAltitude = groundalt + MSFSHEIGHT + inc;
            }
        } else {
            if (landing) {
                FinalAltitude = groundalt + PSXELEV + incland;
            }
        }
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
    PSXTATL.TA = 2000;
    PSXTATL.TL = 18000;
    PSXDATA = {.XPDR = 0000,
               .IDENT = 0,
               .COM1 = 122800,
               .COM2 = 122800,
               .altimeter = 1035,
               .STD = 0,
               .IAS = 0,
               .GS = 0,
               .TAS = 0,
               .weather_zone = 0,
               .QNH = {2992},
               .acftelev = -999};
}

void SetMSFSPos(void) {

    double latc, longc;

    /*
     * Calculate the coordinates from cetre aircraft
     * derived from those of the flightDeckAlt
     */

    APos.altitude =
        SetAltitude(PSX_on_ground, PSXBoost.flightDeckAlt, -PSXBoost.pitch, PSXDATA.acftelev, ground_altitude);

    CalcCoord(PSXBoost.heading_true, PSXBoost.latitude, PSXBoost.longitude, &latc, &longc);
    APos.latitude = latc;
    APos.longitude = longc;
    APos.pitch = PSXBoost.pitch;
    APos.bank = PSXBoost.bank;
    APos.heading_true = PSXBoost.heading_true;

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

    APos.ias = PSXDATA.IAS;
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
        printDebug("Could not initialize default parameters... Quitting",1);
        exit(EXIT_FAILURE);
    }

    /* 
     * check command line arguments
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

    printDebug("Initializing position", DEBUG);
    init_pos(&P);
    printDebug("Initializing done", DEBUG);

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
        printDebug("Error creating thread Umain",1);
        quit=1;
    }

    if (pthread_create(&t2, NULL, &ptUmainboost, NULL) != 0) {
        printDebug("Error creating thread Umainboost",1);
        quit=1;
    }

    if (pthread_create(&t3, NULL, &ptDatafromMSFS, NULL) != 0) {
        printDebug("Error creating thread DatafromMSFS",1);
        quit=1;
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
