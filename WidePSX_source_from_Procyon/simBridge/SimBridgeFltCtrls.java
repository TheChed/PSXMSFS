// 
// Decompiled by Procyon v0.5.36
// 

package simBridge;

import java.io.IOException;
import network.BridgeSimConnect;
import util.StatusMonitor;

public class SimBridgeFltCtrls
{
    public static void dispatchQs480() throws IOException {
        if (StatusMonitor.getSimBridgeIsRunning()) {
            try {
                final String Qs480 = SimBridgeBase.getQs480();
                int ailerons = 0;
                final int aileronL = Integer.parseInt(Qs480.substring(6, 8));
                final int aileronR = Integer.parseInt(Qs480.substring(12, 14));
                int elev = Integer.parseInt(Qs480.substring(20, 22));
                int rudder = Integer.parseInt(Qs480.substring(22, 24));
                if (aileronL > 15) {
                    ailerons = (aileronL - 15) * 655;
                }
                else {
                    ailerons = (aileronR - 15) * -655;
                }
                BridgeSimConnect.updateAileronsPos(ailerons);
                if (rudder == 32) {
                    rudder = 0;
                }
                else if (rudder > 32) {
                    rudder = (rudder - 32) * -511;
                }
                else {
                    rudder = (32 - rudder) * 511;
                }
                BridgeSimConnect.updateRudderPos(rudder);
                if (elev == 15) {
                    elev = 0;
                }
                else if (elev > 15) {
                    elev = (elev - 15) * -1092;
                }
                else {
                    elev = (15 - elev) * 1092;
                }
                BridgeSimConnect.updateElevPos(elev);
            }
            catch (Exception ex) {}
        }
    }
    
    public static void dispatchQh388() throws IOException {
        if (StatusMonitor.getSimBridgeIsRunning()) {
            try {
                String Qh388 = SimBridgeBase.getQh388();
                Qh388 = Qh388.substring(Qh388.indexOf(61) + 1);
                double value = Double.parseDouble(Qh388);
                value *= 20.47875;
                if (value < 1300.0) {
                    value = 0.0;
                }
                BridgeSimConnect.updateSpdbrkPos((int)value);
            }
            catch (Exception ex) {}
        }
    }
    
    public static void dispatchQh389() throws IOException {
        if (StatusMonitor.getSimBridgeIsRunning()) {
            try {
                String Qh389 = SimBridgeBase.getQh389();
                Qh389 = Qh389.substring(Qh389.indexOf(61) + 1);
                int value = Integer.parseInt(Qh389);
                value *= (int)2730.5;
                BridgeSimConnect.updateFlapsPos(value);
            }
            catch (Exception ex) {}
        }
    }
    
    public static void dispatchQh397(final String argValue) throws IOException {
        if (StatusMonitor.getSimBridgeIsRunning()) {
            try {
                final String Qh397 = argValue;
                final int value = Integer.parseInt(Qh397.substring(Qh397.indexOf(61) + 1));
                BridgeSimConnect.updateParkingBrake(value);
            }
            catch (Exception ex) {}
        }
    }
}
