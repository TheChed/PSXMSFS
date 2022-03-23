// 
// Decompiled by Procyon v0.5.36
// 

package aloftWx;

import network.SocketClientPSXMain;
import util.StatusMonitor;
import util.Randomizer;

public class TurbAirGen implements Runnable
{
    private TurbSoundGen turbSoundGen;
    private Thread turbSoundGenThread;
    
    @Override
    public void run() {
        int soundLengthMin = 0;
        int soundLengthMax = 0;
        int intervalMin = 0;
        int intervalMax = 0;
        boolean airspeed = false;
        int airspeedVarMin = 0;
        int airspeedVarMax = 0;
        boolean yaw = false;
        int yawVarMin = 0;
        int yawVarMax = 0;
        boolean bank = false;
        int bankVarMin = 0;
        int bankVarMax = 0;
        boolean sinkBalloon = false;
        int sinkBalloonVarMin = 0;
        int sinkBalloonVarMax = 0;
        while (true) {
            if (TurbBase.getTurbIntensity().equals("N")) {
                try {
                    Thread.sleep(15000L);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else {
                if (TurbBase.getTurbIntensity().equals("L")) {
                    TurbBase.setTurbSound("L");
                    soundLengthMin = 3;
                    soundLengthMax = 5;
                    intervalMin = 5;
                    intervalMax = 10;
                    airspeed = true;
                    airspeedVarMin = 15;
                    airspeedVarMax = 25;
                    yaw = false;
                    yawVarMin = 0;
                    yawVarMax = 0;
                    bank = false;
                    bankVarMin = 0;
                    bankVarMax = 0;
                    sinkBalloon = true;
                    sinkBalloonVarMin = 10;
                    sinkBalloonVarMax = 20;
                }
                if (TurbBase.getTurbIntensity().equals("M")) {
                    TurbBase.setTurbSound("M");
                    soundLengthMin = 3;
                    soundLengthMax = 5;
                    intervalMin = 5;
                    intervalMax = 8;
                    airspeed = true;
                    airspeedVarMin = 25;
                    airspeedVarMax = 45;
                    yaw = false;
                    yawVarMin = 0;
                    yawVarMax = 0;
                    bank = true;
                    bankVarMin = 10;
                    bankVarMax = 20;
                    sinkBalloon = true;
                    sinkBalloonVarMin = 20;
                    sinkBalloonVarMax = 35;
                }
                if (TurbBase.getTurbIntensity().equals("S")) {
                    TurbBase.setTurbSound("S");
                    soundLengthMin = 3;
                    soundLengthMax = 5;
                    intervalMin = 5;
                    intervalMax = 8;
                    airspeed = true;
                    airspeedVarMin = 35;
                    airspeedVarMax = 70;
                    yaw = true;
                    yawVarMin = 8;
                    yawVarMax = 15;
                    bank = true;
                    bankVarMin = 15;
                    bankVarMax = 35;
                    sinkBalloon = true;
                    sinkBalloonVarMin = 35;
                    sinkBalloonVarMax = 55;
                }
                boolean turbSoundLoaded = false;
                TurbBase.setTurbulenceSoundDuration(Randomizer.getBoundRand(soundLengthMin, soundLengthMax));
                String airspeedVarTendancy = new String("");
                String yawVarTendancy = new String("");
                String bankVarTendancy = new String("");
                String sinkBalloonVarTendancy = new String("");
                final int interval = Randomizer.getBoundRand(intervalMin, intervalMax);
                if (airspeed) {
                    final int airspeedVar = Randomizer.getRand(1);
                    if (airspeedVar == 1) {
                        if (!turbSoundLoaded) {
                            turbSoundLoaded = true;
                            this.turbSoundGen = new TurbSoundGen();
                            this.turbSoundGenThread = new Thread(this.turbSoundGen);
                            try {
                                this.turbSoundGenThread.start();
                            }
                            catch (IllegalThreadStateException ex) {}
                        }
                        final int airspeedVarDir = Randomizer.getRand(1);
                        if (airspeedVarDir == 0) {
                            airspeedVarTendancy = "-";
                        }
                        final int airspeedVarInt = 300 + Randomizer.getBoundRand(airspeedVarMin, airspeedVarMax);
                        if (StatusMonitor.getAloftWxRunning()) {
                            SocketClientPSXMain.send("Qi141=" + airspeedVarTendancy + String.valueOf(airspeedVarInt));
                        }
                    }
                    try {
                        Thread.sleep(200L);
                    }
                    catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                }
                if (yaw) {
                    final int yawVar = Randomizer.getRand(1);
                    if (yawVar == 1) {
                        if (!turbSoundLoaded) {
                            turbSoundLoaded = true;
                            this.turbSoundGen = new TurbSoundGen();
                            this.turbSoundGenThread = new Thread(this.turbSoundGen);
                            try {
                                this.turbSoundGenThread.start();
                            }
                            catch (IllegalThreadStateException ex2) {}
                        }
                        final int yawVarDir = Randomizer.getRand(1);
                        if (yawVarDir == 0) {
                            yawVarTendancy = "-";
                        }
                        final int yawVarInt = 200 + Randomizer.getBoundRand(yawVarMin, yawVarMax);
                        if (StatusMonitor.getAloftWxRunning()) {
                            SocketClientPSXMain.send("Qi141=" + yawVarTendancy + String.valueOf(yawVarInt));
                        }
                    }
                    try {
                        Thread.sleep(200L);
                    }
                    catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                }
                if (bank) {
                    final int bankVar = Randomizer.getRand(1);
                    if (bankVar == 1) {
                        if (!turbSoundLoaded) {
                            turbSoundLoaded = true;
                            this.turbSoundGen = new TurbSoundGen();
                            this.turbSoundGenThread = new Thread(this.turbSoundGen);
                            try {
                                this.turbSoundGenThread.start();
                            }
                            catch (IllegalThreadStateException ex3) {}
                        }
                        final int bankVarDir = Randomizer.getRand(1);
                        if (bankVarDir == 0) {
                            bankVarTendancy = "-";
                        }
                        final int bankVarInt = 100 + Randomizer.getBoundRand(bankVarMin, bankVarMax);
                        if (StatusMonitor.getAloftWxRunning()) {
                            SocketClientPSXMain.send("Qi141=" + bankVarTendancy + String.valueOf(bankVarInt));
                        }
                    }
                    try {
                        Thread.sleep(200L);
                    }
                    catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                }
                if (sinkBalloon) {
                    final int sinkBalloonVar = Randomizer.getRand(1);
                    if (sinkBalloonVar == 1) {
                        if (!turbSoundLoaded) {
                            turbSoundLoaded = true;
                            this.turbSoundGen = new TurbSoundGen();
                            this.turbSoundGenThread = new Thread(this.turbSoundGen);
                            try {
                                this.turbSoundGenThread.start();
                            }
                            catch (IllegalThreadStateException ex4) {}
                        }
                        final int sinkBalloonVarDir = Randomizer.getRand(1);
                        if (sinkBalloonVarDir == 0) {
                            sinkBalloonVarTendancy = "-";
                        }
                        final int sinkBalloonVarInt = Randomizer.getBoundRand(sinkBalloonVarMin, sinkBalloonVarMax);
                        if (StatusMonitor.getAloftWxRunning()) {
                            SocketClientPSXMain.send("Qi141=" + sinkBalloonVarTendancy + String.valueOf(sinkBalloonVarInt));
                        }
                    }
                }
                try {
                    Thread.sleep(interval * 1000);
                }
                catch (InterruptedException e3) {
                    e3.printStackTrace();
                }
            }
        }
    }
}
