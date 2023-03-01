
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
void SetMSFSPos(void);


/*
 * Setting the correct altitude
 * plus some hacks to make the transition ground<->flight smooth in MSFS
 */
double SetAltitude(int onGround, double altfltdeck, double pitch, double PSXELEV, double groundalt);
