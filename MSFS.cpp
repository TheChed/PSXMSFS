#include <cmath>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <windows.h>
#include "PSXMSFSLIB.h"
#include "SimConnect.h"
#include "util.h"
#include "update.h"
#include "MSFS.h"
#include "log.h"

struct {
    int altitude;
    double latitude;
    double longitude;
    int heading;
    double distance;
} tcas_acft[7];

/*
 * Structure of AI traffic present in MSFS
 */

struct AI_TCAS {
    double altitude;
    double latitude;
    double longitude;
    double heading;
};

static struct Struct_MSFS MSFS_POS;
static double ground_altitude;
static double MSL_pressure;
static double MSL_temperature;
static double MSFS_baro;
static double MSFS_indicated_altitude;
static int MSFS_POS_avail = 0;
static int key_press = 0;
static int nb_acft = 0;
static double min_dist = 999999;

double getGroundAltitude(void)
{
    return ground_altitude;
}
double getMSL_pressure(void)
{
    return MSL_pressure;
}
double getMSL_temperature(void)
{
    return MSL_temperature;
}
double getMSFS_baro(void)
{
    return MSFS_baro;
}
double getIndAltitude(void)
{
    if (MSFS_POS_avail) {
        return MSFS_indicated_altitude;
    } else {
        return -1;
    }
}
int isGroundAltitudeAvailable(void)
{
    return (ground_altitude != -9999 ? 1 : 0);
}
void Inject_MSFS_PSX(void)
{
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
void update_TCAS(const struct AI_TCAS *ai, double d)
{

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
void IA_update()
{

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
void updateTCASPSX(void)
{
    char tmpchn[128] = {0};
    char QsTfcPos[999] = {0}; // max lenght = 999
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

void freezeMSFS(int freeze)
{

    if (freeze) {
        printDebug(LL_VERBOSE, "Freezing Altitude, Attitude and Coordinates in MSFS.");
    } else {

        printDebug(LL_VERBOSE, "Unfreezing Altitude, Attitude and Coordinates in MSFS.");
    }
    SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_FREEZE_ALT, freeze,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST,
                                   SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
    SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_FREEZE_ATT, freeze,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST,
                                   SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
    SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_FREEZE_LAT_LONG, freeze,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST,
                                   SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
}

void init_variables(void)
{

    ground_altitude = -9999;
    MSFS_POS_avail = 0;
}

void CALLBACK SimmConnectProcess(SIMCONNECT_RECV *pData, DWORD cbData, void *pContext)
{
    UNUSED(cbData);
    UNUSED(pContext);

    switch (pData->dwID) {

    case SIMCONNECT_RECV_ID_EXCEPTION: {
        SIMCONNECT_RECV_EXCEPTION *evt = (SIMCONNECT_RECV_EXCEPTION *)pData;
        printDebug(LL_ERROR,
                   "Exception risen: %ld, by sender: %ld at index: %ld",
                   evt->dwException, evt->dwSendID, evt->dwIndex);
    } break;

    case SIMCONNECT_RECV_ID_OPEN: {

        /*---------------------------------------------
         * Structure received containing some info about
         * the version of MSFS and Simconnect.
         * Just usefull for debugging and info purposes
         *-----------------------------------------------*/

        SIMCONNECT_RECV_OPEN *evt = (SIMCONNECT_RECV_OPEN *)pData;

        printDebug(LL_INFO, "MSFS %ld.%ld (build %ld.%ld) Simconnect %ld.%ld (build %ld.%ld)",
                   evt->dwApplicationVersionMajor, evt->dwApplicationVersionMinor,
                   evt->dwApplicationBuildMajor, evt->dwApplicationBuildMinor,
                   evt->dwSimConnectVersionMajor, evt->dwSimConnectVersionMinor,
                   evt->dwSimConnectBuildMajor, evt->dwSimConnectBuildMinor);

        /*
         * In this event, received when MSFS is opened
         * we freeze the altitude, attitude and coordinates
         * so that there is no stuttering and conflict with MSFS's
         * rendering engines.
         */

        freezeMSFS(1);
    } break;

    case SIMCONNECT_RECV_ID_EVENT: {
        SIMCONNECT_RECV_EVENT *evt = (SIMCONNECT_RECV_EVENT *)pData;

        switch (evt->uEventID) {

        case EVENT_ONE_SEC: {
            key_press = 0;
        } break;

        case EVENT_6_HZ: {
            if (PSXflags.SLAVE) {

                freezeMSFS(0);
                Inject_MSFS_PSX();
            }
        } break;

        case EVENT_4_SEC: {
            /*
             * TCAS injection every 4 seconds but only if TCAS switch is on
             */
            if (PSXflags.TCAS_INJECT) {
                SimConnect_RequestDataOnSimObjectType(hSimConnect, DATA_REQUEST_TCAS,
                                                      TCAS_TRAFFIC_DATA, 40 * NM,
                                                      SIMCONNECT_SIMOBJECT_TYPE_AIRCRAFT);
                IA_update();
            }

        } break;

        case EVENT_P_PRESS: {
            if (!key_press) {
                key_press = 1;
                SimConnect_TransmitClientEvent(
                    hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_FREEZE_ALT_TOGGLE, 0,
                    SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
                if (LL_ERROR) {
                    if (!PSXflags.SLAVE) {
                        sendQPSX("Qs419=");
                        printDebug(LL_INFO, "Injecting position to MSFS from PSX\n");
                    } else {
                        sendQPSX("Qs419=>MSFS MASTER");
                        printDebug(LL_INFO, "Injecting position to PSX from MSFS\n");
                    }
                }
            }
        } break;

        case EVENT_QUIT: {
            quit = 1;

        } break;

        default:
            printDebug(LL_ERROR, "Event %i not captured", evt->uEventID);
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
            MSFS_POS.indicated_altitude = pS->indicated_altitude;
            MSFS_POS.pitch = pS->pitch;
            MSFS_POS.bank = pS->bank;
            MSFS_POS.heading_true = pS->heading_true;
            MSFS_POS.VS = pS->VS;
            MSFS_POS.TAS = pS->TAS;
            MSFS_POS.altitude = pS->altitude;

            MSFS_POS.latitude = pS->latitude;
            MSFS_POS.longitude = pS->longitude;
            MSFS_POS.mmHg = pS->mmHg;
            MSFS_POS.MSL = pS->MSL;
            MSFS_POS.baro = pS->baro;
            MSFS_baro = pS->baro;
            ground_altitude = pS->ground_altitude;
            MSL_pressure = pS->MSL;
            MSFS_indicated_altitude = MSFS_POS.indicated_altitude;
            MSL_temperature = pS->temperature;
            break;
        }

        case DATA_REQUEST_FREEZE: {
            break;
        }

        default:
            printDebug(LL_VERBOSE, "Did not process request ID: %lu\n", pObjData->dwRequestID);
        }

    } break;

    case SIMCONNECT_RECV_ID_SIMOBJECT_DATA_BYTYPE: {
        SIMCONNECT_RECV_SIMOBJECT_DATA_BYTYPE *pObjData =
            (SIMCONNECT_RECV_SIMOBJECT_DATA_BYTYPE *)pData;

        switch (pObjData->dwRequestID) {

        case DATA_REQUEST_TCAS: {

            AI_TCAS *ai = (AI_TCAS *)&pObjData->dwData;
            double d, lat, lon, alt;

            lat = MSFS_POS.latitude;
            lon = MSFS_POS.longitude;
            alt = MSFS_POS.altitude;
            if (pObjData->dwentrynumber > 1) {
                d = dist(ai->latitude, lat, ai->longitude, lon) / NM;
                if (abs(ai->altitude - alt) < 7000.0f) { // show only aircraft 2700 above or below us
                    update_TCAS(ai, d);
                }
            }

            /*
             * We have scanned all the planes in the vicinit
             * it is time to update PSX
             */
            if (pObjData->dwentrynumber == pObjData->dwoutof) {
                updateTCASPSX();
            }
        } break;
        }
    } break;
    case SIMCONNECT_RECV_ID_QUIT: {
        quit = 1;
        printDebug(LL_VERBOSE, "MSFS closed. I guess I should do the same...");
    } break;

    case SIMCONNECT_RECV_ID_EVENT_FRAME: {

        // while (intflags.updateNewSitu) { }
        if (!intflags.updateNewSitu) {
            WaitForSingleObject(mutex, INFINITE);
            SetMSFSPos();
            ReleaseMutex(mutex);
        }
    }

    break;
    default:
        printDebug(LL_VERBOSE, "In Callback function default case: nothing was done. Event: %ld",
                   pData->dwID);
        break;
    }
}

void init_MS_data(void)
{

    /* Here we map all the variables that are used to update the 747 in MSFS.
     * It is VERY important that the order of those variables matches the order
     * in with the structures defined in PSXMSFSLIB.h
     */
    SimConnect_AddToDataDefinition(hSimConnect, BOOST_TO_MSFS_ALT, "PLANE ALTITUDE", "feet");
    SimConnect_AddToDataDefinition(hSimConnect, BOOST_TO_MSFS, "PLANE ALTITUDE", "feet");
    SimConnect_AddToDataDefinition(hSimConnect, BOOST_TO_MSFS_STD_ALT, "INDICATED ALTITUDE", "feet");
    SimConnect_AddToDataDefinition(hSimConnect, BOOST_TO_MSFS, "PLANE LATITUDE", "radians");
    SimConnect_AddToDataDefinition(hSimConnect, BOOST_TO_MSFS, "PLANE LONGITUDE", "radians");
    SimConnect_AddToDataDefinition(hSimConnect, BOOST_TO_MSFS, "PLANE HEADING DEGREES TRUE",
                                   "radians");
    SimConnect_AddToDataDefinition(hSimConnect, BOOST_TO_MSFS, "PLANE PITCH DEGREES",
                                   "radians");
    SimConnect_AddToDataDefinition(hSimConnect, BOOST_TO_MSFS, "PLANE BANK DEGREES", "radians");

    /*
     * Moving Surfaces: Ailerons, rudder , elevator
     *
     */
    SimConnect_AddToDataDefinition(hSimConnect, DATA_MOVING_SURFACES, "GEAR HANDLE POSITION",
                                   "percent over 100");
    SimConnect_AddToDataDefinition(hSimConnect, DATA_MOVING_SURFACES, "FLAPS HANDLE INDEX",
                                   "number");
    SimConnect_AddToDataDefinition(hSimConnect, DATA_MOVING_SURFACES,
                                   "SPOILERS HANDLE POSITION", "position 16K");
    SimConnect_AddToDataDefinition(hSimConnect, DATA_MOVING_SURFACES, "RUDDER POSITION",
                                   "position 16K");
    SimConnect_AddToDataDefinition(hSimConnect, DATA_MOVING_SURFACES, "ELEVATOR POSITION",
                                   "position 16K");
    SimConnect_AddToDataDefinition(hSimConnect, DATA_MOVING_SURFACES, "AILERON POSITION",
                                   "position 16K");

    /*
     * Data definition for lights. Even though in the SDK documentation they are
     * defined as non settable, Setting them like this works just fine.
     * Alternative is to use EVENTS, but in that case all 4 landing light
     * switches cannot be synchronised.
     */

    SimConnect_AddToDataDefinition(hSimConnect, DATA_LIGHT, "LIGHT LANDING:1", "Number");
    SimConnect_AddToDataDefinition(hSimConnect, DATA_LIGHT, "LIGHT LANDING:2", "Number");
    SimConnect_AddToDataDefinition(hSimConnect, DATA_LIGHT, "LIGHT LANDING:3", "Number");
    SimConnect_AddToDataDefinition(hSimConnect, DATA_LIGHT, "LIGHT LANDING:4", "Number");
    SimConnect_AddToDataDefinition(hSimConnect, DATA_LIGHT, "LIGHT TAXI:1", "Number");
    SimConnect_AddToDataDefinition(hSimConnect, DATA_LIGHT, "LIGHT TAXI:2", "Number");
    SimConnect_AddToDataDefinition(hSimConnect, DATA_LIGHT, "LIGHT TAXI:3", "Number");
    SimConnect_AddToDataDefinition(hSimConnect, DATA_LIGHT, "LIGHT NAV", "Number");
    SimConnect_AddToDataDefinition(hSimConnect, DATA_LIGHT, "LIGHT STROBE", "Number");
    SimConnect_AddToDataDefinition(hSimConnect, DATA_LIGHT, "LIGHT BEACON:1", "Number");
    SimConnect_AddToDataDefinition(hSimConnect, DATA_LIGHT, "LIGHT BEACON:2", "Number");
    SimConnect_AddToDataDefinition(hSimConnect, DATA_LIGHT, "LIGHT WING", "Number");
    SimConnect_AddToDataDefinition(hSimConnect, DATA_LIGHT, "LIGHT LOGO", "Number");

    /* This is to get the ground altitude when positionning the aircraft at
     * initialization or once on ground */
    SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "GROUND ALTITUDE", "feet");
    SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE ALT ABOVE GROUND",
                                   "feet");
    SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA,
                                   "PLANE ALT ABOVE GROUND MINUS CG", "feet");
    SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "INDICATED ALTITUDE", "feet");
    SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE LATITUDE", "radians");

    SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE LONGITUDE", "radians");
    SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE PITCH DEGREES",
                                   "radians");
    SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE BANK DEGREES",
                                   "radians");
    SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE HEADING DEGREES TRUE",
                                   "radians");
    SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "VERTICAL SPEED",
                                   "feet per minute");
    SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "AIRSPEED TRUE", "knots");
    SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE ALTITUDE", "feet");
    SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "AMBIENT PRESSURE", "mmhg");
    SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "SEA LEVEL PRESSURE", "hectopascal");
    SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "BAROMETER PRESSURE", "hectopascal");
    SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "AMBIENT TEMPERATURE", "celsius");

    /*
     * Definition to store various speeds
     */
    SimConnect_AddToDataDefinition(hSimConnect, DATA_SPEED, "AIRSPEED INDICATED", "knots");
    SimConnect_AddToDataDefinition(hSimConnect, DATA_SPEED, "AIRSPEED TRUE", "knots");
    SimConnect_AddToDataDefinition(hSimConnect, DATA_SPEED, "VERTICAL SPEED", "feet per minute");

    SimConnect_RequestDataOnSimObject(hSimConnect, DATA_REQUEST, MSFS_CLIENT_DATA,
                                      SIMCONNECT_OBJECT_ID_USER,
                                      SIMCONNECT_PERIOD_VISUAL_FRAME);

    // Request a simulation start event

    SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_SIM_START, "SimStart");
    SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_4_SEC, "4sec");
    SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_ONE_SEC, "1sec");
    SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_6_HZ, "6Hz");
    SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_FRAME, "Frame");

    /* Mapping Events to the client*/

    /*Events used to freeze the internal MSFS engine and allow injection of
     * positionning from PSX
     */

    /*
     * EVENTS used to set the time
     */

    SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_ZULU_DAY, "ZULU_DAY_SET");
    SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_ZULU_HOURS, "ZULU_HOURS_SET");
    SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_ZULU_MINUTES, "ZULU_MINUTES_SET");
    SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_ZULU_YEAR, "ZULU_YEAR_SET");

    /* Eventys used to freeze altitude, longitude, latitude and attitude*/

    SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_FREEZE_ALT, "FREEZE_ALTITUDE_SET");
    SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_FREEZE_ALT_TOGGLE,
                                        "FREEZE_ALTITUDE_TOGGLE");
    SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_FREEZE_ATT, "FREEZE_ATTITUDE_SET");
    SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_FREEZE_ATT_TOGGLE,
                                        "FREEZE_ATTITUDE_TOGGLE");
    SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_FREEZE_LAT_LONG,
                                        "FREEZE_LATITUDE_LONGITUDE_SET");
    SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_FREEZE_LAT_LONG_TOGGLE,
                                        "FREEZE_LATITUDE_LONGITUDE_TOGGLE");
    /*
     * EVENT used to set the parking break
     */
    SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_PARKING, "PARKING_BRAKE_SET");

    /*
     * EVENT used for steering wheel
     */
    SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_STEERING, "STEERING_SET");

    /*
     * EVENT used for XPDR
     */
    SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_XPDR, "XPNDR_SET");
    SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_XPDR_IDENT, "XPNDR_IDENT_SET");

    /*
     * EVENT used for COMM & stdy COMM
     */
    SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_COM, "COM_RADIO_SET_HZ");
    SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_COM_STDBY, "COM_STBY_RADIO_SET_HZ");

    /*
     * EVENT Barometer settings
     *
     */

    SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_BARO, "KOHLSMAN_SET");
    SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_BARO_STD, "BAROMETRIC");

    /* Custom EVENTS
     *
     * Here pressing the P or Q key in MSFS
     * Note: the name of the event shall have a "."
     *
     */

    SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_P_PRESS, "My.CTRLP");
    SimConnect_MapInputEventToClientEvent(hSimConnect, INPUT_P_PRESS, "p", EVENT_P_PRESS);
    SimConnect_AddClientEventToNotificationGroup(hSimConnect, GROUP0, EVENT_P_PRESS);

    SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_QUIT, "My.CTRLQ");
    SimConnect_MapInputEventToClientEvent(hSimConnect, INPUT_QUIT, "q", EVENT_QUIT);
    SimConnect_AddClientEventToNotificationGroup(hSimConnect, GROUP0, EVENT_QUIT);

    /*
     * TCAS EVENT INITIALIZATION
     */

    /*
     * This is the data that will be fetched from the aircraft in vicinity of
     * PSX And will be used in the PSX TCAS
     */

    SimConnect_AddToDataDefinition(hSimConnect, TCAS_TRAFFIC_DATA, "PLANE ALTITUDE", "feet");

    SimConnect_AddToDataDefinition(hSimConnect, TCAS_TRAFFIC_DATA, "PLANE LATITUDE", "radians");
    SimConnect_AddToDataDefinition(hSimConnect, TCAS_TRAFFIC_DATA, "PLANE LONGITUDE",
                                   "radians");
    SimConnect_AddToDataDefinition(hSimConnect, TCAS_TRAFFIC_DATA,
                                   "PLANE HEADING DEGREES MAGNETIC", "radians");
}
