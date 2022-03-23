// 
// Decompiled by Procyon v0.5.36
// 

package gndService;

import util.StatusMonitor;
import network.SocketClientPSXMain;
import util.StringSplitter;
import util.PlaceBearingDistance;
import gui.TabGndService;

public class GndServicePushBack implements Runnable
{
    private static double[] originCoord;
    private static double[] destCoord;
    private static final double DEG2RAD = 0.017453292519943295;
    private static final double RAD2DEG = 57.29577951308232;
    private static final double RAD2NM = 3437.746770784939;
    private static final double MTR2FT = 3.28084;
    private static final double FT2NM = 1.6457883E-4;
    private static final double FT2RAD = 4.787404104300033E-8;
    private static final double FREQUENCY = 30.0;
    private static final double STEPDISTANCE = 0.17497812239719998;
    private static final double TURNRATE = 0.1;
    private static final double STOPTURNTOLERANCE = 2.0;
    
    static {
        GndServicePushBack.originCoord = new double[] { 0.0, 0.0 };
        GndServicePushBack.destCoord = new double[] { 0.0, 0.0 };
    }
    
    @Override
    public void run() {
        final String path = new String(GndServiceBase.getSoundsPath(true));
        final double targetHdg = TabGndService.getPushBackHdg();
        double targetHdgUpBound = targetHdg + 2.0;
        if (targetHdgUpBound > 360.0) {
            final double diff = targetHdgUpBound - 360.0;
            targetHdgUpBound = 0.0 + diff;
        }
        else if (targetHdgUpBound == 360.0) {
            targetHdgUpBound = 1.0;
        }
        double targetHdgLoBound = targetHdg - 2.0;
        if (targetHdgLoBound < 0.0) {
            final double diff2 = Math.abs(targetHdgLoBound);
            targetHdgLoBound = 360.0 - diff2;
        }
        else if (targetHdgLoBound == 0.0) {
            targetHdgLoBound = 1.0;
        }
        boolean turnLeft = false;
        boolean turnRight = false;
        double stepHdg;
        final double initialHdg = stepHdg = getPsxHeading() * 57.29577951308232;
        final double result = initialHdg - targetHdg;
        final double absResult = Math.abs(result);
        if (result < 0.0) {
            if (absResult >= 180.0) {
                turnLeft = true;
            }
            else {
                turnRight = true;
            }
        }
        else if (absResult >= 180.0) {
            turnRight = true;
        }
        else {
            turnLeft = true;
        }
        final double straightDistance = GndServiceBase.getPushBackStraightLen();
        double trip = 0.0;
        GndServicePushBack.originCoord[0] = getPsxLatitude();
        GndServicePushBack.originCoord[1] = getPsxLongitude();
        double psxHdg = 0.0;
        double lastPsxHdg = 0.0;
        boolean skip = false;
        while (true) {
            if (targetHdgUpBound > getPsxHeading() * 57.29577951308232 && turnLeft && !skip) {
                psxHdg = 360.0 + getPsxHeading() * 57.29577951308232;
            }
            else if (turnLeft) {
                psxHdg = getPsxHeading() * 57.29577951308232;
                skip = true;
            }
            if (targetHdgLoBound < getPsxHeading() * 57.29577951308232 && turnRight && !skip) {
                double temp = 360.0 - getPsxHeading() * 57.29577951308232;
                temp += targetHdgLoBound;
                psxHdg = 0.0 - temp;
            }
            else if (turnRight) {
                psxHdg = getPsxHeading() * 57.29577951308232;
                skip = true;
            }
            lastPsxHdg = psxHdg;
            if (((psxHdg > targetHdgUpBound && turnLeft) || (psxHdg < targetHdgLoBound && turnRight)) && trip >= straightDistance) {
                if ((psxHdg > lastPsxHdg && turnLeft) || (psxHdg < lastPsxHdg && turnRight)) {
                    skip = true;
                }
                if (turnLeft) {
                    double temp;
                    stepHdg = (temp = stepHdg - 0.1);
                    if (temp < 0.0) {
                        final double diff3 = Math.abs(temp);
                        temp = 360.0 - diff3;
                    }
                    stepHdg = temp;
                }
                else if (turnRight) {
                    double temp;
                    stepHdg = (temp = stepHdg + 0.1);
                    if (temp >= 360.0) {
                        final double diff3 = temp - 360.0;
                        temp = 0.0 + diff3;
                    }
                    stepHdg = temp;
                }
            }
            else if (trip >= straightDistance) {
                break;
            }
            GndServicePushBack.destCoord = PlaceBearingDistance.getPbdDestCoords(GndServicePushBack.originCoord[0], GndServicePushBack.originCoord[1], PlaceBearingDistance.getReverseHdg(getPsxHeading()), 8.376909813270687E-9);
            GndServicePushBack.originCoord = GndServicePushBack.destCoord;
            final StringSplitter ss = new StringSplitter(GndServiceBase.getQs121());
            final String he = new String(String.valueOf(stepHdg * 0.017453292519943295));
            final String alTas = new String(ss.splitBetween(';', ';', 3, 5, true, true));
            final String lat = new String(String.valueOf(String.valueOf(GndServicePushBack.destCoord[0])) + ";");
            final String lon = new String(String.valueOf(GndServicePushBack.destCoord[1]));
            SocketClientPSXMain.send("Qs121=0;0;" + he + alTas + lat + lon);
            trip += 0.17497812239719998;
            try {
                Thread.sleep(33L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        final long PUSHENDTIME = System.currentTimeMillis();
        try {
            while (System.currentTimeMillis() < PUSHENDTIME + 20000L) {
                GndServiceBase.sendStaticPosition();
                Thread.sleep(500L);
            }
            while (GndServiceBase.getConversation()) {
                GndServiceBase.sendStaticPosition();
                Thread.sleep(1000L);
            }
        }
        catch (InterruptedException ex) {}
        if (!StatusMonitor.getGndServiceSimplified()) {
            GndServiceBase.setConversation(true);
            GndServiceBase.playSoundClip(true, String.valueOf(path) + "pushComplete.wav");
            GndServiceFlags.setPushBackFlag(8);
            GndServiceBase.sendStaticPosition();
            GndServiceBase.setDynText("Confirm parking brake is set");
        }
        else {
            GndServiceBase.setDynText("Pushback is completed, please set parking brake.");
            GndServiceBase.playSoundClip(true, String.valueOf(path) + "pushComplete.wav");
            while (!GndServiceBase.getParkBrkLev()) {
                try {
                    Thread.sleep(2000L);
                }
                catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
            }
            if (!StatusMonitor.getGndServiceSimplified() || GndServiceBase.getAcftVersion() != 0) {
                GndServiceBase.setDynText("Info : Waiting for landing -> Set PRK BRK, Engines OFF, Beacon OFF");
            }
            else {
                GndServiceBase.setDynText("Info : Waiting for cabin ready announcement...");
            }
        }
    }
    
    private static double getPsxLatitude() {
        final StringSplitter ss = new StringSplitter(GndServiceBase.getQs121());
        final Double temp = Double.parseDouble(ss.splitBetween(';', ';', 5, 6, false, false));
        return temp;
    }
    
    private static double getPsxLongitude() {
        final StringSplitter ss = new StringSplitter(GndServiceBase.getQs121());
        final Double temp = Double.parseDouble(ss.splitFrom(';', 6, false));
        return temp;
    }
    
    private static double getPsxHeading() {
        final StringSplitter ss = new StringSplitter(GndServiceBase.getQs121());
        final Double temp = Double.parseDouble(ss.splitBetween(';', ';', 2, 3, false, false));
        return temp;
    }
}
