// #include <cstdlib>
#include <cmath>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <time.h>
// #include <windows.h>
#include "PSXMSFSLIB.h"
#include "util.h"
#include "update.h"

size_t bufboost_used = 0;
size_t bufmain_used = 0;
char bufboost[256];
char bufmain[4096];

char *extractnth(const char *s, size_t n)
{
    char *result, *fin, *ini;
    size_t i = 1;

    // some basic checks
    if (n == 0 || strlen(s) == 0)
        return NULL;

    if ((result = (char *)strchr(s, '=')))
        result++;
    else
        result = (char *)s;

    // if only one token
    if (strchr(s, ';') == NULL) {
        return _strdup(result);
    }

    if (n > 1) {
        while ((result = strchr(result, ';'))) {
            result++;
            i++;
            if (i == n)
                break;
        }
    }
    // We are at the nth token, just need to chop off what comes after

    if (result == NULL)
        return NULL;
    ini = _strdup(result);
    if ((fin = strchr(ini, ';')) != NULL) {
        ini[fin - ini] = '\0';
    }
    return ini;
}
/* MSFS SDK: The gear handle position, where
 * 0 means the handle is retracted and 1 is the handle fully applied.
 *
 * in PSX, 1 = up, 2 = off and 3 = down
 */
void H170(const char *s)
{
    int gearpos;
    struct SurfaceUpdate S;

    gearpos = (int)(s[6] - '0');
    S.Type = GEAR;
    S.UN.GearDown = ((gearpos == 3) ? 1.0 : 0.0);

    SetMovingSurfaces(&S);
}

/* Index of current flap position.
 * Qh389="FlapLever"; Mode=ECON; Min=0; Max=6;
 */
void H389(const char *s)
{
    int position = 0;
    struct SurfaceUpdate S;

    position = (int)(s[6] - '0');

    position = (position < 0) ? 0 : position;
    position = (position > 6) ? 6 : position;

    S.Type = FLAPS;
    S.UN.FlapsPosition = position;
    SetMovingSurfaces(&S);
}

// Parking break
void H397(const char *s)
{
    int position;
    position = (int)(s[6] - '0');
    position = (position == 0) ? 0 : 1;

    updateParkingBreak(position);
}

/* MSFS SDK:
 *Sets the value of the nose wheel steering position.
 Zero is straight ahead (-16383, far left +16383, far right).
 */
void H426(const char *s)
{
    double pos;

    pos = 16383.0 * strtol(s + 6, NULL, 10) / 999.0;

    pos = (pos < -16383) ? -16383 : pos;
    pos = (pos > 16383) ? 16383 : pos;

    updateSteeringWheel(DWORD(pos));
}

/*
 *  Speedbrake lever variable
 *  Qh388="SpdBrkLever"; Mode=ECON; Min=0; Max=800;
 */
void H388(const char *s)
{
    float SpeedBrakelevel = 0;
    struct SurfaceUpdate S;

    SpeedBrakelevel = 16384.0f * strtol(strrchr(s, '=') + 1, NULL, 10) / 800.0f;

    /*
     * Since SpeedBrakeLEvel is set as a position 16K
     * in MSFS.cpp, we make some sanity checks
     */
    SpeedBrakelevel = ((SpeedBrakelevel < 0) ? 0 : SpeedBrakelevel);
    SpeedBrakelevel = ((SpeedBrakelevel > 16384) ? 16384 : SpeedBrakelevel);

    S.Type = SPEED;
    S.UN.SpeedBrake = SpeedBrakelevel;

    SetMovingSurfaces(&S);
}

/*
 * Qs121="PiBaHeAlTas"; Mode=ECON; Min=9; Max=200;
 *
 */
void S121(const char *s)
{

    struct SpeedUpdate SU;
    char *token;
    double speed = 0;

    token = extractnth(s, 5);
    speed = (double)strtoul(token, NULL, 10) / 1000.0;

    speed = (speed < 0) ? 0 : speed;
    speed = (speed > 530) ? 530 : speed;

    SU.Type = TAS;
    SU.Speed.TAS = speed;

    SetSpeed(&SU);
    free(token);
}

void S483(const char *s)
{
    struct SpeedUpdate SU;
    char *token;
    double speed = 0;

    token = extractnth(s, 1);
    speed = strtol(token, NULL, 10) / 10.0;
    speed = (speed < 0) ? 0 : speed;

    SU.Speed.IAS = speed;
    SU.Type = IAS;
    SetSpeed(&SU);
    free(token);
}

