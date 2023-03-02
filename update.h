
/*
 * Function used to update the lights in MSFS once we got a change in PSX
 */
void updateLights(int *L);


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
void  updatePSXBOOST(double flightDeckAlt,double heading_true, double pitch,double bank, double latitude, double longitude);
