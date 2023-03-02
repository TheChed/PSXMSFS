// Copyright (c) Asobo Studio, All rights reserved. www.asobostudio.com
//------------------------------------------------------------------------------
//
//  SimConnect Data Request Sample
//
//	Description:
//				After a flight has loaded, request the
// lat/lon/alt of the user 				aircraft
//------------------------------------------------------------------------------

#include <pthread.h>
#include <stdio.h>
#include <strsafe.h>
#include <tchar.h>
#include <windows.h>

#include "SimConnect.h"

int quit = 0;
HANDLE hSimConnect = NULL;
void update_pos();

double heading = 0.0;
double lat = 0.8552322;

struct AcftPosition {
  double altitude;
  double latitude;
  double longitude;
  double heading_true;
  double pitch;
};

struct AcftPosition_BIG {
  double altitude;
  double latitude;
  double longitude;
  double heading_true;
  double pitch;
  double bank;
  double tas;
  double ias;
  double vertical_speed;
  double GearDown;
  double FlapsPosition;
  double Speedbrake;
  // Lights
  double LandLeftOutboard;  // L Outboard
  double LandLeftInboard;   // L Inboard
  double LandRightInboard;  // R Inboard
  double LandRightOutboard; // R Outboard
  double LeftRwyTurnoff;    // L Runway Turnoff light
  double RightRwyTurnoff;   // R Runway Turnoff light
  double LightTaxi;	    // Taxi light
  double LightNav;	    // Nav light
  double Strobe;	    // Strobe light
  double BeaconLwr;	    // Lower Beacon light
  double Beacon;	    // Both Beacon light
  double LightWing;	    // Wing light
  double LightLogo;	    // Wing light
  // moving surfaces
  double rudder;
  double elevator;
  double ailerons;
};

struct AcftPosition APos;
struct AcftPosition_BIG APosBIG;

enum EVENT_ID {
  EVENT_SIM_START,
  EVENT_ONE_SEC,
  EVENT_6_HZ,
  EVENT_4_SEC,
  EVENT_FRAME,
  EVENT_FREEZE_ALT,
  EVENT_FREEZE_ATT,
  EVENT_FREEZE_LAT_LONG
};

enum DATA_DEFINE_ID { DATA_PSX, BIG_DATA_PSX };

enum DATA_REQUEST_ID {
  REQUEST_1,
  REQUEST_2,
};

void CALLBACK MyDispatchProcRD(SIMCONNECT_RECV *pData, DWORD cbData, void *pContext)
{
  HRESULT hr;
  static int nb = 0;
  switch (pData->dwID) {
  case SIMCONNECT_RECV_ID_EVENT: {
    SIMCONNECT_RECV_EVENT *evt = (SIMCONNECT_RECV_EVENT *)pData;

    switch (evt->uEventID) {
    case EVENT_SIM_START:

      break;
    case EVENT_ONE_SEC:

      break;
    case EVENT_4_SEC:
      break;

    default:
      break;
    }
    break;
  }

  case SIMCONNECT_RECV_ID_EVENT_FRAME: {
    // lat = lat +0.01;
    printf("Frame received:%d\r", nb);
    nb++;
    //      hr = SimConnect_SetDataOnSimObject(hSimConnect, DATA_PSX,
    //      SIMCONNECT_OBJECT_ID_USER, 0, 0, sizeof(APos), &APos);
    hr = SimConnect_SetDataOnSimObject(hSimConnect, BIG_DATA_PSX, SIMCONNECT_OBJECT_ID_USER, 0, 0,
				       sizeof(APosBIG), &APosBIG);
  } break;
  case SIMCONNECT_RECV_ID_SIMOBJECT_DATA_BYTYPE: {

    break;
  }
  case SIMCONNECT_RECV_ID_QUIT: {
    quit = 1;
    break;
  }

  default:
    break;
  }
}

