// 
// Decompiled by Procyon v0.5.36
// 

package gndService;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;

public class GndServiceFlags
{
    private static int powerFlag;
    private static int refuelFlag;
    private static int boardingFlag;
    private static int miscFlag;
    private static int pushBackFlag;
    private static int engineStartFlag;
    private static int deIcingFlag;
    private static final Lock lockPowerFlag;
    private static final Lock lockRefuelFlag;
    private static final Lock lockBoardingFlag;
    private static final Lock lockMiscFlag;
    private static final Lock lockPushBackFlag;
    private static final Lock lockEngineStartFlag;
    private static final Lock lockDeIcingFlag;
    
    static {
        GndServiceFlags.powerFlag = 0;
        GndServiceFlags.refuelFlag = 0;
        GndServiceFlags.boardingFlag = 0;
        GndServiceFlags.miscFlag = 0;
        GndServiceFlags.pushBackFlag = 0;
        GndServiceFlags.engineStartFlag = 0;
        GndServiceFlags.deIcingFlag = 0;
        lockPowerFlag = new ReentrantLock();
        lockRefuelFlag = new ReentrantLock();
        lockBoardingFlag = new ReentrantLock();
        lockMiscFlag = new ReentrantLock();
        lockPushBackFlag = new ReentrantLock();
        lockEngineStartFlag = new ReentrantLock();
        lockDeIcingFlag = new ReentrantLock();
    }
    
    protected static void setPowerFlag(final int argFlag) {
        GndServiceFlags.lockPowerFlag.lock();
        GndServiceFlags.powerFlag = argFlag;
        GndServiceFlags.lockPowerFlag.unlock();
    }
    
    protected static int getPowerFlag() {
        GndServiceFlags.lockPowerFlag.lock();
        final int temp = GndServiceFlags.powerFlag;
        GndServiceFlags.lockPowerFlag.unlock();
        return temp;
    }
    
    protected static void setRefuelFlag(final int argFlag) {
        GndServiceFlags.lockRefuelFlag.lock();
        GndServiceFlags.refuelFlag = argFlag;
        GndServiceFlags.lockRefuelFlag.unlock();
    }
    
    protected static int getRefuelFlag() {
        GndServiceFlags.lockRefuelFlag.lock();
        final int temp = GndServiceFlags.refuelFlag;
        GndServiceFlags.lockRefuelFlag.unlock();
        return temp;
    }
    
    protected static void setBoardingFlag(final int argFlag) {
        GndServiceFlags.lockBoardingFlag.lock();
        GndServiceFlags.boardingFlag = argFlag;
        GndServiceFlags.lockBoardingFlag.unlock();
    }
    
    protected static int getBoardingFlag() {
        GndServiceFlags.lockBoardingFlag.lock();
        final int temp = GndServiceFlags.boardingFlag;
        GndServiceFlags.lockBoardingFlag.unlock();
        return temp;
    }
    
    protected static void setMiscFlag(final int argFlag) {
        GndServiceFlags.lockMiscFlag.lock();
        GndServiceFlags.miscFlag = argFlag;
        GndServiceFlags.lockMiscFlag.unlock();
    }
    
    protected static int getMiscFlag() {
        GndServiceFlags.lockMiscFlag.lock();
        final int temp = GndServiceFlags.miscFlag;
        GndServiceFlags.lockMiscFlag.unlock();
        return temp;
    }
    
    protected static void setPushBackFlag(final int argFlag) {
        GndServiceFlags.lockPushBackFlag.lock();
        GndServiceFlags.pushBackFlag = argFlag;
        GndServiceFlags.lockPushBackFlag.unlock();
    }
    
    protected static int getPushBackFlag() {
        GndServiceFlags.lockPushBackFlag.lock();
        final int temp = GndServiceFlags.pushBackFlag;
        GndServiceFlags.lockPushBackFlag.unlock();
        return temp;
    }
    
    protected static void setEngineStartFlag(final int argFlag) {
        GndServiceFlags.lockEngineStartFlag.lock();
        GndServiceFlags.engineStartFlag = argFlag;
        GndServiceFlags.lockEngineStartFlag.unlock();
    }
    
    protected static int getEngineStartFlag() {
        GndServiceFlags.lockEngineStartFlag.lock();
        final int temp = GndServiceFlags.engineStartFlag;
        GndServiceFlags.lockEngineStartFlag.unlock();
        return temp;
    }
    
    protected static void setDeIcingFlag(final int argFlag) {
        GndServiceFlags.lockDeIcingFlag.lock();
        GndServiceFlags.deIcingFlag = argFlag;
        GndServiceFlags.lockDeIcingFlag.unlock();
    }
    
    protected static int getDeIcingFlag() {
        GndServiceFlags.lockDeIcingFlag.lock();
        final int temp = GndServiceFlags.deIcingFlag;
        GndServiceFlags.lockDeIcingFlag.unlock();
        return temp;
    }
}
