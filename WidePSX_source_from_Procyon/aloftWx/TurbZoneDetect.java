// 
// Decompiled by Procyon v0.5.36
// 

package aloftWx;

import util.StatusMonitor;
import util.MutableDouble;

public class TurbZoneDetect implements Runnable
{
    private static TurbAirGen turbAirGen;
    private static Thread turbAirGenThread;
    private static boolean insideZone;
    private static boolean zoneSleep;
    private static boolean firstCall;
    
    static {
        TurbZoneDetect.turbAirGen = new TurbAirGen();
        TurbZoneDetect.turbAirGenThread = new Thread(TurbZoneDetect.turbAirGen);
        TurbZoneDetect.insideZone = false;
        TurbZoneDetect.zoneSleep = false;
        TurbZoneDetect.firstCall = true;
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(5000L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean emptyTable = true;
            for (int i = 0; i < 5; ++i) {
                TurbZoneDetect.firstCall = true;
                if (!TurbWptData.getTableTurbLegsValueAt(i, 0).isEmpty()) {
                    emptyTable = false;
                    final MutableDouble d1 = new MutableDouble(TurbBase.getDistance(TurbBase.getPsxLatitude(), TurbBase.getPsxLongitude(), Double.parseDouble(TurbWptData.getTableTurbLegsValueAt(i, 9)), Double.parseDouble(TurbWptData.getTableTurbLegsValueAt(i, 10))));
                    final MutableDouble d2 = new MutableDouble(TurbBase.getDistance(TurbBase.getPsxLatitude(), TurbBase.getPsxLongitude(), Double.parseDouble(TurbWptData.getTableTurbLegsValueAt(i, 11)), Double.parseDouble(TurbWptData.getTableTurbLegsValueAt(i, 12))));
                    final MutableDouble d3 = new MutableDouble(TurbBase.getDistance(Double.parseDouble(TurbWptData.getTableTurbLegsValueAt(i, 9)), Double.parseDouble(TurbWptData.getTableTurbLegsValueAt(i, 10)), Double.parseDouble(TurbWptData.getTableTurbLegsValueAt(i, 11)), Double.parseDouble(TurbWptData.getTableTurbLegsValueAt(i, 12))));
                    final double d1BeforeOffset = d1.get();
                    final double d2BeforeOffset = d2.get();
                    getDistancesWithOffset(i, d1, d2, d3);
                    if ((d3.get() - 2.0 < d1.get() + d2.get() && d1.get() + d2.get() < d3.get() + 2.0) || (d1.get() == 0.0 && TurbWptData.getTableTurbLegsValueAt(i, 5).equals("B") && d1BeforeOffset < Double.parseDouble(TurbWptData.getTableTurbLegsValueAt(i, 6))) || (d1.get() == 0.0 && TurbWptData.getTableTurbLegsValueAt(i, 5).equals("A") && d1BeforeOffset > Double.parseDouble(TurbWptData.getTableTurbLegsValueAt(i, 6))) || (d2.get() == 0.0 && TurbWptData.getTableTurbLegsValueAt(i, 7).equals("B") && d2BeforeOffset > Double.parseDouble(TurbWptData.getTableTurbLegsValueAt(i, 8))) || (d2.get() == 0.0 && TurbWptData.getTableTurbLegsValueAt(i, 7).equals("A") && d2BeforeOffset < Double.parseDouble(TurbWptData.getTableTurbLegsValueAt(i, 8)))) {
                        if (!TurbZoneDetect.insideZone || TurbZoneDetect.zoneSleep || TurbZoneDetect.firstCall) {
                            TurbZoneDetect.insideZone = true;
                            TurbZoneDetect.zoneSleep = false;
                            TurbZoneDetect.firstCall = false;
                            if (getIsInsideVerticalLimits(i)) {
                                TurbBase.setTurbIntensity(TurbWptData.getTableTurbLegsValueAt(i, 0));
                                try {
                                    TurbZoneDetect.turbAirGenThread.start();
                                }
                                catch (IllegalThreadStateException ex) {}
                            }
                            else {
                                TurbBase.setTurbIntensity("N");
                            }
                        }
                        else {
                            TurbZoneDetect.zoneSleep = true;
                        }
                    }
                    else {
                        TurbZoneDetect.insideZone = false;
                        TurbBase.setTurbIntensity("N");
                    }
                }
            }
            if (emptyTable) {
                TurbZoneDetect.insideZone = false;
                TurbBase.setTurbIntensity("N");
            }
        }
    }
    
