// 
// Decompiled by Procyon v0.5.36
// 

package aloftWx;

import util.StringSplitter;
import network.DataFromPsxMain;
import util.ObservableData;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;
import util.ObserverData;

public class AloftWxBase implements ObserverData
{
    private static String Qs121;
    private static String Qs483;
    private static String Qs327;
    private static String Qs395;
    private static String Qs397;
    private static String Qi257;
    private static final Lock lockQs121;
    private static final Lock lockQs483;
    private static final Lock lockQs327;
    private static final Lock lockQs395;
    private static final Lock lockQs397;
    private static final Lock lockQi257;
    private static int psxOat;
    private static String Qs327End;
    private static final Lock lockPsxOat;
    private static final Lock lockQs327End;
    
    static {
        AloftWxBase.Qs121 = new String("");
        AloftWxBase.Qs483 = new String("");
        AloftWxBase.Qs327 = new String("");
        AloftWxBase.Qs395 = new String("");
        AloftWxBase.Qs397 = new String("");
        AloftWxBase.Qi257 = new String("");
        lockQs121 = new ReentrantLock();
        lockQs483 = new ReentrantLock();
        lockQs327 = new ReentrantLock();
        lockQs395 = new ReentrantLock();
        lockQs397 = new ReentrantLock();
        lockQi257 = new ReentrantLock();
        AloftWxBase.psxOat = 0;
        AloftWxBase.Qs327End = new String();
        lockPsxOat = new ReentrantLock();
        lockQs327End = new ReentrantLock();
    }
    
    @Override
    public void updateObservers(final ObservableData argObs, final Object argId, final Object argData) {
        if (argObs instanceof DataFromPsxMain) {
            final String s;
            switch (s = (String)argId) {
                case "Qi257": {
                    AloftWxBase.lockQi257.lock();
                    AloftWxBase.Qi257 = (String)argData;
                    AloftWxBase.lockQi257.unlock();
                    break;
                }
                case "Qs121": {
                    AloftWxBase.lockQs121.lock();
                    AloftWxBase.Qs121 = (String)argData;
                    AloftWxBase.lockQs121.unlock();
                    break;
                }
                case "Qs327": {
                    AloftWxBase.lockQs327.lock();
                    AloftWxBase.Qs327 = (String)argData;
                    AloftWxBase.lockQs327.unlock();
                    setQs327End(getQs327());
                    break;
                }
                case "Qs395": {
                    AloftWxBase.lockQs395.lock();
                    AloftWxBase.Qs395 = (String)argData;
                    AloftWxBase.lockQs395.unlock();
                    break;
                }
                case "Qs397": {
                    AloftWxBase.lockQs397.lock();
                    AloftWxBase.Qs397 = (String)argData;
                    AloftWxBase.lockQs397.unlock();
                    break;
                }
                case "Qs483": {
                    AloftWxBase.lockQs483.lock();
                    AloftWxBase.Qs483 = (String)argData;
                    AloftWxBase.lockQs483.unlock();
                    setPsxOat(getQs483());
                    break;
                }
                default:
                    break;
            }
        }
    }
    
    protected static int getPsxAltitude() {
        final String temp = new String(getQs121());
        final StringSplitter ss = new StringSplitter(temp);
        return Integer.parseInt(ss.splitBetween(';', ';', 3, 4, false, false)) / 1000;
    }
    
    protected static int getPsxOat() {
        AloftWxBase.lockPsxOat.lock();
        final int temp = AloftWxBase.psxOat;
        AloftWxBase.lockPsxOat.unlock();
        return temp;
    }
    
    protected static int smoothOat(final int argOat) {
        final int localPsxOat = getPsxOat();
        final int requestedOat = argOat;
        if (requestedOat < localPsxOat - 1) {
            return localPsxOat - 1;
        }
        if (requestedOat > localPsxOat + 1) {
            return localPsxOat + 1;
        }
        return requestedOat;
    }
    
    protected static String addOatLeadZero(final int argOat) {
        final int oat = argOat;
        if (oat >= 10) {
            return String.valueOf(oat);
        }
        return "0" + String.valueOf(oat);
    }
    
    protected static String addWindLeadZero(final int argWindDir) {
        int windDir = argWindDir;
        if (windDir < 0) {
            windDir += 360;
        }
        if (windDir == 360) {
            windDir = 0;
        }
        if (windDir > 360) {
            final int temp = windDir - 360;
            windDir = 0 + temp;
        }
        if (windDir < 10) {
            return "00" + String.valueOf(windDir);
        }
        if (windDir >= 10 && windDir < 100) {
            return "0" + String.valueOf(windDir);
        }
        return String.valueOf(windDir);
    }
    
    protected static String getQs327End() {
        AloftWxBase.lockQs327End.lock();
        final String temp = new String(AloftWxBase.Qs327End);
        AloftWxBase.lockQs327End.unlock();
        return temp;
    }
    
    protected static String getQs395() {
        AloftWxBase.lockQs395.lock();
        final String temp = new String(AloftWxBase.Qs395);
        AloftWxBase.lockQs395.unlock();
        return temp;
    }
    
    protected static String getQs397() {
        AloftWxBase.lockQs397.lock();
        final String temp = new String(AloftWxBase.Qs397);
        AloftWxBase.lockQs397.unlock();
        return temp;
    }
    
    private static void setQs327End(final String argQs327) {
        final StringSplitter ss = new StringSplitter(argQs327);
        final String temp = new String(ss.splitFrom(';', 2, true));
        AloftWxBase.lockQs327End.lock();
        AloftWxBase.Qs327End = temp;
        AloftWxBase.lockQs327End.unlock();
    }
    
    private static void setPsxOat(final String argQs483) {
        final StringSplitter ss = new StringSplitter(argQs483);
        try {
            final int rawPsxOat = Integer.parseInt(ss.splitBetween(';', ';', 1, 2, false, false));
            int tempOat = 0;
            float absOat = 0.0f;
            final float temp = rawPsxOat / 10.0f;
            if (temp >= 0.0f) {
                absOat = temp;
            }
            if (temp < 0.0f) {
                absOat = temp * -1.0f;
            }
            final float roundedOat = (float)Math.floor(absOat + 0.5);
            if (temp > 0.0f) {
                tempOat = (int)roundedOat;
            }
            if (temp < 0.0f) {
                tempOat = -1 * (int)roundedOat;
            }
            if (temp == 0.0f) {
                tempOat = 0;
            }
            AloftWxBase.lockPsxOat.lock();
            AloftWxBase.psxOat = tempOat;
            AloftWxBase.lockPsxOat.unlock();
        }
        catch (NumberFormatException e) {}
    }
    
    private static String getQs121() {
        AloftWxBase.lockQs121.lock();
        final String temp = new String(AloftWxBase.Qs121);
        AloftWxBase.lockQs121.unlock();
        return temp;
    }
    
    private static String getQs483() {
        AloftWxBase.lockQs483.lock();
        final String temp = new String(AloftWxBase.Qs483);
        AloftWxBase.lockQs483.unlock();
        return temp;
    }
    
    private static String getQs327() {
        AloftWxBase.lockQs327.lock();
        final String temp = new String(AloftWxBase.Qs327);
        AloftWxBase.lockQs327.unlock();
        return temp;
    }
    
    protected static boolean getPsxOnGround() {
        AloftWxBase.lockQi257.lock();
        String temp = new String(AloftWxBase.Qi257);
        AloftWxBase.lockQi257.unlock();
        temp = temp.substring(temp.indexOf(61) + 1);
        try {
            final int onGround = Integer.parseInt(temp);
            return onGround == 1;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }
}
