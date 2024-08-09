#include <cmath>
#include <ctime>
#include <cstdlib>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <windows.h>
#include "PSXMSFSLIB.h"
#include "util.h"
#include "update.h"
#include "MSFS.h"

static BOOST PSXBoost;

// static BOOST PSXBoost;
static struct MovingParts APos;
static struct PSX PSXDATA;
static struct SpeedStruct PSXSPEED;
struct INTERNALPSXflags intflags;

double getlocalQNH(void)
{
    return PSXDATA.QNH[PSXDATA.weatherZone] / 2992 * 1013.25;
}

void SetOnGround(int onGround)
{
    PSXBoost.onGround = onGround;
}

int GetOnGround(void)
{
    return PSXBoost.onGround;
}

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

void SetAcftElevation(double elevation)
{
    PSXDATA.acftelev = elevation;
}

int getFlightPhase(void)
{
    return PSXDATA.flightPhase;
}
double getElevation(void)
{
    return PSXDATA.acftelev;
}

void getTATL(int *TA, int *TL)
{
    *TA = PSXDATA.TA;
    *TL = PSXDATA.TL;
}

void Qi198Update(int onGround, double elevation)
{

    int QSentGround, QSentFlight;
    char sQi198[128];

    QSentGround = intflags.Qi198Sentground;
    QSentFlight = intflags.Qi198SentFlight;

    if (!intflags.updateNewSitu) {
        if (onGround || (elevation < 300)) {
            if (!QSentGround) {
                printDebug(LL_INFO, "Below 300 ft AGL => using MSFS elevation.");
                QSentGround = 1;
                QSentFlight = 0;
            }

            if (GetTickCount() > intflags.NewSituTimeLoad + 10000) {
                if (isGroundAltitudeAvailable()) {
                    // wait 10 seconds after new situ loaded to send elevation
                    sprintf(sQi198, "Qi198=%d", (int)(getGroundAltitude() * 100));
                    sendQPSX(sQi198);
                }
            }
        } else {

            if (!QSentFlight) {

                printDebug(LL_INFO, "Above 300 ft AGL => using PSX elevation.");
                sendQPSX("Qi198=-9999999"); // if airborne, use PSX elevation data
                QSentFlight = 1;
                QSentGround = 0;
            }
        }
    }

    intflags.Qi198Sentground = QSentGround;
    intflags.Qi198SentFlight = QSentFlight;
}

