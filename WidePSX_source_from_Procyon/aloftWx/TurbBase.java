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

public class TurbBase implements ObserverData
{
    private static final int EDGEOFFSETPERCENTAGE = 20;
    private static String Qs121;
    private static String Qi129;
    private static String turbulenceIntensity;
    private static String turbulenceSound;
    private static int turbulenceSoundDuration;
    private static final Lock lockQs121;
    private static final Lock lockQi129;
    private static final Lock lockTurbulenceIntensity;
    private static final Lock lockTurbulenceSound;
    private static final Lock lockTurbulenceSoundDuration;
    
    static {
        TurbBase.Qs121 = new String("");
        TurbBase.Qi129 = new String("");
        TurbBase.turbulenceIntensity = new String("N");
        TurbBase.turbulenceSound = new String("N");
        TurbBase.turbulenceSoundDuration = 0;
        lockQs121 = new ReentrantLock();
        lockQi129 = new ReentrantLock();
        lockTurbulenceIntensity = new ReentrantLock();
        lockTurbulenceSound = new ReentrantLock();
        lockTurbulenceSoundDuration = new ReentrantLock();
    }
    
    @Override
    public void updateObservers(final ObservableData argObs, final Object argId, final Object argData) {
        if (argObs instanceof DataFromPsxMain) {
            final String s;
            switch (s = (String)argId) {
                case "Qi129": {
                    TurbBase.lockQi129.lock();
                    TurbBase.Qi129 = (String)argData;
                    TurbBase.lockQi129.unlock();
                    break;
                }
                case "Qs121": {
                    TurbBase.lockQs121.lock();
                    TurbBase.Qs121 = (String)argData;
                    TurbBase.lockQs121.unlock();
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
    
    protected static double getPsxLatitude() {
        final StringSplitter ss = new StringSplitter(getQs121());
        return Double.parseDouble(ss.splitBetween(';', ';', 5, 6, false, false));
    }
    
    protected static double getPsxLongitude() {
        final StringSplitter ss = new StringSplitter(getQs121());
        return Double.parseDouble(ss.splitFrom(';', 6, false));
    }
    
    protected static int getSimPaused() {
        final String temp = new String(getQi129());
        return Integer.parseInt(temp.substring(temp.indexOf(61) + 1));
    }
    
    protected static double getDistance(final double argLatitudeWpt1, final double argLongitudeWpt1, final double argLatitudeWpt2, final double argLongitudeWpt2) {
        final double a = Math.sin((argLatitudeWpt2 - argLatitudeWpt1) / 2.0) * Math.sin((argLatitudeWpt2 - argLatitudeWpt1) / 2.0) + Math.cos(argLatitudeWpt1) * Math.cos(argLatitudeWpt2) * Math.sin((argLongitudeWpt2 - argLongitudeWpt1) / 2.0) * Math.sin((argLongitudeWpt2 - argLongitudeWpt1) / 2.0);
        final double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));
        final double d = 6371000.0 * c;
        return d * 5.39957E-4;
    }
    
    protected static void setTurbIntensity(final String argIntensity) {
        TurbBase.lockTurbulenceIntensity.lock();
        TurbBase.turbulenceIntensity = argIntensity;
        TurbBase.lockTurbulenceIntensity.unlock();
    }
    
    protected static String getTurbIntensity() {
        TurbBase.lockTurbulenceIntensity.lock();
        final String temp = new String(TurbBase.turbulenceIntensity);
        TurbBase.lockTurbulenceIntensity.unlock();
        return temp;
    }
    
    protected static void setTurbSound(final String argSound) {
        TurbBase.lockTurbulenceSound.lock();
        TurbBase.turbulenceSound = argSound;
        TurbBase.lockTurbulenceSound.unlock();
    }
    
    protected static String getTurbSound() {
        TurbBase.lockTurbulenceSound.lock();
        final String temp = new String(TurbBase.turbulenceSound);
        TurbBase.lockTurbulenceSound.unlock();
        return temp;
    }
    
    protected static void setTurbulenceSoundDuration(final int argDuration) {
        TurbBase.lockTurbulenceSoundDuration.lock();
        TurbBase.turbulenceSoundDuration = argDuration;
        TurbBase.lockTurbulenceSoundDuration.unlock();
    }
    
    protected static int getTurbulenceSoundDuration() {
        TurbBase.lockTurbulenceSoundDuration.lock();
        final int temp = TurbBase.turbulenceSoundDuration;
        TurbBase.lockTurbulenceSoundDuration.unlock();
        return temp;
    }
    
    protected static int getEdgeOffsetPercentage() {
        return 20;
    }
    
    protected static String getQs121() {
        TurbBase.lockQs121.lock();
        final String temp = new String(TurbBase.Qs121);
        TurbBase.lockQs121.unlock();
        return temp;
    }
    
    protected static String getQi129() {
        TurbBase.lockQi129.lock();
        final String temp = new String(TurbBase.Qi129);
        TurbBase.lockQi129.unlock();
        return temp;
    }
}
