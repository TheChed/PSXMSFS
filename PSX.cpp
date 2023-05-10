#include <cstdlib>
#include <math.h>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <strings.h>
#include <sys/time.h>
#include <sys/types.h>
#include <time.h>
#include <unistd.h>
#include <windows.h>
#include "PSXMSFS.h"
#include "SimConnect.h"
#include "util.h"
#include "update.h"
#include "MSFS.h"

#define VSSAMPLE 50 // number of samples used from boost string to calculate the vertical speed

size_t bufboost_used = 0;
size_t bufmain_used = 0;
char bufboost[256];
char bufmain[4096];

// Position of Gear

void H170(char *s)
{
	int gearpos;
	struct SurfaceUpdate S;

	gearpos = (int)(s[6] - '0');
	S.Type = GEAR;
	S.UN.GearDown = ((gearpos == 3) ? 1.0 : 0.0);
	SetMovingSurfaces(&S);
}

// Flap lever variable Qh389
void H389(const char *s)
{
	struct SurfaceUpdate S;
	int position;

	position = (int)(s[6] - '0');

	S.Type = FLAPS;
	S.UN.FlapsPosition = position;
	SetMovingSurfaces(&S);
}

// Parking break
void H397(const char *s)
{
	int position;
	position = (int)(s[6] - '0');
	updateParkingBreak(position);
}

// Steering wheel
void H426(const char *s)
{
	double pos;

	pos = strtol(s + 6, NULL, 10) / 999.0 * 16384.0;
	if (abs(pos) > 16385) {
		pos = 0;
	}

	updateSteeringWheel(pos);
}

// Speedbrake lever variable Qh389
void H388(char *s)
{
	double SpeedBrakelevel = 0;
	struct SurfaceUpdate S;
	char *token, *ptr, *savptr;
	if ((token = strtok_r(s + 6, DELIM, &savptr)) != NULL) {
		SpeedBrakelevel = strtol(token, &ptr, 10);
	}
	S.Type = SPEED;
	S.UN.SpeedBrake = SpeedBrakelevel;
	SetMovingSurfaces(&S);
}

void S121(char *s)
{

	char *token, *savptr;
	struct SpeedUpdate SU;

	token = strtok_r(s + 6, DELIM, &savptr);
	token = strtok_r(NULL, DELIM, &savptr);
	token = strtok_r(NULL, DELIM, &savptr);
	token = strtok_r(NULL, DELIM, &savptr);
	if ((token = strtok_r(NULL, DELIM, &savptr)) != NULL) {
		SU.Speed.TAS = (double)strtoul(token, NULL, 10) / 1000.0;
	}
	SU.Type = TAS;
	SetSpeed(&SU);
}

void S483(char *s)
{
	char *token, *ptr;
	struct SpeedUpdate SU;

	if ((token = strtok_r(s + 6, DELIM, &ptr)) != NULL) {
		SU.Speed.IAS = strtol(token, NULL, 10) / 10.0;
	}
	SU.Type = IAS;
	SetSpeed(&SU);
}

void S392(char *s)
{
	char *token, *savptr;
	int TA, TL;
	int flightPhase;

	// TA and TL are the 2nd and 3rd token
	token = strtok_r(s, DELIM, &savptr);

	/*
	 * We now try to get the flight phase
	 */

	flightPhase = token[9] - '0';

	if ((token = strtok_r(NULL, DELIM, &savptr)) != NULL) {
		TA = (int)strtoul(token, NULL, 10);
	}
	if ((token = strtok_r(NULL, DELIM, &savptr)) != NULL) {
		TL = (int)strtoul(token, NULL, 10);
	}
	updateFlightPhase(flightPhase, TA, TL);
}
void S78(const char *s)
{

	if (strstr(s, "MSFS")) {
		flags.SLAVE = 1;
	} else {
		if (strstr(s, "PSX")) {
			flags.SLAVE = 0;
		}
	}
}

void S448(char *s)
{

	char *token, *ptr, *savptr;
	int stdbar;
	long altimeter;

	/* get the first token
	 * Altimeter setting is the 4th token
	 * 5th token is STD setting
	 */

	token = strtok_r(s + 6, DELIM, &savptr);
	token = strtok_r(NULL, DELIM, &savptr);
	token = strtok_r(NULL, DELIM, &savptr);

	if ((token = strtok_r(NULL, DELIM, &savptr)) != NULL) {
		altimeter = strtol(token, &ptr, 10) / 100.0;
	}
	/* STD setting*/
	if ((token = strtok_r(NULL, DELIM, &savptr)) != NULL) {
		stdbar = ((abs(strtod(token, NULL)) == 1) ? 0 : 1);
	}
	SetBARO(altimeter, stdbar);
}