double SetAltitude(int onLinHack, int onGround, double altfltdeck, double pitch, double PSXELEV, double groundalt)
{

    double FinalAltitude;
    double ctrAltitude;             // altitude of Aircraft centre
    static double oldctr;           // to keep track of last good altitude
    static double oldctrcrz = -1.0; // to keep track of last good indicated altitude
    static double delta = 0;
    double offset;
    double msfsindalt;
    static double inc = 0;
    static int initalt = 0;
    static double incland = 0;
    static double oldElevation = 0;
    static int takingoff, landing;
    int flightPhase;
    int TA, TL;
    double elevation;
    double deltaPressure, deltaPresureMSFS;

    /*
     * PSXELEV = -999 when we just launched PSXMSFS
     */

    if (PSXELEV == -999 || intflags.updateNewSitu) {
        /*we have a new situ
         * lets reset the static variables
         */

        oldctr = 0;
        oldctrcrz = -1.0; // to keep track of last good indicated altitude
        delta = 0;
        inc = 0;
        initalt = 0;
        incland = 0;
        oldElevation = 0;

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

    /*
     * Check whether we have to send the Qi198
     * variable or not to PSX
     */

    if (initalt) {
        delta = ctrAltitude - oldctr;
    }
    initalt = 1;
    oldctr = ctrAltitude;
    inc += delta;

    /*
     * we received a new elevation from PSX
     * therefore we can reset the decrement
     * used during landing (incland)
     */

    elevation = getElevation();
    incland = ((oldElevation != elevation) ? 0 : +delta);
    oldElevation = elevation;

    /*
     * by default the altitude is the ctrAltitude given by boost
     * But we will adjust that depeding on the flight phase
     * to cater for MSFS discrepencies with PSX
     */

    FinalAltitude = ctrAltitude;

    /*
     * Initialise once a static variable holding the current cruize altitude
     */

    /*
     * If we are crusing, return the pressure altitude to have it correcly
     * displayed in VATSIM or IVAO
     * subject to flag set in ini file
     */

    flightPhase = getFlightPhase();
    msfsindalt = getIndAltitude();
    if (fabs(msfsindalt - ctrAltitude) > 3500) {
        msfsindalt = ctrAltitude;
    }

    /* apply a correction for PSX QNH
     * 27.3 feet per hPa (at sea level
     * as well as for the MSFS QNH pressure, as those might differ
     */
    deltaPressure = (getlocalQNH() - 1013.25) * 27.3;
    deltaPresureMSFS = (getMSL_pressure() - 1013.25) * 27.3;

    offset = ctrAltitude - msfsindalt - (deltaPressure + deltaPresureMSFS);

    getTATL(&TA, &TL);

    if ((flightPhase == 0 && ctrAltitude > TA) || (flightPhase == 2 && ctrAltitude > TL) ||
        flightPhase == 1) {

        if (!intflags.oldcrz) {
            oldctrcrz = ctrAltitude;
            intflags.oldcrz = 1;
        }
        if (onLinHack & F_ONLINE) {
            oldctrcrz += offset / 100;
            FinalAltitude = oldctrcrz;
        }

        takingoff = 0;
        landing = 1; // only choice now is to land !
        return FinalAltitude;
    }

    if (onGround || (PSXELEV + incland < MSFSHEIGHT)) {

        if (fabs(groundalt - ctrAltitude) > 100) {
            groundalt = ctrAltitude;
        }
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

    struct AcftMSFS MSFS;
    double latc, longc, groundAltitude;

    /*
     * Calculate the coordinates from centre aircraft
     * derived from those of the flightDeckAlt
     */
    CalcCoord(PSXBoost.heading_true, PSXBoost.latitude, PSXBoost.longitude, &latc, &longc);

    /*
     * Calculate the altitude depeding on the phase of flight
     * in order to smoothen the ground effects in MSFS
     */

    groundAltitude = getGroundAltitude();
    if (PSXMSFS.switches & F_INJECT)
        Qi198Update(PSXBoost.onGround, PSXDATA.acftelev);
    MSFS.altitude =
        SetAltitude(PSXMSFS.switches & F_ONLINE, PSXBoost.onGround, PSXBoost.flightDeckAlt, -PSXBoost.pitch, PSXDATA.acftelev, groundAltitude);
    MSFS.latitude = latc;
    MSFS.longitude = longc;
    MSFS.pitch = PSXBoost.pitch;
    MSFS.bank = PSXBoost.bank;
    MSFS.heading_true = PSXBoost.heading_true;

    /*
     * And the most important:
     * update positions in MSFS
     * Could be duplicate from the receive frame event in the
     * main callback function
     */

    SimConnect_SetDataOnSimObject(PSXMSFS.hSimConnect, BOOST_TO_MSFS, SIMCONNECT_OBJECT_ID_USER, 0, 0,
                                  sizeof(MSFS), &MSFS);

    SimConnect_SetDataOnSimObject(PSXMSFS.hSimConnect, DATA_SPEED, SIMCONNECT_OBJECT_ID_USER, 0, 0,
                                  sizeof(PSXSPEED), &PSXSPEED);
    SimConnect_SetDataOnSimObject(PSXMSFS.hSimConnect, DATA_MOVING_SURFACES, SIMCONNECT_OBJECT_ID_USER, 0, 0,
                                  sizeof(APos), &APos);
}

void SetMovingSurfaces(struct SurfaceUpdate *S)
{
    switch (S->Type) {
    case GEAR:
        APos.GearDown = S->UN.GearDown;
        break;
    case FLAPS:
        APos.FlapsPosition = S->UN.FlapsPosition;
        break;
    case SPEED:
        APos.SpeedBrake = S->UN.SpeedBrake;
        break;
    case MOVING:
        APos.rudder = S->UN.movingElements.rudder;
        APos.ailerons = S->UN.movingElements.ailerons;
        APos.elevator = S->UN.movingElements.elevator;
        break;
    }
}
void SetSpeed(struct SpeedUpdate *S)
{

    switch (S->Type) {
    case IAS:
        PSXSPEED.IAS = S->Speed.IAS;
        break;
    case TAS:
        PSXSPEED.TAS = S->Speed.TAS;
        break;
    case VS:
        PSXSPEED.VS = S->Speed.VS;
        break;
    }
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
    SimConnect_SetDataOnSimObject(PSXMSFS.hSimConnect, DATA_LIGHT, SIMCONNECT_OBJECT_ID_USER, 0, 0,
                                  sizeof(Lights), &Lights);
}

void setWeatherZone(int zone)
{
    PSXDATA.weatherZone = zone;
}

void setWeather(int zone, double QNH)
{
    PSXDATA.QNH[zone] = QNH;
}

void SetXPDR(int XPDR, int IDENT)
{

    SimConnect_TransmitClientEvent(PSXMSFS.hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_XPDR, XPDR,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST,
                                   SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
    SimConnect_TransmitClientEvent(PSXMSFS.hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_XPDR_IDENT, IDENT,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST,
                                   SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
}

void init_pos(void)
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

    /*--------------------------------------------------------------------------
     * Sending Q423 DEMAND variable to PSX for the winds
     * Sending Q480 DEMAND variable to get aileron, rudder and elevator position
     * Sending Q562 DEMAND variable  to get Baro altitude and STD altitude shown on captain's PFD
     *-------------------------------------------------------------------------*/

    sendQPSX("demand=Qs483");
    sendQPSX("demand=Qs480");
    sendQPSX("demand=Qs562");
}

void updateSteeringWheel(DWORD wheelangle)
{

    SimConnect_TransmitClientEvent(PSXMSFS.hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_STEERING, wheelangle,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST,
                                   SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
}

void updateParkingBreak(int position)
{

    SimConnect_TransmitClientEvent(PSXMSFS.hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_PARKING, position,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST,
                                   SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
}

void SetUTCTime(int hour, int minute, int day, int year)
{

    SimConnect_TransmitClientEvent(PSXMSFS.hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_ZULU_HOURS, hour,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST,
                                   SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
    SimConnect_TransmitClientEvent(PSXMSFS.hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_ZULU_MINUTES,
                                   minute, SIMCONNECT_GROUP_PRIORITY_HIGHEST,
                                   SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
    SimConnect_TransmitClientEvent(PSXMSFS.hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_ZULU_DAY, day,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST,
                                   SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
    SimConnect_TransmitClientEvent(PSXMSFS.hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_ZULU_YEAR, year,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST,
                                   SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
}
void SetCOMM(int COM1, int COM2)
{

    SimConnect_TransmitClientEvent(PSXMSFS.hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_COM, COM1,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST,
                                   SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
    SimConnect_TransmitClientEvent(PSXMSFS.hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_COM_STDBY, COM2,
                                   SIMCONNECT_GROUP_PRIORITY_HIGHEST,
                                   SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
}

void SetBARO(DWORD altimeter, int standard)
{

    SimConnect_TransmitClientEvent(PSXMSFS.hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_BARO,
                                   altimeter * 16, SIMCONNECT_GROUP_PRIORITY_HIGHEST,
                                   SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
    if (standard) {
        SimConnect_TransmitClientEvent(PSXMSFS.hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_BARO_STD, 1,
                                       SIMCONNECT_GROUP_PRIORITY_HIGHEST,
                                       SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
        SimConnect_TransmitClientEvent(PSXMSFS.hSimConnect, SIMCONNECT_OBJECT_ID_USER, EVENT_BARO,
                                       (DWORD)(1013.25 * 16.0), SIMCONNECT_GROUP_PRIORITY_HIGHEST,
                                       SIMCONNECT_EVENT_FLAG_GROUPID_IS_PRIORITY);
    }
}

void resetInternalFlags(void)
{
    intflags.oldcrz = 0;
    intflags.updateNewSitu = 1;
}

DWORD newSituLoaded(void)
{
    int crashInhibit = (PSXMSFS.switches & F_INHIB);
    if (crashInhibit) {
        printDebug(LL_INFO, "No crash detection for 10 seconds");
        sendQPSX("Qi198=-9999910"); // no crash detection fort 10 seconds
    }
    resetInternalFlags();

    printDebug(LL_INFO, "New situ loaded. Resetting some parameters...");
    printDebug(LL_INFO, "Let's wait 2-3 seconds to get everyone ready, shall we?");

    freezeMSFS(1); // New Situ loaded, let's preventively freeze MSFS
    init_variables();

    return GetTickCount();
}

BOOST getPSXBoost(void)
{
    return PSXBoost;
}
BOOST getACFTInfo(void)
{

    return getPSXBoost();
}
