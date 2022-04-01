#include <assert.h>
#include <cstdint>
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
int updateLights;
HRESULT hr;

void CALLBACK ReadPositionFromMSFS(SIMCONNECT_RECV *pData, DWORD cbData, void *pContext) {

    (DWORD)(cbData);
    (void *)(pContext);

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
            //    printf("Inside EVENT_ONE_SEC\n");

        } break;

        case EVENT_6_HZ: {

        } break;
        case EVENT_FREEZE_ALT: {

        } break;

        case EVENT_PRINT: {
            printf("Inside EVENT_PRINT\n");

        } break;

        case EVENT_QUIT: {
            printf("Inside QUIT\n");
            quit = 1;
        } break;

        default:
            printf("Another event received\n");
        }
        break;
    }

    case SIMCONNECT_RECV_ID_SIMOBJECT_DATA: {
        SIMCONNECT_RECV_SIMOBJECT_DATA *pObjData = (SIMCONNECT_RECV_SIMOBJECT_DATA *)pData;

        // printf("dwRequestID: %lu received\n", pObjData->dwRequestID);
        // printf("MSFS_CLIENT_DATA: %d ", MSFS_CLIENT_DATA);
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

        // Add code to process the structure appropriately

    case SIMCONNECT_RECV_ID_QUIT: {

        printf("\nSIMCONNECT_RECV_ID_QUIT received and data sent");
    } break;

    default:
        break;
    }
}

int init_MS_data(void) {

    /* Standard Plane attributes*/

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

    /*Data definition for lights. Even though in the SDK documentation they are defined as non settable,
    /*Setting them like this works just fine. Alternative is to use EVENTS, but in that case all 4 landing
    /*light switches cannot be synchronised. */

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

    /* This is to get the ground altitude when positionning the aircraft at initialization or once on ground */
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "GROUND ALTITUDE", "feet");

    hr = SimConnect_RequestDataOnSimObject(hSimConnect, DATA_REQUEST, MSFS_CLIENT_DATA, SIMCONNECT_OBJECT_ID_USER,
                                           SIMCONNECT_PERIOD_VISUAL_FRAME);

    // Request a simulation start event
    //
    hr = SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_SIM_START, "SimStart");
    hr = SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_ONE_SEC, "1sec");
    hr = SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_6_HZ, "6Hz");
    hr = SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_FRAME, "frame");
    hr = SimConnect_SetSystemEventState(hSimConnect, EVENT_FRAME, SIMCONNECT_STATE_ON);
    hr = SimConnect_AIReleaseControl(hSimConnect, SIMCONNECT_OBJECT_ID_USER, DATA_REQUEST);

    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_FREEZE_ALT, "FREEZE_ALTITUDE_SET");
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_FREEZE_ATT, "FREEZE_ATTITUDE_SET");
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_FREEZE_LAT_LONG, "FREEZE_LATITUDE_LONGITUDE_SET");

    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_PRINT, "My.CTRLP");
    hr = SimConnect_MapInputEventToClientEvent(hSimConnect, INPUT_PRINT, "p", EVENT_PRINT);
    hr = SimConnect_AddClientEventToNotificationGroup(hSimConnect, GROUP0, EVENT_PRINT);

    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_QUIT, "My.CTRLQ");
    hr = SimConnect_MapInputEventToClientEvent(hSimConnect, INPUT_QUIT, "q", EVENT_QUIT);
    hr = SimConnect_AddClientEventToNotificationGroup(hSimConnect, GROUP0, EVENT_QUIT);

    return hr;
}

void *ptDatafromMSFS(void *Param) {
    while (!quit) {
        hr = SimConnect_CallDispatch(hSimConnect, ReadPositionFromMSFS, NULL);
    }
    return NULL;
}

void *ptUmainboost(void *thread_id) {

    Target *T;
    T = (Target *)(thread_id);
    while (!quit) {
        if (umainBoost2(T)) {
            pthread_mutex_lock(&mutex);
            SetMSFSPos(T);
            pthread_mutex_unlock(&mutex);
        }
    }

    return NULL;
}

