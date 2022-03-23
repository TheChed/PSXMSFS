// 
// Decompiled by Procyon v0.5.36
// 

package gndService;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;

public class GndServiceRadios
{
    private static boolean fromRtAcpL;
    private static boolean fromIntAcpL;
    private static boolean fromRtAcpC;
    private static boolean fromIntAcpC;
    private static boolean fromRtAcpR;
    private static boolean fromIntAcpR;
    private static int pttCptCounter;
    private static int pttFoCounter;
    private static final Lock lockFromRtAcpL;
    private static final Lock lockFromIntAcpL;
    private static final Lock lockFromRtAcpC;
    private static final Lock lockFromIntAcpC;
    private static final Lock lockFromRtAcpR;
    private static final Lock lockFromIntAcpR;
    private static final Lock lockPttCptCounter;
    private static final Lock lockPttFoCounter;
    
    static {
        GndServiceRadios.fromRtAcpL = false;
        GndServiceRadios.fromIntAcpL = false;
        GndServiceRadios.fromRtAcpC = false;
        GndServiceRadios.fromIntAcpC = false;
        GndServiceRadios.fromRtAcpR = false;
        GndServiceRadios.fromIntAcpR = false;
        GndServiceRadios.pttCptCounter = 0;
        GndServiceRadios.pttFoCounter = 0;
        lockFromRtAcpL = new ReentrantLock();
        lockFromIntAcpL = new ReentrantLock();
        lockFromRtAcpC = new ReentrantLock();
        lockFromIntAcpC = new ReentrantLock();
        lockFromRtAcpR = new ReentrantLock();
        lockFromIntAcpR = new ReentrantLock();
        lockPttCptCounter = new ReentrantLock();
        lockPttFoCounter = new ReentrantLock();
    }
    
    protected static void pttDispatch(final boolean argCptPtt, final boolean argFoPtt, final boolean argAcpLsw, final boolean argAcpCsw, final boolean argAcpRsw, final boolean argRt) {
        boolean sendFltCall = false;
        boolean sendCabCall = false;
        if (argCptPtt) {
            incPttCptCounter();
            if (getPttCptCounter() % 2 != 0 && getPttCptCounter() != 1) {
                if (GndServiceBase.getObsAudioSys() == 0 || GndServiceBase.getObsAudioSys() == 1) {
                    if (GndServiceBase.getMicSelectL() == 3) {
                        sendFltCall = true;
                    }
                    else if (GndServiceBase.getMicSelectL() == 4) {
                        sendCabCall = true;
                    }
                }
                else if (GndServiceBase.getMicSelectC() == 3) {
                    sendFltCall = true;
                }
                else if (GndServiceBase.getMicSelectC() == 4) {
                    sendCabCall = true;
                }
            }
        }
        if (argFoPtt) {
            incPttFoCounter();
            if (getPttFoCounter() % 2 != 0 && getPttFoCounter() != 1) {
                if (GndServiceBase.getObsAudioSys() == 0 || GndServiceBase.getObsAudioSys() == -1) {
                    if (GndServiceBase.getMicSelectR() == 3) {
                        sendFltCall = true;
                    }
                    else if (GndServiceBase.getMicSelectR() == 4) {
                        sendCabCall = true;
                    }
                }
                else if (GndServiceBase.getMicSelectC() == 3) {
                    sendFltCall = true;
                }
                else if (GndServiceBase.getMicSelectC() == 4) {
                    sendCabCall = true;
                }
            }
        }
        if (argAcpLsw) {
            if (argRt) {
                if (GndServiceBase.getMicSelectL() == 3) {
                    sendFltCall = true;
                }
                else if (GndServiceBase.getMicSelectL() == 4) {
                    sendCabCall = true;
                }
            }
            else {
                sendFltCall = true;
            }
        }
        if (argAcpCsw) {
            if (GndServiceBase.getObsAudioSys() == 0) {
                if (argRt) {
                    if (GndServiceBase.getMicSelectC() == 3) {
                        sendFltCall = true;
                    }
                    else if (GndServiceBase.getMicSelectC() == 4) {
                        sendCabCall = true;
                    }
                }
                else {
                    sendFltCall = true;
                }
            }
            else if (GndServiceBase.getObsAudioSys() == -1) {
                if (argRt) {
                    if (GndServiceBase.getMicSelectC() == 3) {
                        sendFltCall = true;
                    }
                    else if (GndServiceBase.getMicSelectC() == 4) {
                        sendCabCall = true;
                    }
                }
                else {
                    sendFltCall = true;
                }
            }
            else if (argRt) {
                if (GndServiceBase.getMicSelectC() == 3) {
                    sendFltCall = true;
                }
                else if (GndServiceBase.getMicSelectC() == 4) {
                    sendCabCall = true;
                }
            }
            else {
                sendFltCall = true;
            }
        }
        if (argAcpRsw) {
            if (argRt) {
                if (GndServiceBase.getMicSelectR() == 3) {
                    sendFltCall = true;
                }
                else if (GndServiceBase.getMicSelectR() == 4) {
                    sendCabCall = true;
                }
            }
            else {
                sendFltCall = true;
            }
        }
        if (sendFltCall) {
            final int powerFlags = GndServiceFlags.getPowerFlag();
            final int refuelFlags = GndServiceFlags.getRefuelFlag();
            final int miscFlags = GndServiceFlags.getMiscFlag();
            final int pushFlags = GndServiceFlags.getPushBackFlag();
            final int startFlags = GndServiceFlags.getEngineStartFlag();
            final int boardingFlags = GndServiceFlags.getBoardingFlag();
            final int deIceFlags = GndServiceFlags.getDeIcingFlag();
            if (powerFlags == 2 || powerFlags == 4 || powerFlags == 7 || powerFlags == 9 || refuelFlags == 1 || refuelFlags == 3 || refuelFlags == 5 || refuelFlags == 7 || refuelFlags == 9 || refuelFlags == 11 || miscFlags == 1 || miscFlags == 3 || miscFlags == 5 || miscFlags == 8 || miscFlags == 11 || miscFlags == 13 || pushFlags == 1 || pushFlags == 3 || pushFlags == 5 || pushFlags == 8 || pushFlags == 10 || deIceFlags == 2 || deIceFlags == 4 || deIceFlags == 8 || deIceFlags == 10 || startFlags == 1 || startFlags == 3 || startFlags == 4 || startFlags == 6 || startFlags == 7 || startFlags == 9 || startFlags == 10 || startFlags == 12 || boardingFlags == 14 || boardingFlags == 16 || boardingFlags == 18 || boardingFlags == 20) {
                GndServiceBase.setCockpitCallFlt(true);
            }
        }
        if (sendCabCall) {
            final int boardingFlags2 = GndServiceFlags.getBoardingFlag();
            if (boardingFlags2 == 1 || boardingFlags2 == 3 || boardingFlags2 == 6 || boardingFlags2 == 8 || boardingFlags2 == 10 || boardingFlags2 == 12) {
                GndServiceBase.setCockpitCallCab(true);
            }
        }
    }
    
