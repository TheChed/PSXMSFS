// 
// Decompiled by Procyon v0.5.36
// 

package simBridge;

import util.PlaceBearingDistance;
import network.SocketClientPSXMain;
import util.StringSplitter;
import network.BridgeSimConnect;
import gui.TextTabParser;
import files.FileRunways;
import util.StatusMonitor;
import java.io.IOException;
import network.DataFromPsxMain;
import util.ObservableData;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;
import util.ObserverData;

public class SimBridgeOffsetPos implements ObserverData
{
    private static final double RAD2NM = 3437.746770784939;
    private static final double DEG2RAD = 0.017453292519943295;
    private static final double FT2NM = 1.6457883E-4;
    private static final double FT2RAD = 4.787404104300033E-8;
    private static double[] offsets;
    private static double[] fileRwyPosAlt;
    private static double[] psxRwyPosAltThr;
    private static boolean beaconWasOff;
    private static String lastFmcLdgRwy;
    private static final Lock lockFileRwyPosAlt;
    private static final Lock lockPsxRwyPosAltThr;
    private static final Lock lockBeaconWasOff;
    private static String Qs444;
    private static String Qs121;
    private static final Lock lockQs444;
    private static final Lock lockQs121;
    private static String Qs373;
    private static String Qs388;
    private static String Qs389;
    private static String Qs376;
    private static String Qs377;
    private static String Qs382;
    private static String Qs383;
    private static final Lock lockQs373;
    private static final Lock lockQs388;
    private static final Lock lockQs389;
    private static final Lock lockQs376;
    private static final Lock lockQs377;
    private static final Lock lockQs382;
    private static final Lock lockQs383;
    