void S392(const char *s)
{
    int TA = 0, TL = 0;
    char *flightPhase, *tokenTA, *tokenTL;

    /*
     * We now try to get the flight phase
     */
    flightPhase = extractnth(s, 1);

    // TA and TL are the 2nd and 3rd token
    tokenTA = extractnth(s, 2);
    tokenTL = extractnth(s, 3);
    TA = (int)strtoul(tokenTA, NULL, 10);
    TL = (int)strtoul(tokenTL, NULL, 10);

    TL = (TL < 0) ? 0 : TL;
    TA = (TA < 0) ? 0 : TA;

    updateFlightPhase(flightPhase[3] - '0', TA, TL);
    free(flightPhase);
    free(tokenTA);
    free(tokenTL);
}
void S78(const char *s)
{

    if (strstr(s, "MSFS")) {
        //    PSXflags.SLAVE = 1;
    } else {
        if (strstr(s, "PSX")) {
            //      PSXflags.SLAVE = 0;
        }
    }
}

void S448(char *s)
{

    char *tokenALT, *tokenSTD;
    int stdbar = 0;
    int altimeter = 0;

    /* Altimeter setting is the 4th token
     * 5th token is STD setting
     */
    tokenALT = extractnth(s, 4);
    tokenSTD = extractnth(s, 5);

    altimeter = strtol(tokenALT, NULL, 10) / 100;
    stdbar = ((fabs(strtod(tokenSTD, NULL)) == 1) ? 0 : 1);

    SetBARO(altimeter, stdbar);
    free(tokenSTD);
    free(tokenALT);
}

void S458(char *s)
{
    int C1 = 122800000, C2 = 122800000;
    char COM1[10] = {0}, COM2[10] = {0};
    /*
     * discard the last digit from the Qs string as it is not taken into MSFS.
     * and start at second digit, as first one is always 1
     */
    strncpy(COM1, s + 6, 3);
    strncat(COM1, s + 10, 3);
    strcat(COM1, "000");
    COM1[9] = '\0';

    C1 = strtol(COM1, NULL, 10);

    if (C1 < 118000000 || C1 > 136990000) {
        C1 = 122800000;
    }

    strncpy(COM2, s + 13, 3);
    strncat(COM2, s + 17, 3);
    strcat(COM2, "000");
    COM2[9] = '\0';

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

    rudder = 16384 * (double)((val[8] + val[9]) / 2.0 - 32.0) / 32.0; // maximum deflection = 64
    aileron = 16384 * (double)(val[2] - 20) / 20.0;                   // maximum deflection in PSX  = 40
    elevator = 16384 * (double)(val[6] - 21) / 21.0;                  // maximum deflection in PSX = 42
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
    day = time_PSX->tm_yday + 1;     // nb days since January 1st, starts at 0
    hour = time_PSX->tm_hour;
    minute = time_PSX->tm_min;

    SetUTCTime(hour, minute, day, year);
}

void S122(const char *s)
{

    int reposition = (int)(s[6] - '0');
    if (reposition) {
        sendQPSX("bang");
        printDebug(LL_DEBUG, "PSX repositionned");
    }
}
void S443(const char *s)
{

    int light[14];

    for (int i = 0; i < 14; i++) {
        light[i] = (int)(s[i + 6] - '0') < 5 ? 0 : 1;
    }
    updateLights(light);
}