void S458(char *s)
{
	int C1, C2;
	char COM1[9] = {0}, COM2[9] = {0};
	/*
	 * discard the last digit from the Qs string as it is not taken into MSFS.
	 * and start at second digit, as first one is always 1
	 */
	strncpy(COM1, s + 6, 3);
	strncat(COM1, s + 10, 3);
	strcat(COM1, "000");

	C1 = strtol(COM1, NULL, 10);

	if (C1 < 118000000 || C1 > 136990000) {
		C1 = 122800000;
	}

	strncpy(COM2, s + 13, 3);
	strncat(COM2, s + 17, 3);
	strcat(COM2, "000");

	C2 = strtol(COM2, NULL, 10);
	if (C2 < 118000000 || C2 > 136990000) {
		C2 = 122800000;
	}
	SetCOMM(C1, C2);
}
void S480(char *s)
{
	double rudder, aileron, elevator;
	struct SurfaceUpdate S;
	int val[10];
	for (int i = 0; i < 10; i++) {
		val[i] = (s[2 * i + 6] - '0') * 10 + (s[2 * i + 1 + 6] - '0');
	}

	rudder = 16384 * ((val[8] + val[9]) / 2 - 32) / 32.0; // maximum deflection = 64
	aileron = -16384 * (val[0] - 20) / 20.0;			  // maximum deflection in PSX  = 40
	elevator = 16384 * (val[6] - 21) / 21.0;			  // maximum deflection in PSX = 42
														  //
	S.Type = MOVING;
	S.UN.movingElements.rudder = rudder;
	S.UN.movingElements.ailerons = aileron;
	S.UN.movingElements.elevator = elevator;
	SetMovingSurfaces(&S);
}

void S124(const char *s)
{

	int hour, minute, day, year;
	struct tm *time_PSX;
	time_t timeUTC;

	timeUTC = strtoll(s + 6, NULL, 10) / 1000;

	if ((time_PSX = gmtime(&timeUTC)) == NULL) {
		printDebug(LL_ERROR, "Error creating timePSX");
		return;
	}

	year = time_PSX->tm_year + 1900; // year starts in 1900
	day = time_PSX->tm_yday + 1;	 // nb days since January 1st, starts at 0
	hour = time_PSX->tm_hour;
	minute = time_PSX->tm_min;

	SetUTCTime(hour, minute, day, year);
}

void S443(const char *s)
{

	int *light = (int *)malloc(14 * sizeof(int));

	for (int i = 0; i < 14; i++) {
		light[i] = (int)(s[i + 6] - '0') < 5 ? 0 : 1;
	}
	updateLights(light);
	free(light);
}

void I240(const char *s)
{

	int zone;

	zone = strtoul(s + 6, NULL, 10);
	if (zone < 0 || zone > 7) {
		zone = 0;
	}
	setWeatherZone(zone);
	printDebug(LL_DEBUG, "Active weather zone: %d\t", zone);
}
void I204(const char *s)
{
	int XPDR = 2000, IDENT = 0;

	XPDR = strtol(s + 8, NULL, 16);
	if (isdigit(s[7])) {
		IDENT = (int)(s[7] - '0');
	} else {
		IDENT = 0;
	}

	SetXPDR(XPDR, IDENT);
}

void I257(const char *s)
{

	int onGround = (int)(s[6] - '0');
	SetOnGround(onGround);
}

void I219(const char *s)
{
	double acftelev;
	acftelev = strtol(s + 6, NULL, 10);
	SetAcftElevation(acftelev); // we got a fresh elevation
}

void Qsweather(char *s)
{

	char *token, *savptr;
	int zone;
	double QNH;
	char sav[128];

	/* Get the active zone */

	zone = (int)strtoul(s + 2, NULL, 10) - 328; // Because the first zone is Qs328

	if ((token = strtok_r(s + 6, DELIM, &savptr)) != NULL) {

		// last token is the QNH, need to save a copy before it is set to NULL
		while (token) {
			strcpy(sav, token);
			token = strtok_r(NULL, DELIM, &savptr);
		}
	}

	if (zone >= 0 && zone < 8) {
		QNH = strtoul(sav, NULL, 10);
		setWeather(zone, QNH);
	}
	printDebug(LL_DEBUG, "Weather zone: %d\t QNH:%.2f", zone, QNH);
}

