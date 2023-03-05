#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <strings.h>
#include <windows.h>
#include "PSXMSFS.h"
#include "SimConnect.h"
#include "util.h"
#include "MSFS.h"
#include "update.h"

static struct BOOST PSXBoost;
static struct MovingParts APos;
static struct PSX PSXDATA;
static struct SpeedStruct PSXSPEED;

double getlocalQNH(void) { return PSXDATA.QNH[PSXDATA.weatherZone]; }

void SetOnGround(int onGround) { PSXBoost.onGround = onGround; }

int GetOnGround(void) { return PSXBoost.onGround; }

void updatePSXBOOST(double altitude, double heading, double pitch, double bank, double latitude,
					double longitude, int onGround)
{

	PSXBoost = {.flightDeckAlt = altitude,
				.latitude = latitude,
				.longitude = longitude,
				.heading_true = heading,
				.pitch = pitch,
				.bank = bank,
				.onGround = onGround};
}

struct BOOST getPSXBoost(void) { return PSXBoost; }

void SetMovingSurfaces(double rudder, double aileron, double elevator)
{
	/*
	 * Set the moving surfaces: aileron, rudder, elevator

	APos.Speedbrake = Tmain.SpdBrkLever / 800.0;
	 */

	APos.rudder = rudder;
	APos.ailerons = aileron;
	APos.elevator = elevator;
	SimConnect_SetDataOnSimObject(hSimConnect, DATA_MOVING_SURFACES, SIMCONNECT_OBJECT_ID_USER, 0,
								  0, sizeof(APos), &APos);
}

void updateFlap(int position)
{
	APos.FlapsPosition = position;
	SimConnect_SetDataOnSimObject(hSimConnect, DATA_MOVING_SURFACES, SIMCONNECT_OBJECT_ID_USER, 0,
								  0, sizeof(APos), &APos);
}
void updateGear(double position)
{
	APos.GearDown = position;
	SimConnect_SetDataOnSimObject(hSimConnect, DATA_MOVING_SURFACES, SIMCONNECT_OBJECT_ID_USER, 0,
								  0, sizeof(APos), &APos);
}
void SetSpeedBrake(double position)
{
	APos.Speedbrake = position;
	SimConnect_SetDataOnSimObject(hSimConnect, DATA_MOVING_SURFACES, SIMCONNECT_OBJECT_ID_USER, 0,
								  0, sizeof(APos), &APos);
}

void SetAcftElevation(double elevation)
{
	PSXDATA.acftelev = elevation;
}

int getFlightPhase(void) { return PSXDATA.flightPhase; }
int getElevation(void) { return PSXDATA.acftelev; }

