// 
// Decompiled by Procyon v0.5.36
// 

package gndService;

import util.StatusMonitor;
import simBridge.SimBridgeDoors;
import network.SocketClientPSXMain;
import java.awt.Font;
import javax.swing.JLabel;

public class GndServicePreFltSimplified implements Runnable
{
    private static JLabel lblDynStatus;
    private static boolean refuelingStarted;
    private static boolean refuelingInProgress;
    private static boolean refuelComplete;
    private static boolean doorsOpened;
    private static boolean doorsClosed;
    private static boolean gndStuffRemoved;
    private static boolean pushStarted;
    private static boolean cabinReadyCountdown;
    
    static {
        GndServicePreFltSimplified.lblDynStatus = new JLabel();
        GndServicePreFltSimplified.refuelingStarted = false;
        GndServicePreFltSimplified.refuelingInProgress = false;
        GndServicePreFltSimplified.refuelComplete = false;
        GndServicePreFltSimplified.doorsOpened = false;
        GndServicePreFltSimplified.doorsClosed = false;
        GndServicePreFltSimplified.gndStuffRemoved = false;
        GndServicePreFltSimplified.pushStarted = false;
        GndServicePreFltSimplified.cabinReadyCountdown = false;
    }
    
    public GndServicePreFltSimplified(final JLabel argLabel) {
        (GndServicePreFltSimplified.lblDynStatus = argLabel).setFont(new Font("Tahoma", 2, 11));
    }
    