void I240(const char *s)
{

    int zone;

    zone = strtol(s + 6, NULL, 10);
    if (zone < 0 || zone > 7) {
        zone = 0;
    }
    setWeatherZone(zone);
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

void Qsweather(const char *s)
{

    int zone;
    double QNH;

    /* Get the active zone */

    zone = (int)strtoul(s + 2, NULL, 10) - 328; // Because the first zone is Qs328

    if (zone >= 0 && zone < 8) {

        // convert the last 4 digits as the string
        // is in format : QSxxx=xxxxxxxxxxxxxxxxxxxxxx;3021

        QNH = strtoul(strrchr(s, ';') + 1, NULL, 10);
        setWeather(zone, QNH);
    }
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
void Decodeboost(const char *strboost)
{

    double flightDeckAlt = 0.0, heading_true = 0.0, pitch = 0.0, bank = 0.0;
    double latitude = 0.0, longitude = 0.0;
    int onGround = 1, ms = 0;
    char *token;

    struct SpeedUpdate SU;

    /* get the first token */
    token = extractnth(strboost, 1);
    onGround = (strcmp(token, "G") == 0 ? 1 : 0);
    free(token);

    token = extractnth(strboost, 2);
    flightDeckAlt = strtol(token, NULL, 10) / 100;
    free(token);

    token = extractnth(strboost, 3);
    heading_true = strtol(token, NULL, 10) / 100.0 * DEG2RAD;
    free(token);

    token = extractnth(strboost, 4);
    pitch = -strtol(token, NULL, 10) / 100.0 * DEG2RAD;
    free(token);

    token = extractnth(strboost, 5);
    bank = strtol(token, NULL, 10) / 100.0 * DEG2RAD;
    free(token);

    token = extractnth(strboost, 6);
    latitude = strtod(token, NULL) * DEG2RAD; // Boost gives lat & long in degrees
    free(token);

    token = extractnth(strboost, 7);
    longitude = strtod(token, NULL) * DEG2RAD; // Boost gives lat & long in degrees;
    free(token);

    token = extractnth(strboost, 8);
    ms = strtol(token, NULL, 10);
    free(token);

    /*
     * We update the speed via the speed structure
     */
    SU.Type = VS;
    SU.Speed.VS = calcVS(flightDeckAlt, ms);

    SetSpeed(&SU);
    updatePSXBOOST(flightDeckAlt, heading_true, pitch, bank, latitude, longitude, onGround);
}

void Decode(char *s)
{

    // Check if repositionning occured on first character of string Qs122
    if (strstr(s, "Qs122")) {
        S122(strstr(s, "Qs122="));
        return;
    }
    // ExtLts : External lights, Mode=XECON
    if (strstr(s, "Qs443")) {
        S443(strstr(s, "Qs443="));
        return;
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

int getDataFromPSX(FLAGS *flags)
{
    char *line_start = bufmain;
    char *line_end;
    size_t bufmain_remain = sizeof(bufmain) - bufmain_used;

    if (bufmain_remain == 0) {
        printDebug(LL_DEBUG, "PSXMSFS buffer overflow: discarding input. Buffer starts with %.10s...", bufmain);
        bufmain_used = 0;
        return 0;
    }

    int nbread = recv(flags->PSXsocket, (char *)&bufmain[bufmain_used], (int)(bufmain_remain), 0);

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
        if (!quit)
            printDebug(LL_ERROR, "Main socket connection error");
        return 0;
    }
    bufmain_used += nbread;

    /* Scan for newlines in the line buffer; we're careful here to deal with
     * embedded \0s an evil server may send, as well as only processing lines
     * that are complete.
     */
    while ((line_end =
                (char *)memchr((void *)line_start, '\n', bufmain_used - (line_start - bufmain)))) {
        *line_end = 0;

        WaitForSingleObject(mutex, INFINITE);
        // New situ loaded
        if (strstr(line_start, "load3")) {
            intflags.NewSituTimeLoad = newSituLoaded(flags);
        }


        if (line_start[0] == 'Q') {

            Decode(line_start);
        }

        line_start = line_end + 1;
        ReleaseMutex(mutex);
    }
    /* Shift buffer down so the unprocessed data is at the start */
    bufmain_used -= (line_start - bufmain);
    memmove(bufmain, line_start, bufmain_used);
    return nbread;
}

int getDataFromBoost(SOCKET sPSXBOOST)
{

    size_t bufboost_remain = sizeof(bufboost) - bufboost_used;

    if (bufboost_remain == 0) {
        printDebug(LL_VERBOSE, "Boost Line exceeded buffer length!");
        return 0;
    }

    int nbread = recv(sPSXBOOST, (char *)&bufboost[bufboost_used], (int)(bufboost_remain), 0);
    if (nbread == 0) {
        printDebug(LL_ERROR, "Boost connection closed.");
        quit = 1;
        return 0;
    }
    if (nbread < 0 && errno == EAGAIN) {
        printDebug(LL_ERROR, "No data for now, call back when the socket is readable");
        return 0;
    }
    if (nbread < 0) {
        printDebug(LL_ERROR, "Boost Connection error");
        quit = 1;
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
            WaitForSingleObject(mutex, INFINITE);
            Decodeboost(line_start);
            ReleaseMutex(mutex);
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
