#include <assert.h>
#include <cstddef>
#include <cstdint>
#include <cstring>
#include <getopt.h>
#include <math.h>
#include <pthread.h>
#include <stdio.h>
#include <unistd.h>
#include <windows.h>
#include "PSXMSFS.h"
#include "SimConnect.h"

int quit = 0;
Target T;
float ground_elev = 0;
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
FILE *fdebug;

/*
 * Global variables used for TCAS updating
 */

TCAS tcas_acft[7];
double min_dist = 999999;
int nb_acft = 0;

void update_TCAS(AI_TCAS *ai, double d);

double dist(double lat1, double lat2, double long1, double long2) {
    return 2 * EARTH_RAD *
           (sqrt(pow(sin((lat2 * M_PI / 180 - lat1 * M_PI / 180) / 2), 2) +
                 cos(lat1 * M_PI / 180) * cos(lat2 * M_PI / 180) *
                     pow(sin((long2 * M_PI / 180 - long1 * M_PI / 180) / 2), 2)));
}
void SetUTCTime(void) {

    if (UTCupdate && validtime) {
        SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_ZULU_HOURS, T.hour,
                                       SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
        SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_ZULU_MINUTES, T.minute,
                                       SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
        SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_ZULU_DAY, T.day,
                                       SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
        SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_ZULU_YEAR, T.year,
                                       SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);

        UTCupdate = 1; // continous update of UTC time
    }
}

void SetCOMM(void) {

    SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_XPDR, T.XPDR,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
    SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_XPDR_IDENT, T.IDENT,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
    SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_COM, T.COM1,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
    SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_COM_STDBY, T.COM2,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
}
void SetBARO(void) {

    SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_BARO, T.altimeter * 16.0,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
    if (T.STD) {
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
            printf("Inside EVENT_SIM_START\n");
        } break;

        case EVENT_ONE_SEC: {

        } break;

        case EVENT_6_HZ: {
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

        case EVENT_PRINT: {
        } break;

        case EVENT_QUIT: {
            quit = 1;
            printf("Preparing to close PSXMSFS...\n");

        } break;

        default:
            printf("Another event received\n");
        }
        break;
    }

    case SIMCONNECT_RECV_ID_SIMOBJECT_DATA: {
        SIMCONNECT_RECV_SIMOBJECT_DATA *pObjData = (SIMCONNECT_RECV_SIMOBJECT_DATA *)pData;

        switch (pObjData->dwRequestID) {

        case MSFS_CLIENT_DATA: {
            Struct_MSFS *pS = (Struct_MSFS *)&pObjData->dwData;
            ground_elev = pS->ground;
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
                d = dist(ai->latitude, T.latitude, ai->longitude, T.longitude) / NM;
                if ((d < 40) &&                              // less than 40 NM away
                    abs(ai->altitude - T.altitude) < 2700 && // below or above 2700 feet
                    (!(T.onGround == 2) ||
                     ((T.onGround == 2) &&
                      abs(ai->altitude - T.altitude) > 500))) { // onground dont show acft below 500 above us

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
        tcas_acft[0].latitude = ai->latitude * M_PI / 180.0;
        tcas_acft[0].longitude = ai->longitude * M_PI / 180.0;
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
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "PLANE LATITUDE", "degrees");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "PLANE LONGITUDE", "degrees");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "PLANE HEADING DEGREES TRUE", "degrees");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "PLANE PITCH DEGREES", "degrees");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "PLANE BANK DEGREES", "degrees");

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
    hr = SimConnect_RequestDataOnSimObject(hSimConnect, DATA_REQUEST, MSFS_CLIENT_DATA, SIMCONNECT_OBJECT_ID_USER,
                                           SIMCONNECT_PERIOD_SECOND);

    /*
     * This is the data that will be fetched from the aircraft in vicinity of PSX
     * And will be used in the PSX TCAS
     */

    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_TCAS_TRAFFIC, "PLANE ALTITUDE", "feet");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_TCAS_TRAFFIC, "PLANE LATITUDE", "degrees");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_TCAS_TRAFFIC, "PLANE LONGITUDE", "degrees");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_TCAS_TRAFFIC, "PLANE HEADING DEGREES TRUE", "degrees");

    hr = SimConnect_RequestDataOnSimObject(hSimConnect, DATA_REQUEST_TCAS, DATA_TCAS_TRAFFIC, SIMCONNECT_OBJECT_ID_USER,
                                           SIMCONNECT_PERIOD_SECOND);

    // Request a simulation start event

    hr = SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_SIM_START, "SimStart");
    hr = SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_ONE_SEC, "1sec");
    hr = SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_6_HZ, "6Hz");
    hr = SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_4_SEC, "4sec");
    hr = SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_FRAME, "frame");
    hr = SimConnect_SetSystemEventState(hSimConnect, EVENT_FRAME, SIMCONNECT_STATE_ON);
    hr = SimConnect_AIReleaseControl(hSimConnect, SIMCONNECT_OBJECT_ID_USER, DATA_REQUEST);

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

    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_PRINT, "My.CTRLP");
    hr = SimConnect_MapInputEventToClientEvent(hSimConnect, INPUT_PRINT, "p", EVENT_PRINT);
    hr = SimConnect_AddClientEventToNotificationGroup(hSimConnect, GROUP0, EVENT_PRINT);

    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_QUIT, "My.CTRLQ");
    hr = SimConnect_MapInputEventToClientEvent(hSimConnect, INPUT_QUIT, "q", EVENT_QUIT);
    hr = SimConnect_AddClientEventToNotificationGroup(hSimConnect, GROUP0, EVENT_QUIT);

    return hr;
}

