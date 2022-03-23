// 
// Decompiled by Procyon v0.5.36
// 

package gndService;

import network.SocketClientPSXMain;

public class GndServicePostFlt implements Runnable
{
    @Override
    public void run() {
        final String path = new String(GndServiceBase.getSoundsPath(false));
        while (GndServiceBase.getBeacon()) {
            try {
                Thread.sleep(1000L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            GndServiceBase.setDynText("Info : Ground Engineer is connecting headset. Please wait...");
            Thread.sleep(60000L);
        }
        catch (InterruptedException e2) {
            e2.printStackTrace();
        }
        SocketClientPSXMain.send("Qi101=71");
        GndServiceFlags.setMiscFlag(11);
        GndServiceBase.setDynText("FLT : Answer the Ground Engineer call \"Go ahead Ground\"");
    Label_0069_Outer:
        while (true) {
            while (true) {
                try {
                    while (true) {
                        if (GndServiceBase.getCockpitCallFlt() && GndServiceFlags.getMiscFlag() == 11) {
                            GndServiceBase.setDynText("");
                            GndServiceBase.setCockpitCallFlt(false);
                            GndServiceFlags.setMiscFlag(12);
                        }
                        if (GndServiceFlags.getMiscFlag() == 12) {
                            Thread.sleep(3000L);
                            GndServiceBase.playSoundClip(true, String.valueOf(path) + "helloWelcome.wav");
                            GndServiceFlags.setMiscFlag(13);
                            GndServiceBase.setDynText("Answer the Ground Engineer question about the aircraft status");
                        }
                        if (GndServiceBase.getCockpitCallFlt() && GndServiceFlags.getMiscFlag() == 13) {
                            GndServiceBase.setDynText("");
                            GndServiceBase.setCockpitCallFlt(false);
                            GndServiceFlags.setMiscFlag(14);
                        }
                        if (GndServiceFlags.getMiscFlag() == 14) {
                            break;
                        }
                        Thread.sleep(1000L);
                    }
                    Thread.sleep(2000L);
                    GndServiceBase.playSoundClip(true, String.valueOf(path) + "rogerTechIssues.wav");
                    GndServiceFlags.setMiscFlag(15);
                    GndServiceBase.setDynText("Info : Ground staff is preparing doors opening...");
                    break;
                }
                catch (Exception e3) {
                    e3.printStackTrace();
                    continue Label_0069_Outer;
                }
                continue;
            }
        }
        try {
            GndServiceBase.setDynText("Info : Ground staff is preparing doors opening...");
            Thread.sleep(30000L);
        }
        catch (InterruptedException e4) {
            e4.printStackTrace();
        }
        final GndServiceDoors gndServiceDoors = new GndServiceDoors("open");
        final Thread gndServiceDoorsThread = new Thread(gndServiceDoors);
        gndServiceDoorsThread.start();
        try {
            GndServiceBase.setDynText("Info : Ground staff is connecting the ground equipment...");
            Thread.sleep(60000L);
        }
        catch (InterruptedException e5) {
            e5.printStackTrace();
        }
        int tempElec = GndServiceBase.getPsxElecSysBits();
        tempElec |= 0x9;
        SocketClientPSXMain.send("Qi132=" + String.valueOf(tempElec));
        if (GndServiceBase.getPsxOat() > 25 || GndServiceBase.getPsxOat() < 15) {
            int tempBleed = GndServiceBase.getPsxBleedAirBits();
            tempBleed |= 0x2;
            SocketClientPSXMain.send("Qi174=" + String.valueOf(tempBleed));
        }
        SocketClientPSXMain.send("Qi101=71");
        GndServiceFlags.setPowerFlag(7);
        GndServiceBase.setDynText("FLT : Answer the Ground Engineer call \"Go ahead Ground\"");
    Label_0402_Outer:
        while (true) {
            while (true) {
                try {
                    while (true) {
                        if (GndServiceBase.getCockpitCallFlt() && GndServiceFlags.getPowerFlag() == 7) {
                            GndServiceBase.setDynText("");
                            GndServiceBase.setCockpitCallFlt(false);
                            GndServiceFlags.setPowerFlag(8);
                        }
                        if (GndServiceFlags.getPowerFlag() == 8) {
                            Thread.sleep(3500L);
                            GndServiceBase.playSoundClip(true, String.valueOf(path) + "gndEqConnected.wav");
                            GndServiceFlags.setPowerFlag(9);
                            GndServiceBase.setDynText("Acknowledge the ground equipment and chocks in place announcement");
                        }
                        if (GndServiceBase.getCockpitCallFlt() && GndServiceFlags.getPowerFlag() == 9) {
                            break;
                        }
                        Thread.sleep(1000L);
                    }
                    GndServiceBase.setDynText("");
                    GndServiceBase.setCockpitCallFlt(false);
                    GndServiceFlags.setPowerFlag(10);
                    GndServiceBase.setDynText("End of the Ground Services simulation. Restart WidePSX and module for a new process to begin");
                    break;
                }
                catch (Exception e6) {
                    e6.printStackTrace();
                    continue Label_0402_Outer;
                }
                continue;
            }
        }
    }
}
