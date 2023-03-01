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


    APos.ias = PSXDATA.IAS;

    /*
     * And the most important:
     * update positions in MSFS
     * Could be duplicate from the receive frame event in the
     * main callback function
     */

    SimConnect_SetDataOnSimObject(hSimConnect, DATA_MSFS, SIMCONNECT_OBJECT_ID_USER, 0, 0, sizeof(APos), &APos);
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