double calcVS(double alt, int ms)
{
	static double altarray[VSSAMPLE];
	static int timearray[VSSAMPLE];
	static int nbiter;
	double altdiff = 0;
	double VS = 0;
	int lapsedtime = 0;

	altarray[nbiter] = alt;
	timearray[nbiter] = ms;
	nbiter++;
	if (nbiter == VSSAMPLE) {
		for (int i = 0; i < VSSAMPLE - 1; i++) {
			lapsedtime += ((timearray[i + 1] - timearray[i] + 1000) % 1000);
			altdiff += (altarray[i + 1] - altarray[i]);
		}
		memmove(&altarray[0], &altarray[1], sizeof(double) * (VSSAMPLE - 1));
		memmove(&timearray[0], &timearray[1], sizeof(int) * (VSSAMPLE - 1));
		if (lapsedtime)
			VS = 60 * 1000 * altdiff / lapsedtime;
		else
			VS = 0;
		lapsedtime = 0;
		nbiter = VSSAMPLE - 1; // to update the last item of the array
		altdiff = 0;
	}
	return VS;
}
void Decodeboost(char *s)
{

	double flightDeckAlt, heading_true, pitch, bank;
	double latitude, longitude;
	int onGround, ms;
	char *token, *ptr, *savptr;

	struct SpeedUpdate SU;

	/* get the first token */
	if ((token = strtok_r(s, DELIM, &savptr)) != NULL) {
		onGround = (strcmp(token, "G") == 0 ? 1 : 0);
	}

	if ((token = strtok_r(NULL, DELIM, &savptr)) != NULL) {

		flightDeckAlt = strtol(token, &ptr, 10) / 100;
	}

	if ((token = strtok_r(NULL, DELIM, &savptr)) != NULL) {
		heading_true = strtol(token, &ptr, 10) / 100.0 * DEG2RAD;
	}

	if ((token = strtok_r(NULL, DELIM, &savptr)) != NULL) {
		pitch = -strtol(token, &ptr, 10) / 100.0 * DEG2RAD;
	}

	if ((token = strtok_r(NULL, DELIM, &savptr)) != NULL) {
		bank = strtol(token, &ptr, 10) / 100.0 * DEG2RAD;
	}

	if ((token = strtok_r(NULL, DELIM, &savptr)) != NULL) {
		latitude = strtod(token, &ptr) * DEG2RAD; // Boost gives lat & long in degrees
	}

	if ((token = strtok_r(NULL, DELIM, &savptr)) != NULL) {
		longitude = strtod(token, &ptr) * DEG2RAD; // Boost gives lat & long in degrees;
	}

	if ((token = strtok_r(NULL, DELIM, &savptr)) != NULL) {
		ms = strtol(token, NULL, 10);
	}

	/*
	 * We update the speed via the speed structure
	 */
	SU.Type = VS;
	SU.Speed.VS = calcVS(flightDeckAlt, ms);
	SetSpeed(&SU);

	updatePSXBOOST(flightDeckAlt, heading_true, pitch, bank, latitude, longitude, onGround);
}

void newSituLoaded(void){

			resetInternalFlags();
			
      printDebug(LL_INFO, "New situ loaded. Resetting some parameters...");
			printDebug(LL_INFO, "Let's wait a few seconds to get everyone ready, shall we?");

			freezeMSFS(); // New Situ loaded, let's preventively freeze MSFS
			init_variables();

      /*
       * Now we wait and lock the update thread in order to get variables
       * to reset themselves, especially those in MSFS
       * we do this with conditional waits for threads
       */

			pthread_mutex_lock(&mutexsitu);
			if (flags.INHIB_CRASH_DETECT) {
				printDebug(LL_INFO, "No crash detection for 10 seconds");
				sendQPSX("Qi198=-9999910"); // no crash detection fort 10 seconds
			}
			sleep(5);
			intflags.updateNewSitu = 0;
			printDebug(LL_INFO, "Resuming normal operations.");
			pthread_mutex_unlock(&mutexsitu);
			pthread_cond_signal(&condNewSitu);
}

