/*
 * Structures used to communicate between
 * PSX and MSFS
 */

/*
 * Structure storing information send by
 * the boost server and later used to update MSFS
 */

struct BOOST {
    // Updated by Boost server
    double flightDeckAlt;
    double latitude;
    double longitude;
    double heading_true;
    double pitch;
    double bank;
    int onGround;
};

struct MovingParts{
    double GearDown;
    double FlapsPosition;
    double Speedbrake;
    double rudder;
    double elevator;
    double ailerons;
};

struct SpeedStruct{
    double IAS;
    double TAS;
  //  double GS;
    double VS;
};
/*
 * Structure containing PSX information
 * we want to update in MSFS
 */
struct PSX{
    //COMMS & XPDR
    int XPDR;
    int IDENT;
    int COM1;
    int COM2;

    //Altimeter
    double altimeter;
    int STD ;

    //Speed
    double IAS;
    double GS;
    double TAS;

    //local QNH on the weather zone
    int weatherZone;
    double QNH[7];

    /*
     * Acft elevation and indicator whether it has been 
     *  updated from PSX
     */
    double acftelev;
    int elevupdated;
    
    int flightPhase;
    int TA;
    int TL;

};

/*
 * Functions declaration
 */
void SetUTCTime(int hour, int minute, int day, int year);
void SetCOMM(int COM1, int COM2);
void SetBARO(long altimeter, int stdbar);
void SetXPDR(int XPDR, int IDENT); 
void SetAcftElevation(double elevation);
void init_pos(void);
int getFlightPhase(void);
void getTATL(int *TA, int *TL);
/*
 * Function used to update the state inflight<->onground
 */
void SetOnGround(int onGround);

int GetOnGround(void);


void SetSpeedIAS(double IAS);

 /*
 * Function used to update the lights in MSFS once we got a change in PSX
 */
void updateLights(int *L);
 /*
 * Function used to update the flight phase (landing, cruise, takingoff)
 * as well as the TL and TA as set up in PSX
 */
void updateFlightPhase(int phase, int TA, int TL);

/*
 * Functions used to set the weather zones
 */

void setWeather(int zone, double QNH);
void setWeatherZone(int zone);

/*
 * Function used to update the moving surface in MSFS once we got a change in PSX
 */
void SetMovingSurfaces(void);

/*
 * Updates the position of MSFS 
 * This function should be called in a frame change event in the callback function
 * and should not be called at will as it leads to some untraceable crashes
 */
void SetMSFSPos(double flightDeckAlt, double heading, double latitude, double longitude,
		double bank, double pitch);

/*
 * Setting the correct altitude
 * plus some hacks to make the transition ground<->flight smooth in MSFS
 */
double SetAltitude(int onGround, double altfltdeck, double pitch, double PSXELEV, double groundalt);


/*
 * Function used to update the PSXBOOST structure as soon as we got info from PSX
 */
void  updatePSXBOOST(double flightDeckAlt,double heading_true, double pitch,double bank, double latitude, double longitude, int onGround);

/*
 * Update gear position
 */
void updateGear(double position);

/*
 * Update gear position
 */
void updateParkingBreak(int position);

/*
 * Update Flapposition
 */
void updateFlap(int position);

/*
 * Update moving surfaces
 */
void SetMovingSurfaces(double rudder, double aileron, double elevator);

/*
 * Update Speedbrake
 */
void SetSpeedBrake(double position);
