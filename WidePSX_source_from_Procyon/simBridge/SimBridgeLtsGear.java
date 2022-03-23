// 
// Decompiled by Procyon v0.5.36
// 

package simBridge;

import java.io.IOException;
import network.BridgeSimConnect;
import util.StatusMonitor;
import util.StringSplitter;

public class SimBridgeLtsGear
{
    private static boolean gearUp;
    private static boolean gearDn;
    private static boolean toggleTaxi;
    private static boolean toggleBcn;
    private static boolean toggleNav;
    private static boolean toggleWing;
    private static boolean toggleLogo;
    
    static {
        SimBridgeLtsGear.gearUp = false;
        SimBridgeLtsGear.gearDn = false;
        SimBridgeLtsGear.toggleTaxi = true;
        SimBridgeLtsGear.toggleBcn = true;
        SimBridgeLtsGear.toggleNav = true;
        SimBridgeLtsGear.toggleWing = true;
        SimBridgeLtsGear.toggleLogo = true;
    }
    
    public static void dispatchQs424(final String argQs424) throws IOException {
        int gearPos = 0;
        try {
            final StringSplitter ss = new StringSplitter(argQs424);
            gearPos = Integer.parseInt(ss.splitBetween('=', ';', 1, 1, false, false));
        }
        catch (NumberFormatException e) {
            return;
        }
        if (gearPos == 0) {
            if (!SimBridgeBase.getOnGround() && StatusMonitor.getSimBridgeIsRunning()) {
                BridgeSimConnect.updateGearPos(true);
            }
            SimBridgeLtsGear.gearUp = true;
            SimBridgeLtsGear.gearDn = false;
        }
        else if (gearPos == 20000) {
            if (StatusMonitor.getSimBridgeIsRunning()) {
                BridgeSimConnect.updateGearPos(false);
            }
            SimBridgeLtsGear.gearDn = true;
            SimBridgeLtsGear.gearUp = false;
        }
        if (StatusMonitor.getSimBridgeIsRunning() && !SimBridgeBase.getOnGround()) {
            if (SimBridgeLtsGear.gearUp && gearPos > 0) {
                BridgeSimConnect.updateGearPos(false);
            }
            else if (SimBridgeLtsGear.gearDn && gearPos < 20000) {
                BridgeSimConnect.updateGearPos(true);
            }
        }
    }
    
    public static void dispatchQs443(final String argQs443) throws IOException {
        String temp = argQs443;
        temp = temp.substring(temp.indexOf(61) + 1);
        try {
            final int ldg1 = Integer.parseInt(temp.substring(0, 1));
            final int ldg2 = Integer.parseInt(temp.substring(1, 2));
            final int ldg3 = Integer.parseInt(temp.substring(2, 3));
            final int ldg4 = Integer.parseInt(temp.substring(3, 4));
            final int taxi = Integer.parseInt(temp.substring(6, 7));
            final int beaconUp = Integer.parseInt(temp.substring(7, 8));
            final int navL = Integer.parseInt(temp.substring(9, 10));
            final int strobe = Integer.parseInt(temp.substring(11, 12));
            final int wing = Integer.parseInt(temp.substring(12, 13));
            final int logo = Integer.parseInt(temp.substring(13, 14));
            if (StatusMonitor.getSimBridgeIsRunning()) {
                final int simLtsState = BridgeSimConnect.getSimLightState();
                if (ldg1 <= 1 && ldg2 <= 1 && ldg3 <= 1 && ldg4 <= 1) {
                    BridgeSimConnect.updateLdgLts(false);
                }
                else {
                    BridgeSimConnect.updateLdgLts(true);
                }
                if ((taxi == 0 && (simLtsState & 0x8) == 0x0) || (taxi == 1 && (simLtsState & 0x8) != 0x0)) {
                    SimBridgeLtsGear.toggleTaxi = false;
                }
                else {
                    SimBridgeLtsGear.toggleTaxi = true;
                }
                if (SimBridgeLtsGear.toggleTaxi) {
                    if (taxi == 0) {
                        BridgeSimConnect.updateLtsTaxi(false);
                    }
                    else {
                        BridgeSimConnect.updateLtsTaxi(true);
                    }
                }
                if ((beaconUp == 0 && (simLtsState & 0x2) == 0x0) || (beaconUp == 1 && (simLtsState & 0x2) != 0x0)) {
                    SimBridgeLtsGear.toggleBcn = false;
                }
                else {
                    SimBridgeLtsGear.toggleBcn = true;
                }
                if (SimBridgeLtsGear.toggleBcn) {
                    if (beaconUp == 0) {
                        BridgeSimConnect.updateLtsBcn(false);
                    }
                    else {
                        BridgeSimConnect.updateLtsBcn(true);
                    }
                }
                if ((navL == 0 && (simLtsState & 0x1) == 0x0) || (navL == 1 && (simLtsState & 0x1) != 0x0)) {
                    SimBridgeLtsGear.toggleNav = false;
                }
                else {
                    SimBridgeLtsGear.toggleNav = true;
                }
                if (SimBridgeLtsGear.toggleNav) {
                    if (navL == 0) {
                        BridgeSimConnect.updateLtsNav(false);
                        BridgeSimConnect.updateLtsCabin(false);
                    }
                    else {
                        BridgeSimConnect.updateLtsNav(true);
                        BridgeSimConnect.updateLtsCabin(false);
                    }
                }
                if (strobe == 0) {
                    BridgeSimConnect.updateLtsStrobe(false);
                }
                else {
                    BridgeSimConnect.updateLtsStrobe(true);
                }
                if ((wing == 0 && (simLtsState & 0x80) == 0x0) || (wing == 1 && (simLtsState & 0x80) != 0x0)) {
                    SimBridgeLtsGear.toggleWing = false;
                }
                else {
                    SimBridgeLtsGear.toggleWing = true;
                }
                if (SimBridgeLtsGear.toggleWing) {
                    if (wing == 0) {
                        BridgeSimConnect.updateLtsWing(false);
                    }
                    else {
                        BridgeSimConnect.updateLtsWing(true);
                    }
                }
                if ((logo == 0 && (simLtsState & 0x100) == 0x0) || (logo == 1 && (simLtsState & 0x100) != 0x0)) {
                    SimBridgeLtsGear.toggleLogo = false;
                }
                else {
                    SimBridgeLtsGear.toggleLogo = true;
                }
                if (SimBridgeLtsGear.toggleLogo) {
                    if (logo == 0) {
                        BridgeSimConnect.updateLtsLogo(false);
                    }
                    else {
                        BridgeSimConnect.updateLtsLogo(true);
                    }
                }
            }
        }
        catch (NumberFormatException ex) {}
    }
}
