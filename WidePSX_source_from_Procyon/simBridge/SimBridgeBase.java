// 
// Decompiled by Procyon v0.5.36
// 

package simBridge;

import network.BridgeSimConnect;
import util.StringSplitter;
import gndService.GndServiceBase;
import util.StatusMonitor;
import java.io.IOException;
import network.DataFromPsxMain;
import util.ObservableData;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;
import util.ObserverData;

public class SimBridgeBase implements ObserverData
{
    private static String Qs123;
    private static String Qs424;
    private static String Qs443;
    private static String Qs120;
    private static String Qh388;
    private static String Qh389;
    private static String Qh397;
    private static String Qs438;
    private static String Qi123;
    private static String Qi0;
    private static String Qs436;
    private static String Qs121;
    private static String Qh392;
    private static String Qh393;
    private static String Qh394;
    private static String Qh395;
    private static String Qi257;
    private static String Qh239;
    private static String Qh240;
    private static String Qh225;
    private static String Qh226;
    private static String Qh227;
    private static String Qh228;
    private static String Qh229;
    private static String Qh230;
    private static String Qh231;
    private static String Qh232;
    private static String Qh233;
    private static String Qh234;
    private static String Qh235;
    private static String Qh236;
    private static String Qi21;
    private static String Qi22;
    private static String Qs480;
    private static final Lock lockQs123;
    private static final Lock lockQs424;
    private static final Lock lockQs443;
    private static final Lock lockQs120;
    private static final Lock lockQh388;
    private static final Lock lockQh389;
    private static final Lock lockQh397;
    private static final Lock lockQs438;
    private static final Lock lockQi123;
    private static final Lock lockQi0;
    private static final Lock lockQs436;
    private static final Lock lockQs121;
    private static final Lock lockQh392;
    private static final Lock lockQh393;
    private static final Lock lockQh394;
    private static final Lock lockQh395;
    private static final Lock lockQi257;
    private static final Lock lockQh239;
    private static final Lock lockQh240;
    private static final Lock lockQh225;
    private static final Lock lockQh226;
    private static final Lock lockQh227;
    private static final Lock lockQh228;
    private static final Lock lockQh229;
    private static final Lock lockQh230;
    private static final Lock lockQh231;
    private static final Lock lockQh232;
    private static final Lock lockQh233;
    private static final Lock lockQh234;
    private static final Lock lockQh235;
    private static final Lock lockQh236;
    private static final Lock lockQi21;
    private static final Lock lockQi22;
    private static final Lock lockQs480;
    private static final double DEG2RAD = 0.017453292519943295;
    
