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

public class FmcUplinkBase implements ObserverData
{
    private static String Qs373;
    private static String Qs376;
    private static String Qs377;
    private static final Lock lockQs373;
    private static final Lock lockQs376;
    private static final Lock lockQs377;
    private static int fmcRteLoaded;
    private static final Lock lockFmcRteLoaded;
    
    static {
        FmcUplinkBase.Qs373 = new String("");
        FmcUplinkBase.Qs376 = new String("");
        FmcUplinkBase.Qs377 = new String("");
        lockQs373 = new ReentrantLock();
        lockQs376 = new ReentrantLock();
        lockQs377 = new ReentrantLock();
        FmcUplinkBase.fmcRteLoaded = 0;
        lockFmcRteLoaded = new ReentrantLock();
    }
    
    @Override
    public void updateObservers(final ObservableData argObs, final Object argId, final Object argData) {
        if (argObs instanceof DataFromPsxMain) {
            final String s;
            switch (s = (String)argId) {
                case "Qs373": {
                    FmcUplinkBase.lockQs373.lock();
                    FmcUplinkBase.Qs373 = (String)argData;
                    FmcUplinkBase.lockQs373.unlock();
                    break;
                }
                case "Qs376": {
                    FmcUplinkBase.lockQs376.lock();
                    FmcUplinkBase.Qs376 = (String)argData;
                    FmcUplinkBase.lockQs376.unlock();
                    break;
                }
                case "Qs377": {
                    FmcUplinkBase.lockQs377.lock();
                    FmcUplinkBase.Qs377 = (String)argData;
                    FmcUplinkBase.lockQs377.unlock();
                    break;
                }
                default:
                    break;
            }
        }
    }
    
    protected static String getRteInfo() {
        final String rte1 = getQs376();
        final String rte2 = getQs377();
        if (getFmcActiveRte() == 1) {
            final String dep = new String(rte1.substring(0, rte1.indexOf(59)));
            final StringSplitter ss = new StringSplitter(rte1);
            final String arr = ss.splitBetween(';', ';', 1, 2, false, false);
            if (rte1.isEmpty()) {
                setFmcRteLoaded(0);
                return "";
            }
            if (dep.equals("bbbb") || arr.equals("bbbb")) {
                setFmcRteLoaded(0);
                return "";
            }
            setFmcRteLoaded(1);
            return rte1.substring(rte1.indexOf(61) + 1);
        }
        else {
            if (getFmcActiveRte() != 2) {
                setFmcRteLoaded(0);
                return "";
            }
            final String dep = new String(rte2.substring(0, rte2.indexOf(59)));
            final StringSplitter ss = new StringSplitter(rte2);
            final String arr = ss.splitBetween(';', ';', 1, 2, false, false);
            if (rte2.isEmpty()) {
                setFmcRteLoaded(0);
                return "";
            }
            if (dep.equals("bbbb") || arr.equals("bbbb")) {
                setFmcRteLoaded(0);
                return "";
            }
            setFmcRteLoaded(1);
            return rte2.substring(rte2.indexOf(61) + 1);
        }
    }
    
    protected static int getFmcRteLoaded() {
        FmcUplinkBase.lockFmcRteLoaded.lock();
        final int temp = FmcUplinkBase.fmcRteLoaded;
        FmcUplinkBase.lockFmcRteLoaded.unlock();
        return temp;
    }
    
    private static int getFmcActiveRte() {
        String temp = new String(getQs373());
        temp = temp.substring(temp.indexOf(61) + 1);
        return Integer.parseInt(temp.substring(2, 3));
    }
    
    private static void setFmcRteLoaded(final int argRteLoaded) {
        FmcUplinkBase.lockFmcRteLoaded.lock();
        FmcUplinkBase.fmcRteLoaded = argRteLoaded;
        FmcUplinkBase.lockFmcRteLoaded.unlock();
    }
    
    private static String getQs373() {
        FmcUplinkBase.lockQs373.lock();
        final String temp = new String(FmcUplinkBase.Qs373);
        FmcUplinkBase.lockQs373.unlock();
        return temp;
    }
    
    private static String getQs376() {
        FmcUplinkBase.lockQs376.lock();
        final String temp = new String(FmcUplinkBase.Qs376);
        FmcUplinkBase.lockQs376.unlock();
        return temp;
    }
    
    private static String getQs377() {
        FmcUplinkBase.lockQs377.lock();
        final String temp = new String(FmcUplinkBase.Qs377);
        FmcUplinkBase.lockQs377.unlock();
        return temp;
    }
}