    protected static void setFromRtAcpL(final boolean argValue) {
        GndServiceRadios.lockFromRtAcpL.lock();
        GndServiceRadios.fromRtAcpL = argValue;
        GndServiceRadios.lockFromRtAcpL.unlock();
    }
    
    protected static boolean getFromRtAcpL() {
        GndServiceRadios.lockFromRtAcpL.lock();
        final boolean temp = GndServiceRadios.fromRtAcpL;
        GndServiceRadios.lockFromRtAcpL.unlock();
        return temp;
    }
    
    protected static void setFromRtAcpC(final boolean argValue) {
        GndServiceRadios.lockFromRtAcpC.lock();
        GndServiceRadios.fromRtAcpC = argValue;
        GndServiceRadios.lockFromRtAcpC.unlock();
    }
    
    protected static boolean getFromRtAcpC() {
        GndServiceRadios.lockFromRtAcpC.lock();
        final boolean temp = GndServiceRadios.fromRtAcpC;
        GndServiceRadios.lockFromRtAcpC.unlock();
        return temp;
    }
    
    protected static void setFromRtAcpR(final boolean argValue) {
        GndServiceRadios.lockFromRtAcpR.lock();
        GndServiceRadios.fromRtAcpR = argValue;
        GndServiceRadios.lockFromRtAcpR.unlock();
    }
    
    protected static boolean getFromRtAcpR() {
        GndServiceRadios.lockFromRtAcpR.lock();
        final boolean temp = GndServiceRadios.fromRtAcpR;
        GndServiceRadios.lockFromRtAcpR.unlock();
        return temp;
    }
    
    protected static void setFromIntAcpL(final boolean argValue) {
        GndServiceRadios.lockFromIntAcpL.lock();
        GndServiceRadios.fromIntAcpL = argValue;
        GndServiceRadios.lockFromIntAcpL.unlock();
    }
    
    protected static boolean getFromIntAcpL() {
        GndServiceRadios.lockFromIntAcpL.lock();
        final boolean temp = GndServiceRadios.fromIntAcpL;
        GndServiceRadios.lockFromIntAcpL.unlock();
        return temp;
    }
    
    protected static void setFromIntAcpC(final boolean argValue) {
        GndServiceRadios.lockFromIntAcpC.lock();
        GndServiceRadios.fromIntAcpC = argValue;
        GndServiceRadios.lockFromIntAcpC.unlock();
    }
    
    protected static boolean getFromIntAcpC() {
        GndServiceRadios.lockFromIntAcpC.lock();
        final boolean temp = GndServiceRadios.fromIntAcpC;
        GndServiceRadios.lockFromIntAcpC.unlock();
        return temp;
    }
    
    protected static void setFromIntAcpR(final boolean argValue) {
        GndServiceRadios.lockFromIntAcpR.lock();
        GndServiceRadios.fromIntAcpR = argValue;
        GndServiceRadios.lockFromIntAcpR.unlock();
    }
    
    protected static boolean getFromIntAcpR() {
        GndServiceRadios.lockFromIntAcpR.lock();
        final boolean temp = GndServiceRadios.fromIntAcpR;
        GndServiceRadios.lockFromIntAcpR.unlock();
        return temp;
    }
    
    private static void incPttCptCounter() {
        GndServiceRadios.lockPttCptCounter.lock();
        ++GndServiceRadios.pttCptCounter;
        GndServiceRadios.lockPttCptCounter.unlock();
    }
    
    private static int getPttCptCounter() {
        GndServiceRadios.lockPttCptCounter.lock();
        final int temp = GndServiceRadios.pttCptCounter;
        GndServiceRadios.lockPttCptCounter.unlock();
        return temp;
    }
    
    private static void incPttFoCounter() {
        GndServiceRadios.lockPttFoCounter.lock();
        ++GndServiceRadios.pttFoCounter;
        GndServiceRadios.lockPttFoCounter.unlock();
    }
    
    private static int getPttFoCounter() {
        GndServiceRadios.lockPttFoCounter.lock();
        final int temp = GndServiceRadios.pttFoCounter;
        GndServiceRadios.lockPttFoCounter.unlock();
        return temp;
    }
}
