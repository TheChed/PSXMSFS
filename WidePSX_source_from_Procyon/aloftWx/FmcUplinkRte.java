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

public class FmcUplinkRte implements ObserverData
{
    private static boolean sendOk;
    private static String message;
    private static String Qi242;
    private static final Lock lockQi242;
    
    static {
        FmcUplinkRte.sendOk = true;
        FmcUplinkRte.message = new String("");
        FmcUplinkRte.Qi242 = new String("");
        lockQi242 = new ReentrantLock();
    }
    
    @Override
    public void updateObservers(final ObservableData argObs, final Object argId, final Object argData) {
        if (argObs instanceof DataFromPsxMain) {
            final String s;
            switch (s = (String)argId) {
                case "Qi242": {
                    FmcUplinkRte.lockQi242.lock();
                    FmcUplinkRte.Qi242 = (String)argData;
                    FmcUplinkRte.lockQi242.unlock();
                    try {
                        dispatchUplinkBits();
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
    
    private static void dispatchUplinkBits() throws IOException {
        final String tempQi242 = getQi242();
        final String temp = new String(tempQi242.substring(tempQi242.indexOf(61) + 1));
        final int bits = Integer.parseInt(temp);
        if (bits != 0 && StatusMonitor.getAloftWxRunning()) {
            if (StatusMonitor.getAloftWxFileLoaded()) {
                StatusMonitor.setFmcUplinkNoWxFileLoaded(false);
                if ((bits & 0x1) != 0x0) {
                    prepareData();
                }
                else if ((bits & 0x2) != 0x0) {
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
        String route = new String("");
        String leg = new String("");
        String legWithoutWxPart1 = new String("");
        String legWithoutWxPart2 = new String("");
        String legWpt = new String("");
        String temp = new String("");
        final MutableString wptWxInfos = new MutableString("");
        String info30000 = new String("");
        String info30001 = new String("");
        String info30002 = new String("");
        String info30003 = new String("");
        if (FmcUplinkBase.getFmcRteLoaded() == 0) {
            FmcUplinkRte.sendOk = false;
            StatusMonitor.setFmcUplinkNoPsxRteLoaded(true);
        }
        else {
            StatusMonitor.setFmcUplinkNoPsxRteLoaded(false);
        }
        if (FmcUplinkRte.sendOk) {
            FmcUplinkRte.message = "Qs375=";
            if (FmcUplinkBase.getFmcRteLoaded() != 0) {
                route = FmcUplinkBase.getRteInfo();
            }
            StringSplitter ss = new StringSplitter(route);
            FmcUplinkRte.message = String.valueOf(FmcUplinkRte.message) + ss.splitTo(';', 5, true) + "30000;34000;39000;44000" + ss.splitBetween(';', '#', 9, 1, true, true);
            for (route = route.substring(route.indexOf(35) + 1); route.indexOf(59) != -1; route = route.substring(route.indexOf(59) + 1)) {
                leg = route.substring(0, route.indexOf(59) + 1);
                legWpt = leg.substring(0, leg.indexOf(39));
                legWpt = legWpt.substring(10);
                if (FileAsnWx.searchWptInfoDyn(legWpt, wptWxInfos)) {
                    if (leg.indexOf(64) != -1) {
                        legWithoutWxPart1 = leg.substring(0, leg.indexOf(64));
                        legWithoutWxPart2 = leg.substring(leg.indexOf(36) + 1);
                    }
                    else {
                        ss = new StringSplitter(leg);
                        legWithoutWxPart1 = ss.splitTo('\'', 7, true);
                        legWithoutWxPart2 = ss.splitFrom('\'', 7, false);
                    }
                    ss = new StringSplitter(wptWxInfos.get());
                    info30000 = ss.splitBetween('\t', '\t', 9, 10, false, false);
                    info30001 = ss.splitBetween('\t', '\t', 10, 11, false, false);
                    info30002 = ss.splitBetween('\t', '\t', 11, 12, false, false);
                    info30003 = ss.splitBetween('\t', '\t', 12, 13, false, false);
                    final StringSplitter ss2 = new StringSplitter(info30000);
                    final StringSplitter ss3 = new StringSplitter(info30001);
                    final StringSplitter ss4 = new StringSplitter(info30002);
                    final StringSplitter ss5 = new StringSplitter(info30003);
                    leg = String.valueOf(legWithoutWxPart1) + '@' + info30000.substring(0, info30000.indexOf(64)) + '/' + ss2.splitBetween('@', '(', 1, 1, false, false) + '\'' + info30001.substring(0, info30001.indexOf(64)) + '/' + ss3.splitBetween('@', '(', 1, 1, false, false) + '\'' + info30002.substring(0, info30002.indexOf(64)) + '/' + ss4.splitBetween('@', '(', 1, 1, false, false) + '\'' + info30003.substring(0, info30003.indexOf(64)) + '/' + ss5.splitBetween('@', '(', 1, 1, false, false) + '\'' + "30000/" + ss2.splitBetween('(', ')', 1, 1, false, false) + '$' + legWithoutWxPart2;
                }
                else if (leg.indexOf(64) != -1) {
                    temp = leg.substring(leg.indexOf(36) + 1);
                    leg = leg.substring(0, leg.indexOf(64));
                    leg = String.valueOf(leg) + temp;
                }
                FmcUplinkRte.message = String.valueOf(FmcUplinkRte.message) + leg;
            }
        }
    }
    
    private static void sendData() {
        if (FmcUplinkRte.sendOk && StatusMonitor.getAloftWxRunning()) {
            SocketClientPSXMain.send(FmcUplinkRte.message);
        }
        FmcUplinkRte.sendOk = true;
    }
    
    private static String getQi242() {
        FmcUplinkRte.lockQi242.lock();
        final String temp = new String(FmcUplinkRte.Qi242);
        FmcUplinkRte.lockQi242.unlock();
        return temp;
    }
}
