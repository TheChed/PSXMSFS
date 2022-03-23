// 
// Decompiled by Procyon v0.5.36
// 

package gndService;

import simBridge.SimBridgeDoors;
import util.StatusMonitor;
import network.SocketClientPSXMain;
import java.awt.Font;
import javax.swing.JLabel;

public class GndServicePreFltConv implements Runnable
{
    private static JLabel lblDynStatus;
    private static GndServicePreFltTimer gndServicePreFltTimer;
    private static Thread gndServicePreFltTimerThread;
    
    static {
        GndServicePreFltConv.lblDynStatus = new JLabel();
        GndServicePreFltConv.gndServicePreFltTimer = new GndServicePreFltTimer();
        GndServicePreFltConv.gndServicePreFltTimerThread = new Thread(GndServicePreFltConv.gndServicePreFltTimer);
    }
    
    public GndServicePreFltConv(final JLabel argLabel) {
        (GndServicePreFltConv.lblDynStatus = argLabel).setFont(new Font("Tahoma", 2, 11));
    }
    
    @Override
    public void run() {
        final String path = new String(GndServiceBase.getSoundsPath(true));
        if (!GndServiceBase.getOnGround()) {
            GndServiceBase.setDynText("Info : Waiting for landing -> Set PRK BRK, Engines OFF, Beacon OFF");
            SocketClientPSXMain.send("Qi102=0");
            return;
        }
        if (StatusMonitor.getGndServiceColdDarkPhase()) {
            GndServiceFlags.setPowerFlag(0);
            GndServiceFlags.setRefuelFlag(0);
            GndServiceFlags.setBoardingFlag(0);
            GndServiceFlags.setMiscFlag(0);
            GndServiceFlags.setPushBackFlag(0);
            GndServiceFlags.setEngineStartFlag(0);
        }
        else if (StatusMonitor.getGndServiceRefuelCompletePhase()) {
            GndServiceFlags.setPowerFlag(0);
            GndServiceFlags.setRefuelFlag(12);
            if (GndServiceBase.getAcftVersion() == 0) {
                GndServiceFlags.setBoardingFlag(13);
            }
            else {
                GndServiceFlags.setBoardingFlag(21);
            }
            GndServiceFlags.setMiscFlag(0);
            GndServiceFlags.setPushBackFlag(0);
            GndServiceFlags.setEngineStartFlag(0);
        }
        else {
            GndServiceFlags.setPowerFlag(6);
            GndServiceFlags.setRefuelFlag(12);
            if (GndServiceBase.getAcftVersion() == 0) {
                GndServiceFlags.setBoardingFlag(13);
            }
            else {
                GndServiceFlags.setBoardingFlag(21);
            }
            GndServiceFlags.setMiscFlag(7);
            GndServiceFlags.setPushBackFlag(0);
            GndServiceFlags.setEngineStartFlag(0);
        }
        if (StatusMonitor.getGndServiceColdDarkPhase()) {
            try {
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
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            GndServiceBase.sendReleaseFuel();
        }
        else {
            if (StatusMonitor.getGndServicePushReadyPhase()) {
                GndServiceBase.setDynText("When ready for Push & Start : Set beacon ON");
            }
            try {
                if (GndServiceBase.getAcftVersion() == 1) {
                    final String Qi0 = GndServiceBase.getQi0();
                    SocketClientPSXMain.send("Qi0=0");
                    Thread.sleep(500L);
                    SocketClientPSXMain.send(Qi0);
                    GndServiceBase.setModDoorComBits(32);
                    GndServiceBase.setModDoorOpenBits(0);
                    SocketClientPSXMain.send("Qi179=32");
                    SocketClientPSXMain.send("Qi180=0");
                }
                else {
                    GndServiceBase.setModDoorComBits(32);
                    GndServiceBase.setModDoorOpenBits(0);
                    GndServiceBase.setModDoorManBits(0);
                    SocketClientPSXMain.send("Qi179=32");
                    SocketClientPSXMain.send("Qi180=0");
                    SocketClientPSXMain.send("Qi181=0");
                }
                SimBridgeDoors.dispatchQi180(GndServiceBase.getAcftVersion(), 0);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            GndServiceBase.setModDoorComBits(0);
            SocketClientPSXMain.send("Qi179=0");
        }
        GndServiceBase.sendZfw();
        SocketClientPSXMain.send("Qi191=110250");
    Label_3606:
        while (true) {
            while (GndServiceBase.getOnGround()) {
                if (GndServiceFlags.getPowerFlag() == 0) {
                    if (GndServiceBase.getPsxOat() > 25 || GndServiceBase.getPsxOat() < 15) {
                        int tempBleed = GndServiceBase.getPsxBleedAirBits();
                        tempBleed |= 0x2;
                        SocketClientPSXMain.send("Qi174=" + String.valueOf(tempBleed));
                    }
                    else {
                        int tempBleed = GndServiceBase.getPsxBleedAirBits();
                        tempBleed &= 0xFFFD;
                        SocketClientPSXMain.send("Qi174=" + String.valueOf(tempBleed));
                    }
                }
                if (GndServiceFlags.getPowerFlag() == 0 && (GndServiceBase.getPsxElecSysBits() & 0x1) == 0x0 && (GndServiceBase.getPsxElecSysBits() & 0x8) == 0x0 && (GndServiceBase.getPsxElecSysBits() & 0x2) == 0x0 && (GndServiceBase.getPsxElecSysBits() & 0x4) == 0x0) {
                    int tempElec = GndServiceBase.getPsxElecSysBits();
                    tempElec |= 0x9;
                    SocketClientPSXMain.send("Qi132=" + String.valueOf(tempElec));
                }
                if (((GndServiceBase.getPsxElecSysBits() & 0x200) != 0x0 || (GndServiceBase.getPsxElecSysBits() & 0x400) != 0x0) && GndServiceFlags.getPowerFlag() == 0) {
                    GndServiceFlags.setPowerFlag(1);
                }
                try {
                    GndServicePreFltConv.gndServicePreFltTimerThread.start();
                }
                catch (IllegalThreadStateException ex) {}
                if (GndServiceBase.getCockpitCallFlt() && GndServiceFlags.getPowerFlag() == 2) {
                    GndServiceBase.setDynText("");
                    GndServiceBase.setCockpitCallFlt(false);
                    GndServiceFlags.setPowerFlag(3);
                }
                if (GndServiceFlags.getPowerFlag() == 3) {
                    try {
                        Thread.sleep(3000L);
                    }
                    catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                    GndServiceBase.playSoundClip(true, String.valueOf(path) + "askGndEqRemove.wav");
                    GndServiceFlags.setPowerFlag(4);
                    GndServiceBase.setDynText("Check PRK BRK set and permit the Ground Engineer to remove ground equipment and chocks");
                }
                if (GndServiceBase.getCockpitCallFlt() && GndServiceFlags.getPowerFlag() == 4) {
                    GndServiceBase.setDynText("");
                    GndServiceBase.setCockpitCallFlt(false);
                    GndServiceFlags.setPowerFlag(5);
                }
                if (GndServiceFlags.getPowerFlag() == 5) {
                    try {
                        Thread.sleep(2500L);
                    }
                    catch (InterruptedException e3) {
                        e3.printStackTrace();
                    }
                    GndServiceBase.playSoundClip(true, String.valueOf(path) + "rgrGndEqRemove.wav");
                    GndServiceBase.setDynText("Info : Ground equipment and chocks are being removed...");
                    try {
                        Thread.sleep(5000L);
                    }
                    catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                    int tempElec = GndServiceBase.getPsxElecSysBits();
                    tempElec &= 0x7FFFFFF6;
                    SocketClientPSXMain.send("Qi132=" + String.valueOf(tempElec));
                    int tempBleed2 = GndServiceBase.getPsxBleedAirBits();
                    tempBleed2 &= 0xFFFD;
                    SocketClientPSXMain.send("Qi174=" + String.valueOf(tempBleed2));
                    GndServiceFlags.setPowerFlag(6);
                    GndServiceBase.setConversation(false);
                    GndServiceBase.setDynText("Info : Ground equipment and chocks are removed");
                }
                if (GndServiceBase.getCockpitCallFlt() && GndServiceFlags.getRefuelFlag() == 1) {
                    GndServiceBase.setDynText("");
                    GndServiceBase.setCockpitCallFlt(false);
                    GndServiceFlags.setRefuelFlag(2);
                }
                if (GndServiceFlags.getRefuelFlag() == 2) {
                    try {
                        Thread.sleep(2000L);
                    }
                    catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                    GndServiceBase.playSoundClip(true, String.valueOf(path) + "refuelDelay.wav");
                    GndServiceFlags.setRefuelFlag(3);
                    GndServiceBase.setDynText("Acknowledge the refueling delayed announcement");
                }
                if (GndServiceBase.getCockpitCallFlt() && GndServiceFlags.getRefuelFlag() == 3) {
                    GndServiceBase.setDynText("Info : Refueling is delayed...");
                    GndServiceBase.setCockpitCallFlt(false);
                    GndServiceFlags.setRefuelFlag(4);
                    GndServiceBase.setConversation(false);
                }
                if (GndServiceBase.getCockpitCallFlt() && GndServiceFlags.getRefuelFlag() == 5) {
                    GndServiceBase.setDynText("");
                    GndServiceBase.setCockpitCallFlt(false);
                    GndServiceFlags.setRefuelFlag(6);
                }
                if (GndServiceFlags.getRefuelFlag() == 6) {
                    try {
                        Thread.sleep(2500L);
                    }
                    catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                    GndServiceBase.playSoundClip(true, String.valueOf(path) + "refuelStart.wav");
                    GndServiceFlags.setRefuelFlag(7);
                    GndServiceBase.setDynText("Acknowledge the refueling started announcement");
                }
                if (GndServiceBase.getCockpitCallFlt() && GndServiceFlags.getRefuelFlag() == 7) {
                    GndServiceBase.setDynText("Info : Refueling is in progress...");
                    GndServiceBase.setCockpitCallFlt(false);
                    GndServiceFlags.setRefuelFlag(8);
                    try {
                        Thread.sleep(3000L);
                    }
                    catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                    SocketClientPSXMain.send("Qi220=1");
                    GndServiceBase.setConversation(false);
                }
                if (GndServiceBase.getCockpitCallFlt() && GndServiceFlags.getRefuelFlag() == 9) {
                    GndServiceBase.setDynText("");
                    GndServiceBase.setCockpitCallFlt(false);
                    GndServiceFlags.setRefuelFlag(10);
                }
                if (GndServiceFlags.getRefuelFlag() == 10) {
                    try {
                        Thread.sleep(3500L);
                    }
                    catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                    GndServiceBase.playSoundClip(true, String.valueOf(path) + "refuelComplete.wav");
                    GndServiceFlags.setRefuelFlag(11);
                    GndServiceBase.setDynText("Acknowledge the refueling completed announcement");
                }
                if (GndServiceBase.getCockpitCallFlt() && GndServiceFlags.getRefuelFlag() == 11) {
                    GndServiceBase.setDynText("Info : Refueling is completed");
                    GndServiceBase.setCockpitCallFlt(false);
                    GndServiceFlags.setRefuelFlag(12);
                    GndServiceBase.setConversation(false);
                }
                if (GndServiceBase.getCockpitCallCab() && GndServiceFlags.getBoardingFlag() == 1) {
                    GndServiceBase.setDynText("");
                    if (GndServiceBase.getAcftVersion() == 0) {
                        GndServiceBase.setCockpitCallCab(false);
                        GndServiceFlags.setBoardingFlag(2);
                    }
                    else {
                        GndServiceBase.setCockpitCallCab(false);
                    }
                }
                if (GndServiceFlags.getBoardingFlag() == 2) {
                    try {
                        Thread.sleep(3000L);
                    }
                    catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                    GndServiceBase.playSoundClip(false, String.valueOf(path) + "boardingReady.wav");
                    GndServiceFlags.setBoardingFlag(3);
                    GndServiceBase.setDynText("Acknowledge the boarding started announcement");
                }
                if (GndServiceBase.getCockpitCallCab() && GndServiceFlags.getBoardingFlag() == 3) {
                    GndServiceBase.setDynText("Info : Boarding is in progress...");
                    GndServiceBase.setCockpitCallCab(false);
                    GndServiceFlags.setBoardingFlag(4);
                }
                if (GndServiceFlags.getBoardingFlag() == 4) {
                    try {
                        Thread.sleep(2500L);
                    }
                    catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                    GndServiceBase.playSoundClip(false, String.valueOf(path) + "boardingStart.wav");
                    GndServiceFlags.setBoardingFlag(5);
                    SocketClientPSXMain.send("Qi102=0");
                    GndServiceBase.setConversation(false);
                }
                if (GndServiceBase.getCockpitCallCab() && GndServiceFlags.getBoardingFlag() == 6) {
                    GndServiceBase.setDynText("");
                    GndServiceBase.setCockpitCallCab(false);
                    GndServiceFlags.setBoardingFlag(7);
                }
                if (GndServiceFlags.getBoardingFlag() == 7) {
                    try {
                        Thread.sleep(3500L);
                    }
                    catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                    GndServiceBase.playSoundClip(false, String.valueOf(path) + "boardingDelay.wav");
                    GndServiceFlags.setBoardingFlag(8);
                    GndServiceBase.setDynText("Acknowledge the boarding delayed announcement");
                }
                if (GndServiceBase.getCockpitCallCab() && GndServiceFlags.getBoardingFlag() == 8) {
                    GndServiceBase.setDynText("Info : Boarding end is delayed...");
                    GndServiceBase.setCockpitCallCab(false);
                    GndServiceFlags.setBoardingFlag(9);
                    SocketClientPSXMain.send("Qi102=0");
                    GndServiceBase.setConversation(false);
                }
                if (GndServiceBase.getCockpitCallCab() && GndServiceFlags.getBoardingFlag() == 10) {
                    GndServiceBase.setDynText("");
                    GndServiceBase.setCockpitCallCab(false);
                    GndServiceFlags.setBoardingFlag(11);
                }
                if (GndServiceFlags.getBoardingFlag() == 11) {
                    try {
                        Thread.sleep(2000L);
                    }
                    catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                    GndServiceBase.playSoundClip(false, String.valueOf(path) + "boardingComplete.wav");
                    GndServiceFlags.setBoardingFlag(12);
                    GndServiceBase.setDynText("Acknowledge the boarding completed announcement");
                }
                if (GndServiceBase.getCockpitCallCab() && GndServiceFlags.getBoardingFlag() == 12) {
                    GndServiceBase.setDynText("Info : Boarding is completed. Doors are being closed...");
                    GndServiceBase.setCockpitCallCab(false);
                    try {
                        Thread.sleep(5000L);
                    }
                    catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                    final GndServiceDoors gndServiceDoors = new GndServiceDoors("close");
                    final Thread gndServiceDoorsThread = new Thread(gndServiceDoors);
                    gndServiceDoorsThread.start();
                    SocketClientPSXMain.send("Qi102=0");
                    GndServiceBase.setConversation(false);
                }
                if (GndServiceBase.getCockpitCallFlt() && GndServiceFlags.getBoardingFlag() == 14) {
                    GndServiceBase.setDynText("");
                    GndServiceBase.setCockpitCallFlt(false);
                    GndServiceFlags.setBoardingFlag(15);
                }
                if (GndServiceFlags.getBoardingFlag() == 15) {
                    try {
                        Thread.sleep(3000L);
                    }
                    catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                    GndServiceBase.playSoundClip(true, String.valueOf(path) + "loadingDelay.wav");
                    GndServiceFlags.setBoardingFlag(16);
                    GndServiceBase.setDynText("Acknowledge the cargo loading delayed announcement");
                }
                if (GndServiceBase.getCockpitCallFlt() && GndServiceFlags.getBoardingFlag() == 16) {
                    GndServiceBase.setDynText("Info : Cargo loading is delayed...");
                    GndServiceBase.setCockpitCallFlt(false);
                    GndServiceFlags.setBoardingFlag(17);
                    GndServiceBase.setConversation(false);
                }
                if (GndServiceBase.getCockpitCallFlt() && GndServiceFlags.getBoardingFlag() == 18) {
                    GndServiceBase.setDynText("");
                    GndServiceBase.setCockpitCallFlt(false);
                    GndServiceFlags.setBoardingFlag(19);
                }
                if (GndServiceFlags.getBoardingFlag() == 19) {
                    try {
                        Thread.sleep(2000L);
                    }
                    catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                    GndServiceBase.playSoundClip(true, String.valueOf(path) + "loadingComplete.wav");
                    GndServiceFlags.setBoardingFlag(20);
                    GndServiceBase.setDynText("Acknowledge the cargo loading completed announcement");
                }
                if (GndServiceBase.getCockpitCallFlt() && GndServiceFlags.getBoardingFlag() == 20) {
                    GndServiceBase.setDynText("Info : Cargo loading is completed. Doors are being closed...");
                    GndServiceBase.setCockpitCallFlt(false);
                    try {
                        Thread.sleep(5000L);
                    }
                    catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                    final GndServiceDoors gndServiceDoors = new GndServiceDoors("close");
                    final Thread gndServiceDoorsThread = new Thread(gndServiceDoors);
                    gndServiceDoorsThread.start();
                    GndServiceBase.setConversation(false);
                }
                if (GndServiceBase.getCockpitCallFlt() && GndServiceFlags.getMiscFlag() == 1) {
                    GndServiceBase.setDynText("Ground Engineer is being called...");
                    GndServiceFlags.setMiscFlag(2);
                    while (GndServiceBase.getConversation()) {
                        try {
                            Thread.sleep(1000L);
                        }
                        catch (InterruptedException e2) {
                            e2.printStackTrace();
                        }
                    }
                    GndServiceBase.setConversation(true);
                    GndServiceBase.setCockpitCallFlt(false);
                }
                if (GndServiceFlags.getMiscFlag() == 2) {
                    try {
                        Thread.sleep(3000L);
                    }
                    catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                    GndServiceBase.playSoundClip(true, String.valueOf(path) + "goAhead.wav");
                    GndServiceBase.setDynText("");
                    GndServiceFlags.setMiscFlag(3);
                    GndServiceBase.setDynText("Request hydraulics pressurization clearance from the Ground Engineer");
                }
                if (GndServiceBase.getCockpitCallFlt() && GndServiceFlags.getMiscFlag() == 3) {
                    GndServiceBase.setDynText("");
                    GndServiceBase.setCockpitCallFlt(false);
                    GndServiceFlags.setMiscFlag(4);
                }
                if (GndServiceFlags.getMiscFlag() == 4) {
                    try {
                        Thread.sleep(4000L);
                    }
                    catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                    GndServiceBase.playSoundClip(true, String.valueOf(path) + "clearedHydPress.wav");
                    GndServiceFlags.setMiscFlag(5);
                    GndServiceBase.setDynText("Acknowledge the hydraulics pressurization clearance");
                }
                if (GndServiceBase.getCockpitCallFlt() && GndServiceFlags.getMiscFlag() == 5) {
                    GndServiceBase.setCockpitCallFlt(false);
                    GndServiceBase.setDynText("");
                    GndServiceFlags.setMiscFlag(6);
                    GndServiceBase.setConversation(false);
                }
                if (GndServiceBase.getCockpitCallFlt() && GndServiceFlags.getDeIcingFlag() == 2) {
                    GndServiceBase.setDynText("");
                    GndServiceBase.setCockpitCallFlt(false);
                    GndServiceFlags.setDeIcingFlag(3);
                }
                if (GndServiceFlags.getDeIcingFlag() == 3) {
                    try {
                        Thread.sleep(2000L);
                    }
                    catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                    GndServiceBase.playSoundClip(true, String.valueOf(path) + "askDeIce.wav");
                    GndServiceFlags.setDeIcingFlag(4);
                    GndServiceBase.setDynText("Clear the ground engineer to start the de-icing");
                }
                if (GndServiceBase.getCockpitCallFlt() && GndServiceFlags.getDeIcingFlag() == 4) {
                    GndServiceBase.setDynText("");
                    GndServiceBase.setCockpitCallFlt(false);
                    GndServiceFlags.setDeIcingFlag(5);
                }
                if (GndServiceFlags.getDeIcingFlag() == 5) {
                    try {
                        Thread.sleep(2000L);
                    }
                    catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                    GndServiceBase.playSoundClip(true, String.valueOf(path) + "deIceStart.wav");
                    GndServiceFlags.setDeIcingFlag(6);
                    GndServiceBase.setDynText("De-icing in progress. Wait for the ground engineer completion announcement...");
                    long deIcingTime = 600000L;
                    final int icingIntensity = GndServiceDeIcing.getIcingIntensity();
                    if (icingIntensity == 1) {
                        deIcingTime = 1200000L;
                    }
                    try {
                        Thread.sleep(deIcingTime);
                    }
                    catch (InterruptedException e4) {
                        e4.printStackTrace();
                    }
                    GndServiceDeIcing.deIce();
                    GndServiceFlags.setDeIcingFlag(7);
                    GndServiceBase.setConversation(false);
                }
                if (GndServiceBase.getCockpitCallFlt() && GndServiceFlags.getDeIcingFlag() == 8) {
                    GndServiceBase.setDynText("");
                    GndServiceBase.setCockpitCallFlt(false);
                    GndServiceFlags.setDeIcingFlag(9);
                }
                if (GndServiceFlags.getDeIcingFlag() == 9) {
                    try {
                        Thread.sleep(2000L);
                    }
                    catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                    GndServiceBase.playSoundClip(true, String.valueOf(path) + "deIceComplete.wav");
                    GndServiceFlags.setDeIcingFlag(10);
                    GndServiceBase.setDynText("Acknowledge the de-icing completed announcement");
                }
                if (GndServiceBase.getCockpitCallFlt() && GndServiceFlags.getDeIcingFlag() == 10) {
                    GndServiceBase.setDynText("");
                    GndServiceBase.setCockpitCallFlt(false);
                    GndServiceBase.setConversation(false);
                    GndServiceFlags.setDeIcingFlag(11);
                }
                if (GndServiceFlags.getDeIcingFlag() == 11 && GndServiceFlags.getMiscFlag() == 6) {
                    if (StatusMonitor.getGndServiceExtPush()) {
                        int tempElec = GndServiceBase.getPsxElecSysBits();
                        tempElec &= 0x7FFFFFF6;
                        SocketClientPSXMain.send("Qi132=" + String.valueOf(tempElec));
                        int tempBleed2 = GndServiceBase.getPsxBleedAirBits();
                        tempBleed2 &= 0xFFFD;
                        SocketClientPSXMain.send("Qi174=" + String.valueOf(tempBleed2));
                        GndServiceFlags.setMiscFlag(10);
                        GndServiceBase.setDynText("You are free to start the pushback using an external pushback system");
                    }
                    else {
                        GndServiceBase.setDynText("When ready for Push & Start : Set beacon ON");
                        GndServiceBase.setCockpitCallFlt(false);
                        GndServiceBase.setConversation(false);
                        GndServiceFlags.setMiscFlag(7);
                    }
                }
                if (GndServiceBase.getBeacon() && GndServiceFlags.getMiscFlag() == 7 && GndServiceFlags.getPushBackFlag() == 0) {
                    GndServiceFlags.setPushBackFlag(1);
                    GndServiceBase.setDynText("When ready for Pushback : call the Ground Engineer : \"Cockpit to Ground\"");
                }
                if (GndServiceBase.getCockpitCallFlt() && GndServiceFlags.getPushBackFlag() == 1) {
                    GndServiceBase.setDynText("Ground Engineer is being called...");
                    while (GndServiceBase.getConversation()) {
                        try {
                            Thread.sleep(1000L);
                        }
                        catch (InterruptedException e2) {
                            e2.printStackTrace();
                        }
                    }
                    GndServiceBase.setConversation(true);
                    GndServiceBase.setCockpitCallFlt(false);
                    GndServiceFlags.setPushBackFlag(2);
                }
                if (GndServiceFlags.getPushBackFlag() == 2) {
                    try {
                        Thread.sleep(2000L);
                    }
                    catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                    GndServiceBase.playSoundClip(true, String.valueOf(path) + "goAhead.wav");
                    GndServiceBase.setDynText("");
                    GndServiceFlags.setPushBackFlag(3);
                    GndServiceBase.setDynText("Request pushback from the Ground Engineer");
                }
                if (GndServiceBase.getCockpitCallFlt() && GndServiceFlags.getPushBackFlag() == 3) {
                    GndServiceBase.setDynText("");
                    GndServiceBase.setCockpitCallFlt(false);
                    GndServiceFlags.setPushBackFlag(4);
                }
                if (GndServiceFlags.getPushBackFlag() == 4) {
                    try {
                        Thread.sleep(2000L);
                    }
                    catch (InterruptedException e3) {
                        e3.printStackTrace();
                    }
                    int tempElec = GndServiceBase.getPsxElecSysBits();
                    tempElec &= 0x7FFFFFF6;
                    SocketClientPSXMain.send("Qi132=" + String.valueOf(tempElec));
                    int tempBleed2 = GndServiceBase.getPsxBleedAirBits();
                    tempBleed2 &= 0xFFFD;
                    SocketClientPSXMain.send("Qi174=" + String.valueOf(tempBleed2));
                    try {
                        Thread.sleep(4000L);
                    }
                    catch (InterruptedException e5) {
                        e5.printStackTrace();
                    }
                    GndServiceBase.playSoundClip(true, String.valueOf(path) + "pushBrkRelease.wav");
                    GndServiceFlags.setPushBackFlag(5);
                    GndServiceBase.setDynText("Confirm brakes are released");
                }
                if (GndServiceBase.getCockpitCallFlt() && GndServiceFlags.getPushBackFlag() == 5) {
                    GndServiceBase.setDynText("");
                    GndServiceBase.setCockpitCallFlt(false);
                    GndServiceFlags.setPushBackFlag(6);
                }
                if (GndServiceFlags.getEngineStartFlag() == 1 && !GndServiceBase.getConversation()) {
                    GndServiceBase.setDynText("Request engine start clearance \"Ready to start the engines\"");
                }
                if (GndServiceFlags.getPushBackFlag() == 6) {
                    try {
                        Thread.sleep(2500L);
                    }
                    catch (InterruptedException e3) {
                        e3.printStackTrace();
                    }
                    try {
                        GndServiceBase.playSoundClip(true, String.valueOf(path) + "pushingBack.wav");
                        GndServiceBase.setDynText("Info : Pushback is in progress...");
                    }
                    catch (Exception ex2) {}
                    GndServiceFlags.setPushBackFlag(7);
                    GndServiceFlags.setEngineStartFlag(1);
                    final GndServicePushBack gndServicePushBack = new GndServicePushBack();
                    final Thread gndServicePushBackThread = new Thread(gndServicePushBack);
                    gndServicePushBackThread.start();
                    GndServiceBase.setConversation(false);
                    GndServiceBase.setDynText("Request engine start clearance \"Ready to start the engines\"");
                }
                if (GndServiceBase.getCockpitCallFlt() && GndServiceFlags.getEngineStartFlag() == 1 && GndServiceFlags.getPushBackFlag() != 8 && GndServiceFlags.getPushBackFlag() != 10) {
                    GndServiceBase.setDynText("");
                    while (GndServiceBase.getConversation()) {
                        try {
                            Thread.sleep(1000L);
                        }
                        catch (InterruptedException e2) {
                            e2.printStackTrace();
                        }
                    }
                    GndServiceBase.setConversation(true);
                    GndServiceBase.setCockpitCallFlt(false);
                    GndServiceFlags.setEngineStartFlag(2);
                }
                if (GndServiceFlags.getEngineStartFlag() == 2) {
                    try {
                        Thread.sleep(2500L);
                    }
                    catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                    try {
                        GndServiceBase.playSoundClip(true, String.valueOf(path) + "clearedStartEngines.wav");
                    }
                    catch (Exception ex3) {}
                    GndServiceFlags.setEngineStartFlag(3);
                    GndServiceBase.setDynText("Acknowledge the engine start clearance");
                }
                if (GndServiceBase.getCockpitCallFlt() && GndServiceFlags.getEngineStartFlag() == 3) {
                    GndServiceBase.setDynText("");
                    GndServiceBase.setCockpitCallFlt(false);
                    GndServiceBase.setConversation(false);
                    GndServiceFlags.setEngineStartFlag(13);
                }
                if (GndServiceFlags.getPushBackFlag() == 8) {
                    GndServiceBase.sendStaticPosition();
                }
                if (GndServiceBase.getCockpitCallFlt() && GndServiceFlags.getPushBackFlag() == 8) {
                    GndServiceBase.setDynText("");
                    GndServiceBase.setCockpitCallFlt(false);
                    GndServiceFlags.setPushBackFlag(9);
                }
                if (GndServiceFlags.getPushBackFlag() == 9) {
                    try {
                        Thread.sleep(2500L);
                    }
                    catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                    GndServiceBase.playSoundClip(true, String.valueOf(path) + "pushTowPinRemoved.wav");
                    GndServiceFlags.setPushBackFlag(10);
                    GndServiceBase.setDynText("Acknowledge the tow bar and steering pin removed announcement");
                }
                if (GndServiceBase.getCockpitCallFlt() && GndServiceFlags.getPushBackFlag() == 10) {
                    GndServiceBase.setDynText("");
                    GndServiceBase.setCockpitCallFlt(false);
                    GndServiceBase.setConversation(false);
                    GndServiceFlags.setPushBackFlag(11);
                }
                if (GndServiceFlags.getPushBackFlag() == 11 && GndServiceFlags.getEngineStartFlag() == 13) {
                    GndServiceFlags.setMiscFlag(8);
                    GndServiceBase.setDynText("Permit the Ground Engineer to disconnect, leave and request salute on the L/R hand side...");
                }
                if (GndServiceBase.getCockpitCallFlt() && GndServiceFlags.getMiscFlag() == 8) {
                    GndServiceBase.setDynText("");
                    GndServiceBase.setCockpitCallFlt(false);
                    GndServiceFlags.setMiscFlag(9);
                }
                if (GndServiceFlags.getMiscFlag() == 9) {
                    try {
                        Thread.sleep(2500L);
                    }
                    catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                    try {
                        GndServiceBase.playSoundClip(true, String.valueOf(path) + "bye.wav");
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    GndServiceFlags.setMiscFlag(10);
                }
                if (GndServiceBase.getAcftVersion() == 0 && GndServiceFlags.getMiscFlag() == 10) {
                    if (StatusMonitor.getGndServiceExtPush() && !GndServiceBase.getReleaseScenGen()) {
                        GndServiceBase.setDynText("Info : Waiting for the Cabin Ready announcement...");
                        try {
                            Thread.sleep(300000L);
                        }
                        catch (InterruptedException e2) {
                            e2.printStackTrace();
                        }
                        SocketClientPSXMain.send("Qi102=67");
                        GndServiceBase.setDynText("Info : Waiting for landing -> Set PRK BRK, Engines OFF, Beacon OFF");
                        GndServiceFlags.setMiscFlag(11);
                        break Label_3606;
                    }
                    if (!StatusMonitor.getGndServiceExtPush()) {
                        GndServiceBase.setDynText("Info : Waiting for the Cabin Ready announcement...");
                        try {
                            Thread.sleep(300000L);
                        }
                        catch (InterruptedException e2) {
                            e2.printStackTrace();
                        }
                        SocketClientPSXMain.send("Qi102=67");
                        GndServiceBase.setDynText("Info : Waiting for landing -> Set PRK BRK, Engines OFF, Beacon OFF");
                        GndServiceFlags.setMiscFlag(11);
                        break Label_3606;
                    }
                }
                else if (GndServiceFlags.getMiscFlag() == 10) {
                    if (StatusMonitor.getGndServiceExtPush() && !GndServiceBase.getReleaseScenGen()) {
                        GndServiceBase.setDynText("Info : Waiting for landing -> Set PRK BRK, Engines OFF, Beacon OFF");
                        break Label_3606;
                    }
                    if (!StatusMonitor.getGndServiceExtPush()) {
                        GndServiceBase.setDynText("Info : Waiting for landing -> Set PRK BRK, Engines OFF, Beacon OFF");
                        break Label_3606;
                    }
                }
                try {
                    Thread.sleep(1000L);
                }
                catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
                continue;
                GndServiceBase.setConversation(false);
                return;
            }
            GndServiceBase.setDynText("Info : Waiting for landing -> Set PRK BRK, Engines OFF, Beacon OFF");
            continue Label_3606;
        }
    }
}
