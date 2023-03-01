#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <strings.h>
#include <windows.h>
#include "PSXMSFS.h"
#include "SimConnect.h"
#include "util.h"


void SetMovingSurfaces(void){
    /*
     * Set the moving surfaces: aileron, rudder, elevator

    APos.FlapsPosition = Tmain.FlapLever;
    APos.Speedbrake = Tmain.SpdBrkLever / 800.0;

    APos.rudder = Tmain.rudder;
    APos.ailerons = Tmain.aileron;
    APos.elevator = Tmain.elevator;
     */
}

double SetAltitude(int onGround, double altfltdeck, double pitch, double PSXELEV, double groundalt) {

    static int landing, takingoff;
    double FinalAltitude;
    double ctrAltitude;   // altitude of Aircraft centre
    static double oldctr; // to keep track of last good altitude
    static double delta = 0;
    static double inc = 0;
    static int initalt = 0;
    static double incland = 0;
    static int Qi198SentLand, Qi198SentAirborne;

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

    if (elevupdated) {
        incland = 0;
        elevupdated = 0;
    } else {
        incland += delta;
    }

    if (ELEV_INJECT) {
        if (onGround || (PSXELEV < 300)) {
            if (!Qi198SentLand) {
                printDebug("Below 300 ft AGL => using MSFS elevation", DEBUG);
                Qi198SentLand = 1;
            }
            Qi198SentAirborne = 0;
            sprintf(sQi198, "Qi198=%d", (int)(ground_altitude * 100));
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

    if ((PSXTATL.phase == 0 && ctrAltitude > PSXTATL.TA) || (PSXTATL.phase == 2 && ctrAltitude > PSXTATL.TL) ||
        PSXTATL.phase == 1) {

        if (ONLINE) {
            FinalAltitude = pressure_altitude(PSXDATA.QNH[PSXDATA.weather_zone]) + ctrAltitude;
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
            if (ground_altitude_avail) {
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


void SetMSFSPos(double flightDeckAlt ,double heading, double latitude, double longitude, double bank, double pitch)  {

    struct AcftMSFS MSFS;
    double latc, longc;

    /*
     * Calculate the coordinates from cetre aircraft
     * derived from those of the flightDeckAlt
     */
    CalcCoord(heading,latitude, longitude, &latc, &longc);

    /*
     * Calculate the altitude depeding on the phase of flight
     * in order to smoothen the ground effects in MSFS
     */

    MSFS.altitude =
        SetAltitude(PSX_on_ground, flightDeckAlt, -pitch, PSXDATA.acftelev, ground_altitude);

    MSFS.latitude = latc;
    MSFS.longitude = longc;
    MSFS.pitch = pitch;
    MSFS.bank = bank;
    MSFS.heading_true = heading;

    /*
     * And the most important:
     * update positions in MSFS
     * Could be duplicate from the receive frame event in the
     * main callback function
     */

    SimConnect_SetDataOnSimObject(hSimConnect, DATA_MSFS, SIMCONNECT_OBJECT_ID_USER, 0, 0, sizeof(MSFS), &MSFS);
}


void updateLights(int *L){

   struct AcftLight Lights;
    
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
    if (PSX_on_ground) {
        Lights.LeftRwyTurnoff = 0.0;
        Lights.RightRwyTurnoff = 0.0;
    }
    
    //Finally update in MSFS the lights
    SimConnect_SetDataOnSimObject(hSimConnect, DATA_LIGHT, SIMCONNECT_OBJECT_ID_USER, 0, 0, sizeof(Lights), &Lights);

}