    static {
        SimBridgeBase.Qs123 = new String("");
        SimBridgeBase.Qs424 = new String("");
        SimBridgeBase.Qs443 = new String("");
        SimBridgeBase.Qs120 = new String("");
        SimBridgeBase.Qh388 = new String("");
        SimBridgeBase.Qh389 = new String("");
        SimBridgeBase.Qh397 = new String("");
        SimBridgeBase.Qs438 = new String("");
        SimBridgeBase.Qi123 = new String("");
        SimBridgeBase.Qi0 = new String("");
        SimBridgeBase.Qs436 = new String("");
        SimBridgeBase.Qs121 = new String("");
        SimBridgeBase.Qh392 = new String("");
        SimBridgeBase.Qh393 = new String("");
        SimBridgeBase.Qh394 = new String("");
        SimBridgeBase.Qh395 = new String("");
        SimBridgeBase.Qi257 = new String("");
        SimBridgeBase.Qh239 = new String("");
        SimBridgeBase.Qh240 = new String("");
        SimBridgeBase.Qh225 = new String("");
        SimBridgeBase.Qh226 = new String("");
        SimBridgeBase.Qh227 = new String("");
        SimBridgeBase.Qh228 = new String("");
        SimBridgeBase.Qh229 = new String("");
        SimBridgeBase.Qh230 = new String("");
        SimBridgeBase.Qh231 = new String("");
        SimBridgeBase.Qh232 = new String("");
        SimBridgeBase.Qh233 = new String("");
        SimBridgeBase.Qh234 = new String("");
        SimBridgeBase.Qh235 = new String("");
        SimBridgeBase.Qh236 = new String("");
        SimBridgeBase.Qi21 = new String("");
        SimBridgeBase.Qi22 = new String("");
        SimBridgeBase.Qs480 = new String("");
        lockQs123 = new ReentrantLock();
        lockQs424 = new ReentrantLock();
        lockQs443 = new ReentrantLock();
        lockQs120 = new ReentrantLock();
        lockQh388 = new ReentrantLock();
        lockQh389 = new ReentrantLock();
        lockQh397 = new ReentrantLock();
        lockQs438 = new ReentrantLock();
        lockQi123 = new ReentrantLock();
        lockQi0 = new ReentrantLock();
        lockQs436 = new ReentrantLock();
        lockQs121 = new ReentrantLock();
        lockQh392 = new ReentrantLock();
        lockQh393 = new ReentrantLock();
        lockQh394 = new ReentrantLock();
        lockQh395 = new ReentrantLock();
        lockQi257 = new ReentrantLock();
        lockQh239 = new ReentrantLock();
        lockQh240 = new ReentrantLock();
        lockQh225 = new ReentrantLock();
        lockQh226 = new ReentrantLock();
        lockQh227 = new ReentrantLock();
        lockQh228 = new ReentrantLock();
        lockQh229 = new ReentrantLock();
        lockQh230 = new ReentrantLock();
        lockQh231 = new ReentrantLock();
        lockQh232 = new ReentrantLock();
        lockQh233 = new ReentrantLock();
        lockQh234 = new ReentrantLock();
        lockQh235 = new ReentrantLock();
        lockQh236 = new ReentrantLock();
        lockQi21 = new ReentrantLock();
        lockQi22 = new ReentrantLock();
        lockQs480 = new ReentrantLock();
    }
    
    public SimBridgeBase() {
        final SimBridgeTimeSync simBridgeTimeSync = new SimBridgeTimeSync();
        final Thread timeSyncThread = new Thread(simBridgeTimeSync);
        timeSyncThread.start();
    }
    