void getTATL(int *TA, int *TL)
{
	*TA = PSXDATA.TA;
	*TL = PSXDATA.TL;
}
double SetAltitude(int onGround, double altfltdeck, double pitch, double PSXELEV, double groundalt)
{

	double FinalAltitude;
	double ctrAltitude;	  // altitude of Aircraft centre
	static double oldctr; // to keep track of last good altitude
	static double delta = 0;
	static double inc = 0;
	static int initalt = 0;
	static double incland = 0;
	static int Qi198SentLand, Qi198SentAirborne;
	static int oldElevation = 0;
	static int takingoff, landing;
	int flightPhase;
	int TA, TL;
	int elevation;

	char sQi198[128];

	/*
	 * PSXELEV = -999 when we just launched PSXMSFS
	 */

	if (PSXELEV == -999) {
		return altfltdeck;
	}

	/*
	 * Boost servers gives altitude of flight deck
	 * Need to get the altitude of the Aircraft centre first
	 */
	ctrAltitude = altfltdeck - (28.412073 + 92.5 * sin(pitch));

	/*
	 * Now check if we are close to the ground or not
	 * by checking the Variable Qi219 from PSX
	 * that give the acft height above ground
	 * We assume that below 50 feet we are close
	 */
	landing = (PSXELEV < 50);

	if (initalt) {
		delta = ctrAltitude - oldctr;
	}
	initalt = 1;
	oldctr = ctrAltitude;
	inc += delta;

	/*
	 * we received a new elevation from PSX
	 * therefore we can reset the decrement (incland)
	 */

	elevation = getElevation();
	incland = ((oldElevation != elevation) ? 0 : +delta);
	oldElevation = elevation;

	if (ELEV_INJECT) {
		if (onGround || (PSXELEV < 300)) {
			if (!Qi198SentLand) {
				printDebug("Below 300 ft AGL => using MSFS elevation", DEBUG);
				Qi198SentLand = 1;
			}
			Qi198SentAirborne = 0;
			sprintf(sQi198, "Qi198=%d", (int)(getGroundAltitude() * 100));
			sendQPSX(sQi198);
		} else {

			if (!Qi198SentAirborne) {

				printDebug("Above 300 ft AGL => using PSX elevation.", DEBUG);
				sendQPSX("Qi198=-999999"); // if airborne, use PSX elevation data
				Qi198SentAirborne = 1;
			}
			Qi198SentLand = 0;
		}
	}

	/*
	 * by default the altitude is the ctrAltitude given by boost
	 * But we will adjust that depeding on the flight phase
	 * to cater for MSFS discrepencies with PSX
	 */

	FinalAltitude = ctrAltitude;

	/*
	 * If we are crusing, return the pressure altitude to have it correcly
	 * displayed in VATSIM or IVAO
	 */

	flightPhase = getFlightPhase();

	getTATL(&TA, &TL);

	if ((flightPhase == 0 && ctrAltitude > TA) || (flightPhase == 2 && ctrAltitude > TL) ||
		flightPhase == 1) {

		if (ONLINE) {
			FinalAltitude = pressure_altitude(getlocalQNH()) + ctrAltitude;
		}

		takingoff = 0;
		landing = 1; // only choice now is to land !
		return FinalAltitude;
	}

	if (onGround || (PSXELEV + incland < MSFSHEIGHT)) {
		FinalAltitude = groundalt + MSFSHEIGHT;
		inc = 0;
		landing = 0;
		takingoff = 1; // what else can we do except to take off ?

	} else {
		if (takingoff && inc < 300) {
			if (isGroundAltitudeAvailable()) {
				FinalAltitude = groundalt + MSFSHEIGHT + inc;
			}
		} else {
			if (landing) {
				FinalAltitude = groundalt + PSXELEV + incland;
			}
		}
	}

	return FinalAltitude;
}

void SetMSFSPos()
{

	struct BOOST PSX;
	struct AcftMSFS MSFS;
	double latc, longc, groundAltitude;

	PSX = getPSXBoost();

	/*
	 * Calculate the coordinates from cetre aircraft
	 * derived from those of the flightDeckAlt
	 */
	CalcCoord(PSX.heading_true, PSX.latitude, PSX.longitude, &latc, &longc);

	/*
	 * Calculate the altitude depeding on the phase of flight
	 * in order to smoothen the ground effects in MSFS
	 */

	groundAltitude = getGroundAltitude();
	MSFS.altitude =
		SetAltitude(PSX.onGround, PSX.flightDeckAlt, -PSX.pitch, PSXDATA.acftelev, groundAltitude);

	MSFS.latitude = latc;
	MSFS.longitude = longc;
	MSFS.pitch = PSX.pitch;
	MSFS.bank = PSX.bank;
	MSFS.heading_true = PSX.heading_true;

	/*
	 * And the most important:
	 * update positions in MSFS
	 * Could be duplicate from the receive frame event in the
	 * main callback function
	 */

	SimConnect_SetDataOnSimObject(hSimConnect, BOOST_TO_MSFS, SIMCONNECT_OBJECT_ID_USER, 0, 0,
								  sizeof(MSFS), &MSFS);
	SimConnect_SetDataOnSimObject(hSimConnect, DATA_SPEED, SIMCONNECT_OBJECT_ID_USER, 0, 0,
								  sizeof(PSXSPEED), &PSXSPEED);
}

void SetSpeedIAS(double IAS) { 

    PSXSPEED.IAS = IAS;
  //  PSXSPEED.GS=575.0;
    PSXSPEED.TAS=0.0;
    PSXSPEED.VS=0.0;
    
}

