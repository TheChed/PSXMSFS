// 
// Decompiled by Procyon v0.5.36
// 

package gndService;

import util.StatusMonitor;
import simBridge.SimBridgeDoors;
import network.SocketClientPSXMain;

public class GndServiceDoors implements Runnable
{
    private String action;
    
    public GndServiceDoors(final String argAction) {
        this.action = new String("");
        this.action = argAction;
    }
    
    @Override
    public void run() {
        if (this.action.equals("open")) {
            this.openDoors();
        }
        else {
            this.closeDoors();
        }
    }
    
    private void openDoors() {
        try {
            GndServiceBase.setModDoorComBits(32);
            SocketClientPSXMain.send("Qi179=32");
            if (GndServiceBase.getAcftVersion() == 1) {
                GndServiceBase.setModDoorOpenBits(8);
                SocketClientPSXMain.send("Qi180=8");
                Thread.sleep(5000L);
                GndServiceBase.setModDoorOpenBits(133128);
                SocketClientPSXMain.send("Qi180=133128");
            }
            else {
                GndServiceBase.setModDoorOpenBits(32);
                GndServiceBase.setModDoorManBits(32);
                SocketClientPSXMain.send("Qi180=32");
                SocketClientPSXMain.send("Qi181=32");
                Thread.sleep(5000L);
                GndServiceBase.setModDoorOpenBits(133152);
                GndServiceBase.setModDoorManBits(133152);
                SocketClientPSXMain.send("Qi180=133152");
                SocketClientPSXMain.send("Qi181=133152");
                Thread.sleep(6000L);
                GndServiceBase.setModDoorOpenBits(395296);
                GndServiceBase.setModDoorManBits(395296);
                SocketClientPSXMain.send("Qi180=395296");
                SocketClientPSXMain.send("Qi181=395296");
            }
            SimBridgeDoors.dispatchQi180(GndServiceBase.getAcftVersion(), 1);
            if (StatusMonitor.getGndServiceSimplified()) {
                GndServiceBase.setDynText("Info : Doors are opened. Ground supplies are being connected...");
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private void closeDoors() {
        try {
            GndServiceBase.setModDoorComBits(32);
            SocketClientPSXMain.send("Qi179=32");
            if (GndServiceBase.getAcftVersion() == 0) {
                GndServiceBase.setModDoorOpenBits(133152);
                GndServiceBase.setModDoorManBits(133152);
                SocketClientPSXMain.send("Qi180=133152");
                SocketClientPSXMain.send("Qi181=133152");
                Thread.sleep(5000L);
                GndServiceBase.setModDoorOpenBits(32);
                GndServiceBase.setModDoorManBits(32);
                SocketClientPSXMain.send("Qi180=32");
                SocketClientPSXMain.send("Qi181=32");
                Thread.sleep(6500L);
                GndServiceBase.setModDoorOpenBits(0);
                GndServiceBase.setModDoorManBits(0);
                SocketClientPSXMain.send("Qi180=0");
                SocketClientPSXMain.send("Qi181=0");
            }
            else {
                GndServiceBase.setModDoorOpenBits(8);
                SocketClientPSXMain.send("Qi180=8");
                Thread.sleep(6000L);
                GndServiceBase.setModDoorOpenBits(0);
                SocketClientPSXMain.send("Qi180=0");
            }
            SimBridgeDoors.dispatchQi180(GndServiceBase.getAcftVersion(), 0);
            GndServiceBase.setModDoorComBits(0);
            SocketClientPSXMain.send("Qi179=0");
            if (GndServiceBase.getAcftVersion() == 0) {
                GndServiceFlags.setBoardingFlag(13);
            }
            else {
                GndServiceFlags.setBoardingFlag(21);
            }
            if (StatusMonitor.getGndServiceSimplified()) {
                GndServiceBase.setDynText("Doors are closed. Ground supplies will be automatically removed when an APU GEN is switched ON.");
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
