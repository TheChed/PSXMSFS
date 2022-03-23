// 
// Decompiled by Procyon v0.5.36
// 

package simBridge;

import java.io.IOException;
import network.BridgeSimConnect;
import util.StatusMonitor;

public class SimBridgeDoors
{
    public static void dispatchQi180(final int argAcftVersion, final int argDoorOpenBits) {
        if (StatusMonitor.getSimBridgeIsRunning() && StatusMonitor.getSimBridgeDoorsSync()) {
            try {
                if (argDoorOpenBits != 0) {
                    BridgeSimConnect.openDoors(argAcftVersion);
                }
                else {
                    BridgeSimConnect.closeDoors(argAcftVersion);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
