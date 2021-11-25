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
    T->altitude+=1000;
    switch (pData->dwID) {

    case SIMCONNECT_RECV_ID_OPEN: {
        // Turn the input events on now
        printf("inside SIMCONNECT_RECV_ID_OPEN\n");
    } break;

    case SIMCONNECT_RECV_ID_EVENT: {
        SIMCONNECT_RECV_EVENT *evt = (SIMCONNECT_RECV_EVENT *)pData;

        switch (evt->uEventID) {
        case EVENT_SIM_START: {
            // Turn the input events on now
            printf("Inside EVENT_SIM_START\n");
        } break;

        case EVENT_ONE_SEC: {
            //state(T);

            SimResponse SR;

            SR.altitude = 1179;
            SR.latitude = 0.87455;
            SR.longitude = 0.24891;
            SR.pitch = 0.0;
            SR.bank = 0.0;
            SR.VS = 0.0;
            SR.IAS = 0.0;
            SR.TAS = 0.0;
            SR.heading = 58;

              //  SimConnect_SetDataOnSimObject(hSimConnect,DATA_PSX_TO_MSFS,SIMCONNECT_OBJECT_ID_USER, 0, 0, sizeof(SR), &SR);
        } break;

        case EVENT_INIT: {

            printf("Inside EVENT_INIT\n");
            SIMCONNECT_DATA_INITPOSITION Init;

            Init.Altitude = T->altitude / 1000.0;
            Init.Latitude = T->latitude / M_PI * 180.0;
            Init.Longitude = T->longitude / M_PI * 180.0;
            Init.Pitch = 0.0;
            Init.Bank = 0.0;
            Init.Heading = T->heading * 180.0 / M_PI;
            Init.OnGround = 1;
            Init.Airspeed = 0;
            SimConnect_SetDataOnSimObject(
                hSimConnect, DEFINITION_INIT, SIMCONNECT_OBJECT_ID_USER, 0, 0, sizeof(Init), &Init);

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
            //printf("MSFS: Alt:%f, Head:%f, Lat:%f, Long:%f, mmHG=%f\n", pS->altitude,pS->heading, pS->latitude,pS->longitude,pS->kohlsmann);
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
    hr =
        SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "INDICATED ALTITUDE", "Feet");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "PLANE HEADING DEGREES TRUE","degree");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "PLANE LATITUDE", "radian");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "PLANE LONGITUDE", "radian");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "PLANE PITCH DEGREES","degree");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "PLANE BANK DEGREES","degree");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "AIRSPEED TRUE", "knots");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "AIRSPEED INDICATED","knots");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX_TO_MSFS, "VERTICAL SPEED","Feet per second");

    hr = SimConnect_AddToDataDefinition(hSimConnect, DEFINITION_INIT, "Initial Position", NULL,SIMCONNECT_DATATYPE_INITPOSITION);

    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_DEFINITION, "Kohlsman setting hg", "mbar");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_DEFINITION, "Indicated Altitude", "feet");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_DEFINITION, "Plane Latitude", "degrees");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_DEFINITION, "Plane Longitude", "degrees");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_DEFINITION, "PLANE HEADING DEGREES TRUE","degree");

    hr = SimConnect_RequestDataOnSimObject(hSimConnect, DATA_REQUEST_3, DATA_DEFINITION,SIMCONNECT_OBJECT_ID_USER, SIMCONNECT_PERIOD_SECOND);

    // Request a simulation start event
    hr = SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_SIM_START, "SimStart");
    hr = SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_ONE_SEC, "1sec");

    /////////////////////////////////////////////////////
    //
    //
    // Create custom events. Needs to have a "." in the name to be a custom event
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_QUIT, "Q.toquit");

    // Link the custom event to some keyboard keys, and turn the input event off
    hr = SimConnect_MapInputEventToClientEvent(hSimConnect, INPUT_QUIT, "Q", EVENT_QUIT);
    hr = SimConnect_SetInputGroupState(hSimConnect, INPUT_QUIT, SIMCONNECT_STATE_ON);

    // Sign up for notifications for EVENT_QUIT
    hr = SimConnect_AddClientEventToNotificationGroup(hSimConnect, GROUP0, EVENT_QUIT);
    //
    //////////////////////////////////////////////////////
    // Create custom events. Needs to have a "." in the name to be a custom event
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_INIT, "U.updatepos");

    // Link the custom event to some keyboard keys, and turn the input event off
    hr = SimConnect_MapInputEventToClientEvent(hSimConnect, INPUT_INIT, "U", EVENT_INIT);
    hr = SimConnect_SetInputGroupState(hSimConnect, INPUT_INIT, SIMCONNECT_STATE_ON);

    // Sign up for notifications for EVENT_INIT
    hr = SimConnect_AddClientEventToNotificationGroup(hSimConnect, GROUP1, EVENT_INIT);

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

            T->altitude = 100179;
            T->latitude = 0.87455;
            T->longitude = 0.24891;
            T->pitch = 0.0;
            T->bank = 0.0;
            T->TAS = 0.0;
            T->heading = 58;
            state(T);
    while (!quit) {
        SimConnect_CallDispatch(hSimConnect, ReadPositionFromMSFS, T);
        umain(T);
    };
    
    SimConnect_Close(hSimConnect);


    printf("Normal exit\n");
    return 0;
}