    static {
        SimBridgeOffsetPos.offsets = new double[] { 0.0, 0.0, 0.0, 0.0 };
        SimBridgeOffsetPos.fileRwyPosAlt = new double[] { 0.0, 0.0, 0.0, 0.0 };
        SimBridgeOffsetPos.psxRwyPosAltThr = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0 };
        SimBridgeOffsetPos.beaconWasOff = false;
        SimBridgeOffsetPos.lastFmcLdgRwy = "";
        lockFileRwyPosAlt = new ReentrantLock();
        lockPsxRwyPosAltThr = new ReentrantLock();
        lockBeaconWasOff = new ReentrantLock();
        SimBridgeOffsetPos.Qs444 = new String("");
        SimBridgeOffsetPos.Qs121 = new String("");
        lockQs444 = new ReentrantLock();
        lockQs121 = new ReentrantLock();
        SimBridgeOffsetPos.Qs373 = new String("");
        SimBridgeOffsetPos.Qs388 = new String("");
        SimBridgeOffsetPos.Qs389 = new String("");
        SimBridgeOffsetPos.Qs376 = new String("");
        SimBridgeOffsetPos.Qs377 = new String("");
        SimBridgeOffsetPos.Qs382 = new String("");
        SimBridgeOffsetPos.Qs383 = new String("");
        lockQs373 = new ReentrantLock();
        lockQs388 = new ReentrantLock();
        lockQs389 = new ReentrantLock();
        lockQs376 = new ReentrantLock();
        lockQs377 = new ReentrantLock();
        lockQs382 = new ReentrantLock();
        lockQs383 = new ReentrantLock();
    }
    
    @Override
    public void updateObservers(final ObservableData argObs, final Object argId, final Object argData) {
        if (argObs instanceof DataFromPsxMain) {
            final String s;
            switch (s = (String)argId) {
                case "Qh178": {
                    dispatchQh178((String)argData);
                    break;
                }
                case "Qs121": {
                    SimBridgeOffsetPos.lockQs121.lock();
                    SimBridgeOffsetPos.Qs121 = (String)argData;
                    SimBridgeOffsetPos.lockQs121.unlock();
                    break;
                }
                case "Qs373": {
                    SimBridgeOffsetPos.lockQs373.lock();
                    SimBridgeOffsetPos.Qs373 = (String)argData;
                    SimBridgeOffsetPos.lockQs373.unlock();
                    break;
                }
                case "Qs376": {
                    SimBridgeOffsetPos.lockQs376.lock();
                    SimBridgeOffsetPos.Qs376 = (String)argData;
                    SimBridgeOffsetPos.lockQs376.unlock();
                    break;
                }
                case "Qs377": {
                    SimBridgeOffsetPos.lockQs377.lock();
                    SimBridgeOffsetPos.Qs377 = (String)argData;
                    SimBridgeOffsetPos.lockQs377.unlock();
                    break;
                }
                case "Qs382": {
                    SimBridgeOffsetPos.lockQs382.lock();
                    SimBridgeOffsetPos.Qs382 = (String)argData;
                    SimBridgeOffsetPos.lockQs382.unlock();
                    break;
                }
                case "Qs383": {
                    SimBridgeOffsetPos.lockQs383.lock();
                    SimBridgeOffsetPos.Qs383 = (String)argData;
                    SimBridgeOffsetPos.lockQs383.unlock();
                    break;
                }
                case "Qs388": {
                    SimBridgeOffsetPos.lockQs388.lock();
                    SimBridgeOffsetPos.Qs388 = (String)argData;
                    SimBridgeOffsetPos.lockQs388.unlock();
                    try {
                        setOffsets(false);
                    }
                    catch (IOException e) {}
                    break;
                }
                case "Qs389": {
                    SimBridgeOffsetPos.lockQs389.lock();
                    SimBridgeOffsetPos.Qs389 = (String)argData;
                    SimBridgeOffsetPos.lockQs389.unlock();
                    try {
                        setOffsets(false);
                    }
                    catch (IOException ex) {}
                    break;
                }
                case "Qs444": {
                    SimBridgeOffsetPos.lockQs444.lock();
                    SimBridgeOffsetPos.Qs444 = (String)argData;
                    SimBridgeOffsetPos.lockQs444.unlock();
                    try {
                        setOffsets(false);
                    }
                    catch (IOException e) {}
                    break;
                }
                default:
                    break;
            }
        }
    }
    
    public static void setOffsets(final boolean argUpdateAnyway) throws IOException {
        if (!StatusMonitor.getSimBridgeIsRunning()) {
            return;
        }
        final String fmcLdgArpt = getFmcLdgArpt();
        String fmcLdgRwy = getFmcLdgRwy();
        final String arptId = new String(getPsxEgpwsArptId());
        final String rwyId = new String(getPsxEgpwsRwyId());
        SimBridgeOffsetPos.lockPsxRwyPosAltThr.lock();
        SimBridgeOffsetPos.psxRwyPosAltThr = getPsxEgpwsRwyLatLonAltThr();
        SimBridgeOffsetPos.lockPsxRwyPosAltThr.unlock();
        SimBridgeOffsetPos.lockFileRwyPosAlt.lock();
        SimBridgeOffsetPos.fileRwyPosAlt = FileRunways.getFileRwyLatLonAltLen(arptId, rwyId);
        SimBridgeOffsetPos.lockFileRwyPosAlt.unlock();
        if (fmcLdgRwy.startsWith("0")) {
            fmcLdgRwy = fmcLdgRwy.substring(1);
        }
        if (fmcLdgRwy.isEmpty() && SimBridgeOffsetPos.lastFmcLdgRwy.equals(rwyId)) {
            fmcLdgRwy = rwyId;
        }
        if ((StatusMonitor.getSimBridgeUsingDestOffsets() && fmcLdgArpt.equals(arptId) && fmcLdgRwy.equals(rwyId)) || StatusMonitor.getSimBridgeUsingAllOffsets()) {
            if (SimBridgeOffsetPos.fileRwyPosAlt[0] != 9999.0) {
                SimBridgeOffsetPos.lastFmcLdgRwy = rwyId;
                StatusMonitor.setSimBridgeOffsetsNotFound(false);
                SimBridgeOffsetPos.offsets[0] = SimBridgeOffsetPos.fileRwyPosAlt[0] - SimBridgeOffsetPos.psxRwyPosAltThr[0];
                SimBridgeOffsetPos.offsets[1] = SimBridgeOffsetPos.fileRwyPosAlt[1] - SimBridgeOffsetPos.psxRwyPosAltThr[1];
                SimBridgeOffsetPos.offsets[3] = SimBridgeOffsetPos.psxRwyPosAltThr[2];
            }
            else if (SimBridgeOffsetPos.psxRwyPosAltThr[0] != 9999.0 && getPsxAltitude() < 20000 && !SimBridgeBase.getOnGround()) {
                SimBridgeOffsetPos.lastFmcLdgRwy = "";
                SimBridgeOffsetPos.offsets[0] = 0.0;
                SimBridgeOffsetPos.offsets[1] = 0.0;
                SimBridgeOffsetPos.offsets[3] = 0.0;
                StatusMonitor.setSimBridgeOffsetsNotFound(true);
            }
            if (!StatusMonitor.getSimBridgeAlignPsxWithDepRwy()) {
                TextTabParser.setArptRwyText(arptId, rwyId);
            }
        }
        else if (!StatusMonitor.getSimBridgeUsingDestOffsets() && !StatusMonitor.getSimBridgeUsingAllOffsets()) {
            if (SimBridgeOffsetPos.fileRwyPosAlt[0] != 9999.0) {
                StatusMonitor.setSimBridgeOffsetsNotFound(false);
                SimBridgeOffsetPos.offsets[0] = 0.0;
                SimBridgeOffsetPos.offsets[1] = 0.0;
                SimBridgeOffsetPos.offsets[3] = SimBridgeOffsetPos.psxRwyPosAltThr[2];
            }
            else if (SimBridgeOffsetPos.psxRwyPosAltThr[0] != 9999.0 && getPsxAltitude() < 20000 && !SimBridgeBase.getOnGround()) {
                SimBridgeOffsetPos.offsets[0] = 0.0;
                SimBridgeOffsetPos.offsets[1] = 0.0;
                SimBridgeOffsetPos.offsets[3] = 0.0;
                StatusMonitor.setSimBridgeOffsetsNotFound(true);
            }
        }
        if (!StatusMonitor.getSimBridgeAlignPsxWithDepRwy() && (!SimBridgeBase.getOnGround() || argUpdateAnyway)) {
            BridgeSimConnect.setFileSimOffsets(SimBridgeOffsetPos.offsets);
        }
    }
    
    public static void setPsxPosToSimPos(final double argPsxLat, final double argPsxLon, final double argSimLat, final double argSimLon) {
        SimBridgeOffsetPos.lockQs121.lock();
        final StringSplitter ss = new StringSplitter(SimBridgeOffsetPos.Qs121);
        SimBridgeOffsetPos.lockQs121.unlock();
        final double psxLat = argSimLat;
        final double psxLon = argSimLon;
        String hdgString = new String(ss.splitBetween(';', ';', 2, 3, false, false));
        final String hdgStringFirstPart = new String(hdgString.substring(0, 1));
        final String hdgStringSecPart = new String(hdgString.substring(2, 5));
        hdgString = String.valueOf(hdgStringFirstPart) + hdgStringSecPart;
        final int hdg = Integer.parseInt(hdgString);
        String alt = new String(ss.splitBetween(';', ';', 3, 4, false, false));
        if (alt.startsWith("-")) {
            if (alt.length() == 5) {
                alt = alt.substring(0, 2);
            }
            else if (alt.length() == 6) {
                alt = alt.substring(0, 3);
            }
        }
        else if (alt.length() == 4) {
            alt = alt.substring(0, 1);
        }
        else if (alt.length() == 5) {
            alt = alt.substring(0, 2);
        }
        else if (alt.length() == 6) {
            alt = alt.substring(0, 3);
        }
        else if (alt.length() == 7) {
            alt = alt.substring(0, 4);
        }
        else if (alt.length() == 8) {
            alt = alt.substring(0, 5);
        }
        SocketClientPSXMain.send("Qs122=1;0;0;" + String.valueOf(hdg) + ";" + alt + ";0;0;0;" + String.valueOf(psxLat) + ";" + String.valueOf(psxLon) + ";" + alt + "0");
    }
    
    public static void alignPsxWithDepRwy(final boolean argCalledFromBtn) {
        if (!SimBridgeBase.getOnGround()) {
            return;
        }
        try {
            final double[] offsets = { 0.0, 0.0, 0.0, 0.0 };
            SimBridgeOffsetPos.lockQs121.lock();
            final StringSplitter ss = new StringSplitter(SimBridgeOffsetPos.Qs121);
            SimBridgeOffsetPos.lockQs121.unlock();
            String hdgString = new String(ss.splitBetween(';', ';', 2, 3, false, false));
            final String hdgStringFirstPart = new String(hdgString.substring(0, 1));
            final String hdgStringSecPart = new String(hdgString.substring(2, 5));
            hdgString = String.valueOf(hdgStringFirstPart) + hdgStringSecPart;
            final int hdg = Integer.parseInt(hdgString);
            final Double psxLatitude = Double.parseDouble(ss.splitBetween(';', ';', 5, 6, false, false));
            final Double psxLongitude = Double.parseDouble(ss.splitFrom(';', 6, false));
            if (StatusMonitor.getSimBridgeAutoPsxDepAlign() && !argCalledFromBtn) {
                if (!getFmcDepArpt().isEmpty() && !getFmcDepRwy().isEmpty()) {
                    final String arptId = getFmcDepArpt();
                    final String rwyId = getFmcDepRwy();
                    final double[] psxRwyPos = getPsxFmcDepRwyLatLonAlt(rwyId);
                    final double[] simRwyPos = FileRunways.getFileRwyLatLonAltLen(arptId, rwyId);
                    if (psxRwyPos[0] != 9999.0 && simRwyPos[0] != 9999.0) {
                        StatusMonitor.setSimBridgeOffsetsNotFound(false);
                        final double latOffset = simRwyPos[0] - psxRwyPos[0];
                        final double lonOffset = simRwyPos[1] - psxRwyPos[1];
                        final int locElev = (int)(BridgeSimConnect.getSimGndAlt() * 10.0);
                        final String alt = String.valueOf(locElev / 10);
                        SocketClientPSXMain.send("Qs122=1;0;0;" + String.valueOf(hdg) + ";" + alt + ";0;0;0;" + String.valueOf(psxLatitude - latOffset) + ";" + String.valueOf(psxLongitude - lonOffset) + ";" + String.valueOf(locElev));
                        offsets[0] = latOffset;
                        offsets[1] = lonOffset;
                        offsets[2] = 0.0;
                        offsets[3] = psxRwyPos[2];
                        StatusMonitor.setSimBridgeAlignPsxWithDepRwy(true);
                        TextTabParser.setArptRwyText(arptId, rwyId);
                        BridgeSimConnect.setFileSimOffsets(offsets);
                        BridgeSimConnect.forceDepRwyOffset();
                    }
                    else {
                        TextTabParser.setArptRwyText(arptId, rwyId);
                        StatusMonitor.setSimBridgeOffsetsNotFound(true);
                    }
                }
            }
            else if (argCalledFromBtn) {
                if (!getFmcDepArpt().isEmpty() && !getFmcDepRwy().isEmpty()) {
                    final String arptId = getFmcDepArpt();
                    final String rwyId = getFmcDepRwy();
                    final double[] psxRwyPos = getPsxFmcDepRwyLatLonAlt(rwyId);
                    final double[] simRwyPos = FileRunways.getFileRwyLatLonAltLen(arptId, rwyId);
                    if (psxRwyPos[0] != 9999.0 && simRwyPos[0] != 9999.0) {
                        StatusMonitor.setSimBridgeOffsetsNotFound(false);
                        final double latOffset = simRwyPos[0] - psxRwyPos[0];
                        final double lonOffset = simRwyPos[1] - psxRwyPos[1];
                        final int locElev = (int)(BridgeSimConnect.getSimGndAlt() * 10.0);
                        final String alt = String.valueOf(locElev / 10);
                        SocketClientPSXMain.send("Qs122=1;0;0;" + String.valueOf(hdg) + ";" + alt + ";0;0;0;" + String.valueOf(psxLatitude - latOffset) + ";" + String.valueOf(psxLongitude - lonOffset) + ";" + String.valueOf(locElev));
                        offsets[0] = latOffset;
                        offsets[1] = lonOffset;
                        offsets[2] = 0.0;
                        offsets[3] = psxRwyPos[2];
                        StatusMonitor.setSimBridgeAlignPsxWithDepRwy(true);
                        TextTabParser.setArptRwyText(arptId, rwyId);
                        BridgeSimConnect.setFileSimOffsets(offsets);
                        BridgeSimConnect.forceDepRwyOffset();
                    }
                    else {
                        TextTabParser.setArptRwyText(arptId, rwyId);
                        StatusMonitor.setSimBridgeOffsetsNotFound(true);
                    }
                }
                else {
                    SimBridgeOffsetPos.lockQs444.lock();
                    final StringSplitter ss2 = new StringSplitter(SimBridgeOffsetPos.Qs444);
                    SimBridgeOffsetPos.lockQs444.unlock();
                    final String arptId2 = ss2.splitBetween(';', ';', 6, 7, false, false);
                    final String rwyId2 = ss2.splitBetween('=', ';', 1, 1, false, false);
                    final double[] psxRwyPos2 = getPsxEgpwsRwyLatLonAltThr();
                    final double[] simRwyPos2 = FileRunways.getFileRwyLatLonAltLen(arptId2, rwyId2);
                    if (psxRwyPos2[0] != 9999.0 && simRwyPos2[0] != 9999.0) {
                        StatusMonitor.setSimBridgeOffsetsNotFound(false);
                        final double latOffset2 = simRwyPos2[0] - psxRwyPos2[0];
                        final double lonOffset2 = simRwyPos2[1] - psxRwyPos2[1];
                        final int locElev2 = (int)(BridgeSimConnect.getSimGndAlt() * 10.0);
                        final String alt2 = String.valueOf(locElev2 / 10);
                        SocketClientPSXMain.send("Qs122=1;0;0;" + String.valueOf(hdg) + ";" + alt2 + ";0;0;0;" + String.valueOf(psxLatitude - latOffset2) + ";" + String.valueOf(psxLongitude - lonOffset2) + ";" + String.valueOf(locElev2));
                        offsets[0] = latOffset2;
                        offsets[1] = lonOffset2;
                        offsets[2] = 0.0;
                        offsets[3] = psxRwyPos2[2];
                        StatusMonitor.setSimBridgeAlignPsxWithDepRwy(true);
                        TextTabParser.setArptRwyText(arptId2, rwyId2);
                        BridgeSimConnect.setFileSimOffsets(offsets);
                        BridgeSimConnect.forceDepRwyOffset();
                    }
                    else {
                        TextTabParser.setArptRwyText(arptId2, rwyId2);
                        StatusMonitor.setSimBridgeOffsetsNotFound(true);
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static String getFmcLdgArpt() {
        if (getFmcActiveRte() == 1) {
            SimBridgeOffsetPos.lockQs376.lock();
            final StringSplitter ss = new StringSplitter(SimBridgeOffsetPos.Qs376);
            SimBridgeOffsetPos.lockQs376.unlock();
            return ss.splitBetween(';', ';', 1, 2, false, false);
        }
        if (getFmcActiveRte() == 2) {
            SimBridgeOffsetPos.lockQs377.lock();
            final StringSplitter ss = new StringSplitter(SimBridgeOffsetPos.Qs377);
            SimBridgeOffsetPos.lockQs377.unlock();
            return ss.splitBetween(';', ';', 1, 2, false, false);
        }
        return "";
    }
    
    private static int getFmcActiveRte() {
        SimBridgeOffsetPos.lockQs373.lock();
        String temp = new String(SimBridgeOffsetPos.Qs373);
        SimBridgeOffsetPos.lockQs373.unlock();
        temp = temp.substring(temp.indexOf(61) + 1);
        int result = 0;
        try {
            result = Integer.parseInt(temp.substring(2, 3));
        }
        catch (IndexOutOfBoundsException e) {
            result = 0;
        }
        return result;
    }
    
    private static String getFmcDepArpt() {
        if (getFmcActiveRte() == 1) {
            SimBridgeOffsetPos.lockQs376.lock();
            final StringSplitter ss = new StringSplitter(SimBridgeOffsetPos.Qs376);
            SimBridgeOffsetPos.lockQs376.unlock();
            return ss.splitBetween('=', ';', 1, 1, false, false);
        }
        if (getFmcActiveRte() == 2) {
            SimBridgeOffsetPos.lockQs377.lock();
            final StringSplitter ss = new StringSplitter(SimBridgeOffsetPos.Qs377);
            SimBridgeOffsetPos.lockQs377.unlock();
            return ss.splitBetween('=', ';', 1, 1, false, false);
        }
        return "";
    }
    
    private static String getFmcDepRwy() {
        if (getFmcActiveRte() == 1) {
            SimBridgeOffsetPos.lockQs376.lock();
            final StringSplitter ss = new StringSplitter(SimBridgeOffsetPos.Qs376);
            SimBridgeOffsetPos.lockQs376.unlock();
            return ss.splitBetween(';', ';', 2, 3, false, false);
        }
        if (getFmcActiveRte() == 2) {
            SimBridgeOffsetPos.lockQs377.lock();
            final StringSplitter ss = new StringSplitter(SimBridgeOffsetPos.Qs377);
            SimBridgeOffsetPos.lockQs377.unlock();
            return ss.splitBetween(';', ';', 2, 3, false, false);
        }
        return "";
    }
    
    private static String getFmcLdgRwy() {
        try {
            if (getFmcActiveRte() == 1) {
                SimBridgeOffsetPos.lockQs388.lock();
                final StringSplitter ss = new StringSplitter(SimBridgeOffsetPos.Qs388);
                SimBridgeOffsetPos.lockQs388.unlock();
                final String temp = ss.splitBetween(';', ';', 5, 6, false, false);
                if (temp.contains("_")) {
                    return temp.substring(1, temp.indexOf(95));
                }
                return temp.substring(1, temp.indexOf(45));
            }
            else {
                if (getFmcActiveRte() != 2) {
                    return "";
                }
                SimBridgeOffsetPos.lockQs389.lock();
                final StringSplitter ss = new StringSplitter(SimBridgeOffsetPos.Qs389);
                SimBridgeOffsetPos.lockQs389.unlock();
                final String temp = ss.splitBetween(';', ';', 5, 6, false, false);
                if (temp.contains("_")) {
                    return temp.substring(1, temp.indexOf(95));
                }
                return temp.substring(1, temp.indexOf(45));
            }
        }
        catch (IndexOutOfBoundsException e) {
            return "";
        }
    }
    
    private static double[] getPsxEgpwsRwyLatLonAltThr() {
        final double[] result = { 0.0, 0.0, 0.0 };
        SimBridgeOffsetPos.lockQs444.lock();
        final StringSplitter ssSplitter = new StringSplitter(SimBridgeOffsetPos.Qs444);
        SimBridgeOffsetPos.lockQs444.unlock();
        try {
            double dthrDistance = Double.parseDouble(ssSplitter.splitBetween(';', ';', 7, 8, false, false));
            final double simRwyLength = FileRunways.getFileRwyLatLonAltLen(ssSplitter.splitBetween(';', ';', 6, 7, false, false), ssSplitter.splitBetween('=', ';', 1, 1, false, false))[3];
            if (dthrDistance != 0.0 && simRwyLength != 9999.0 && simRwyLength - dthrDistance < 8000.0) {
                dthrDistance = 0.0;
            }
            if (dthrDistance == 0.0) {
                result[0] = Double.parseDouble(ssSplitter.splitBetween(';', ';', 1, 2, false, false));
                result[1] = Double.parseDouble(ssSplitter.splitBetween(';', ';', 2, 3, false, false));
                result[2] = Double.parseDouble(ssSplitter.splitBetween(';', ';', 3, 4, false, false));
            }
            else {
                final double rwyHdg = Double.parseDouble(ssSplitter.splitBetween(';', ';', 5, 6, false, false)) / 100.0;
                final double rwyElev = Double.parseDouble(ssSplitter.splitBetween(';', ';', 3, 4, false, false));
                final double rwySlope = Double.parseDouble(ssSplitter.splitBetween(';', ';', 4, 5, false, false)) / 1000.0;
                double realTdnPointAlt = rwyElev;
                final double slopeElevation = dthrDistance * rwySlope / 100.0;
                realTdnPointAlt += slopeElevation;
                final double[] realTdnPointCoord = PlaceBearingDistance.getPbdDestCoords(Double.parseDouble(ssSplitter.splitBetween(';', ';', 1, 2, false, false)), Double.parseDouble(ssSplitter.splitBetween(';', ';', 2, 3, false, false)), PlaceBearingDistance.getReverseHdg(rwyHdg * 0.017453292519943295), dthrDistance * 4.787404104300033E-8);
                result[0] = realTdnPointCoord[0];
                result[1] = realTdnPointCoord[1];
                result[2] = realTdnPointAlt;
            }
        }
        catch (NumberFormatException e2) {
            result[0] = 9999.0;
            result[2] = (result[1] = 9999.0);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    private static double[] getPsxFmcDepRwyLatLonAlt(final String argRwyId) {
        final double[] result = { 0.0, 0.0, 0.0 };
        StringSplitter ss = null;
        int n = 1;
        String rwyId = argRwyId;
        String suffix = "";
        if (rwyId.contains("L")) {
            suffix = "R";
            rwyId = rwyId.substring(0, rwyId.indexOf(76));
        }
        else if (rwyId.contains("C")) {
            suffix = "C";
            rwyId = rwyId.substring(0, rwyId.indexOf(67));
        }
        else if (rwyId.contains("R")) {
            suffix = "L";
            rwyId = rwyId.substring(0, rwyId.indexOf(82));
        }
        int temp = Integer.parseInt(rwyId);
        temp = (temp + 18) % 36;
        String oppositeRwyId = String.valueOf(String.valueOf(temp)) + suffix;
        if (!suffix.isEmpty()) {
            if (oppositeRwyId.length() == 2) {
                oppositeRwyId = "0" + oppositeRwyId;
            }
        }
        else if (oppositeRwyId.length() == 1) {
            oppositeRwyId = "0" + oppositeRwyId;
        }
        try {
            if (getFmcActiveRte() == 1) {
                SimBridgeOffsetPos.lockQs382.lock();
                ss = new StringSplitter(SimBridgeOffsetPos.Qs382);
                SimBridgeOffsetPos.lockQs382.unlock();
            }
            else {
                if (getFmcActiveRte() != 2) {
                    result[1] = (result[0] = 9999.0);
                    result[2] = 0.0;
                    return result;
                }
                SimBridgeOffsetPos.lockQs383.lock();
                ss = new StringSplitter(SimBridgeOffsetPos.Qs383);
                SimBridgeOffsetPos.lockQs383.unlock();
            }
            String info;
            do {
                info = ss.splitBetween(';', ';', n, n + 1, false, false);
                if (info.equals(argRwyId)) {
                    result[2] = Double.parseDouble(ss.splitBetween(';', ';', n + 5, n + 6, false, false));
                    break;
                }
                ++n;
            } while (!info.isEmpty());
            n = 1;
            do {
                info = ss.splitBetween(';', ';', n, n + 1, false, false);
                if (info.equals(oppositeRwyId)) {
                    final double oppositeThrLat = Double.parseDouble(ss.splitBetween(';', ';', n + 1, n + 2, false, false));
                    final double oppositeThrLon = Double.parseDouble(ss.splitBetween(';', ';', n + 2, n + 3, false, false));
                    final double oppositeHdg = Double.parseDouble(ss.splitBetween(';', ';', n + 3, n + 4, false, false));
                    final double oppositeLength = Double.parseDouble(ss.splitBetween(';', ';', n + 4, n + 5, false, false));
                    final double[] originCoords = PlaceBearingDistance.getPbdDestCoords(oppositeThrLat, oppositeThrLon, oppositeHdg, oppositeLength * 4.787404104300033E-8);
                    result[0] = originCoords[0];
                    result[1] = originCoords[1];
                    break;
                }
                ++n;
            } while (!info.isEmpty());
            if (result[0] == 0.0) {
                result[1] = (result[0] = 9999.0);
                result[2] = 0.0;
            }
        }
        catch (NumberFormatException e) {
            result[1] = (result[0] = 9999.0);
            result[2] = 0.0;
        }
        return result;
    }
    
    private static int getPsxAltitude() {
        SimBridgeOffsetPos.lockQs121.lock();
        final StringSplitter ss = new StringSplitter(SimBridgeOffsetPos.Qs121);
        SimBridgeOffsetPos.lockQs121.unlock();
        return Integer.parseInt(ss.splitBetween(';', ';', 3, 4, false, false)) / 1000;
    }
    
    private static String getPsxEgpwsArptId() {
        SimBridgeOffsetPos.lockQs444.lock();
        final StringSplitter ssSplitter = new StringSplitter(SimBridgeOffsetPos.Qs444);
        SimBridgeOffsetPos.lockQs444.unlock();
        return ssSplitter.splitBetween(';', ';', 6, 7, false, false);
    }
    
    private static String getPsxEgpwsRwyId() {
        SimBridgeOffsetPos.lockQs444.lock();
        final StringSplitter ssSplitter = new StringSplitter(SimBridgeOffsetPos.Qs444);
        SimBridgeOffsetPos.lockQs444.unlock();
        return ssSplitter.splitBetween('=', ';', 1, 1, false, false);
    }
    
    private static void dispatchQh178(final String argQh178) {
        try {
            String Qh178 = argQh178;
            Qh178 = Qh178.substring(Qh178.indexOf(61) + 1);
            final int value = Integer.parseInt(Qh178);
            if (value == 0) {
                SimBridgeOffsetPos.lockBeaconWasOff.lock();
                SimBridgeOffsetPos.beaconWasOff = true;
                SimBridgeOffsetPos.lockBeaconWasOff.unlock();
            }
            else {
                SimBridgeOffsetPos.lockBeaconWasOff.lock();
                final boolean temp = SimBridgeOffsetPos.beaconWasOff;
                SimBridgeOffsetPos.lockBeaconWasOff.unlock();
                if (temp) {
                    alignPsxWithDepRwy(false);
                }
            }
        }
        catch (Exception ex) {}
    }
}
