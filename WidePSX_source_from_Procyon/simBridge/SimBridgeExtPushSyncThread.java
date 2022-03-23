// 
// Decompiled by Procyon v0.5.36
// 

package simBridge;

import network.SocketClientPSXMain;
import util.StringSplitter;
import network.BridgeSimConnect;
import gndService.GndServiceBase;

public class SimBridgeExtPushSyncThread implements Runnable
{
    @Override
    public void run() {
        while (true) {
            if (GndServiceBase.getReleaseScenGen()) {
                final double[] simPosAltAtt = BridgeSimConnect.getSimPosAltAtt();
                final StringSplitter ss = new StringSplitter(GndServiceBase.getQs121());
                final String alTas = new String(ss.splitBetween(';', ';', 3, 5, true, true));
                SocketClientPSXMain.send("Qs121=0;0;" + String.valueOf(simPosAltAtt[6]) + alTas + String.valueOf(simPosAltAtt[0]) + ";" + String.valueOf(simPosAltAtt[1]));
                try {
                    Thread.sleep(100L);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
