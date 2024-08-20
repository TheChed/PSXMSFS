/*
 * Structures used to communicate between
 * PSX and MSFS
 */

/*
 * Structure storing information send by
 * the boost server and later used to update MSFS
 *
 */

struct MovingParts {
    double GearDown;
    double FlapsPosition;
    double SpeedBrake;
    double rudder;
    double elevator;
    double ailerons;
};

/*
 * Storage for Transation Altitude
 * and tranition level
 */

struct TATL {
    int TA;
    int TL;

    // Flight phase as per Qs392
    // 0 : climb
    // 1: cruise
    // 2: descent

    //int phase;
    enum phase {CLIMB,CRUISE,DESCENT};
};
/*
 * Structures used to update MSFS
 */
enum SpeedType { IAS,
                 TAS,
                 VS };

enum SurfaceType {
    GEAR,
    FLAPS,
    SPEED,
    MOVING
};

struct SpeedUpdate {
    enum SpeedType Type;
    union {
        double IAS;
        double TAS;
        double VS;
    } Speed;
};

struct SurfaceUpdate {
    enum SurfaceType Type;
    union {
        double FlapsPosition;
        double SpeedBrake;
        double GearDown;
        struct {
            double rudder;
            double ailerons;
            double elevator;
        } movingElements;
    } UN;
};

struct SpeedStruct {
    double IAS;
    double TAS;
    double VS;
};
/*
 * Structure containing PSX information
 * we want to update in MSFS
 */
struct PSX {
    // COMMS & XPDR
    int XPDR;
    int IDENT;
    int COM1;
    int COM2;

    // Altimeter
    double altimeter;
    int STD;

    // Speed
    double IAS;
    double GS;
    double TAS;

    // local QNH on the weather zone
    int weatherZone;
    double QNH[8];

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
void SetBARO(DWORD altimeter, int stdbar);
void SetXPDR(int XPDR, int IDENT);
void SetAcftElevation(double elevation);
void resetPSXDATA(void);
double getElevation(void);
int getFlightPhase(void);
DWORD newSituLoaded(FLAGS *flags);
void getTATL(int *TA, int *TL);
/*
 * Function used to update the state inflight<->onground
 */
void SetOnGround(int onGround);

void resetPSXDATA(void);
int GetOnGround(void);

/*
 * Function used to update the speeds in MSFS
 */
void SetSpeed(struct SpeedUpdate *S);

/*
 * Function used to update the moving Surfaces,
 * gear and Speedbrakes in MSFS
 */
void SetMovingSurfaces(struct SurfaceUpdate *S);

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
 * Updates steering wheel position
 */

void updateSteeringWheel(DWORD wheelangle);

/*
 * Functions used to set the weather zones
 */

void setWeather(int zone, double QNH);
void setWeatherZone(int zone);

/*
 * Updates the position of MSFS
 * This function should be called in a frame change event in the callback function
 * and should not be called at will as it leads to some untraceable crashes
 */
void SetMSFSPos(FLAGS *flags);

/*
 * Setting the correct altitude
 * plus some hacks to make the transition ground<->flight smooth in MSFS
 */
double SetAltitude(int onGround, double altfltdeck, double pitch, double PSXELEV, double groundalt);

/*
 * Function used to update the PSXBOOST structure as soon as we got info from PSX
 */
void updatePSXBOOST(double flightDeckAlt, double heading_true, double pitch, double bank, double latitude, double longitude, int onGround);

/*
 * Update gear position
 */
void updateGear(double position);

/*
 * Update gear position
 */
void updateParkingBreak(int position);

