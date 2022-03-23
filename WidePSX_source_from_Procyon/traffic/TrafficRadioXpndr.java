// 
// Decompiled by Procyon v0.5.36
// 

package traffic;

import network.BridgeSimConnect;
import util.StringSplitter;
import network.SocketClientPSXMain;
import util.StatusMonitor;
import java.io.IOException;
import network.DataFromPsxMain;
import util.ObservableData;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.JLabel;
import java.util.concurrent.locks.Lock;
import java.awt.Color;
import util.ObserverData;

public class TrafficRadioXpndr implements ObserverData, Runnable
{
    private static final Color warning;
    private static final Color neutral;
    private static String Qs121;
    private static String Qs112;
    private static String Qs117;
    private static String Qs118;
    private static String Qh414;
    private static final Lock lockQs121;
    private static final Lock lockQs112;
    private static final Lock lockQs117;
    private static final Lock lockQs118;
    private static final Lock lockQh414;
    private static TrafficAiAcft aiAcft1;
    private static TrafficAiAcft aiAcft2;
    private static TrafficAiAcft aiAcft3;
    private static TrafficAiAcft aiAcft4;
    private static TrafficAiAcft aiAcft5;
    private static TrafficAiAcft aiAcft6;
    private static TrafficAiAcft aiAcft7;
    private static JLabel lblAi1;
    private static JLabel lblAi2;
    private static JLabel lblAi3;
    private static JLabel lblAi4;
    private static JLabel lblAi5;
    private static JLabel lblAi6;
    private static JLabel lblAi7;
    private static Thread trafficThread;
    
    static {
        warning = new Color(255, 0, 0);
        neutral = new Color(0, 0, 0);
        TrafficRadioXpndr.Qs121 = new String("");
        TrafficRadioXpndr.Qs112 = new String("");
        TrafficRadioXpndr.Qs117 = new String("");
        TrafficRadioXpndr.Qs118 = new String("");
        TrafficRadioXpndr.Qh414 = new String("");
        lockQs121 = new ReentrantLock();
        lockQs112 = new ReentrantLock();
        lockQs117 = new ReentrantLock();
        lockQs118 = new ReentrantLock();
        lockQh414 = new ReentrantLock();
        TrafficRadioXpndr.aiAcft1 = new TrafficAiAcft(47.0);
        TrafficRadioXpndr.aiAcft2 = new TrafficAiAcft(46.0);
        TrafficRadioXpndr.aiAcft3 = new TrafficAiAcft(45.0);
        TrafficRadioXpndr.aiAcft4 = new TrafficAiAcft(44.0);
        TrafficRadioXpndr.aiAcft5 = new TrafficAiAcft(43.0);
        TrafficRadioXpndr.aiAcft6 = new TrafficAiAcft(42.0);
        TrafficRadioXpndr.aiAcft7 = new TrafficAiAcft(41.0);
        TrafficRadioXpndr.lblAi1 = new JLabel();
        TrafficRadioXpndr.lblAi2 = new JLabel();
        TrafficRadioXpndr.lblAi3 = new JLabel();
        TrafficRadioXpndr.lblAi4 = new JLabel();
        TrafficRadioXpndr.lblAi5 = new JLabel();
        TrafficRadioXpndr.lblAi6 = new JLabel();
        TrafficRadioXpndr.lblAi7 = new JLabel();
    }
    
    public TrafficRadioXpndr() {
        (TrafficRadioXpndr.trafficThread = new Thread(this)).start();
    }
    
    public static void passJLabel(final JLabel... argLabel) {
        TrafficRadioXpndr.lblAi1 = argLabel[0];
        TrafficRadioXpndr.lblAi2 = argLabel[1];
        TrafficRadioXpndr.lblAi3 = argLabel[2];
        TrafficRadioXpndr.lblAi4 = argLabel[3];
        TrafficRadioXpndr.lblAi5 = argLabel[4];
        TrafficRadioXpndr.lblAi6 = argLabel[5];
        TrafficRadioXpndr.lblAi7 = argLabel[6];
    }
    