    @Override
    public void updateObservers(final ObservableData argObs, final Object argId, final Object argData) {
        if (argObs instanceof DataFromPsxMain) {
            final String s;
            switch (s = (String)argId) {
                case "Qi0": {
                    SimBridgeBase.lockQi0.lock();
                    SimBridgeBase.Qi0 = (String)argData;
                    SimBridgeBase.lockQi0.unlock();
                    break;
                }
                case "Qi21": {
                    SimBridgeBase.lockQi21.lock();
                    SimBridgeBase.Qi21 = (String)argData;
                    SimBridgeBase.lockQi21.unlock();
                    break;
                }
                case "Qi22": {
                    SimBridgeBase.lockQi22.lock();
                    SimBridgeBase.Qi22 = (String)argData;
                    SimBridgeBase.lockQi22.unlock();
                    break;
                }
                case "Qh225": {
                    SimBridgeBase.lockQh225.lock();
                    SimBridgeBase.Qh225 = (String)argData;
                    SimBridgeBase.lockQh225.unlock();
                    try {
                        SimBridgeAntiIce.dispatchAntiIceEng1(getQh225(), getQh229(), getQi21());
                    }
                    catch (IOException ex) {}
                    break;
                }
                case "Qh226": {
                    SimBridgeBase.lockQh226.lock();
                    SimBridgeBase.Qh226 = (String)argData;
                    SimBridgeBase.lockQh226.unlock();
                    try {
                        SimBridgeAntiIce.dispatchAntiIceEng2(getQh226(), getQh230(), getQi21());
                    }
                    catch (IOException ex2) {}
                    break;
                }
                case "Qh227": {
                    SimBridgeBase.lockQh227.lock();
                    SimBridgeBase.Qh227 = (String)argData;
                    SimBridgeBase.lockQh227.unlock();
                    try {
                        SimBridgeAntiIce.dispatchAntiIceEng3(getQh227(), getQh231(), getQi21());
                    }
                    catch (IOException ex3) {}
                    break;
                }
                case "Qh228": {
                    SimBridgeBase.lockQh228.lock();
                    SimBridgeBase.Qh228 = (String)argData;
                    SimBridgeBase.lockQh228.unlock();
                    try {
                        SimBridgeAntiIce.dispatchAntiIceEng4(getQh228(), getQh232(), getQi21());
                    }
                    catch (IOException ex4) {}
                    break;
                }
                case "Qh229": {
                    SimBridgeBase.lockQh229.lock();
                    SimBridgeBase.Qh229 = (String)argData;
                    SimBridgeBase.lockQh229.unlock();
                    try {
                        SimBridgeAntiIce.dispatchAntiIceEng1(getQh225(), getQh229(), getQi21());
                    }
                    catch (IOException ex5) {}
                    break;
                }
                case "Qh230": {
                    SimBridgeBase.lockQh230.lock();
                    SimBridgeBase.Qh230 = (String)argData;
                    SimBridgeBase.lockQh230.unlock();
                    try {
                        SimBridgeAntiIce.dispatchAntiIceEng2(getQh226(), getQh230(), getQi21());
                    }
                    catch (IOException ex6) {}
                    break;
                }
                case "Qh231": {
                    SimBridgeBase.lockQh231.lock();
                    SimBridgeBase.Qh231 = (String)argData;
                    SimBridgeBase.lockQh231.unlock();
                    try {
                        SimBridgeAntiIce.dispatchAntiIceEng3(getQh227(), getQh231(), getQi21());
                    }
                    catch (IOException ex7) {}
                    break;
                }
                case "Qh232": {
                    SimBridgeBase.lockQh232.lock();
                    SimBridgeBase.Qh232 = (String)argData;
                    SimBridgeBase.lockQh232.unlock();
                    try {
                        SimBridgeAntiIce.dispatchAntiIceEng4(getQh228(), getQh232(), getQi21());
                    }
                    catch (IOException ex8) {}
                    break;
                }
                case "Qh233": {
                    SimBridgeBase.lockQh233.lock();
                    SimBridgeBase.Qh233 = (String)argData;
                    SimBridgeBase.lockQh233.unlock();
                    try {
                        SimBridgeAntiIce.dispatchAntiIceWing(getQh233(), getQh234(), getQi22());
                    }
                    catch (IOException ex9) {}
                    break;
                }
                case "Qh234": {
                    SimBridgeBase.lockQh234.lock();
                    SimBridgeBase.Qh234 = (String)argData;
                    SimBridgeBase.lockQh234.unlock();
                    try {
                        SimBridgeAntiIce.dispatchAntiIceWing(getQh233(), getQh234(), getQi22());
                    }
                    catch (IOException ex10) {}
                    break;
                }
                case "Qh235": {
                    SimBridgeBase.lockQh235.lock();
                    SimBridgeBase.Qh235 = (String)argData;
                    SimBridgeBase.lockQh235.unlock();
                    try {
                        SimBridgeAntiIce.dispatchWipers(getQh235(), getQh236());
                    }
                    catch (IOException ex11) {}
                    break;
                }
                case "Qh236": {
                    SimBridgeBase.lockQh236.lock();
                    SimBridgeBase.Qh236 = (String)argData;
                    SimBridgeBase.lockQh236.unlock();
                    try {
                        SimBridgeAntiIce.dispatchWipers(getQh235(), getQh236());
                    }
                    catch (IOException ex12) {}
                    break;
                }
                case "Qh239": {
                    SimBridgeBase.lockQh239.lock();
                    SimBridgeBase.Qh239 = (String)argData;
                    SimBridgeBase.lockQh239.unlock();
                    try {
                        SimBridgeAntiIce.dispatchWdoHeat(getQh239(), getQh240());
                    }
                    catch (IOException ex13) {}
                    break;
                }
                case "Qh240": {
                    SimBridgeBase.lockQh240.lock();
                    SimBridgeBase.Qh240 = (String)argData;
                    SimBridgeBase.lockQh240.unlock();
                    try {
                        SimBridgeAntiIce.dispatchWdoHeat(getQh239(), getQh240());
                    }
                    catch (IOException ex14) {}
                    break;
                }
                case "Qh388": {
                    try {
                        SimBridgeBase.lockQh388.lock();
                        SimBridgeBase.Qh388 = (String)argData;
                        SimBridgeBase.lockQh388.unlock();
                        SimBridgeFltCtrls.dispatchQh388();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case "Qh389": {
                    try {
                        SimBridgeBase.lockQh389.lock();
                        SimBridgeBase.Qh389 = (String)argData;
                        SimBridgeBase.lockQh389.unlock();
                        SimBridgeFltCtrls.dispatchQh389();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case "Qh392": {
                    SimBridgeBase.lockQh392.lock();
                    SimBridgeBase.Qh392 = (String)argData;
                    SimBridgeBase.lockQh392.unlock();
                    try {
                        SimBridgeEngines.dispatchQh392((String)argData);
                    }
                    catch (IOException ex15) {}
                    break;
                }
                case "Qh393": {
                    SimBridgeBase.lockQh393.lock();
                    SimBridgeBase.Qh393 = (String)argData;
                    SimBridgeBase.lockQh393.unlock();
                    try {
                        SimBridgeEngines.dispatchQh393((String)argData);
                    }
                    catch (IOException ex16) {}
                    break;
                }
                case "Qh394": {
                    SimBridgeBase.lockQh394.lock();
                    SimBridgeBase.Qh394 = (String)argData;
                    SimBridgeBase.lockQh394.unlock();
                    try {
                        SimBridgeEngines.dispatchQh394((String)argData);
                    }
                    catch (IOException ex17) {}
                    break;
                }
                case "Qh395": {
                    SimBridgeBase.lockQh395.lock();
                    SimBridgeBase.Qh395 = (String)argData;
                    SimBridgeBase.lockQh395.unlock();
                    try {
                        SimBridgeEngines.dispatchQh395((String)argData);
                    }
                    catch (IOException ex18) {}
                    break;
                }
                case "Qh397": {
                    SimBridgeBase.lockQh397.lock();
                    SimBridgeBase.Qh397 = (String)argData;
                    SimBridgeBase.lockQh397.unlock();
                    try {
                        SimBridgeFltCtrls.dispatchQh397((String)argData);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case "Qi123": {
                    SimBridgeBase.lockQi123.lock();
                    SimBridgeBase.Qi123 = (String)argData;
                    SimBridgeBase.lockQi123.unlock();
                    break;
                }
                case "Qi257": {
                    SimBridgeBase.lockQi257.lock();
                    SimBridgeBase.Qi257 = (String)argData;
                    SimBridgeBase.lockQi257.unlock();
                    break;
                }
                case "Qs121": {
                    SimBridgeBase.lockQs121.lock();
                    SimBridgeBase.Qs121 = (String)argData;
                    SimBridgeBase.lockQs121.unlock();
                    break;
                }
                case "Qs122": {
                    dispatchQs122();
                    break;
                }
                case "Qs123": {
                    SimBridgeBase.lockQs123.lock();
                    SimBridgeBase.Qs123 = (String)argData;
                    SimBridgeBase.lockQs123.unlock();
                    break;
                }
                case "Qs424": {
                    try {
                        SimBridgeBase.lockQs424.lock();
                        SimBridgeBase.Qs424 = (String)argData;
                        SimBridgeBase.lockQs424.unlock();
                        SimBridgeLtsGear.dispatchQs424((String)argData);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case "Qs436": {
                    SimBridgeBase.lockQs436.lock();
                    SimBridgeBase.Qs436 = (String)argData;
                    SimBridgeBase.lockQs436.unlock();
                    try {
                        SimBridgeEngines.dispatchQs436((String)argData);
                    }
                    catch (IOException ex19) {}
                    break;
                }
                case "Qs438": {
                    SimBridgeBase.lockQs438.lock();
                    SimBridgeBase.Qs438 = (String)argData;
                    SimBridgeBase.lockQs438.unlock();
                    break;
                }
                case "Qs443": {
                    try {
                        SimBridgeBase.lockQs443.lock();
                        SimBridgeBase.Qs443 = (String)argData;
                        SimBridgeBase.lockQs443.unlock();
                        SimBridgeLtsGear.dispatchQs443((String)argData);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case "Qs480": {
                    try {
                        SimBridgeBase.lockQs480.lock();
                        SimBridgeBase.Qs480 = (String)argData;
                        SimBridgeBase.lockQs480.unlock();
                        SimBridgeFltCtrls.dispatchQs480();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    }
    
    public static void dispatchBoostData(final String argData) throws IOException {
        if (StatusMonitor.getGndServiceExtPush() && GndServiceBase.getReleaseScenGen()) {
            System.out.println("RETURN");
            return;
        }
        final StringSplitter ss = new StringSplitter(argData);
        boolean value = false;
        if (ss.splitTo(';', 1, false).equals("F")) {
            value = true;
        }
        BridgeSimConnect.updateAcftPos(value, Double.parseDouble(ss.splitBetween(';', ';', 5, 6, false, false)) * 0.017453292519943295, Double.parseDouble(ss.splitBetween(';', ';', 6, 7, false, false)) * 0.017453292519943295, Double.parseDouble(ss.splitBetween(';', ';', 1, 2, false, false)) / 100.0, Double.parseDouble(ss.splitBetween(';', ';', 2, 3, false, false)) / 100.0 * 0.017453292519943295, Double.parseDouble(ss.splitBetween(';', ';', 3, 4, false, false)) / 100.0 * 0.017453292519943295, Double.parseDouble(ss.splitBetween(';', ';', 4, 5, false, false)) / 100.0 * 0.017453292519943295);
    }
    
    public static void dispatchQs122() {
        if (StatusMonitor.getSimBridgeIsRunning()) {
            if (!getOnGround()) {
                BridgeSimConnect.notifyPsxSituLoad(0);
            }
            else {
                BridgeSimConnect.notifyPsxSituLoad(1);
            }
        }
    }
    
    public static double getPsxTas() {
        final StringSplitter ss = new StringSplitter(getQs121());
        double temp = 0.0;
        try {
            temp = Double.parseDouble(ss.splitBetween(';', ';', 4, 5, false, false));
        }
        catch (Exception e) {
            temp = 0.0;
        }
        return temp / 1000.0;
    }
    
    public static double getPsxGearCompRatio() {
        final int version = getAcftType();
        double diff = 0.0;
        double gap = 0.0;
        double ratio = 0.0;
        if (version == 0) {
            diff = getPsxGrossWt() - 178800.0;
            if (diff < 0.0) {
                diff = 0.0;
            }
            gap = 2181.0;
            ratio = diff / gap;
        }
        else if (version == 1) {
            diff = getPsxGrossWt() - 184570.0;
            if (diff < 0.0) {
                diff = 0.0;
            }
            gap = 2282.0;
            ratio = diff / gap;
        }
        else if (version == 2) {
            diff = getPsxGrossWt() - 165107.0;
            if (diff < 0.0) {
                diff = 0.0;
            }
            gap = 2318.0;
            ratio = diff / gap;
        }
        else if (version == 3) {
            diff = getPsxGrossWt() - 164382.0;
            if (diff < 0.0) {
                diff = 0.0;
            }
            gap = 2484.0;
            ratio = diff / gap;
        }
        if (ratio > 100.0) {
            ratio = 100.0;
        }
        return ratio;
    }
    
    public static boolean getOnGround() {
        SimBridgeBase.lockQi257.lock();
        final String temp = SimBridgeBase.Qi257;
        SimBridgeBase.lockQi257.unlock();
        int value = 0;
        try {
            value = Integer.parseInt(temp.substring(temp.indexOf(61) + 1));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return value != 0;
    }
    
    public static String getQh392() {
        SimBridgeBase.lockQh392.lock();
        final String temp = SimBridgeBase.Qh392;
        SimBridgeBase.lockQh392.unlock();
        return temp;
    }
    
    public static String getQh393() {
        SimBridgeBase.lockQh393.lock();
        final String temp = SimBridgeBase.Qh393;
        SimBridgeBase.lockQh393.unlock();
        return temp;
    }
    
    public static String getQh394() {
        SimBridgeBase.lockQh394.lock();
        final String temp = SimBridgeBase.Qh394;
        SimBridgeBase.lockQh394.unlock();
        return temp;
    }
    
    public static String getQh395() {
        SimBridgeBase.lockQh395.lock();
        final String temp = SimBridgeBase.Qh395;
        SimBridgeBase.lockQh395.unlock();
        return temp;
    }
    
    public static String getQs424() {
        SimBridgeBase.lockQs424.lock();
        final String temp = SimBridgeBase.Qs424;
        SimBridgeBase.lockQs424.unlock();
        return temp;
    }
    
    public static String getQs443() {
        SimBridgeBase.lockQs443.lock();
        final String temp = SimBridgeBase.Qs443;
        SimBridgeBase.lockQs443.unlock();
        return temp;
    }
    
    public static String getQh397() {
        SimBridgeBase.lockQh397.lock();
        final String temp = SimBridgeBase.Qh397;
        SimBridgeBase.lockQh397.unlock();
        return temp;
    }
    
    public static String getQs436() {
        SimBridgeBase.lockQs436.lock();
        final String temp = SimBridgeBase.Qs436;
        SimBridgeBase.lockQs436.unlock();
        return temp;
    }
    
    protected static String getQs123() {
        SimBridgeBase.lockQs123.lock();
        final String temp = SimBridgeBase.Qs123;
        SimBridgeBase.lockQs123.unlock();
        return temp;
    }
    
    protected static String getQs120() {
        SimBridgeBase.lockQs120.lock();
        final String temp = SimBridgeBase.Qs120;
        SimBridgeBase.lockQs120.unlock();
        return temp;
    }
    
    protected static String getQh388() {
        SimBridgeBase.lockQh388.lock();
        final String temp = SimBridgeBase.Qh388;
        SimBridgeBase.lockQh388.unlock();
        return temp;
    }
    
    protected static String getQh389() {
        SimBridgeBase.lockQh389.lock();
        final String temp = SimBridgeBase.Qh389;
        SimBridgeBase.lockQh389.unlock();
        return temp;
    }
    
    protected static String getQh239() {
        SimBridgeBase.lockQh239.lock();
        final String temp = SimBridgeBase.Qh239;
        SimBridgeBase.lockQh239.unlock();
        return temp;
    }
    
    protected static String getQh240() {
        SimBridgeBase.lockQh240.lock();
        final String temp = SimBridgeBase.Qh240;
        SimBridgeBase.lockQh240.unlock();
        return temp;
    }
    
    protected static String getQh225() {
        SimBridgeBase.lockQh225.lock();
        final String temp = SimBridgeBase.Qh225;
        SimBridgeBase.lockQh225.unlock();
        return temp;
    }
    
    protected static String getQh226() {
        SimBridgeBase.lockQh226.lock();
        final String temp = SimBridgeBase.Qh226;
        SimBridgeBase.lockQh226.unlock();
        return temp;
    }
    
    protected static String getQh227() {
        SimBridgeBase.lockQh227.lock();
        final String temp = SimBridgeBase.Qh227;
        SimBridgeBase.lockQh227.unlock();
        return temp;
    }
    
    protected static String getQh228() {
        SimBridgeBase.lockQh228.lock();
        final String temp = SimBridgeBase.Qh228;
        SimBridgeBase.lockQh228.unlock();
        return temp;
    }
    
    protected static String getQh229() {
        SimBridgeBase.lockQh229.lock();
        final String temp = SimBridgeBase.Qh229;
        SimBridgeBase.lockQh229.unlock();
        return temp;
    }
    
    protected static String getQh230() {
        SimBridgeBase.lockQh230.lock();
        final String temp = SimBridgeBase.Qh230;
        SimBridgeBase.lockQh230.unlock();
        return temp;
    }
    
    protected static String getQh231() {
        SimBridgeBase.lockQh231.lock();
        final String temp = SimBridgeBase.Qh231;
        SimBridgeBase.lockQh231.unlock();
        return temp;
    }
    
    protected static String getQh232() {
        SimBridgeBase.lockQh232.lock();
        final String temp = SimBridgeBase.Qh232;
        SimBridgeBase.lockQh232.unlock();
        return temp;
    }
    
    protected static String getQh233() {
        SimBridgeBase.lockQh233.lock();
        final String temp = SimBridgeBase.Qh233;
        SimBridgeBase.lockQh233.unlock();
        return temp;
    }
    
    protected static String getQh234() {
        SimBridgeBase.lockQh234.lock();
        final String temp = SimBridgeBase.Qh234;
        SimBridgeBase.lockQh234.unlock();
        return temp;
    }
    
    protected static String getQh235() {
        SimBridgeBase.lockQh235.lock();
        final String temp = SimBridgeBase.Qh235;
        SimBridgeBase.lockQh235.unlock();
        return temp;
    }
    
    protected static String getQh236() {
        SimBridgeBase.lockQh236.lock();
        final String temp = SimBridgeBase.Qh236;
        SimBridgeBase.lockQh236.unlock();
        return temp;
    }
    
    protected static String getQi21() {
        SimBridgeBase.lockQi21.lock();
        final String temp = SimBridgeBase.Qi21;
        SimBridgeBase.lockQi21.unlock();
        return temp;
    }
    
    protected static String getQi22() {
        SimBridgeBase.lockQi22.lock();
        final String temp = SimBridgeBase.Qi22;
        SimBridgeBase.lockQi22.unlock();
        return temp;
    }
    
    protected static String getQs480() {
        SimBridgeBase.lockQs480.lock();
        final String temp = SimBridgeBase.Qs480;
        SimBridgeBase.lockQs480.unlock();
        return temp;
    }
    
    private static double getPsxGrossWt() {
        SimBridgeBase.lockQs438.lock();
        String Qs438Loc = SimBridgeBase.Qs438;
        SimBridgeBase.lockQs438.unlock();
        SimBridgeBase.lockQi123.lock();
        String Qi123Loc = SimBridgeBase.Qi123;
        SimBridgeBase.lockQi123.unlock();
        Qs438Loc = Qs438Loc.substring(Qs438Loc.indexOf(61) + 2);
        final StringSplitter ss = new StringSplitter(Qs438Loc);
        final int fuelQty = (Integer.parseInt(ss.splitTo(';', 1, false)) + Integer.parseInt(ss.splitBetween(';', ';', 1, 2, false, false)) + Integer.parseInt(ss.splitBetween(';', ';', 2, 3, false, false)) + Integer.parseInt(ss.splitBetween(';', ';', 3, 4, false, false)) + Integer.parseInt(ss.splitBetween(';', ';', 4, 5, false, false)) + Integer.parseInt(ss.splitBetween(';', ';', 5, 6, false, false)) + Integer.parseInt(ss.splitBetween(';', ';', 6, 7, false, false)) + Integer.parseInt(ss.splitBetween(';', ';', 7, 8, false, false)) + Integer.parseInt(ss.splitBetween(';', ';', 8, 9, false, false))) / 10;
        Qi123Loc = Qi123Loc.substring(Qi123Loc.indexOf(61) + 1);
        return (fuelQty + Double.parseDouble(Qi123Loc)) * 0.454;
    }
    
    private static int getAcftType() {
        SimBridgeBase.lockQi0.lock();
        String temp = new String(SimBridgeBase.Qi0);
        SimBridgeBase.lockQi0.unlock();
        temp = temp.substring(temp.indexOf(61) + 1);
        final int version = Integer.parseInt(temp);
        if (version == 2) {
            return 0;
        }
        if (version == 4) {
            return 1;
        }
        if (version == 0 || version == 1) {
            return 2;
        }
        if (version == 3) {
            return 3;
        }
        return 0;
    }
    
    private static String getQs121() {
        SimBridgeBase.lockQs121.lock();
        final String temp = SimBridgeBase.Qs121;
        SimBridgeBase.lockQs121.unlock();
        return temp;
    }
}