void update_pos()
{
  APosBIG.altitude = 360.5; // 358 + 15.6;
  APosBIG.latitude = 0.8552322;
  // APosBIG.latitude = APosBIG.latitude + 0.01;
  APosBIG.longitude = 0.044982;
  APosBIG.heading_true = APosBIG.heading_true + 0.01;
  APosBIG.pitch = 0.0;
  APosBIG.bank = 0.0;
  APosBIG.tas = 0.0;
  APosBIG.ias = 0.0;
  APosBIG.vertical_speed = 0.0;
  APosBIG.FlapsPosition = 0.0; // Flaps down
  APosBIG.Speedbrake = 0.0;    // Spoilers down
  APosBIG.GearDown = 1.0;

  // All lights off
  APosBIG.LandLeftOutboard = 0.0;
  APosBIG.LandLeftInboard = 0.0;
  APosBIG.LandRightInboard = 0.0;
  APosBIG.LandRightOutboard = 0.0;
  APosBIG.LeftRwyTurnoff = 0.0;
  APosBIG.RightRwyTurnoff = 0.0;
  APosBIG.LightTaxi = 0.0;
  APosBIG.Strobe = 0.0;
  APosBIG.LightNav = 0.0;
  APosBIG.Beacon = 0.0;
  APosBIG.BeaconLwr = 0.0;
  APosBIG.LightWing = 0.0;
  APosBIG.LightLogo = 0.0;
  APosBIG.rudder = 0.0;
  APosBIG.ailerons = 0.0;
  APosBIG.elevator = 0.0;
}

void init_pos()
{
  APosBIG.altitude = 360.5; // 358 + 15.6;
  APosBIG.latitude = 0.8552322;
  APosBIG.longitude = 0.044982;
  APosBIG.heading_true = 6.2133717;
  APosBIG.pitch = 0.0;
  APosBIG.bank = 0.0;
  APosBIG.tas = 0.0;
  APosBIG.ias = 0.0;
  APosBIG.vertical_speed = 0.0;
  APosBIG.FlapsPosition = 0.0; // Flaps down
  APosBIG.Speedbrake = 0.0;    // Spoilers down
  APosBIG.GearDown = 1.0;

  // All lights off
  APosBIG.LandLeftOutboard = 0.0;
  APosBIG.LandLeftInboard = 0.0;
  APosBIG.LandRightInboard = 0.0;
  APosBIG.LandRightOutboard = 0.0;
  APosBIG.LeftRwyTurnoff = 0.0;
  APosBIG.RightRwyTurnoff = 0.0;
  APosBIG.LightTaxi = 0.0;
  APosBIG.Strobe = 0.0;
  APosBIG.LightNav = 0.0;
  APosBIG.Beacon = 0.0;
  APosBIG.BeaconLwr = 0.0;
  APosBIG.LightWing = 0.0;
  APosBIG.LightLogo = 0.0;
  APosBIG.rudder = 0.0;
  APosBIG.ailerons = 0.0;
  APosBIG.elevator = 0.0;
}

