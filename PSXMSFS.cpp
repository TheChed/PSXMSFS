#include <assert.h>
#include <cstdint>
#include <math.h>
#include <stdio.h>
#include <windows.h>
#include "PSXMSFS.h"
#include "SimConnect.h"

#define PRINT 1
float lat, longi,head;
int quit = 0;
HRESULT hr;

#define max_send_records 20

QPSX **Q;

void CALLBACK TestWriteData(SIMCONNECT_RECV *pData, DWORD cbData, void *pContext) {

  HRESULT hr;

  SIMCONNECT_DATA_INITPOSITION Init;
  Init.Altitude=7326;
  Init.Latitude=lat;
  Init.Longitude=longi;
  Init.Pitch=0.0;
  Init.Bank=-1.0;
  Init.Heading=head;
  Init.OnGround=1;
  Init.Airspeed=0;
 //lat=lat+0.00001;
 //longi=longi+0.00001;
   head=head+0.01;

 printf("Lat: %f\n",lat);
  hr=SimConnect_SetDataOnSimObject(hSimConnect, DEFINITION_6,SIMCONNECT_OBJECT_ID_USER,0,0,sizeof(Init),&Init);
}


void CALLBACK ReadPositionFromMSFS(SIMCONNECT_RECV *pData, DWORD cbData, void *pContext) {

    Target *T;
    T = (Target *)pContext;
    (void)cbData;
    switch (pData->dwID) {

    case SIMCONNECT_RECV_ID_OPEN: {
        printf("inside SIMCONNECT_RECV_ID_OPEN\n");
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

            SimResponse SR;
            SR.altitude = (double)T->altitude / 1000.0;
            SR.latitude = T->latitude;
            SR.longitude = T->longitude;
            SR.pitch = (double)T->pitch / 100000.0;
            SR.bank = (double)T->bank / 100000.0;
            SR.heading = T->heading;
            SR.pos = 1;
            SR.alt = 1;
            SR.att = 1;
 //           SimConnect_SetDataOnSimObject(hSimConnect, DATA_PSX_TO_MSFS, SIMCONNECT_OBJECT_ID_USER, 0, 0, sizeof(SR),
 //                                         &SR);
        } break;

        case EVENT_FREEZE: {
            printf("Inside EVENT_FREEZE\n");
        } break;

        case EVENT_INIT: {

            printf("Inside EVENT_INIT\n");
            SimConnect_TransmitClientEvent(hSimConnect, 0, EVENT_INIT, SIMCONNECT_GROUP_PRIORITY_HIGHEST,
                                           SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY, 0);

            SimConnect_TransmitClientEvent(hSimConnect, 0, EVENT_INIT, 2, SIMCONNECT_GROUP_PRIORITY_DEFAULT,
                                           SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
            printf("\nEVENT_INIT received and data sent");
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

        switch (pObjData->dwRequestID) {

        case MSFS_CLIENT_DATA: {
            Struct_MSFS *pS = (Struct_MSFS *)&pObjData->dwData;
            if (PRINT) {
                printf("MSFS: Alt: %.0f\tHead: %.2f\tLat: %.4f\tLong: %.4f\tpitch: %.4f\tbank: "
                       "%.4f\talt: %d\tatt: %d\tpos: %d\n",
                       pS->altitude, pS->heading * 180 / M_PI, pS->latitude * 180 / M_PI, pS->longitude * 180 / M_PI,
                       pS->pitch * 180 / M_PI, pS->bank * 180 / M_PI, pS->alt, pS->att, pS->pos);
                state(T);
                //printf("\n");
            }
        } break;

        default:
            printf("dwRequestID: %lu received\n", pObjData->dwRequestID);
            break;
        }
        break;
    }

        // Add code to process the structure appropriately

    case SIMCONNECT_RECV_ID_QUIT: {

        printf("\nSIMCONNECT_RECV_ID_QUIT received and data sent");
        quit = 1;
    } break;

    default:
        break;
    }
}

int init_MS_data(void) {
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "INDICATED ALTITUDE", "feet");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "PLANE LATITUDE", "radian");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "PLANE LONGITUDE", "radian");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "PLANE HEADING DEGREES TRUE", "radian");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "PLANE PITCH DEGREES", "radian");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "PLANE BANK DEGREES", "radian");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "IS ALTITUDE FREEZE ON", "Bool");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "IS ATTITUDE FREEZE ON", "Bool");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "IS POSITION FREZE ON", "Bool");

    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "Indicated Altitude", "feet");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "Plane Latitude", "radian");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "Plane Longitude", "radian");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE HEADING DEGREES TRUE", "radian");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE PITCH DEGREES", "radian");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "PLANE BANK DEGREES", "radian");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "IS ALTITUDE FREEZE ON", NULL,
                                        SIMCONNECT_DATATYPE_INT32);
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "IS ATTITUDE FREEZE ON", "Bool");
    hr = SimConnect_AddToDataDefinition(hSimConnect, MSFS_CLIENT_DATA, "IS POSITION FREEZE ON", "Bool");

    hr = SimConnect_RequestDataOnSimObject(hSimConnect, DATA_REQUEST_3, MSFS_CLIENT_DATA, SIMCONNECT_OBJECT_ID_USER,
                                           SIMCONNECT_PERIOD_SECOND);

    // Request a simulation start event
    hr = SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_SIM_START, "SimStart");
    hr = SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_ONE_SEC, "1sec");
    hr = SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_6_HZ, "6Hz");

    /////////////////////////////////////////////////////
    //
    //
    // Create custom events. Needs to have a "." in the name to be a custom event
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_QUIT, "Q.toquit");
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_INIT, "FREEZE_LATITUDE_LONGITUDE_TOGGLE");

    // Link the custom event to some keyboard keys, and turn the input event off
    hr = SimConnect_MapInputEventToClientEvent(hSimConnect, INPUT_QUIT, "Q", EVENT_QUIT);

    // Sign up for notifications for EVENT_QUIT
    hr = SimConnect_AddClientEventToNotificationGroup(hSimConnect, GROUP0, EVENT_QUIT, TRUE);
    hr = SimConnect_SetNotificationGroupPriority(hSimConnect, GROUP0, SIMCONNECT_GROUP_PRIORITY_HIGHEST);
    hr = SimConnect_SetInputGroupState(hSimConnect, INPUT_QUIT, SIMCONNECT_STATE_ON);
    //
    //////////////////////////////////////////////////////
    // Create custom events. Needs to have a "." in the name to be a custom event
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_INIT, "U.updatepos");

    // Link the custom event to some keyboard keys, and turn the input event off
    hr = SimConnect_MapInputEventToClientEvent(hSimConnect, INPUT_INIT, "U", EVENT_INIT);
    hr = SimConnect_SetInputGroupState(hSimConnect, INPUT_INIT, SIMCONNECT_STATE_ON);

    // Sign up for notifications for EVENT_INIT
    hr = SimConnect_AddClientEventToNotificationGroup(hSimConnect, GROUP1, EVENT_INIT);
    hr = SimConnect_AddClientEventToNotificationGroup(hSimConnect, GROUP1, EVENT_FREEZE);

    return hr;
}

int main(int argc, char **argv) {

    Target *T;
    T = (Target *)malloc(sizeof(T));
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

lat=19.4416;
longi=-99.0712;
head=180.0;
    hr = SimConnect_AddToDataDefinition(hSimConnect, DEFINITION_6, "Initial Position", NULL,SIMCONNECT_DATATYPE_INITPOSITION);
    while (!quit) {

        SimConnect_CallDispatch(hSimConnect, ReadPositionFromMSFS, T);
        //SimConnect_CallDispatch(hSimConnect, TestWriteData, NULL);
//        umain(T);
    };

    SimConnect_Close(hSimConnect);

    printf("Normal exit\n");
    return 0;
}
