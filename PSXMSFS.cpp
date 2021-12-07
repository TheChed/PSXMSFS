#include <cstdint>
#include <math.h>
#include <assert.h>
#include <stdio.h>
#include <windows.h>
#include "PSXMSFS.h"
#include "SimConnect.h"

int quit = 0;
HRESULT hr;

#define max_send_records 20


QPSX **Q;

void CALLBACK ReadPositionFromMSFS(SIMCONNECT_RECV *pData, DWORD cbData, void *pContext) {

    Target *T;
    T=(Target *)pContext;
    (void ) cbData;
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
            SR.altitude = (double) T->altitude/1000.0;
            SR.latitude = T->latitude ;
            SR.longitude = T->longitude;
            SR.pitch = (double) T->pitch/100000.0;
            SR.bank = (double) T->bank/100000.0;
            SR.heading = T->heading ;
            SR.pos= 1 ;
            SR.alt= 1 ;
            SR.att= 1 ;
            SimConnect_SetDataOnSimObject(hSimConnect,DATA_PSX_TO_MSFS,SIMCONNECT_OBJECT_ID_USER, 0, 0, sizeof(SR), &SR);
        } break;

        case EVENT_FREEZE: {
            printf("Inside EVENT_FREEZE\n");
                         } break;


        case EVENT_INIT: {

            printf("Inside EVENT_INIT\n");
            

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

        case DATA_DEFINITION: {
            Struct_MSFS *pS = (Struct_MSFS *)&pObjData->dwData;
            printf("MSFS: Alt: %.0f\tHead: %.2f\tLat: %.4f\tLong: %.4f\tpitch: %.4f\tbank: %.4f\talt: %d\tatt: %d\tpos: %d\n", pS->altitude,pS->heading*180/M_PI, pS->latitude*180/M_PI,pS->longitude*180/M_PI,pS->pitch*180/M_PI, pS->bank*180/M_PI,  pS->alt, pS->att, pS->pos);
            state(T);
            printf("\n");
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
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "PLANE HEADING DEGREES TRUE","radian");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "PLANE PITCH DEGREES","radian");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "PLANE BANK DEGREES","radian");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "IS ALTITUDE FREEZE ON","Bool");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "IS ATTITUDE FREEZE ON","Bool");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "IS POSITION FREZE ON","Bool");


    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_DEFINITION, "Indicated Altitude", "feet");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_DEFINITION, "Plane Latitude", "radian");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_DEFINITION, "Plane Longitude", "radian");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_DEFINITION, "PLANE HEADING DEGREES TRUE","radian");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_DEFINITION, "PLANE PITCH DEGREES","radian");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_DEFINITION, "PLANE BANK DEGREES","radian");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_DEFINITION, "IS ALTITUDE FREEZE ON",NULL,SIMCONNECT_DATATYPE_INT32);
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_DEFINITION, "IS ATTITUDE FREEZE ON","Bool");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_DEFINITION, "IS POSITION FREZE ON","Bool");

    hr = SimConnect_RequestDataOnSimObject(hSimConnect, DATA_REQUEST_3, DATA_DEFINITION,SIMCONNECT_OBJECT_ID_USER, SIMCONNECT_PERIOD_SECOND);

    // Request a simulation start event
    hr = SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_SIM_START, "SimStart");
    hr = SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_ONE_SEC, "1sec");
    hr = SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_6_HZ, "6Hz");

    /////////////////////////////////////////////////////
    //
    //
    // Create custom events. Needs to have a "." in the name to be a custom event
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_QUIT, "Q.toquit");

    // Link the custom event to some keyboard keys, and turn the input event off
    hr = SimConnect_MapInputEventToClientEvent(hSimConnect, INPUT_QUIT, "Q", EVENT_QUIT);

    // Sign up for notifications for EVENT_QUIT
    hr = SimConnect_AddClientEventToNotificationGroup(hSimConnect, GROUP0, EVENT_QUIT,TRUE);
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

    while (!quit) {
        SimConnect_CallDispatch(hSimConnect, ReadPositionFromMSFS, T);
        umain(T);
    };
    
    SimConnect_Close(hSimConnect);


    printf("Normal exit\n");
    return 0;
}