    private static void getDistancesWithOffset(final int argLine, final MutableDouble argD1, final MutableDouble argD2, final MutableDouble argD) {
        final String befAftBegin = new String(TurbWptData.getTableTurbLegsValueAt(argLine, 5));
        final double beginOffset = Double.parseDouble(TurbWptData.getTableTurbLegsValueAt(argLine, 6));
        final String befAftEnd = new String(TurbWptData.getTableTurbLegsValueAt(argLine, 7));
        final double endOffset = Double.parseDouble(TurbWptData.getTableTurbLegsValueAt(argLine, 8));
        if (argD1.get() + argD2.get() < argD.get() + 2.0) {
            if (befAftBegin.equals("B")) {
                argD1.set(argD1.get() + beginOffset);
                argD.set(argD.get() + beginOffset);
            }
            else {
                argD1.set(argD1.get() - beginOffset);
                argD.set(argD.get() - beginOffset);
            }
            if (befAftEnd.equals("B")) {
                argD2.set(argD2.get() - endOffset);
                argD.set(argD.get() - endOffset);
            }
            else {
                argD2.set(argD2.get() + endOffset);
                argD.set(argD.get() + endOffset);
            }
            if (argD1.get() < 0.0) {
                argD1.set(0.0);
            }
            if (argD2.get() < 0.0) {
                argD2.set(0.0);
            }
            if (argD.get() < 0.0) {
                argD.set(0.0);
            }
        }
        else if (argD2.get() > argD1.get()) {
            if (befAftBegin.equals("B")) {
                argD1.set(argD1.get() - beginOffset);
                argD.set(argD.get() + beginOffset);
            }
            else {
                argD1.set(argD1.get() + beginOffset);
                argD.set(argD.get() - beginOffset);
            }
            if (befAftEnd.equals("B")) {
                argD2.set(argD2.get() - endOffset);
                argD.set(argD.get() - endOffset);
            }
            else {
                argD2.set(argD2.get() + endOffset);
                argD.set(argD.get() + endOffset);
            }
            if (argD1.get() < 0.0) {
                argD1.set(0.0);
            }
            if (argD2.get() < 0.0) {
                argD2.set(0.0);
            }
            if (argD.get() < 0.0) {
                argD.set(0.0);
            }
        }
        else {
            if (befAftBegin.equals("B")) {
                argD1.set(argD1.get() + beginOffset);
                argD.set(argD.get() + beginOffset);
            }
            else {
                argD1.set(argD1.get() - beginOffset);
                argD.set(argD.get() - beginOffset);
            }
            if (befAftEnd.equals("B")) {
                argD2.set(argD2.get() + endOffset);
                argD.set(argD.get() - endOffset);
            }
            else {
                argD2.set(argD2.get() - endOffset);
                argD.set(argD.get() + endOffset);
            }
            if (argD1.get() < 0.0) {
                argD1.set(0.0);
            }
            if (argD2.get() < 0.0) {
                argD2.set(0.0);
            }
            if (argD.get() < 0.0) {
                argD.set(0.0);
            }
        }
    }
    
    private static boolean getIsInsideVerticalLimits(final int argLine) {
        try {
            if (TurbWptData.getTableTurbLegsValueAt(argLine, 3).isEmpty()) {
                TurbWptData.setTableTurbLegsValueAt(argLine, 3, "0");
            }
            if (TurbWptData.getTableTurbLegsValueAt(argLine, 4).isEmpty()) {
                TurbWptData.setTableTurbLegsValueAt(argLine, 4, "0");
            }
            final int lowLim = Integer.parseInt(TurbWptData.getTableTurbLegsValueAt(argLine, 3));
            final int upLim = Integer.parseInt(TurbWptData.getTableTurbLegsValueAt(argLine, 4));
            StatusMonitor.setTurbDataInvalidLowUpLim(false);
            StatusMonitor.setTurbConfigIsReady(true);
            if (TurbBase.getPsxAltitude() < 20000) {
                return false;
            }
            if (lowLim == 0 && upLim == 0) {
                return true;
            }
            if (lowLim == 0) {
                return TurbBase.getPsxAltitude() <= upLim;
            }
            if (upLim == 0) {
                return TurbBase.getPsxAltitude() >= lowLim;
            }
            return TurbBase.getPsxAltitude() >= lowLim && TurbBase.getPsxAltitude() <= upLim;
        }
        catch (NumberFormatException e) {
            StatusMonitor.setTurbDataInvalidLowUpLim(true);
            StatusMonitor.setTurbConfigIsReady(false);
            return false;
        }
    }
}
