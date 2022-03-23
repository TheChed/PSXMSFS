// 
// Decompiled by Procyon v0.5.36
// 

package aloftWx;

import network.SocketClientPSXMain;
import files.FileAsnWx;
import util.StringSplitter;
import util.MutableString;
import util.StatusMonitor;
import java.io.IOException;
import network.DataFromPsxMain;
import util.ObservableData;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;
import util.ObserverData;

public class FmcUplinkDes implements ObserverData
{
    private static boolean sendOk;
    private static boolean firstMessage;
    private static boolean sameData;
    private static String message;
    private static String lastMessage;
    private static String Qs393;
    private static String Qi242;
    private static final Lock lockQs393;
    private static final Lock lockQi242;
    private static final Lock lockSameData;
    
    static {
        FmcUplinkDes.sendOk = true;
        FmcUplinkDes.firstMessage = true;
        FmcUplinkDes.sameData = false;
        FmcUplinkDes.message = new String("");
        FmcUplinkDes.lastMessage = new String("");
        FmcUplinkDes.Qs393 = new String("");
        FmcUplinkDes.Qi242 = new String("");
        lockQs393 = new ReentrantLock();
        lockQi242 = new ReentrantLock();
        lockSameData = new ReentrantLock();
    }
    
    @Override
    public void updateObservers(final ObservableData argObs, final Object argId, final Object argData) {
        if (argObs instanceof DataFromPsxMain) {
            final String s;
            switch (s = (String)argId) {
                case "Qi242": {
                    FmcUplinkDes.lockQi242.lock();
                    FmcUplinkDes.Qi242 = (String)argData;
                    FmcUplinkDes.lockQi242.unlock();
                    try {
                        dispatchUplinkBits();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case "Qs393": {
                    if (getSameData()) {
                        sendData();
                    }
                    FmcUplinkDes.lockQs393.lock();
                    FmcUplinkDes.Qs393 = (String)argData;
                    FmcUplinkDes.lockQs393.unlock();
                    break;
                }
                default:
                    break;
            }
        }
    }
    
    private static String getDescFoca() {
        final String temp = new String(getQs393());
        return temp.substring(temp.indexOf(61) + 1);
    }
    
    private static boolean getSameData() {
        FmcUplinkDes.lockSameData.lock();
        final boolean temp = FmcUplinkDes.sameData;
        FmcUplinkDes.lockSameData.unlock();
        return temp;
    }
    
    private static void dispatchUplinkBits() throws IOException {
        final String tempQi242 = getQi242();
        final String temp = new String(tempQi242.substring(tempQi242.indexOf(61) + 1));
        final int bits = Integer.parseInt(temp);
        if (bits != 0 && StatusMonitor.getAloftWxRunning()) {
            if (StatusMonitor.getAloftWxFileLoaded()) {
                StatusMonitor.setFmcUplinkNoWxFileLoaded(false);
                if ((bits & 0x4) != 0x0) {
                    prepareData();
                }
                else if ((bits & 0x8) != 0x0) {
                    sendData();
                }
            }
            else {
                StatusMonitor.setFmcUplinkNoWxFileLoaded(true);
            }
        }
    }
    
    private static void prepareData() throws IOException {
        final String forceRteLoaded = new String(FmcUplinkBase.getRteInfo());
        String destination = new String("");
        final MutableString todInfos = new MutableString("");
        final MutableString destInfos = new MutableString("");
        String info30000 = new String("");
        String info30001 = new String("");
        String info30002 = new String("");
        String info30003 = new String("");
        if (FmcUplinkBase.getFmcRteLoaded() == 0) {
            FmcUplinkDes.sendOk = false;
            StatusMonitor.setFmcUplinkNoPsxRteLoaded(true);
        }
        else {
            StatusMonitor.setFmcUplinkNoPsxRteLoaded(false);
        }
        if (FmcUplinkDes.sendOk) {
            FmcUplinkDes.message = new String("");
            boolean todFound = false;
            boolean destFound = false;
            if (FmcUplinkBase.getFmcRteLoaded() != 0) {
                final StringSplitter ss = new StringSplitter(FmcUplinkBase.getRteInfo());
                destination = ss.splitBetween(';', ';', 1, 2, false, false);
            }
            if (FileAsnWx.searchWptInfoDyn("TOD", todInfos)) {
                todFound = true;
                final StringSplitter ss = new StringSplitter(todInfos.get());
                info30001 = ss.splitBetween('\t', '\t', 7, 8, false, false);
                info30000 = ss.splitBetween('\t', '\t', 9, 10, false, false);
            }
            if (FileAsnWx.searchWptInfoDyn(destination, destInfos)) {
                destFound = true;
                final StringSplitter ss = new StringSplitter(destInfos.get());
                info30003 = ss.splitBetween('\t', '\t', 3, 4, false, false);
                info30002 = ss.splitBetween('\t', '\t', 5, 6, false, false);
            }
            if (todFound && destFound) {
                StatusMonitor.setFmcUplinkTodDestNotFound(false);
                final StringSplitter ss2 = new StringSplitter(info30003);
                final StringSplitter ss3 = new StringSplitter(info30002);
                final StringSplitter ss4 = new StringSplitter(info30001);
                final StringSplitter ss5 = new StringSplitter(info30000);
                final String modDescFoca = new String(getDescFoca().substring(0, getDescFoca().indexOf(59) + 1));
                FmcUplinkDes.message = "Qs393=" + modDescFoca + "6000;" + info30003.substring(0, info30003.indexOf(64)) + ';' + ss2.splitBetween('@', '(', 1, 1, false, false) + ";12000;" + info30002.substring(0, info30002.indexOf(64)) + ';' + ss3.splitBetween('@', '(', 1, 1, false, false) + ";24000;" + info30001.substring(0, info30001.indexOf(64)) + ';' + ss4.splitBetween('@', '(', 1, 1, false, false) + ";30000;" + info30000.substring(0, info30000.indexOf(64)) + ';' + ss5.splitBetween('@', '(', 1, 1, false, false) + ';';
                if (FmcUplinkDes.firstMessage) {
                    FmcUplinkDes.lastMessage = "Qs393=" + getDescFoca();
                    FmcUplinkDes.firstMessage = false;
                }
                if (FmcUplinkDes.lastMessage.equals(FmcUplinkDes.message)) {
                    FmcUplinkDes.lockSameData.lock();
                    FmcUplinkDes.sameData = true;
                    FmcUplinkDes.lockSameData.unlock();
                }
                else {
                    FmcUplinkDes.lockSameData.lock();
                    FmcUplinkDes.sameData = false;
                    FmcUplinkDes.lockSameData.unlock();
                }
                FmcUplinkDes.lastMessage = FmcUplinkDes.message;
            }
            else {
                FmcUplinkDes.sendOk = false;
                StatusMonitor.setFmcUplinkTodDestNotFound(true);
            }
        }
    }
    
    private static void sendData() {
        if (FmcUplinkDes.sendOk && StatusMonitor.getAloftWxRunning()) {
            SocketClientPSXMain.send(FmcUplinkDes.message);
        }
        FmcUplinkDes.sendOk = true;
    }
    
    private static String getQs393() {
        FmcUplinkDes.lockQs393.lock();
        final String temp = new String(FmcUplinkDes.Qs393);
        FmcUplinkDes.lockQs393.unlock();
        return temp;
    }
    
    private static String getQi242() {
        FmcUplinkDes.lockQi242.lock();
        final String temp = new String(FmcUplinkDes.Qi242);
        FmcUplinkDes.lockQi242.unlock();
        return temp;
    }
}