    @Override
    public void run() {
        if (!GndServiceBase.getOnGround()) {
            GndServiceBase.setDynText("Info : Waiting for landing -> Set PRK BRK, Engines OFF, Beacon OFF");
            SocketClientPSXMain.send("Qi102=0");
            return;
        }
        if (GndServiceBase.getPlanedZfw() != 0) {
            GndServiceBase.sendZfw();
        }
        if (GndServiceBase.getPlanedFuel() != 0) {
            GndServiceBase.sendReleaseFuel();
        }
        SocketClientPSXMain.send("Qi191=110250");
    Label_0549:
        while (true) {
            try {
                while (GndServiceBase.getOnGround()) {
                    if (!GndServicePreFltSimplified.doorsOpened) {
                        if (GndServiceBase.getAcftVersion() == 1) {
                            final String Qi0 = GndServiceBase.getQi0();
                            SocketClientPSXMain.send("Qi0=0");
                            Thread.sleep(500L);
                            SocketClientPSXMain.send(Qi0);
                            GndServiceBase.setModDoorComBits(32);
                            GndServiceBase.setModDoorOpenBits(133128);
                            SocketClientPSXMain.send("Qi179=32");
                            SocketClientPSXMain.send("Qi180=133128");
                        }
                        else {
                            GndServiceBase.setModDoorComBits(32);
                            GndServiceBase.setModDoorOpenBits(395296);
                            GndServiceBase.setModDoorManBits(395296);
                            SocketClientPSXMain.send("Qi179=32");
                            SocketClientPSXMain.send("Qi180=395296");
                            SocketClientPSXMain.send("Qi181=395296");
                        }
                        SimBridgeDoors.dispatchQi180(GndServiceBase.getAcftVersion(), 1);
                        GndServicePreFltSimplified.doorsOpened = true;
                    }
                    if (!GndServicePreFltSimplified.refuelingStarted && GndServiceBase.getIrsLNav()) {
                        SocketClientPSXMain.send("Qi220=1");
                        GndServicePreFltSimplified.refuelingStarted = true;
                        GndServicePreFltSimplified.refuelingInProgress = true;
                        GndServicePreFltSimplified.lblDynStatus.setText("Refueling is in progress...");
                        GndServiceBase.setRefuelCompleted(false);
                    }
                    if (GndServicePreFltSimplified.refuelingInProgress && GndServiceBase.getRefuelCompleted()) {
                        GndServicePreFltSimplified.lblDynStatus.setText("Refueling is completed. Doors will close automatically when the HYD DEM Pump 1 is set to \"ON\".");
                        GndServicePreFltSimplified.refuelingInProgress = false;
                        GndServicePreFltSimplified.refuelComplete = true;
                    }
                    if (!GndServicePreFltSimplified.doorsClosed && GndServiceBase.getHydDem1Auto() && GndServicePreFltSimplified.refuelComplete) {
                        final GndServiceDoors gndServiceDoors = new GndServiceDoors("close");
                        final Thread gndServiceDoorsThread = new Thread(gndServiceDoors);
                        gndServiceDoorsThread.start();
                        GndServicePreFltSimplified.doorsClosed = true;
                        GndServicePreFltSimplified.lblDynStatus.setText("Doors are closing...");
                    }
                    if (!GndServicePreFltSimplified.gndStuffRemoved && GndServicePreFltSimplified.doorsClosed && ((GndServiceBase.getPsxElecSysBits() & 0x200) != 0x0 || (GndServiceBase.getPsxElecSysBits() & 0x400) != 0x0)) {
                        GndServicePreFltSimplified.lblDynStatus.setText("Ground equipments are being removed...");
                        Thread.sleep(30000L);
                        int tempElec = GndServiceBase.getPsxElecSysBits();
                        tempElec &= 0x7FFFFFF6;
                        SocketClientPSXMain.send("Qi132=" + String.valueOf(tempElec));
                        int tempBleed = GndServiceBase.getPsxBleedAirBits();
                        tempBleed &= 0xFFFD;
                        SocketClientPSXMain.send("Qi174=" + String.valueOf(tempBleed));
                        GndServicePreFltSimplified.gndStuffRemoved = true;
                    }
                    if (GndServicePreFltSimplified.gndStuffRemoved && !GndServicePreFltSimplified.pushStarted) {
                        if (StatusMonitor.getGndServiceExtPush()) {
                            GndServicePreFltSimplified.lblDynStatus.setText("Ground equipments are removed. Ready for externally controled pushback.");
                            GndServicePreFltSimplified.pushStarted = true;
                        }
                        else {
                            GndServicePreFltSimplified.lblDynStatus.setText("Ground equipments are removed. Pushback will automatically start after Beacon is switched ON and parking brake is released.");
                            if (GndServiceBase.getBeacon() && !GndServiceBase.getParkBrkLev()) {
                                GndServicePreFltSimplified.lblDynStatus.setText("Pushback is in progress...");
                                final GndServicePushBack gndServicePushBack = new GndServicePushBack();
                                final Thread gndServicePushbackThread = new Thread(gndServicePushBack);
                                gndServicePushbackThread.start();
                                GndServicePreFltSimplified.pushStarted = true;
                            }
                        }
                    }
                    if (GndServicePreFltSimplified.pushStarted && !GndServiceBase.getReleaseScenGen() && !GndServicePreFltSimplified.cabinReadyCountdown) {
                        GndServicePreFltSimplified.cabinReadyCountdown = true;
                        if (GndServiceBase.getAcftVersion() == 0) {
                            GndServicePreFltSimplified.lblDynStatus.setText("Info : Waiting for cabin ready announcement...");
                            Thread.sleep(300000L);
                            SocketClientPSXMain.send("Qi102=67");
                            GndServiceBase.setDynText("Info : Waiting for landing -> Set PRK BRK, Engines OFF, Beacon OFF");
                            break Label_0549;
                        }
                        GndServicePreFltSimplified.lblDynStatus.setText("Info : Waiting for landing -> Set PRK BRK, Engines OFF, Beacon OFF");
                        break Label_0549;
                    }
                    else {
                        Thread.sleep(5000L);
                    }
                }
                GndServiceBase.setDynText("Info : Waiting for landing -> Set PRK BRK, Engines OFF, Beacon OFF");
                break;
            }
            catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }
}
