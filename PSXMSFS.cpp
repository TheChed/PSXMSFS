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

HRESULT hr;


void CALLBACK ReadPositionFromMSFS(SIMCONNECT_RECV *pData, DWORD cbData, void *pContext) {

    (DWORD )(cbData);
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

        SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_LIGHT, 1,
                                       SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
        } break;
        
        case EVENT_QUIT: {
            printf("Inside QUTI\n");
            quit=1;
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
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "PLANE ALTITUDE", "feet");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "PLANE LATITUDE", "degrees");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "PLANE LONGITUDE", "degrees");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "PLANE HEADING DEGREES TRUE", "degrees");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "PLANE PITCH DEGREES", "degrees");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "PLANE BANK DEGREES", "degrees");

    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "AIRSPEED TRUE", "knot");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "AIRSPEED INDICATED", "knot");

    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "VERTICAL SPEED", "feet per minute");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "GEAR HANDLE POSITION", "percent over 100");
     hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "FLAPS HANDLE INDEX", "number");
     hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "SPOILERS HANDLE POSITION", "position");



    hr = SimConnect_AddToDataDefinition(hSimConnect,MSFS_CLIENT_DATA, "GROUND ALTITUDE", "feet");
    hr = SimConnect_RequestDataOnSimObject(hSimConnect, DATA_REQUEST, MSFS_CLIENT_DATA, SIMCONNECT_OBJECT_ID_USER,
                                           SIMCONNECT_PERIOD_VISUAL_FRAME);

    // Request a simulation start event:w
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


    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_LIGHT, "BEACON_LIGHTS_ON");

    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_PRINT, "My.CTRLP");
    hr = SimConnect_MapInputEventToClientEvent(hSimConnect, INPUT_PRINT, "p", EVENT_PRINT);
    hr = SimConnect_AddClientEventToNotificationGroup(hSimConnect, GROUP0, EVENT_PRINT);
    
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_QUIT, "My.CTRLQ");
    hr = SimConnect_MapInputEventToClientEvent(hSimConnect, INPUT_QUIT, "q", EVENT_QUIT);
    hr = SimConnect_AddClientEventToNotificationGroup(hSimConnect, GROUP0, EVENT_QUIT);
    
    hr = SimConnect_SetInputGroupPriority(hSimConnect, INPUT_PRINT, SIMCONNECT_GROUP_PRIORITY_HIGHEST);

    return hr;
}

// prints the parameters of the simu objects. simu =0 for both, simu = 1 for
// MSFS, simu = 2 for PSX

void print_state(AcftPosition *Pos, Target *T) {

    if ((T == NULL) or (Pos == NULL)) {
        return;
    }
    printf("MSFS: ");
    printf("Alt: %.2f\t", Pos->altitude);
    printf("Lat: %.4f\t", Pos->latitude);
    printf("Long: %.4f\t", Pos->longitude);
    printf("Heading: %.2f\t", Pos->heading);
    printf("Pitch: %.2f\t", Pos->pitch);
    printf("Bank: %.2f\t", Pos->bank);
    printf("TAS: %.2f\t", Pos->tas);
    printf("VS: %.2f\t", Pos->vertical_speed);

    printf("-----");
    state(T);

    printf("\n");
}


void *ptDatafromMSFS(void *Param) {
    while (!quit){
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
    while (!quit){
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

    printf("Setting initial position at LFPG\n");
    APos.altitude = 360.5; //358 + 15.6;
    APos.latitude = 49.0012;
    APos.longitude = 2.57728;
    APos.heading = 356.0;
    APos.pitch = -1.36;
    APos.bank = 0.0;
    APos.tas = 0.0;
    APos.ias = 0.0;
    APos.vertical_speed = 0.0;
    APos.FlapsPosition = 0.0; //Flaps down
    APos.Speedbrake=0.0;   //Spoilers down
    APos.GearDown = 1.0;
    if (SimConnect_SetDataOnSimObject(hSimConnect, DATA_PSX_TO_MSFS, SIMCONNECT_OBJECT_ID_USER, 0, 0, sizeof(APos),
                                      &APos) != S_OK) {
        err_n_die("Could not update position");
    };
}

int SetMSFSPos(Target *T) {

    AcftPosition APos;
    HRESULT hr;

    if (T->onGround==2) {
        APos.altitude = ground_elev + 15.6;
        APos.GearDown = 1.0;
    } else {
        APos.altitude = T->altitude;
        APos.GearDown = ((T->GearLever==3) ? 1.0 : 0.0);
    }
    APos.latitude = T->latitude;
    APos.longitude = T->longitude;
    APos.heading = T->heading;
    APos.pitch = -T->pitch;
    APos.bank = T->bank;
    APos.tas = T->TAS;
    APos.ias=T->IAS;
    APos.vertical_speed = T->VerticalSpeed;
    APos.FlapsPosition= T->FlapLever;
    APos.Speedbrake=T->SpdBrkLever/800.0;
    hr = SimConnect_SetDataOnSimObject(hSimConnect, DATA_PSX_TO_MSFS, SIMCONNECT_OBJECT_ID_USER, 0, 0, sizeof(APos),&APos);



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

    // Connect to PSX and MSFS sockets
    init_connect_PSX(argv[1], (int)strtol(argv[2], NULL, 0));
    init_connect_PSX_Boost(argv[1], 10749);
    init_connect_MSFS(&hSimConnect);

    // initialize the data to be received
    init_MS_data();

    // set a default location for the plane
    // Here at LFPG stand E22

    init_pos();

    //Sending Q423 DEMAND variable tro PSX for the winds
    sendQPSX("demand=Qs483");
    
    pthread_mutex_init(&mutex, NULL);

    rc = pthread_create(&t1, NULL, &ptUmain, &T);
    rc = pthread_create(&t2, NULL, &ptUmainboost, &T);
    rc = pthread_create(&t3, NULL, &ptDatafromMSFS, NULL);

    pthread_join(t1, NULL);
    pthread_join(t2, NULL);
    pthread_join(t3, NULL);

    pthread_mutex_destroy(&mutex);

    printf("Closing MSFS connection...\n" );
    SimConnect_Close(hSimConnect);

    printf("Normal exit\n");
    return 0;
}
