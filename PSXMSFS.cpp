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
float ground_elev = 0;
static int nbmain = 0, nbboost = 0;

HRESULT hr;

#define max_send_records 20

QPSX **Q;

void CALLBACK ReadPositionFromMSFS(SIMCONNECT_RECV *pData, DWORD cbData, void *pContext) {

    Target *T;
    T = (Target *)pContext;
    switch (pData->dwID) {

    case SIMCONNECT_RECV_ID_OPEN: {

        SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_FREEZE_ALT, 1,
                                       SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
        SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_FREEZE_ATT, 1,
                                       SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
        SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_FREEZE_LAT_LONG, 1,
                                       SIMCONNECT_GROUP_PRIORITY_HIGHEST, SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
        // printf("inside SIMCONNECT_RECV_ID_OPEN\n");
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

            // printf("Inside EVENT_6Hz\n");
        } break;
        case EVENT_FREEZE_ALT: {

            printf("Inside EVENT_FRAME\n");
        } break;

        case EVENT_INIT: {

            printf("Inside EVENT_INIT\n");
        } break;

        case EVENT_QUIT: {
            quit = 1;
            printf("\nEVENT_QUIT received and data sent");
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
            printf("Flaps: %f\r ", pS->Flaps);
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
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "AIRSPEED TRUE", "knots");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "VERTICAL SPEED", "feet per second");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "GEAR POSITION:1", "number");
    // hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "FLAP POSITION SET", "position");

    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "Indicated Altitude", "feet");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "Plane Latitude", "degrees");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "Plane Longitude", "degrees");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE HEADING DEGREES TRUE", "degrees");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE HEADING DEGREES MAGNETIC", "degrees");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE PITCH DEGREES", "degrees");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE BANK DEGREES", "degrees");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "GROUND ALTITUDE", "feet");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE ALTITUDE", "feet");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE ALT ABOVE GROUND", "feet");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE ALT ABOVE GROUND MINUS CG", "feet");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "FLAPS HANDLE PERCENT", "percent");

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

    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_QUIT, "My.CTRLQ");
    hr = SimConnect_MapInputEventToClientEvent(hSimConnect, INPUT_INIT, "Q", EVENT_QUIT);
    // hr = SimConnect_AddClientEventToNotificationGroup(hSimConnect, GROUP_INIT,
    // EVENT_INIT);

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

        printf("\t----- ");
        state(T);

        printf("\r");
}

int SetMSFSPos(Target *T) {

    AcftPosition APos;
    HRESULT hr;

    if (T->onGround) {
        APos.altitude = ground_elev + 15.6;
    } else {
        APos.altitude = T->altitude;
    }
    APos.latitude = T->latitude;
    APos.longitude = T->longitude;
    APos.heading = T->heading;
    APos.pitch = -T->pitch;
    APos.bank = T->bank;
    APos.tas = T->TAS;
    APos.vertical_speed = T->VerticalSpeed;
    APos.GearDown = 1;
    // APos.FlapsPosition=5;
    // print_state(&APos, T);
    hr = SimConnect_SetDataOnSimObject(hSimConnect, DATA_PSX_TO_MSFS, SIMCONNECT_OBJECT_ID_USER, 0, 0, sizeof(APos),
                                       &APos);

    return hr;
}

void *ptUmainboost(void *thread_id) {

    Target *T;
    T = (Target *)(thread_id);
    while (1) {
        if (umainBoost(T)) {
            SetMSFSPos(T);
        }
    }

    return NULL;
}

void *ptUmain(void *thread_id) {
    Target *T;
    T = (Target *)(thread_id);
    while (1) {
        if (umain(T)) {
            SetMSFSPos(T);
        }
    }
    return NULL;
}
int main(int argc, char **argv) {

    Target *T = NULL;
    pthread_t t1, t2;
    int threaded = 1;
    int nbboucle=0;
    int rc;
    T = (Target *)malloc(sizeof(T));
    if (argc != 4) {
        printf("Usage: %s IP:port threaded(0=NO, 1=YES)\n", argv[0]);
        exit(EXIT_FAILURE);
    }

    // Connect to PSX and MSFS
    init_connect_PSX(argv[1], (int)strtol(argv[2], NULL, 0));
    init_connect_PSX_Boost(argv[1], 10749);
    init_connect_MSFS(&hSimConnect);

    threaded = (int)strtol(argv[3],NULL,0);
    // initialize the data to be received
    init_MS_data();

    //        printf("Umain read: %d\t\t  UmainBoost:
    //        %d\t\t\r",umain(T),umainBoost(T)); printf("UmainBoost:
    //        %d\t\t\r",umainBoost(T));
    //       printf("UmainBoost2: %d\t\t\r",umainBoost2(T));
    // umain(T);
    // umainBoost(T);
    //
    if (threaded) {
        rc = pthread_create(&t1, NULL, &ptUmain, T);
        rc = pthread_create(&t2, NULL, &ptUmainboost, T);

        pthread_join(t1, NULL);
        pthread_join(t2, NULL);
    } else {
        while (1) {
            umain(T);
            umainBoost(T);
            nbboucle++;
            if(nbboucle % 100) state(T);
        }
    }

    //    printf("inside main function\n");
    // state(T);
    //     SimConnect_CallDispatch(hSimConnect, ReadPositionFromMSFS, T);

    //    print_state(&APos, T);

    printf("nbmain: %d, nbboost:%d\n", nbmain, nbboost);

    SimConnect_Close(hSimConnect);
    free(T);
    printf("Normal exit\n");
    return 0;
}
