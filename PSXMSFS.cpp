#include <assert.h>
#include <cstdint>
#include <math.h>
#include <stdio.h>
#include <windows.h>
#include "PSXMSFS.h"
#include "SimConnect.h"

float alt, lat, longi, head,pitch,bank;
int quit = 0;
int airborne=1;
    float hCorr=0;
#define STEP 0.001
HRESULT hr;

#define max_send_records 20

QPSX **Q;

void CALLBACK ReadPositionFromMSFS(SIMCONNECT_RECV *pData, DWORD cbData, void *pContext) {

    Target *T;
    T = (Target *)pContext;
    (void)cbData;
    switch (pData->dwID) {

    case SIMCONNECT_RECV_ID_OPEN: {

            SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_FREEZE_ALT, 1,
                                           SIMCONNECT_GROUP_PRIORITY_HIGHEST,
                                           SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
            SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_FREEZE_ATT, 1,
                                           SIMCONNECT_GROUP_PRIORITY_HIGHEST,
                                           SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
            SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_FREEZE_LAT_LONG, 1,
                                           SIMCONNECT_GROUP_PRIORITY_HIGHEST,
                                           SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
        printf("inside SIMCONNECT_RECV_ID_OPEN\n");
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
            SimResponse SR;
            SR.latitude = T->latitude;
            SR.longitude = T->longitude;
            //SR.pitch = -M_PI/4;
            SR.pitch = -T->pitch / 100000.0;
            SR.bank = (double)T->bank / 100000.0;
            SR.heading = T->heading;
            SR.tas= T->TAS/1000;
            SR.altitude = pS->ground+hCorr;
            SR.altitude_cg=0;
           // SR.altitude = -95.45;


            if (pS->plane_alt_above_gnd_minus_cg>1){hCorr -= STEP*10;} else if (pS->plane_alt_above_gnd_minus_cg>0){hCorr -= STEP;}
            if (pS->plane_alt_above_gnd_minus_cg<-1){hCorr += STEP*10;} else if (pS->plane_alt_above_gnd_minus_cg<0){hCorr += STEP;}
            
            SimConnect_SetDataOnSimObject(hSimConnect, DATA_PSX_TO_MSFS, SIMCONNECT_OBJECT_ID_USER, 0, 0, sizeof(SR),&SR);

            if (1) {
                printf("Pitch: %.4f\tHcorr: %.4f\tSR.altitude: %.4f\t Plane alt: %.4f\tPlane alt abv gnd: %.4f\tPlane altmin cg: %.4f\tGround elev: %.4f\r",SR.pitch*180/M_PI, hCorr,SR.altitude,pS->plane_alt ,pS->plane_alt_above_gnd,pS->plane_alt_above_gnd_minus_cg,pS->ground);
                            state(T);
                // printf("\n");
            }
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
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "PLANE LATITUDE", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "PLANE LONGITUDE", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "PLANE HEADING DEGREES TRUE", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "PLANE PITCH DEGREES", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "PLANE BANK DEGREES", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "AIRSPEED TRUE", "knots");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "PLANE ALT ABOVE GROUND MINUS CG", "feet");

    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "Indicated Altitude", "feet");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "Plane Latitude", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "Plane Longitude", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE HEADING DEGREES TRUE", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE HEADING DEGREES MAGNETIC", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE PITCH DEGREES", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE BANK DEGREES", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "GROUND ALTITUDE", "feet");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE ALTITUDE", "feet");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE ALT ABOVE GROUND", "feet");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE ALT ABOVE GROUND MINUS CG", "feet");


    hr = SimConnect_RequestDataOnSimObject(hSimConnect, DATA_REQUEST, MSFS_CLIENT_DATA, SIMCONNECT_OBJECT_ID_USER,
                                           SIMCONNECT_PERIOD_VISUAL_FRAME);

    // Request a simulation start event:w
    //
    hr = SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_SIM_START, "SimStart");
    hr = SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_ONE_SEC, "1sec");
    hr = SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_6_HZ, "6Hz");
    // hr = SimConnect_SetSystemEventState(hSimConnect, EVENT_FRAME, SIMCONNECT_STATE_ON);
    hr = SimConnect_AIReleaseControl(hSimConnect, SIMCONNECT_OBJECT_ID_USER, DATA_REQUEST);

    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_FREEZE_ALT, "FREEZE_ALTITUDE_SET");
     hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_FREEZE_ATT, "FREEZE_ATTITUDE_SET");
     hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_FREEZE_LAT_LONG, "FREEZE_LATITUDE_LONGITUDE_SET");

    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_QUIT, "My.CTRLQ");
    hr = SimConnect_MapInputEventToClientEvent(hSimConnect, INPUT_INIT, "Q", EVENT_QUIT);
    // hr = SimConnect_AddClientEventToNotificationGroup(hSimConnect, GROUP_INIT, EVENT_INIT);

    return hr;
}

int main(int argc, char **argv) {

    Target *T=NULL;
    T=(Target *) malloc(sizeof(T));
    if (argc != 3) {
        printf("Usage: %s IP:port\n", argv[0]);
        exit(EXIT_FAILURE);
    }

    // Connect to PSX and MSFS
    init_connect_PSX(argv[1], (int)strtol(argv[2], NULL, 0));
    init_connect_MSFS(&hSimConnect);

    // initialize the data to be received
    init_MS_data();

    // initialize the PSX variables

    // main loop;
    //
    //

    while (!quit) {
                umain(T);
        SimConnect_CallDispatch(hSimConnect, ReadPositionFromMSFS, T);
    };

    SimConnect_Close(hSimConnect);

    printf("Normal exit\n");
    return 0;
}
