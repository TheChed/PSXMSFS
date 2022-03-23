// 
// Decompiled by Procyon v0.5.36
// 

package gndService;

import network.SocketClientPSXMain;

public class GndServicePostFltSimplified implements Runnable
{
    @Override
    public void run() {
        try {
            while (GndServiceBase.getBeacon()) {
                Thread.sleep(1000L);
            }
            GndServiceBase.setDynText("Info : Doors are being opened...");
            Thread.sleep(60000L);
            final GndServiceDoors gndServiceDoors = new GndServiceDoors("open");
            final Thread gndServiceDoorsThread = new Thread(gndServiceDoors);
            gndServiceDoorsThread.start();
            Thread.sleep(120000L);
            int tempElec = GndServiceBase.getPsxElecSysBits();
            tempElec |= 0x9;
            SocketClientPSXMain.send("Qi132=" + String.valueOf(tempElec));
            int tempBleed = GndServiceBase.getPsxBleedAirBits();
            tempBleed |= 0x2;
            SocketClientPSXMain.send("Qi174=" + String.valueOf(tempBleed));
            GndServiceBase.setDynText("End of the Ground Services simulation. Restart WidePSX and module for a new process to begin");
        }
        catch (InterruptedException ex) {}
    }
}