void *ptUmain(void *thread_id) {
    Target *T;
    T = (Target *)(thread_id);
    while (!quit) {
        if (umain(T)) {
            pthread_mutex_lock(&mutex);
            SetMSFSPos(T);
            pthread_mutex_unlock(&mutex);
        }
    }
    return NULL;
}

void init_pos(void) {

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
    if (SimConnect_SetDataOnSimObject(hSimConnect, DATA_PSX_TO_MSFS, SIMCONNECT_OBJECT_ID_USER, 0, 0, sizeof(APos),
                                      &APos) != S_OK) {
        err_n_die("Could not update position");
    };
}

int SetMSFSPos(Target *T) {

    AcftPosition APos;
    HRESULT hr;

    if (T->onGround == 2) {
        APos.altitude = ground_elev + 15.6;
        APos.GearDown = 1.0;
    } else {
        APos.altitude = T->altitude;
        APos.GearDown = ((T->GearLever == 3) ? 1.0 : 0.0);
    }
    APos.latitude = T->latitude;
    APos.longitude = T->longitude;
    APos.heading = T->heading;
    APos.pitch = -T->pitch;
    APos.bank = T->bank;
    APos.tas = T->TAS;
    APos.ias = T->IAS;
    APos.vertical_speed = T->VerticalSpeed;
    APos.FlapsPosition = T->FlapLever;
    APos.Speedbrake = T->SpdBrkLever / 800.0;

    if (updateLights) {
        for (int i = 0; i < 14; i++) {
            printf("T->light[%d]=%d\n", i, T->light[i]);
        }
        updateLights = 0;
    }

    // Update lights
    APos.LandLeftOutboard = T->light[0];
    APos.LandLeftInboard = T->light[2];
    APos.LandRightInboard = T->light[3];
    APos.LandRightOutboard = T->light[1];
    APos.LeftRwyTurnoff = T->light[4];
    APos.RightRwyTurnoff = T->light[5];
    APos.LightTaxi = T->light[6];
    APos.Strobe = T->light[11];
    APos.LightNav = T->light[9] || T->light[10];
    APos.Beacon = T->light[7];
    APos.BeaconLwr = T->light[8];
    APos.LightWing = T->light[12];
    APos.LightLogo = T->light[13];

    // finally update everything
    hr = SimConnect_SetDataOnSimObject(hSimConnect, DATA_PSX_TO_MSFS, SIMCONNECT_OBJECT_ID_USER, 0, 0, sizeof(APos),
                                       &APos);

    return hr;
}
int main(int argc, char **argv) {

    pthread_t t1, t2, t3;
    int rc;

    // T = (Target *)malloc(sizeof(T));
    if (argc != 3) {
        printf("Usage: %s IP:port\n", argv[0]);
        exit(EXIT_FAILURE);
    }

    /*
     * Initialise and connect to all sockets: PSX, PSX Boost and Simconnect
     */
    open_connections(argv);

    // initialize the data to be received as well as all EVENTS
    init_MS_data();

    // set a default location for the plane
    // Here at LFPG stand E22
    init_pos();

    // Sending Q423 DEMAND variable tro PSX for the winds
    sendQPSX("demand=Qs483");

    pthread_mutex_init(&mutex, NULL);

    rc = pthread_create(&t1, NULL, &ptUmain, &T);
    rc = pthread_create(&t2, NULL, &ptUmainboost, &T);
    rc = pthread_create(&t3, NULL, &ptDatafromMSFS, NULL);

    pthread_join(t1, NULL);
    pthread_join(t2, NULL);
    pthread_join(t3, NULL);

    pthread_mutex_destroy(&mutex);

    printf("Closing MSFS connection...\n");
    SimConnect_Close(hSimConnect);

    // and gracefully close main + boost sockets
    printf("Closing PSX main connection...\n");
    if (close_PSX_socket(sPSX) < 0) {
        printf("Could not close main PSX socket...\n");
    }

    printf("Closing PSX boost connection...\n");
    if (close_PSX_socket(sPSXBOOST) < 0) {
        printf("Could not close boost PSX socket...\n");
    }

    // Finally clean up the Win32 sockets
    WSACleanup();

    printf("Normal exit. See you\n");
    return 0;
}
