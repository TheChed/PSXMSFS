#include <cmath>
#include <cstdlib>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <time.h>
#include <windows.h>
#include "PSXMSFS.h"
#include "SimConnect.h"
#include "util.h"
#include "update.h"

TCAS tcas_acft[7];
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
void update_TCAS(const AI_TCAS *ai, double d)
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

void freezeMSFS(void)
{

    SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_FREEZE_ALT, 1,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST,
                                   SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
    SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_FREEZE_ATT, 1,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST,
                                   SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
    SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_FREEZE_LAT_LONG, 1,
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
    (void)(cbData);
    (void)(pContext);

       switch (pData->dwID) {

    case SIMCONNECT_RECV_ID_EXCEPTION: {
        SIMCONNECT_RECV_EXCEPTION *evt = (SIMCONNECT_RECV_EXCEPTION *)pData;
        printDebug(LL_ERROR,
                   "Exception risen: %ld, by sender: %ld at index: %ld",
                   evt->dwException, evt->dwSendID, evt->dwIndex);
    } break;

    case SIMCONNECT_RECV_ID_OPEN: {

        /* Structure received containing some info about the version
         * of MSFS and Simconnect.
         * Just usefull for debugging and info purposes
         */

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
         * engines.
         */

        printDebug(LL_VERBOSE, "Freezing Altitude, Attitude and Coordinates in MSFS.");
        freezeMSFS();
    } break;

    case SIMCONNECT_RECV_ID_EVENT: {
        SIMCONNECT_RECV_EVENT *evt = (SIMCONNECT_RECV_EVENT *)pData;

        switch (evt->uEventID) {

        case EVENT_ONE_SEC: {
            key_press = 0;
        } break;

        case EVENT_6_HZ: {
            if (flags.SLAVE) {

                SimConnect_TransmitClientEvent(
                    hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_FREEZE_ALT, 0,
                    SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
                SimConnect_TransmitClientEvent(
                    hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_FREEZE_ATT, 0,
                    SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
                SimConnect_TransmitClientEvent(
                    hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_FREEZE_LAT_LONG, 0,
                    SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
                Inject_MSFS_PSX();
            }
        } break;

        case EVENT_4_SEC: {
            /*
             * TCAS injection every 4 seconds but only if TCAS switch is on
             */
            if (flags.TCAS_INJECT) {
                SimConnect_RequestDataOnSimObjectType(hSimConnect, DATA_REQUEST_TCAS,
                                                      TCAS_TRAFFIC_DATA, 40 * NM,
                                                      SIMCONNECT_SIMOBJECT_TYPE_AIRCRAFT);
                IA_update();
            }

        } break;

        case EVENT_P_PRESS: {
            printDebug(LL_ERROR, "In P Event:  %d\n",evt->uEventID);
            if (!key_press) {
                key_press = 1;
                SimConnect_TransmitClientEvent(
                    hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_FREEZE_ALT_TOGGLE, 0,
                    SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
                if (LL_ERROR) {
                    if (!flags.SLAVE) {
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
            printDebug(LL_ERROR, "Event %i not captured",evt->uEventID);
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

        //   pthread_mutex_lock(&mutexsitu);

        WaitForSingleObject(mutexsitu, INFINITE);
        while (intflags.updateNewSitu) {
            // pthread_cond_wait(&condNewSitu, &mutexsitu);
        }
        // pthread_mutex_unlock(&mutexsitu);
        ReleaseMutex(mutexsitu);
        // pthread_mutex_lock(&mutex);
        WaitForSingleObject(mutex, INFINITE);
        SetMSFSPos();
        // pthread_mutex_unlock(&mutex);
        ReleaseMutex(mutex);

    }

    break;
    default:
        printDebug(LL_VERBOSE, "In Callback function default case: nothing was done. Event: %ld",
                   pData->dwID);
        break;
    }
}

int init_MS_data(void)
{
    HRESULT hr;

    /* Here we map all the variables that are used to update the 747 in MSFS.
     * It is VERY important that the order of those variables matches the order
     * in with the structures defined in PSXMSFS.h
     */
    hr = SimConnect_AddToDataDefinition(hSimConnect, BOOST_TO_MSFS_ALT, "PLANE ALTITUDE", "feet");
    hr = SimConnect_AddToDataDefinition(hSimConnect, BOOST_TO_MSFS, "PLANE ALTITUDE", "feet");
    hr = SimConnect_AddToDataDefinition(hSimConnect, BOOST_TO_MSFS_STD_ALT, "INDICATED ALTITUDE", "feet");
    hr = SimConnect_AddToDataDefinition(hSimConnect, BOOST_TO_MSFS, "PLANE LATITUDE", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, BOOST_TO_MSFS, "PLANE LONGITUDE", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, BOOST_TO_MSFS, "PLANE HEADING DEGREES TRUE",
                                        "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, BOOST_TO_MSFS, "PLANE PITCH DEGREES",
                                        "radians");
    hr =
        SimConnect_AddToDataDefinition(hSimConnect, BOOST_TO_MSFS, "PLANE BANK DEGREES", "radians");

    /*
     * Moving Surfaces: Ailerons, rudder , elevator
     *
     */
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_MOVING_SURFACES, "GEAR HANDLE POSITION",
                                        "percent over 100");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_MOVING_SURFACES, "FLAPS HANDLE INDEX",
                                        "number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_MOVING_SURFACES,
                                        "SPOILERS HANDLE POSITION", "position");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_MOVING_SURFACES, "RUDDER POSITION",
                                        "position 16K");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_MOVING_SURFACES, "ELEVATOR POSITION",
                                        "position 16K");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_MOVING_SURFACES, "AILERON POSITION",
                                        "position 16K");

    /*
     * Data definition for lights. Even though in the SDK documentation they are
     * defined as non settable, Setting them like this works just fine.
     * Alternative is to use EVENTS, but in that case all 4 landing light
     * switches cannot be synchronised.
     */

    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_LIGHT, "LIGHT LANDING:1", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_LIGHT, "LIGHT LANDING:2", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_LIGHT, "LIGHT LANDING:3", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_LIGHT, "LIGHT LANDING:4", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_LIGHT, "LIGHT TAXI:1", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_LIGHT, "LIGHT TAXI:2", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_LIGHT, "LIGHT TAXI:3", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_LIGHT, "LIGHT NAV", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_LIGHT, "LIGHT STROBE", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_LIGHT, "LIGHT BEACON:1", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_LIGHT, "LIGHT BEACON:2", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_LIGHT, "LIGHT WING", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_LIGHT, "LIGHT LOGO", "Number");

    /* This is to get the ground altitude when positionning the aircraft at
     * initialization or once on ground */
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "GROUND ALTITUDE", "feet");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE ALT ABOVE GROUND",
                                        "feet");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA,
                                        "PLANE ALT ABOVE GROUND MINUS CG", "feet");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "INDICATED ALTITUDE", "feet");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE LATITUDE", "radians");
    hr =
        SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE LONGITUDE", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE PITCH DEGREES",
                                        "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE BANK DEGREES",
                                        "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE HEADING DEGREES TRUE",
                                        "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "VERTICAL SPEED",
                                        "feet per minute");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "AIRSPEED TRUE", "knots");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE ALTITUDE", "feet");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "AMBIENT PRESSURE", "mmhg");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "SEA LEVEL PRESSURE", "hectopascal");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "BAROMETER PRESSURE", "hectopascal");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "AMBIENT TEMPERATURE", "celsius");

    /*
     * Definition to store various speeds
     */
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_SPEED, "AIRSPEED INDICATED", "knots");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_SPEED, "AIRSPEED TRUE", "knots");
    // hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_SPEED, "GROUND VELOCITY","knots");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_SPEED, "VERTICAL SPEED", "feet per minute");

    hr = SimConnect_RequestDataOnSimObject(hSimConnect, DATA_REQUEST, MSFS_CLIENT_DATA,
                                           SIMCONNECT_OBJECT_ID_USER,
                                           SIMCONNECT_PERIOD_VISUAL_FRAME);

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
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_FREEZE_ALT_TOGGLE,
                                             "FREEZE_ALTITUDE_TOGGLE");
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_FREEZE_ATT, "FREEZE_ATTITUDE_SET");
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_FREEZE_ATT_TOGGLE,
                                             "FREEZE_ATTITUDE_TOGGLE");
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_FREEZE_LAT_LONG,
                                             "FREEZE_LATITUDE_LONGITUDE_SET");
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
    hr =
        SimConnect_AddToDataDefinition(hSimConnect, TCAS_TRAFFIC_DATA, "PLANE LATITUDE", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, TCAS_TRAFFIC_DATA, "PLANE LONGITUDE",
                                        "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, TCAS_TRAFFIC_DATA,
                                        "PLANE HEADING DEGREES MAGNETIC", "radians");
    return hr;
}