void updateFlightPhase(int phase, int TA, int TL)
{
	PSXDATA.flightPhase = phase;
	PSXDATA.TA = TA;
	PSXDATA.TL = TL;
}

void updateLights(int *L)
{

	struct AcftLight Lights;
	int onGround;

	//  Update lights
	Lights.LandLeftOutboard = L[0];
	Lights.LandLeftInboard = L[2];
	Lights.LandRightInboard = L[3];
	Lights.LandRightOutboard = L[1];
	Lights.LeftRwyTurnoff = L[4];
	Lights.RightRwyTurnoff = L[5];
	Lights.LightTaxi = L[6];
	Lights.Strobe = L[11];
	Lights.LightNav = (L[9] || L[10]);
	Lights.Beacon = L[7];
	Lights.BeaconLwr = L[8];
	Lights.LightWing = L[12];
	Lights.LightLogo = L[13];
	// Taxi lights disabled airborne
	onGround = GetOnGround();
	if (onGround) {
		Lights.LeftRwyTurnoff = 0.0;
		Lights.RightRwyTurnoff = 0.0;
	}

	// Finally update in MSFS the lights
	SimConnect_SetDataOnSimObject(hSimConnect, DATA_LIGHT, SIMCONNECT_OBJECT_ID_USER, 0, 0,
								  sizeof(Lights), &Lights);
}

void setWeatherZone(int zone) { PSXDATA.weatherZone = zone; }

void setWeather(int zone, double QNH) { PSXDATA.QNH[zone] = QNH; }

void SetXPDR(int XPDR, int IDENT)
{

	SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_XPDR, XPDR,
								   SIMCONNECT_GROUP_PRIORITY_HIGHEST,
								   SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
	SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_XPDR_IDENT, IDENT,
								   SIMCONNECT_GROUP_PRIORITY_HIGHEST,
								   SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
}

void init_pos()
{

	/*
	 * Setting initial parameters
	 * for the structures used in communicating
	 * between PSX and MSFS
	 */

	PSXDATA = {.XPDR = 0000,
			   .IDENT = 0,
			   .COM1 = 122800,
			   .COM2 = 122800,
			   .altimeter = 1013,
			   .STD = 0,
			   .IAS = 0,
			   .GS = 0,
			   .TAS = 0,
			   .weatherZone = 0,
			   .QNH = {2992},
			   .acftelev = -999,
			   .elevupdated = 0,
			   .flightPhase = 0, // onground by default
			   .TA = 18000,
			   .TL = 18000};
}

void updateParkingBreak(int position)
{

	SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_PARKING, position,
								   SIMCONNECT_GROUP_PRIORITY_HIGHEST,
								   SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
}

void SetUTCTime(int hour, int minute, int day, int year)
{

	SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_ZULU_HOURS, hour,
								   SIMCONNECT_GROUP_PRIORITY_HIGHEST,
								   SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
	SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_ZULU_MINUTES,
								   minute, SIMCONNECT_GROUP_PRIORITY_HIGHEST,
								   SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
	SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_ZULU_DAY, day,
								   SIMCONNECT_GROUP_PRIORITY_HIGHEST,
								   SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
	SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_ZULU_YEAR, year,
								   SIMCONNECT_GROUP_PRIORITY_HIGHEST,
								   SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
}
void SetCOMM(int COM1, int COM2)
{

	SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_COM, COM1,
								   SIMCONNECT_GROUP_PRIORITY_HIGHEST,
								   SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
	SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_COM_STDBY, COM2,
								   SIMCONNECT_GROUP_PRIORITY_HIGHEST,
								   SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
}

void SetBARO(long altimeter, int standard)
{

	SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_BARO,
								   altimeter * 16.0, SIMCONNECT_GROUP_PRIORITY_HIGHEST,
								   SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
	if (standard) {
		SimConnect_TransmitClientEvent(hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_BARO_STD, 1,
									   SIMCONNECT_GROUP_PRIORITY_HIGHEST,
									   SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
	}
}