void testDataRequest()
{
  HRESULT hr;

  if (SUCCEEDED(SimConnect_Open(&hSimConnect, "Request Data", NULL, 0, 0, 0))) {

    // Set up the data definition, but do not yet do anything with it

    // Request an event when the simulation starts
    hr = SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_SIM_START, "SimStart");

    // Request an event every second
    hr = SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_ONE_SEC, "1sec");
    hr = SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_4_SEC, "4sec");
    hr = SimConnect_SubscribeToSystemEvent(hSimConnect, EVENT_FRAME, "frame");

    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX, "PLANE ALTITUDE", "feet");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX, "PLANE LATITUDE", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX, "PLANE LONGITUDE", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX, "PLANE HEADING DEGREES TRUE",
					"radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, DATA_PSX, "PLANE PITCH DEGREES", "radians");

    hr = SimConnect_AddToDataDefinition(hSimConnect, BIG_DATA_PSX, "PLANE ALTITUDE", "feet");
    hr = SimConnect_AddToDataDefinition(hSimConnect, BIG_DATA_PSX, "PLANE LATITUDE", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, BIG_DATA_PSX, "PLANE LONGITUDE", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, BIG_DATA_PSX, "PLANE HEADING DEGREES TRUE",
					"radians");
    hr =
	SimConnect_AddToDataDefinition(hSimConnect, BIG_DATA_PSX, "PLANE PITCH DEGREES", "radians");
    hr = SimConnect_AddToDataDefinition(hSimConnect, BIG_DATA_PSX, "PLANE BANK DEGREES", "radians");

    hr = SimConnect_AddToDataDefinition(hSimConnect, BIG_DATA_PSX, "AIRSPEED TRUE", "knot");
    hr = SimConnect_AddToDataDefinition(hSimConnect, BIG_DATA_PSX, "AIRSPEED INDICATED", "knot");
    hr = SimConnect_AddToDataDefinition(hSimConnect, BIG_DATA_PSX, "VERTICAL SPEED",
					"feet per minute");

    /*Surfaces attributes*/

    hr = SimConnect_AddToDataDefinition(hSimConnect, BIG_DATA_PSX, "GEAR HANDLE POSITION",
					"percent over 100");
    hr = SimConnect_AddToDataDefinition(hSimConnect, BIG_DATA_PSX, "FLAPS HANDLE INDEX", "number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, BIG_DATA_PSX, "SPOILERS HANDLE POSITION",
					"position");

    /*
     * Data definition for lights. Even though in the SDK documentation they are
     * defined as non settable, Setting them like this works just fine.
     * Alternative is to use EVENTS, but in that case all 4 landing light
     * switches cannot be synchronised.
     */

    hr = SimConnect_AddToDataDefinition(hSimConnect, BIG_DATA_PSX, "LIGHT LANDING:1", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, BIG_DATA_PSX, "LIGHT LANDING:2", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, BIG_DATA_PSX, "LIGHT LANDING:3", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, BIG_DATA_PSX, "LIGHT LANDING:4", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, BIG_DATA_PSX, "LIGHT TAXI:1", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, BIG_DATA_PSX, "LIGHT TAXI:2", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, BIG_DATA_PSX, "LIGHT TAXI:3", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, BIG_DATA_PSX, "LIGHT NAV", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, BIG_DATA_PSX, "LIGHT STROBE", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, BIG_DATA_PSX, "LIGHT BEACON:1", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, BIG_DATA_PSX, "LIGHT BEACON:2", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, BIG_DATA_PSX, "LIGHT WING", "Number");
    hr = SimConnect_AddToDataDefinition(hSimConnect, BIG_DATA_PSX, "LIGHT LOGO", "Number");

    /*
     * Moving Surfaces: Ailerons, rudder , elevator
     *
     */

    hr = SimConnect_AddToDataDefinition(hSimConnect, BIG_DATA_PSX, "RUDDER POSITION",
					"position 16K");
    hr = SimConnect_AddToDataDefinition(hSimConnect, BIG_DATA_PSX, "ELEVATOR POSITION",
					"position 16K");
    hr = SimConnect_AddToDataDefinition(hSimConnect, BIG_DATA_PSX, "AILERON POSITION",
					"position 16K");

    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_FREEZE_ALT, "FREEZE_ALTITUDE_SET");
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_FREEZE_ATT, "FREEZE_ATTITUDE_SET");
    hr = SimConnect_MapClientEventToSimEvent(hSimConnect, EVENT_FREEZE_LAT_LONG,
					     "FREEZE_LATITUDE_LONGITUDE_SET");

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
}

void *ptUmain(void *thread_param)
{
  (void)(thread_param);

  while (!quit) {
    SimConnect_CallDispatch(hSimConnect, MyDispatchProcRD, NULL);
    Sleep(1);
  }
  return NULL;
}
void *ptpos(void *thread_param)
{
  (void)(thread_param);

  while (!quit) {
    heading = heading + 0.01;
    update_pos();
    Sleep(1);
  }
  return NULL;
}

int __cdecl _tmain(int argc, _TCHAR *argv[])
{

  pthread_t t1, t2;
  init_pos();
  testDataRequest();
  if (pthread_create(&t1, NULL, &ptUmain, NULL) != 0) {
    printf("Error creating thread Umain");
  }
  if (pthread_create(&t2, NULL, &ptpos, NULL) != 0) {
    printf("Error creating thread Umain");
  }

  if (pthread_join(t1, NULL) != 0) {
    printf("Failed to join Main thread");
  }
  if (pthread_join(t2, NULL) != 0) {
    printf("Failed to join Main thread");
  }

  SimConnect_Close(hSimConnect);
  return 0;
}
