// 
// Decompiled by Procyon v0.5.36
// 

package aloftWx;

import network.SocketClientPSXMain;
import network.BridgeSimConnect;

public class AloftWxDyn implements Runnable
{
    private static boolean firstInjectToPsx;
    
    static {
        AloftWxDyn.firstInjectToPsx = true;
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
            if (AloftWxBase.getPsxOnGround()) {
                AloftWxDyn.firstInjectToPsx = true;
            }
            if (AloftWxBase.getPsxAltitude() >= 20000) {
                AloftWxDyn.firstInjectToPsx = false;
                final int tempOat = Math.abs(AloftWxBase.smoothOat((int)BridgeSimConnect.getSimOat()));
                final String oat = new String(AloftWxBase.addOatLeadZero(tempOat));
                final String windDir = new String(AloftWxBase.addWindLeadZero((int)BridgeSimConnect.getSimWindDir()));
                final int tempWindVel = (int)BridgeSimConnect.getSimWindVel();
                String windVel = new String("");
                if (tempWindVel < 10) {
                    windVel = "00" + String.valueOf(tempWindVel);
                }
                else if (tempWindVel >= 10 && tempWindVel < 100) {
                    windVel = "0" + String.valueOf(tempWindVel);
                }
                else {
                    windVel = String.valueOf(tempWindVel);
                }
                SocketClientPSXMain.send("Qi243=500");
                SocketClientPSXMain.send("Qs327=A" + AloftWxBase.getPsxAltitude() + 'T' + oat + "W000" + windDir + windVel + ';' + windVel + AloftWxBase.getQs327End());
            }
            else {
                if (!AloftWxDyn.firstInjectToPsx) {
                    continue;
                }
                AloftWxDyn.firstInjectToPsx = false;
                final int currentOat = AloftWxBase.getPsxOat();
                final int currentAlt = AloftWxBase.getPsxAltitude();
                final int temp = Math.abs(2 * ((20000 - currentAlt) / 1000));
                final int oat2 = Math.abs(currentOat - temp);
                SocketClientPSXMain.send("Qi243=500");
                SocketClientPSXMain.send("Qs327=A20000T" + AloftWxBase.addOatLeadZero(oat2) + "w00000000;00" + AloftWxBase.getQs327End());
            }
        }
    }
}
