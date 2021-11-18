#include <cstdint>
#include <math.h>
#include <stdio.h>
#include <windows.h>
#include "PSXMSFS.h"
#include "SimConnect.h"

int quit = 0;
HRESULT hr;
target T;

#define max_send_records 20

struct record_struct {
    char call[256];
    DWORD sendid;
};

int record_count = 0;
struct record_struct send_record[max_send_records];

void addSendRecord(char *c) {
    DWORD id;

    if (record_count < max_send_records) {
        int hr = SimConnect_GetLastSentPacketID(hSimConnect, &id);

        strncpy_s(send_record[record_count].call, 255, c, 255);
        send_record[record_count].sendid = id;
        ++record_count;
    }
}

char *findSendRecord(DWORD id) {
    bool found = false;
    int count = 0;
    while (!found && count < record_count) {
        if (id == send_record[count].sendid)
            return send_record[count].call;
        ++count;
    }
    return "Send Record not found";
}

QPSX **Q;

void CALLBACK ReadPositionFromMSFS(SIMCONNECT_RECV *pData, DWORD cbData, void *pContext) {

    HRESULT hr;
    //   printf("pData->dwID: %d received\n",pData->dwID);
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
            state();

            SimResponse SR;
            // SR.altitude = 1179;
            // SR.heading = 58;
            // SR.latitude = 50.1;
            // SR.longitude = -14.26;
            // SR.pitch = 0.0;
            // SR.bank = 0.0;
            // SR.TAS = 0.0;
            // SR.IAS = 0.0;
            // SR.VS = 0.0;

            SR.altitude = T.altitude / 1000.0;
            SR.latitude = T.latitude;
            SR.longitude = T.longitude;
            SR.pitch = 0.0;
            SR.bank = 0.0;
            SR.VS = 0.0;
            SR.IAS = 0.0;
            SR.TAS = 0.0;
            SR.heading = T.heading * 180.0 / M_PI;

            //    hr = SimConnect_SetDataOnSimObject(hSimConnect,
            //    DATA_PSX_TO_MSFS,SIMCONNECT_OBJECT_ID_USER, 0, 0, sizeof(SR), &SR);
        } break;

        case EVENT_INIT: {

            printf("Inside EVENT_INIT\n");
            SIMCONNECT_DATA_INITPOSITION Init;
            printf("A:%f\n", (float)T.altitude);
            printf("Lat:%f\n", T.latitude / M_PI * 180.0);
            printf("Long:%f\n", T.longitude / M_PI * 180);
            printf("P:%f\n", T.pitch * 1000);
            printf("B:%f\n", T.bank * 1000);
            printf("H:%f\n", T.heading * 180.0 / M_PI);

            Init.Altitude = T.altitude / 1000.0;
            Init.Latitude = T.latitude / M_PI * 180.0;
            Init.Longitude = T.longitude / M_PI * 180.0;
            Init.Pitch = 0.0;
            Init.Bank = 0.0;
            Init.Heading = T.heading * 180.0 / M_PI;
            Init.OnGround = 1;
            Init.Airspeed = INITPOSITION_AIRSPEED_KEEP;
            hr = SimConnect_SetDataOnSimObject(
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
            printf("MSFS: Alt:%f, Head:%f, Lat:%f, Long:%f, mmHG=%f\n", pS->altitude,pS->heading, pS->latitude,pS->longitude,pS->kohlsmann);
        } break;

        default:
            printf("dwRequestID: %d received\n", pObjData->dwRequestID);
            break;
        }
        break;
    }

        // Add code to process the structure appropriately

    case SIMCONNECT_RECV_ID_QUIT: {

        printf("\nSIMCONNECT_RECV_ID_QUIT received and data sent");
        quit = 1;
    } break;

    case SIMCONNECT_RECV_ID_EXCEPTION: {
        SIMCONNECT_RECV_EXCEPTION *except = (SIMCONNECT_RECV_EXCEPTION *)pData;
        printf("\n\n***** EXCEPTION=%d  SendID=%d  Index=%d  cbData=%d\n", except->dwException,
               except->dwSendID, except->dwIndex, cbData);

        // Locate the bad call and print it out
        char *s = findSendRecord(except->dwSendID);
        printf("\n%s", s);
        break;
    }

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

    SimConnect_RequestDataOnSimObject(hSimConnect, DATA_REQUEST_3, DATA_DEFINITION,SIMCONNECT_OBJECT_ID_USER, SIMCONNECT_PERIOD_SECOND);

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
    hr = SimConnect_AddClientEventToNotificationGroup(hSimConnect, GROUP0, EVENT_INIT);

    return hr;
}

int main(int argc, char **argv) {

    QPSX **Q;

    if (argc != 3) {
        printf("Usage: %s IP:port\n", argv[0]);
        exit(EXIT_FAILURE);
    }

    // Connect to PSX and MSFS
    init_connect_PSX(argv[1], (int)strtol(argv[2], NULL, 0));
    init_connect_MSFS(&hSimConnect);

    // initialize the data to be received
    hr = init_MS_data();

    // initialize the PSX variables

    Q = (QPSX **)malloc(NB_Q_VAR * sizeof(QPSX));

    init_Q_variables(NB_Q_VAR, Q);

    for (int i = 0; i < NB_Q_VAR; i++) {
        if (Qvariables[i] == NULL) {
            printf("No Variables, exiting...\n");
            exit(EXIT_FAILURE);
        }
    }

    // main loop;
    while (!quit) {
        hr = SimConnect_CallDispatch(hSimConnect, ReadPositionFromMSFS, NULL);
        umain();
        // state();
    };
    hr = SimConnect_Close(hSimConnect);

    for (int i = 0; i < NB_Q_VAR; i++) {
        free(Q[i]);
    }
    free(Q);

    printf("Normal exit\n");
    return 0;
}