void *ptDatafromMSFS(void *thread_param) {
    (void)(&thread_param);
    while (!quit) {
        hr = SimConnect_CallDispatch(hSimConnect, ReadPositionFromMSFS, &T);
        sleep(1);
    }
    return NULL;
}

void *ptUmainboost(void *thread_param) {
    (void)(&thread_param);

    while (1) {
        if (umainBoost(&T)) {

            SetMSFSPos();
        }
    }

    return NULL;
}

void *ptUmain(void *thread_param) {
    (void)(&thread_param);

    while (1) {
        if (umain(&T)) {
            SetMSFSPos();
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
    APos.heading = 356.0;
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
    static int nbupdate = 0;

    AcftPosition APos;
    HRESULT hr;

    if (T.onGround == 2) {
        APos.altitude = ground_elev + 15.6;
        APos.GearDown = 1.0;
    } else {
        APos.altitude = T.altitude;
        APos.GearDown = ((T.GearLever == 3) ? 1.0 : 0.0);
    }
    APos.latitude = T.latitude;
    APos.longitude = T.longitude;
    APos.heading = T.heading;
    APos.pitch = -T.pitch;
    APos.bank = T.bank;
    APos.tas = T.TAS;
    APos.ias = T.IAS;
    APos.vertical_speed = T.VerticalSpeed;
    APos.FlapsPosition = T.FlapLever;
    APos.Speedbrake = T.SpdBrkLever / 800.0;

    // Update lights
    APos.LandLeftOutboard = T.light[0];
    APos.LandLeftInboard = T.light[2];
    APos.LandRightInboard = T.light[3];
    APos.LandRightOutboard = T.light[1];
    APos.LeftRwyTurnoff = T.light[4];
    APos.RightRwyTurnoff = T.light[5];
    APos.LightTaxi = T.light[6];
    APos.Strobe = T.light[11];
    APos.LightNav = T.light[9] || T.light[10];
    APos.Beacon = T.light[7];
    APos.BeaconLwr = T.light[8];
    APos.LightWing = T.light[12];
    APos.LightLogo = T.light[13];

    // Taxi lights disabled airborne
    if (T.onGround != 2) {
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

    APos.rudder = T.rudder;
    APos.ailerons = T.aileron;
    APos.elevator = T.elevator;

    // finally update everything
    //

    hr = SimConnect_SetDataOnSimObject(hSimConnect, DATA_PSX_TO_MSFS, SIMCONNECT_OBJECT_ID_USER, 0, 0, sizeof(APos),
                                       &APos);

    SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_PARKING, T.parkbreak,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
    SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_STEERING, T.steering,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);

    if (DEBUG) {
        nbupdate++;
        if (!(nbupdate % 500)) {
            time_t result = time(NULL);
            printf("%sHR: %ld Apos size %lld OBJECT_ID %lu\n\n", asctime(gmtime(&result)),hr,sizeof(APos),
                   SIMCONNECT_OBJECT_ID_USER);
            fprintf(fdebug,"%sHR: %ld Apos size %lld OBJECT_ID %lu\n\n", asctime(gmtime(&result)),hr,sizeof(APos),
                   SIMCONNECT_OBJECT_ID_USER);
            fflush(NULL);
            if (hr < 0) {
                state(&T);
                printf("\n");
                stateMSFS(&APos, fdebug);
                printf("\n");
                fflush(NULL);
            }
                nbupdate = 0;
        }

        pthread_mutex_unlock(&mutex);
    }
}

void usage() {

    printf("usage: [-h] [-v] [-m IP [-p port]] [-b IP [-c port]]\n");
    printf("\t -h, --help");
    printf("\t Prints this help\n");
    printf("\t --verbose");
    printf("\t verbose. Prints out debug into on console and in file DEBUT.TXT. Warning: can be very verbose\n");
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
                                               {0, 0, 0, 0}};
        /* getopt_long stores the option index here. */
        int option_index = 0;

        c = getopt_long(argc, argv, "thvm:b:c:p:f:", long_options, &option_index);

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
            fdebug = fopen("DEBUG.TXT", "w");
            if (!fdebug) {
                err_n_die("Error creating debug file...");
                exit(-1);
            }
            break;

        case '?':
            /* getopt_long already printed an error message. */
            usage();
            break;

        default:
            abort();
        }
    }

    /* Instead of reporting ‘--verbose’
       and ‘--brief’ as they are encountered,
       we report the final status resulting from them. */

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
     * Initialise and connect to all sockets: PSX, PSX Boost and Simconnect
     */
    open_connections();

    // initialize the data to be received as well as all EVENTS
    init_MS_data();

    // set a default location for the plane
    // Here at LFPG stand E22
    init_pos();

    /*
     * Sending Q423 DEMAND variable to PSX for the winds
     * Sending Q480 DEMAND variable to get aileron, rudder and elevator position
     */

    sendQPSX("demand=Qs483");
    sendQPSX("demand=Qs480");

    pthread_mutex_init(&mutex, NULL);

    if (pthread_create(&t1, NULL, &ptUmain, &T) != 0) {
        err_n_die("Error creating thread Umain");
    }

    if (pthread_create(&t2, NULL, &ptUmainboost, &T) != 0) {
        err_n_die("Error creating thread Umainboost");
    }

    if (pthread_create(&t3, NULL, &ptDatafromMSFS, &T) != 0) {
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
    if (!close_PSX_socket(sPSXBOOST)) {
        printf("Could not close boost PSX socket...\n");
    }
    printf("Closing PSX main connection...\n");
    if (!close_PSX_socket(sPSX)) {
        printf("Could not close main PSX socket...\n");
    }

    // Finally clean up the Win32 sockets
    WSACleanup();

    printf("Normal exit. See you\n");
    return 0;
}
