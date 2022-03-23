// 
// Decompiled by Procyon v0.5.36
// 

package simBridge;

import util.StringSplitter;
import java.io.IOException;
import network.BridgeSimConnect;
import util.StatusMonitor;

public class SimBridgeEngines
{
    public static void dispatchQh392(final String argData) throws IOException {
        final int value = Integer.parseInt(argData.substring(argData.indexOf(61) + 1));
        if (StatusMonitor.getSimBridgeIsRunning()) {
            if (value == 1) {
                BridgeSimConnect.startEngines(1);
            }
            else {
                BridgeSimConnect.stopEngines(1);
            }
        }
    }
    
    public static void dispatchQh393(final String argData) throws IOException {
        final int value = Integer.parseInt(argData.substring(argData.indexOf(61) + 1));
        if (StatusMonitor.getSimBridgeIsRunning()) {
            if (value == 1) {
                BridgeSimConnect.startEngines(2);
            }
            else {
                BridgeSimConnect.stopEngines(2);
            }
        }
    }
    
    public static void dispatchQh394(final String argData) throws IOException {
        final int value = Integer.parseInt(argData.substring(argData.indexOf(61) + 1));
        if (StatusMonitor.getSimBridgeIsRunning()) {
            if (value == 1) {
                BridgeSimConnect.startEngines(3);
            }
            else {
                BridgeSimConnect.stopEngines(3);
            }
        }
    }
    
    public static void dispatchQh395(final String argData) throws IOException {
        final int value = Integer.parseInt(argData.substring(argData.indexOf(61) + 1));
        if (StatusMonitor.getSimBridgeIsRunning()) {
            if (value == 1) {
                BridgeSimConnect.startEngines(4);
            }
            else {
                BridgeSimConnect.stopEngines(4);
            }
        }
    }
    
    public static void dispatchQs436(final String argData) throws IOException {
        if (StatusMonitor.getSimBridgeIsRunning()) {
            final StringSplitter ss = new StringSplitter(argData);
            int tla1 = Integer.parseInt(ss.splitBetween('=', ';', 1, 1, false, false));
            int tla2 = Integer.parseInt(ss.splitBetween(';', ';', 1, 2, false, false));
            int tla3 = Integer.parseInt(ss.splitBetween(';', ';', 2, 3, false, false));
            int tla4 = Integer.parseInt(ss.splitFrom(';', 3, false));
            if (tla1 >= 0 && tla1 <= 3104) {
                tla1 = -16383;
            }
            else if (tla1 > 3104) {
                tla1 *= 2;
            }
            else if (tla1 < -500) {
                tla1 = -32000;
            }
            if (tla2 >= 0 && tla2 <= 3104) {
                tla2 = -16383;
            }
            else if (tla2 > 3104) {
                tla2 *= 2;
            }
            else if (tla2 < -500) {
                tla2 = -32000;
            }
            if (tla3 >= 0 && tla3 <= 3104) {
                tla3 = -16383;
            }
            else if (tla3 > 3104) {
                tla3 *= 2;
            }
            else if (tla3 < -500) {
                tla3 = -32000;
            }
            if (tla4 >= 0 && tla4 <= 3104) {
                tla4 = -16383;
            }
            else if (tla4 > 3104) {
                tla4 *= 2;
            }
            else if (tla4 < -500) {
                tla4 = -32000;
            }
            BridgeSimConnect.updateThrottles(tla1, tla2, tla3, tla4);
        }
    }
}