void Decode(char *s)
{

	// ExtLts : External lights, Mode=XECON
	if (strstr(s, "Qs443")) {
		S443(strstr(s, "Qs443="));
	}

	//// Update Gear position
	if (strstr(s, "Qh170")) {
		H170(strstr(s, "Qh170"));
	}

	//// Update PArking break
	if (strstr(s, "Qh397")) {
		H397(strstr(s, "Qh397"));
	}

	//// Update Flap position
	if (strstr(s, "Qh389")) {
		H389(strstr(s, "Qh389"));
	}
	//// Speedbrake
	if (strstr(s, "Qh388")) {
		H388(strstr(s, "Qh388"));
	}

	// Update Time
	if (strstr(s, "Qs124")) {
		S124(strstr(s, "Qs124"));
	}

	// Update TAS
	if (strstr(s, "Qs121")) {
		S121(strstr(s, "Qs121"));
	}
	// Indicated Airspeed IAS
	if (strstr(s, "Qs483")) {
		S483(strstr(s, "Qs483"));
	}

	// Rudder+aileron+elevator
	if (strstr(s, "Qs480")) {
		S480(strstr(s, "Qs480"));
	}

	// COMMS
	if (strstr(s, "Qs458")) {
		S458(strstr(s, "Qs458"));
	}

	// Steering wheel
	if (strstr(s, "Qh426")) {
		H426(strstr(s, "Qh426"));
	}

	// XPDR
	if (strstr(s, "Qi204")) {
		I204(strstr(s, "Qi204"));
	}
	// Altimeter
	if (strstr(s, "Qs448")) {
		S448(strstr(s, "Qs448"));
	}
	// MSFS slave-Master
	if (strstr(s, "Qs78")) {
		S78(strstr(s, "Qs78"));
	}

	// Grab the active weather zone
	if (strstr(s, "Qi240")) {
		I240(strstr(s, "Qi240"));
	}

	// get the TA & TL as per VNAV CLB page
	//
	if (strstr(s, "Qs392")) {
		S392(strstr(s, "Qs392"));
	}
	if (strstr(s, "Qi219")) {
		I219(strstr(s, "Qi219"));
	}
	if (strstr(s, "Qi257")) {
		I257(strstr(s, "Qi257"));
	}

	// get the weather zones
	if (strstr(s, "Qs328") || strstr(s, "Qs329") || strstr(s, "Qs330") || strstr(s, "Qs331") ||
		strstr(s, "Qs332") || strstr(s, "Qs333") || strstr(s, "Qs334") || strstr(s, "Qs335")) {
		Qsweather(s);
	}
}

int umain(void)
{
	size_t bufmain_remain = sizeof(bufmain) - bufmain_used;

	if (bufmain_remain == 0) {
		printDebug(LL_VERBOSE, "Main socket line exceeded buffer length! Discarding input");
		bufmain_used = 0;
		printDebug(LL_DEBUG, bufmain);
		return 0;
	}

	int nbread = recv(flags.sPSX, (char *)&bufmain[bufmain_used], bufmain_remain, 0);

	if (nbread == 0) {
		printDebug(LL_ERROR, "Main socket connection closed.");
		return 0;
	}
	if (nbread < 0 && errno == EAGAIN) {
		printDebug(LL_VERBOSE, "No data received.");
		/* no data for now, call back when the socket is readable */
		return 0;
	}
	if (nbread < 0) {
		printDebug(LL_ERROR, "Main socket Connection error");
		return 0;
	}
	bufmain_used += nbread;

	/* Scan for newlines in the line buffer; we're careful here to deal with
	 * embedded \0s an evil server may send, as well as only processing lines
	 * that are complete.
	 */
	char *line_start = bufmain;
	char *line_end;
	while ((line_end =
				(char *)memchr((void *)line_start, '\n', bufmain_used - (line_start - bufmain)))) {
		*line_end = 0;

		// New situ loaded
		if (strstr(line_start, "load3")){
        newSituLoaded();
    }

		if (line_start[0] == 'Q') {
			pthread_mutex_lock(&mutex);
			Decode(line_start);
			pthread_mutex_unlock(&mutex);
		}
		
    line_start = line_end + 1;
	}
	/* Shift buffer down so the unprocessed data is at the start */
	bufmain_used -= (line_start - bufmain);
	memmove(bufmain, line_start, bufmain_used);
	return nbread;
}

int umainBoost(void)
{

	size_t bufboost_remain = sizeof(bufboost) - bufboost_used;

	if (bufboost_remain == 0) {
		printDebug(LL_VERBOSE, "Boost Line exceeded buffer length!");
		return 0;
	}

	int nbread = recv(flags.sPSXBOOST, (char *)&bufboost[bufboost_used], bufboost_remain, 0);
	if (nbread == 0) {
		printDebug(LL_ERROR, "Boost connection closed.");
		return 0;
	}
	if (nbread < 0 && errno == EAGAIN) {
		printDebug(LL_ERROR, "No data for now, call back when the socket is readable");
		return 0;
	}
	if (nbread < 0) {
		printDebug(LL_ERROR, "Boost Connection error");
		return 0;
	}
	bufboost_used += nbread;

	/* Scan for newlines in the line buffer; we're careful here to deal with
	 * embedded \0s an evil server may send, as well as only processing lines
	 * that are complete.
	 */
	char *line_start = bufboost;
	char *line_end;
	while ((line_end = (char *)memchr((void *)line_start, '\n',
									  bufboost_used - (line_start - bufboost)))) {
		*line_end = 0;

		if (line_start[0] == 'F' || line_start[0] == 'G') {
			pthread_mutex_lock(&mutex);
			Decodeboost(line_start);
			pthread_mutex_unlock(&mutex);
		} else {
			printDebug(LL_VERBOSE, "Wrong boost string received: %s", line_start);
		}
		line_start = line_end + 1;
	}
	/* Shift buffer down so the unprocessed data is at the start */
	bufboost_used -= (line_start - bufboost);
	memmove(bufboost, line_start, bufboost_used);
	return nbread;
}