    @Override
    public void updateObservers(final ObservableData argObs, final Object argId, final Object argData) {
        if (argObs instanceof DataFromPsxMain) {
            final String s;
            switch (s = (String)argId) {
                case "Qh414": {
                    TrafficRadioXpndr.lockQh414.lock();
                    TrafficRadioXpndr.Qh414 = (String)argData;
                    TrafficRadioXpndr.lockQh414.unlock();
                    dispatchQh414();
                    break;
                }
                case "Qs112": {
                    TrafficRadioXpndr.lockQs112.lock();
                    TrafficRadioXpndr.Qs112 = (String)argData;
                    TrafficRadioXpndr.lockQs112.unlock();
                    updateSimFreqs();
                    break;
                }
                case "Qs117": {
                    TrafficRadioXpndr.lockQs117.lock();
                    TrafficRadioXpndr.Qs117 = (String)argData;
                    TrafficRadioXpndr.lockQs117.unlock();
                    dispatchQs117();
                    break;
                }
                case "Qs118": {
                    TrafficRadioXpndr.lockQs118.lock();
                    TrafficRadioXpndr.Qs118 = (String)argData;
                    TrafficRadioXpndr.lockQs118.unlock();
                    updateSimXpndr();
                    break;
                }
                case "Qs121": {
                    TrafficRadioXpndr.lockQs121.lock();
                    TrafficRadioXpndr.Qs121 = (String)argData;
                    TrafficRadioXpndr.lockQs121.unlock();
                    break;
                }
                case "Qs448": {
                    try {
                        updateSimAltimeter((String)argData);
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
    
    public static void newAiReceived(final double argLat, final double argLon, final double argAlt, final double argTrack, final String argAtcType, final String argAtcId) {
        double distance = getDistance(argLat, argLon);
        final double aiAltitude = argAlt;
        final double upBoundary = getPsxAltitude() + 2700;
        final double lowBoundary = getPsxAltitude() - 2700;
        if (distance <= 40.0 && aiAltitude < upBoundary && aiAltitude > lowBoundary) {
            if (distance < TrafficRadioXpndr.aiAcft1.getDistance() && !TrafficRadioXpndr.aiAcft1.getHasData()) {
                TrafficRadioXpndr.aiAcft1.setLatitude(argLat);
                TrafficRadioXpndr.aiAcft1.setLongitude(argLon);
                TrafficRadioXpndr.aiAcft1.setAltitude(argAlt);
                TrafficRadioXpndr.aiAcft1.setTrack(argTrack);
                TrafficRadioXpndr.aiAcft1.setDistance(distance);
                TrafficRadioXpndr.aiAcft1.setHasData(true);
                TrafficRadioXpndr.aiAcft1.setAtcType(argAtcType);
                TrafficRadioXpndr.aiAcft1.setAtcId(argAtcId);
                distance = 300.0;
            }
            else if (TrafficRadioXpndr.aiAcft7.getHasData() && distance < TrafficRadioXpndr.aiAcft1.getDistance() && TrafficRadioXpndr.aiAcft2.getDistance() <= TrafficRadioXpndr.aiAcft1.getDistance() && TrafficRadioXpndr.aiAcft3.getDistance() <= TrafficRadioXpndr.aiAcft1.getDistance() && TrafficRadioXpndr.aiAcft4.getDistance() <= TrafficRadioXpndr.aiAcft1.getDistance() && TrafficRadioXpndr.aiAcft5.getDistance() <= TrafficRadioXpndr.aiAcft1.getDistance() && TrafficRadioXpndr.aiAcft6.getDistance() <= TrafficRadioXpndr.aiAcft1.getDistance() && TrafficRadioXpndr.aiAcft7.getDistance() <= TrafficRadioXpndr.aiAcft1.getDistance()) {
                TrafficRadioXpndr.aiAcft1.setLatitude(argLat);
                TrafficRadioXpndr.aiAcft1.setLongitude(argLon);
                TrafficRadioXpndr.aiAcft1.setAltitude(argAlt);
                TrafficRadioXpndr.aiAcft1.setTrack(argTrack);
                TrafficRadioXpndr.aiAcft1.setDistance(distance);
                TrafficRadioXpndr.aiAcft1.setHasData(true);
                TrafficRadioXpndr.aiAcft1.setAtcType(argAtcType);
                TrafficRadioXpndr.aiAcft1.setAtcId(argAtcId);
                distance = 300.0;
            }
            if (distance < TrafficRadioXpndr.aiAcft2.getDistance() && !TrafficRadioXpndr.aiAcft2.getHasData()) {
                TrafficRadioXpndr.aiAcft2.setLatitude(argLat);
                TrafficRadioXpndr.aiAcft2.setLongitude(argLon);
                TrafficRadioXpndr.aiAcft2.setAltitude(argAlt);
                TrafficRadioXpndr.aiAcft2.setTrack(argTrack);
                TrafficRadioXpndr.aiAcft2.setDistance(distance);
                TrafficRadioXpndr.aiAcft2.setHasData(true);
                TrafficRadioXpndr.aiAcft2.setAtcType(argAtcType);
                TrafficRadioXpndr.aiAcft2.setAtcId(argAtcId);
                distance = 300.0;
            }
            else if (TrafficRadioXpndr.aiAcft7.getHasData() && distance < TrafficRadioXpndr.aiAcft2.getDistance() && TrafficRadioXpndr.aiAcft1.getDistance() <= TrafficRadioXpndr.aiAcft2.getDistance() && TrafficRadioXpndr.aiAcft3.getDistance() <= TrafficRadioXpndr.aiAcft2.getDistance() && TrafficRadioXpndr.aiAcft4.getDistance() <= TrafficRadioXpndr.aiAcft2.getDistance() && TrafficRadioXpndr.aiAcft5.getDistance() <= TrafficRadioXpndr.aiAcft2.getDistance() && TrafficRadioXpndr.aiAcft6.getDistance() <= TrafficRadioXpndr.aiAcft2.getDistance() && TrafficRadioXpndr.aiAcft7.getDistance() <= TrafficRadioXpndr.aiAcft2.getDistance()) {
                TrafficRadioXpndr.aiAcft2.setLatitude(argLat);
                TrafficRadioXpndr.aiAcft2.setLongitude(argLon);
                TrafficRadioXpndr.aiAcft2.setAltitude(argAlt);
                TrafficRadioXpndr.aiAcft2.setTrack(argTrack);
                TrafficRadioXpndr.aiAcft2.setDistance(distance);
                TrafficRadioXpndr.aiAcft2.setHasData(true);
                TrafficRadioXpndr.aiAcft2.setAtcType(argAtcType);
                TrafficRadioXpndr.aiAcft2.setAtcId(argAtcId);
                distance = 300.0;
            }
            if (distance < TrafficRadioXpndr.aiAcft3.getDistance() && !TrafficRadioXpndr.aiAcft3.getHasData()) {
                TrafficRadioXpndr.aiAcft3.setLatitude(argLat);
                TrafficRadioXpndr.aiAcft3.setLongitude(argLon);
                TrafficRadioXpndr.aiAcft3.setAltitude(argAlt);
                TrafficRadioXpndr.aiAcft3.setTrack(argTrack);
                TrafficRadioXpndr.aiAcft3.setDistance(distance);
                TrafficRadioXpndr.aiAcft3.setHasData(true);
                TrafficRadioXpndr.aiAcft3.setAtcType(argAtcType);
                TrafficRadioXpndr.aiAcft3.setAtcId(argAtcId);
                distance = 300.0;
            }
            else if (TrafficRadioXpndr.aiAcft7.getHasData() && distance < TrafficRadioXpndr.aiAcft3.getDistance() && TrafficRadioXpndr.aiAcft1.getDistance() <= TrafficRadioXpndr.aiAcft3.getDistance() && TrafficRadioXpndr.aiAcft2.getDistance() <= TrafficRadioXpndr.aiAcft3.getDistance() && TrafficRadioXpndr.aiAcft4.getDistance() <= TrafficRadioXpndr.aiAcft3.getDistance() && TrafficRadioXpndr.aiAcft5.getDistance() <= TrafficRadioXpndr.aiAcft3.getDistance() && TrafficRadioXpndr.aiAcft6.getDistance() <= TrafficRadioXpndr.aiAcft3.getDistance() && TrafficRadioXpndr.aiAcft7.getDistance() <= TrafficRadioXpndr.aiAcft3.getDistance()) {
                TrafficRadioXpndr.aiAcft3.setLatitude(argLat);
                TrafficRadioXpndr.aiAcft3.setLongitude(argLon);
                TrafficRadioXpndr.aiAcft3.setAltitude(argAlt);
                TrafficRadioXpndr.aiAcft3.setTrack(argTrack);
                TrafficRadioXpndr.aiAcft3.setDistance(distance);
                TrafficRadioXpndr.aiAcft3.setHasData(true);
                TrafficRadioXpndr.aiAcft3.setAtcType(argAtcType);
                TrafficRadioXpndr.aiAcft3.setAtcId(argAtcId);
                distance = 300.0;
            }
            if (distance < TrafficRadioXpndr.aiAcft4.getDistance() && !TrafficRadioXpndr.aiAcft4.getHasData()) {
                TrafficRadioXpndr.aiAcft4.setLatitude(argLat);
                TrafficRadioXpndr.aiAcft4.setLongitude(argLon);
                TrafficRadioXpndr.aiAcft4.setAltitude(argAlt);
                TrafficRadioXpndr.aiAcft4.setTrack(argTrack);
                TrafficRadioXpndr.aiAcft4.setDistance(distance);
                TrafficRadioXpndr.aiAcft4.setHasData(true);
                TrafficRadioXpndr.aiAcft4.setAtcType(argAtcType);
                TrafficRadioXpndr.aiAcft4.setAtcId(argAtcId);
                distance = 300.0;
            }
            else if (TrafficRadioXpndr.aiAcft7.getHasData() && distance < TrafficRadioXpndr.aiAcft4.getDistance() && TrafficRadioXpndr.aiAcft1.getDistance() <= TrafficRadioXpndr.aiAcft4.getDistance() && TrafficRadioXpndr.aiAcft2.getDistance() <= TrafficRadioXpndr.aiAcft4.getDistance() && TrafficRadioXpndr.aiAcft3.getDistance() <= TrafficRadioXpndr.aiAcft4.getDistance() && TrafficRadioXpndr.aiAcft5.getDistance() <= TrafficRadioXpndr.aiAcft4.getDistance() && TrafficRadioXpndr.aiAcft6.getDistance() <= TrafficRadioXpndr.aiAcft4.getDistance() && TrafficRadioXpndr.aiAcft7.getDistance() <= TrafficRadioXpndr.aiAcft4.getDistance()) {
                TrafficRadioXpndr.aiAcft4.setLatitude(argLat);
                TrafficRadioXpndr.aiAcft4.setLongitude(argLon);
                TrafficRadioXpndr.aiAcft4.setAltitude(argAlt);
                TrafficRadioXpndr.aiAcft4.setTrack(argTrack);
                TrafficRadioXpndr.aiAcft4.setDistance(distance);
                TrafficRadioXpndr.aiAcft4.setHasData(true);
                TrafficRadioXpndr.aiAcft4.setAtcType(argAtcType);
                TrafficRadioXpndr.aiAcft4.setAtcId(argAtcId);
                distance = 300.0;
            }
            if (distance < TrafficRadioXpndr.aiAcft5.getDistance() && !TrafficRadioXpndr.aiAcft5.getHasData()) {
                TrafficRadioXpndr.aiAcft5.setLatitude(argLat);
                TrafficRadioXpndr.aiAcft5.setLongitude(argLon);
                TrafficRadioXpndr.aiAcft5.setAltitude(argAlt);
                TrafficRadioXpndr.aiAcft5.setTrack(argTrack);
                TrafficRadioXpndr.aiAcft5.setDistance(distance);
                TrafficRadioXpndr.aiAcft5.setHasData(true);
                TrafficRadioXpndr.aiAcft5.setAtcType(argAtcType);
                TrafficRadioXpndr.aiAcft5.setAtcId(argAtcId);
                distance = 300.0;
            }
            else if (TrafficRadioXpndr.aiAcft7.getHasData() && distance < TrafficRadioXpndr.aiAcft5.getDistance() && TrafficRadioXpndr.aiAcft1.getDistance() <= TrafficRadioXpndr.aiAcft5.getDistance() && TrafficRadioXpndr.aiAcft2.getDistance() <= TrafficRadioXpndr.aiAcft5.getDistance() && TrafficRadioXpndr.aiAcft3.getDistance() <= TrafficRadioXpndr.aiAcft5.getDistance() && TrafficRadioXpndr.aiAcft4.getDistance() <= TrafficRadioXpndr.aiAcft5.getDistance() && TrafficRadioXpndr.aiAcft6.getDistance() <= TrafficRadioXpndr.aiAcft5.getDistance() && TrafficRadioXpndr.aiAcft7.getDistance() <= TrafficRadioXpndr.aiAcft5.getDistance()) {
                TrafficRadioXpndr.aiAcft5.setLatitude(argLat);
                TrafficRadioXpndr.aiAcft5.setLongitude(argLon);
                TrafficRadioXpndr.aiAcft5.setAltitude(argAlt);
                TrafficRadioXpndr.aiAcft5.setTrack(argTrack);
                TrafficRadioXpndr.aiAcft5.setDistance(distance);
                TrafficRadioXpndr.aiAcft5.setHasData(true);
                TrafficRadioXpndr.aiAcft5.setAtcType(argAtcType);
                TrafficRadioXpndr.aiAcft5.setAtcId(argAtcId);
                distance = 300.0;
            }
            if (distance < TrafficRadioXpndr.aiAcft6.getDistance() && !TrafficRadioXpndr.aiAcft6.getHasData()) {
                TrafficRadioXpndr.aiAcft6.setLatitude(argLat);
                TrafficRadioXpndr.aiAcft6.setLongitude(argLon);
                TrafficRadioXpndr.aiAcft6.setAltitude(argAlt);
                TrafficRadioXpndr.aiAcft6.setTrack(argTrack);
                TrafficRadioXpndr.aiAcft6.setDistance(distance);
                TrafficRadioXpndr.aiAcft6.setHasData(true);
                TrafficRadioXpndr.aiAcft6.setAtcType(argAtcType);
                TrafficRadioXpndr.aiAcft6.setAtcId(argAtcId);
                distance = 300.0;
            }
            else if (TrafficRadioXpndr.aiAcft7.getHasData() && distance < TrafficRadioXpndr.aiAcft6.getDistance() && TrafficRadioXpndr.aiAcft1.getDistance() <= TrafficRadioXpndr.aiAcft6.getDistance() && TrafficRadioXpndr.aiAcft2.getDistance() <= TrafficRadioXpndr.aiAcft6.getDistance() && TrafficRadioXpndr.aiAcft3.getDistance() <= TrafficRadioXpndr.aiAcft6.getDistance() && TrafficRadioXpndr.aiAcft4.getDistance() <= TrafficRadioXpndr.aiAcft6.getDistance() && TrafficRadioXpndr.aiAcft5.getDistance() <= TrafficRadioXpndr.aiAcft6.getDistance() && TrafficRadioXpndr.aiAcft7.getDistance() <= TrafficRadioXpndr.aiAcft6.getDistance()) {
                TrafficRadioXpndr.aiAcft6.setLatitude(argLat);
                TrafficRadioXpndr.aiAcft6.setLongitude(argLon);
                TrafficRadioXpndr.aiAcft6.setAltitude(argAlt);
                TrafficRadioXpndr.aiAcft6.setTrack(argTrack);
                TrafficRadioXpndr.aiAcft6.setDistance(distance);
                TrafficRadioXpndr.aiAcft6.setHasData(true);
                TrafficRadioXpndr.aiAcft6.setAtcType(argAtcType);
                TrafficRadioXpndr.aiAcft6.setAtcId(argAtcId);
                distance = 300.0;
            }
            if (distance < TrafficRadioXpndr.aiAcft7.getDistance() && !TrafficRadioXpndr.aiAcft7.getHasData()) {
                TrafficRadioXpndr.aiAcft7.setLatitude(argLat);
                TrafficRadioXpndr.aiAcft7.setLongitude(argLon);
                TrafficRadioXpndr.aiAcft7.setAltitude(argAlt);
                TrafficRadioXpndr.aiAcft7.setTrack(argTrack);
                TrafficRadioXpndr.aiAcft7.setDistance(distance);
                TrafficRadioXpndr.aiAcft7.setHasData(true);
                TrafficRadioXpndr.aiAcft7.setAtcType(argAtcType);
                TrafficRadioXpndr.aiAcft7.setAtcId(argAtcId);
                distance = 300.0;
            }
            else if (TrafficRadioXpndr.aiAcft7.getHasData() && distance < TrafficRadioXpndr.aiAcft7.getDistance() && TrafficRadioXpndr.aiAcft1.getDistance() <= TrafficRadioXpndr.aiAcft7.getDistance() && TrafficRadioXpndr.aiAcft2.getDistance() <= TrafficRadioXpndr.aiAcft7.getDistance() && TrafficRadioXpndr.aiAcft3.getDistance() <= TrafficRadioXpndr.aiAcft7.getDistance() && TrafficRadioXpndr.aiAcft4.getDistance() <= TrafficRadioXpndr.aiAcft7.getDistance() && TrafficRadioXpndr.aiAcft5.getDistance() <= TrafficRadioXpndr.aiAcft7.getDistance() && TrafficRadioXpndr.aiAcft6.getDistance() <= TrafficRadioXpndr.aiAcft7.getDistance()) {
                TrafficRadioXpndr.aiAcft7.setLatitude(argLat);
                TrafficRadioXpndr.aiAcft7.setLongitude(argLon);
                TrafficRadioXpndr.aiAcft7.setAltitude(argAlt);
                TrafficRadioXpndr.aiAcft7.setTrack(argTrack);
                TrafficRadioXpndr.aiAcft7.setDistance(distance);
                TrafficRadioXpndr.aiAcft7.setHasData(true);
                TrafficRadioXpndr.aiAcft7.setAtcType(argAtcType);
                TrafficRadioXpndr.aiAcft7.setAtcId(argAtcId);
                distance = 300.0;
            }
        }
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(4000L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (StatusMonitor.getSimBridgeIsRunning()) {
                if (!StatusMonitor.getPsxMainIsConnected() || !StatusMonitor.getSimConnectIsConnected() || !StatusMonitor.getSimSetPsxTraffic()) {
                    TrafficRadioXpndr.lblAi1.setText("");
                    TrafficRadioXpndr.lblAi2.setText("");
                    TrafficRadioXpndr.lblAi3.setText("");
                    TrafficRadioXpndr.lblAi4.setText("");
                    TrafficRadioXpndr.lblAi5.setText("");
                    TrafficRadioXpndr.lblAi6.setText("");
                    TrafficRadioXpndr.lblAi7.setText("");
                }
                if (StatusMonitor.getSimSetPsxTraffic() && StatusMonitor.getSimConnectIsConnected() && StatusMonitor.getPsxMainIsConnected()) {
                    send();
                }
                else {
                    if (StatusMonitor.getSimSetPsxTraffic()) {
                        continue;
                    }
                    SocketClientPSXMain.send("Qi201=0");
                }
            }
        }
    }
    
    private static void send() {
        final String message = new String("Qs450=" + String.valueOf(TrafficRadioXpndr.aiAcft1.getLatitude()) + ";" + String.valueOf(TrafficRadioXpndr.aiAcft1.getLongitude()) + ";" + String.valueOf(TrafficRadioXpndr.aiAcft1.getAltitude()) + ";" + String.valueOf(TrafficRadioXpndr.aiAcft1.getTrack()) + ";" + String.valueOf(TrafficRadioXpndr.aiAcft2.getLatitude()) + ";" + String.valueOf(TrafficRadioXpndr.aiAcft2.getLongitude()) + ";" + String.valueOf(TrafficRadioXpndr.aiAcft2.getAltitude()) + ";" + String.valueOf(TrafficRadioXpndr.aiAcft2.getTrack()) + ";" + String.valueOf(TrafficRadioXpndr.aiAcft3.getLatitude()) + ";" + String.valueOf(TrafficRadioXpndr.aiAcft3.getLongitude()) + ";" + String.valueOf(TrafficRadioXpndr.aiAcft3.getAltitude()) + ";" + String.valueOf(TrafficRadioXpndr.aiAcft3.getTrack()) + ";" + String.valueOf(TrafficRadioXpndr.aiAcft4.getLatitude()) + ";" + String.valueOf(TrafficRadioXpndr.aiAcft4.getLongitude()) + ";" + String.valueOf(TrafficRadioXpndr.aiAcft4.getAltitude()) + ";" + String.valueOf(TrafficRadioXpndr.aiAcft4.getTrack()) + ";" + String.valueOf(TrafficRadioXpndr.aiAcft5.getLatitude()) + ";" + String.valueOf(TrafficRadioXpndr.aiAcft5.getLongitude()) + ";" + String.valueOf(TrafficRadioXpndr.aiAcft5.getAltitude()) + ";" + String.valueOf(TrafficRadioXpndr.aiAcft5.getTrack()) + ";" + String.valueOf(TrafficRadioXpndr.aiAcft6.getLatitude()) + ";" + String.valueOf(TrafficRadioXpndr.aiAcft6.getLongitude()) + ";" + String.valueOf(TrafficRadioXpndr.aiAcft6.getAltitude()) + ";" + String.valueOf(TrafficRadioXpndr.aiAcft6.getTrack()) + ";" + String.valueOf(TrafficRadioXpndr.aiAcft7.getLatitude()) + ";" + String.valueOf(TrafficRadioXpndr.aiAcft7.getLongitude()) + ";" + String.valueOf(TrafficRadioXpndr.aiAcft7.getAltitude()) + ";" + String.valueOf(TrafficRadioXpndr.aiAcft7.getTrack()) + ";");
        SocketClientPSXMain.send("Qi201=1");
        SocketClientPSXMain.send(message);
        final float d1 = (float)TrafficRadioXpndr.aiAcft1.getDistance();
        final float d2 = (float)TrafficRadioXpndr.aiAcft2.getDistance();
        final float d3 = (float)TrafficRadioXpndr.aiAcft3.getDistance();
        final float d4 = (float)TrafficRadioXpndr.aiAcft4.getDistance();
        final float d5 = (float)TrafficRadioXpndr.aiAcft5.getDistance();
        final float d6 = (float)TrafficRadioXpndr.aiAcft6.getDistance();
        final float d7 = (float)TrafficRadioXpndr.aiAcft7.getDistance();
        if (TrafficRadioXpndr.aiAcft1.getHasData()) {
            TrafficRadioXpndr.lblAi1.setText(String.valueOf(TrafficRadioXpndr.aiAcft1.getAtcId()) + " , type " + TrafficRadioXpndr.aiAcft1.getAtcType() + " is at " + TrafficRadioXpndr.aiAcft1.getAltitude() / 10L + " ft and " + d1 + " NM.");
            if (TrafficRadioXpndr.aiAcft1.getDistance() < 3.0) {
                TrafficRadioXpndr.lblAi1.setForeground(TrafficRadioXpndr.warning);
            }
            else {
                TrafficRadioXpndr.lblAi1.setForeground(TrafficRadioXpndr.neutral);
            }
        }
        else {
            TrafficRadioXpndr.lblAi1.setText("");
        }
        if (TrafficRadioXpndr.aiAcft2.getHasData()) {
            TrafficRadioXpndr.lblAi2.setText(String.valueOf(TrafficRadioXpndr.aiAcft2.getAtcId()) + " , type " + TrafficRadioXpndr.aiAcft2.getAtcType() + " is at " + TrafficRadioXpndr.aiAcft2.getAltitude() / 10L + " ft and " + d2 + " NM.");
            if (TrafficRadioXpndr.aiAcft2.getDistance() < 3.0) {
                TrafficRadioXpndr.lblAi2.setForeground(TrafficRadioXpndr.warning);
            }
            else {
                TrafficRadioXpndr.lblAi2.setForeground(TrafficRadioXpndr.neutral);
            }
        }
        else {
            TrafficRadioXpndr.lblAi2.setText("");
        }
        if (TrafficRadioXpndr.aiAcft3.getHasData()) {
            TrafficRadioXpndr.lblAi3.setText(String.valueOf(TrafficRadioXpndr.aiAcft3.getAtcId()) + " , type " + TrafficRadioXpndr.aiAcft3.getAtcType() + " is at " + TrafficRadioXpndr.aiAcft3.getAltitude() / 10L + " ft and " + d3 + " NM.");
            if (TrafficRadioXpndr.aiAcft3.getDistance() < 3.0) {
                TrafficRadioXpndr.lblAi3.setForeground(TrafficRadioXpndr.warning);
            }
            else {
                TrafficRadioXpndr.lblAi3.setForeground(TrafficRadioXpndr.neutral);
            }
        }
        else {
            TrafficRadioXpndr.lblAi3.setText("");
        }
        if (TrafficRadioXpndr.aiAcft4.getHasData()) {
            TrafficRadioXpndr.lblAi4.setText(String.valueOf(TrafficRadioXpndr.aiAcft4.getAtcId()) + " , type " + TrafficRadioXpndr.aiAcft4.getAtcType() + " is at " + TrafficRadioXpndr.aiAcft4.getAltitude() / 10L + " ft and " + d4 + " NM.");
            if (TrafficRadioXpndr.aiAcft4.getDistance() < 3.0) {
                TrafficRadioXpndr.lblAi4.setForeground(TrafficRadioXpndr.warning);
            }
            else {
                TrafficRadioXpndr.lblAi4.setForeground(TrafficRadioXpndr.neutral);
            }
        }
        else {
            TrafficRadioXpndr.lblAi4.setText("");
        }
        if (TrafficRadioXpndr.aiAcft5.getHasData()) {
            TrafficRadioXpndr.lblAi5.setText(String.valueOf(TrafficRadioXpndr.aiAcft5.getAtcId()) + " , type " + TrafficRadioXpndr.aiAcft5.getAtcType() + " is at " + TrafficRadioXpndr.aiAcft5.getAltitude() / 10L + " ft and " + d5 + " NM.");
            if (TrafficRadioXpndr.aiAcft5.getDistance() < 3.0) {
                TrafficRadioXpndr.lblAi5.setForeground(TrafficRadioXpndr.warning);
            }
            else {
                TrafficRadioXpndr.lblAi5.setForeground(TrafficRadioXpndr.neutral);
            }
        }
        else {
            TrafficRadioXpndr.lblAi5.setText("");
        }
        if (TrafficRadioXpndr.aiAcft6.getHasData()) {
            TrafficRadioXpndr.lblAi6.setText(String.valueOf(TrafficRadioXpndr.aiAcft6.getAtcId()) + " , type " + TrafficRadioXpndr.aiAcft6.getAtcType() + " is at " + TrafficRadioXpndr.aiAcft6.getAltitude() / 10L + " ft and " + d6 + " NM.");
            if (TrafficRadioXpndr.aiAcft6.getDistance() < 3.0) {
                TrafficRadioXpndr.lblAi6.setForeground(TrafficRadioXpndr.warning);
            }
            else {
                TrafficRadioXpndr.lblAi6.setForeground(TrafficRadioXpndr.neutral);
            }
        }
        else {
            TrafficRadioXpndr.lblAi6.setText("");
        }
        if (TrafficRadioXpndr.aiAcft7.getHasData()) {
            TrafficRadioXpndr.lblAi7.setText(String.valueOf(TrafficRadioXpndr.aiAcft7.getAtcId()) + " , type " + TrafficRadioXpndr.aiAcft7.getAtcType() + " is at " + TrafficRadioXpndr.aiAcft7.getAltitude() / 10L + " ft and " + d7 + " NM.");
            if (TrafficRadioXpndr.aiAcft7.getDistance() < 3.0) {
                TrafficRadioXpndr.lblAi7.setForeground(TrafficRadioXpndr.warning);
            }
            else {
                TrafficRadioXpndr.lblAi7.setForeground(TrafficRadioXpndr.neutral);
            }
        }
        else {
            TrafficRadioXpndr.lblAi7.setText("");
        }
        TrafficRadioXpndr.aiAcft1.reset();
        TrafficRadioXpndr.aiAcft1.setDistance(47.0);
        TrafficRadioXpndr.aiAcft2.reset();
        TrafficRadioXpndr.aiAcft2.setDistance(46.0);
        TrafficRadioXpndr.aiAcft3.reset();
        TrafficRadioXpndr.aiAcft3.setDistance(45.0);
        TrafficRadioXpndr.aiAcft4.reset();
        TrafficRadioXpndr.aiAcft4.setDistance(44.0);
        TrafficRadioXpndr.aiAcft5.reset();
        TrafficRadioXpndr.aiAcft5.setDistance(43.0);
        TrafficRadioXpndr.aiAcft6.reset();
        TrafficRadioXpndr.aiAcft6.setDistance(42.0);
        TrafficRadioXpndr.aiAcft7.reset();
        TrafficRadioXpndr.aiAcft7.setDistance(41.0);
    }
    
    private static int getPsxAltitude() {
        final String temp = new String(getQs121());
        final StringSplitter ss = new StringSplitter(temp);
        try {
            final int tempReturn = Integer.parseInt(ss.splitBetween(';', ';', 3, 4, false, false)) / 1000;
            return tempReturn;
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }
    
    private static double getPsxLatitude() {
        final StringSplitter ss = new StringSplitter(getQs121());
        try {
            final Double temp = Double.parseDouble(ss.splitBetween(';', ';', 5, 6, false, false));
            return temp;
        }
        catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    private static double getPsxLongitude() {
        final StringSplitter ss = new StringSplitter(getQs121());
        try {
            final Double temp = Double.parseDouble(ss.splitFrom(';', 6, false));
            return temp;
        }
        catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    private static double getDistance(final double argLatAi, final double argLonAi) {
        final double a = Math.sin((argLatAi - getPsxLatitude()) / 2.0) * Math.sin((argLatAi - getPsxLatitude()) / 2.0) + Math.cos(getPsxLatitude()) * Math.cos(argLatAi) * Math.sin((argLonAi - getPsxLongitude()) / 2.0) * Math.sin((argLonAi - getPsxLongitude()) / 2.0);
        final double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));
        final double d = 6371000.0 * c;
        return d * 5.39957E-4;
    }
    
    private static void updateSimFreqs() {
        if (StatusMonitor.getSimBridgeIsRunning() && StatusMonitor.getPsxSetSimComXpndrAlt() && StatusMonitor.getPsxMainIsConnected() && StatusMonitor.getSimConnectIsConnected()) {
            try {
                int com1 = 0;
                int com2 = 0;
                final StringSplitter ss = new StringSplitter(getQs112());
                String temp = new String(ss.splitBetween('=', ';', 1, 1, false, false));
                if (temp.length() == 6) {
                    com1 = Integer.parseInt(temp.substring(1, 5), 16);
                }
                temp = new String(ss.splitBetween(';', ';', 2, 3, false, false));
                if (temp.length() == 6) {
                    com2 = Integer.parseInt(temp.substring(1, 5), 16);
                }
                if (com1 != 0) {
                    BridgeSimConnect.setCom1ActiveFreq(com1);
                }
                if (com2 != 0) {
                    BridgeSimConnect.setCom2ActiveFreq(com2);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private static void updateSimXpndr() {
        if (StatusMonitor.getSimBridgeIsRunning() && StatusMonitor.getPsxSetSimComXpndrAlt() && StatusMonitor.getPsxMainIsConnected() && StatusMonitor.getSimConnectIsConnected()) {
            String temp = new String(getQs118());
            temp = temp.substring(temp.indexOf(61) + 1);
            final int code = Integer.parseInt(temp.substring(0, 4), 16);
            try {
                BridgeSimConnect.setXpndrCode(code);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static void updateSimAltimeter(final String arg) throws IOException {
        final StringSplitter ss = new StringSplitter(arg);
        final double qnhDouble = Double.parseDouble(ss.splitBetween(';', ';', 3, 4, false, false)) / 100.0;
        final int std = Math.abs(Integer.parseInt(ss.splitBetween(';', ';', 4, 5, false, false)));
        int qnh = (int)Math.round(qnhDouble);
        if (std == 2 || std == 3) {
            qnh = 1013;
        }
        if (StatusMonitor.getSimBridgeIsRunning() && StatusMonitor.getPsxSetSimComXpndrAlt()) {
            BridgeSimConnect.updateAltimeter(qnh);
        }
    }
    
    private static void dispatchQh414() {
        TrafficRadioXpndr.lockQh414.lock();
        String temp = TrafficRadioXpndr.Qh414;
        TrafficRadioXpndr.lockQh414.unlock();
        try {
            temp = temp.substring(temp.indexOf(61) + 1);
            final int value = Integer.parseInt(temp);
            if (value == 9 && StatusMonitor.getSimBridgeIsRunning() && StatusMonitor.getPsxSetSimComXpndrAlt()) {
                BridgeSimConnect.toggleXpndrIdent();
            }
        }
        catch (Exception e) {}
    }
    
    private static void dispatchQs117() {
        TrafficRadioXpndr.lockQs117.lock();
        final StringSplitter ss = new StringSplitter(TrafficRadioXpndr.Qs117);
        TrafficRadioXpndr.lockQs117.unlock();
        try {
            final int value = Integer.parseInt(ss.splitFrom(';', 3, false));
            if (StatusMonitor.getSimBridgeIsRunning() && StatusMonitor.getPsxSetSimComXpndrAlt()) {
                if (value == 0) {
                    BridgeSimConnect.setXpndrMode(1);
                }
                else {
                    BridgeSimConnect.setXpndrMode(0);
                }
            }
        }
        catch (Exception e) {}
    }
    
    private static String getQs121() {
        TrafficRadioXpndr.lockQs121.lock();
        final String temp = new String(TrafficRadioXpndr.Qs121);
        TrafficRadioXpndr.lockQs121.unlock();
        return temp;
    }
    
    private static String getQs112() {
        TrafficRadioXpndr.lockQs112.lock();
        final String temp = new String(TrafficRadioXpndr.Qs112);
        TrafficRadioXpndr.lockQs112.unlock();
        return temp;
    }
    
    private static String getQs118() {
        TrafficRadioXpndr.lockQs118.lock();
        final String temp = new String(TrafficRadioXpndr.Qs118);
        TrafficRadioXpndr.lockQs118.unlock();
        return temp;
    }
}
