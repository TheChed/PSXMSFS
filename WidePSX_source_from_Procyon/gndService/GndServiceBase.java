// 
// Decompiled by Procyon v0.5.36
// 

package gndService;

import network.BridgeSimConnect;
import gui.TabGndService;
import javax.sound.sampled.Control;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.Line;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import network.SocketClientPSXMain;
import util.StringSplitter;
import java.util.Calendar;
import java.util.TimeZone;
import util.StatusMonitor;
import simBridge.SimBridgeDoors;
import network.DataFromPsxMain;
import util.ObservableData;
import java.util.concurrent.locks.ReentrantLock;
import javax.sound.sampled.Clip;
import java.util.concurrent.locks.Lock;
import javax.swing.JLabel;
import util.ObserverData;

public class GndServiceBase implements ObserverData
{
    private static JLabel lblDynStatus;
    private static String Qi0;
    private static String Qs123;
    private static String Qi257;
    private static String Qh178;
    private static String Qi132;
    private static String Qi174;
    private static String Qi109;
    private static String Qi110;
    private static String Qi111;
    private static String Qh288;
    private static String Qh410;
    private static String Qh411;
    private static String Qh412;
    private static String Qs438;
    private static String Qi220;
    private static String Qs483;
    private static String Qs121;
    private static String Qi179;
    private static String Qi180;
    private static String Qi181;
    private static String Qs113;
    private static String Qs114;
    private static String Qs115;
    private static String Qs429;
    private static String Qh177;
    private static String Qh392;
    private static String Qh213;
    private static String Qh184;
    private static String Qh397;
    private static final Lock lockQi0;
    private static final Lock lockQs123;
    private static final Lock lockQi257;
    private static final Lock lockQh178;
    private static final Lock lockQi132;
    private static final Lock lockQi174;
    private static final Lock lockQi109;
    private static final Lock lockQi110;
    private static final Lock lockQi111;
    private static final Lock lockQh288;
    private static final Lock lockQh410;
    private static final Lock lockQh411;
    private static final Lock lockQh412;
    private static final Lock lockQs438;
    private static final Lock lockQi220;
    private static final Lock lockQs483;
    private static final Lock lockQs121;
    private static final Lock lockQi179;
    private static final Lock lockQi180;
    private static final Lock lockQi181;
    private static final Lock lockQs113;
    private static final Lock lockQs114;
    private static final Lock lockQs115;
    private static final Lock lockQs429;
    private static final Lock lockQh177;
    private static final Lock lockQh392;
    private static final Lock lockQh213;
    private static final Lock lockQh184;
    private static final Lock lockQh397;
    private static String units;
    private static int minutesToBlkOut;
    private static int planedZfw;
    private static int planedFuel;
    private static int pushBackStraightLen;
    private static boolean onGround;
    private static boolean refuelStatus;
    private static boolean refuelCompleted;
    private static boolean conversation;
    private static boolean cockpitCallFlt;
    private static boolean cockpitCallCab;
    private static final Lock lockUnits;
    private static final Lock lockMinutesToBlkOut;
    private static final Lock lockPlanedZfw;
    private static final Lock lockPlanedFuel;
    private static final Lock lockPushBackStraightLen;
    private static final Lock lockOnGround;
    private static final Lock lockRefuelStatus;
    private static final Lock lockRefuelCompleted;
    private static final Lock lockConversation;
    private static final Lock lockCockpitCallFlt;
    private static final Lock lockCockpitCallCab;
    private static int modDoorComBits;
    private static int modDoorOpenBits;
    private static int modDoorManBits;
    private static final Lock lockModDoorComBits;
    private static final Lock lockModDoorOpenBits;
    private static final Lock lockModDoorManBits;
    private static boolean releaseScenGen;
    private static final Lock LOCK_RELEASE_SCENGEN;
    private static Clip clip;
    
    static {
        GndServiceBase.lblDynStatus = new JLabel();
        GndServiceBase.Qi0 = new String("");
        GndServiceBase.Qs123 = new String("");
        GndServiceBase.Qi257 = new String("");
        GndServiceBase.Qh178 = new String("");
        GndServiceBase.Qi132 = new String("");
        GndServiceBase.Qi174 = new String("");
        GndServiceBase.Qi109 = new String("");
        GndServiceBase.Qi110 = new String("");
        GndServiceBase.Qi111 = new String("");
        GndServiceBase.Qh288 = new String("");
        GndServiceBase.Qh410 = new String("");
        GndServiceBase.Qh411 = new String("");
        GndServiceBase.Qh412 = new String("");
        GndServiceBase.Qs438 = new String("");
        GndServiceBase.Qi220 = new String("");
        GndServiceBase.Qs483 = new String("");
        GndServiceBase.Qs121 = new String("");
        GndServiceBase.Qi179 = new String("");
        GndServiceBase.Qi180 = new String("");
        GndServiceBase.Qi181 = new String("");
        GndServiceBase.Qs113 = new String("");
        GndServiceBase.Qs114 = new String("");
        GndServiceBase.Qs115 = new String("");
        GndServiceBase.Qs429 = new String("");
        GndServiceBase.Qh177 = new String("");
        GndServiceBase.Qh392 = new String("");
        GndServiceBase.Qh213 = new String("");
        GndServiceBase.Qh184 = new String("");
        GndServiceBase.Qh397 = new String("");
        lockQi0 = new ReentrantLock();
        lockQs123 = new ReentrantLock();
        lockQi257 = new ReentrantLock();
        lockQh178 = new ReentrantLock();
        lockQi132 = new ReentrantLock();
        lockQi174 = new ReentrantLock();
        lockQi109 = new ReentrantLock();
        lockQi110 = new ReentrantLock();
        lockQi111 = new ReentrantLock();
        lockQh288 = new ReentrantLock();
        lockQh410 = new ReentrantLock();
        lockQh411 = new ReentrantLock();
        lockQh412 = new ReentrantLock();
        lockQs438 = new ReentrantLock();
        lockQi220 = new ReentrantLock();
        lockQs483 = new ReentrantLock();
        lockQs121 = new ReentrantLock();
        lockQi179 = new ReentrantLock();
        lockQi180 = new ReentrantLock();
        lockQi181 = new ReentrantLock();
        lockQs113 = new ReentrantLock();
        lockQs114 = new ReentrantLock();
        lockQs115 = new ReentrantLock();
        lockQs429 = new ReentrantLock();
        lockQh177 = new ReentrantLock();
        lockQh392 = new ReentrantLock();
        lockQh213 = new ReentrantLock();
        lockQh184 = new ReentrantLock();
        lockQh397 = new ReentrantLock();
        GndServiceBase.units = new String("");
        GndServiceBase.minutesToBlkOut = 0;
        GndServiceBase.planedZfw = 0;
        GndServiceBase.planedFuel = 0;
        GndServiceBase.pushBackStraightLen = 0;
        GndServiceBase.onGround = true;
        GndServiceBase.refuelStatus = false;
        GndServiceBase.refuelCompleted = false;
        GndServiceBase.conversation = false;
        GndServiceBase.cockpitCallFlt = false;
        GndServiceBase.cockpitCallCab = false;
        lockUnits = new ReentrantLock();
        lockMinutesToBlkOut = new ReentrantLock();
        lockPlanedZfw = new ReentrantLock();
        lockPlanedFuel = new ReentrantLock();
        lockPushBackStraightLen = new ReentrantLock();
        lockOnGround = new ReentrantLock();
        lockRefuelStatus = new ReentrantLock();
        lockRefuelCompleted = new ReentrantLock();
        lockConversation = new ReentrantLock();
        lockCockpitCallFlt = new ReentrantLock();
        lockCockpitCallCab = new ReentrantLock();
        GndServiceBase.modDoorComBits = 0;
        GndServiceBase.modDoorOpenBits = 0;
        GndServiceBase.modDoorManBits = 0;
        lockModDoorComBits = new ReentrantLock();
        lockModDoorOpenBits = new ReentrantLock();
        lockModDoorManBits = new ReentrantLock();
        GndServiceBase.releaseScenGen = false;
        LOCK_RELEASE_SCENGEN = new ReentrantLock();
    }
    
    @Override
    public void updateObservers(final ObservableData argObs, final Object argId, final Object argData) {
        if (argObs instanceof DataFromPsxMain) {
            final String s;
            switch (s = (String)argId) {
                case "Qi0": {
                    GndServiceBase.lockQi0.lock();
                    GndServiceBase.Qi0 = (String)argData;
                    GndServiceBase.lockQi0.unlock();
                    break;
                }
                case "Qh82": {
                    GndServiceRadios.pttDispatch(true, false, false, false, false, false);
                    break;
                }
                case "Qh93": {
                    GndServiceRadios.pttDispatch(false, true, false, false, false, false);
                    break;
                }
                case "Qh177": {
                    GndServiceBase.lockQh177.lock();
                    GndServiceBase.Qh177 = (String)argData;
                    GndServiceBase.lockQh177.unlock();
                    dispatchLtTaxiCutoff1();
                    break;
                }
                case "Qh178": {
                    GndServiceBase.lockQh178.lock();
                    GndServiceBase.Qh178 = (String)argData;
                    GndServiceBase.lockQh178.unlock();
                    break;
                }
                case "Qh184": {
                    GndServiceBase.lockQh184.lock();
                    GndServiceBase.Qh184 = (String)argData;
                    GndServiceBase.lockQh184.unlock();
                    break;
                }
                case "Qh213": {
                    GndServiceBase.lockQh213.lock();
                    GndServiceBase.Qh213 = (String)argData;
                    GndServiceBase.lockQh213.unlock();
                    break;
                }
                case "Qh288": {
                    GndServiceBase.lockQh288.lock();
                    GndServiceBase.Qh288 = (String)argData;
                    GndServiceBase.lockQh288.unlock();
                    break;
                }
                case "Qh392": {
                    GndServiceBase.lockQh392.lock();
                    GndServiceBase.Qh392 = (String)argData;
                    GndServiceBase.lockQh392.unlock();
                    dispatchLtTaxiCutoff1();
                    break;
                }
                case "Qh397": {
                    GndServiceBase.lockQh397.lock();
                    GndServiceBase.Qh397 = (String)argData;
                    GndServiceBase.lockQh397.unlock();
                    break;
                }
                case "Qh410": {
                    GndServiceBase.lockQh410.lock();
                    GndServiceBase.Qh410 = (String)argData;
                    GndServiceBase.lockQh410.unlock();
                    dispatchQh410();
                    break;
                }
                case "Qh411": {
                    GndServiceBase.lockQh411.lock();
                    GndServiceBase.Qh411 = (String)argData;
                    GndServiceBase.lockQh411.unlock();
                    dispatchQh411();
                    break;
                }
                case "Qh412": {
                    GndServiceBase.lockQh412.lock();
                    GndServiceBase.Qh412 = (String)argData;
                    GndServiceBase.lockQh412.unlock();
                    dispatchQh412();
                    break;
                }
                case "Qi109": {
                    GndServiceBase.lockQi109.lock();
                    GndServiceBase.Qi109 = (String)argData;
                    GndServiceBase.lockQi109.unlock();
                    break;
                }
                case "Qi110": {
                    GndServiceBase.lockQi110.lock();
                    GndServiceBase.Qi110 = (String)argData;
                    GndServiceBase.lockQi110.unlock();
                    break;
                }
                case "Qi111": {
                    GndServiceBase.lockQi111.lock();
                    GndServiceBase.Qi111 = (String)argData;
                    GndServiceBase.lockQi111.unlock();
                    break;
                }
                case "Qi132": {
                    GndServiceBase.lockQi132.lock();
                    GndServiceBase.Qi132 = (String)argData;
                    GndServiceBase.lockQi132.unlock();
                    break;
                }
                case "Qi174": {
                    GndServiceBase.lockQi174.lock();
                    GndServiceBase.Qi174 = (String)argData;
                    GndServiceBase.lockQi174.unlock();
                    break;
                }
                case "Qi179": {
                    GndServiceBase.lockQi179.lock();
                    GndServiceBase.Qi179 = (String)argData;
                    GndServiceBase.lockQi179.unlock();
                    sendModDoorComBits();
                    break;
                }
                case "Qi180": {
                    GndServiceBase.lockQi180.lock();
                    GndServiceBase.Qi180 = (String)argData;
                    GndServiceBase.lockQi180.unlock();
                    SimBridgeDoors.dispatchQi180(getAcftVersion(), getPsxDoorOpenBits());
                    sendModDoorOpenBits();
                    break;
                }
                case "Qi181": {
                    GndServiceBase.lockQi181.lock();
                    GndServiceBase.Qi181 = (String)argData;
                    GndServiceBase.lockQi181.unlock();
                    sendModDoorManBits();
                    break;
                }
                case "Qi220": {
                    GndServiceBase.lockQi220.lock();
                    GndServiceBase.Qi220 = (String)argData;
                    GndServiceBase.lockQi220.unlock();
                    dispatchQi220();
                    break;
                }
                case "Qi257": {
                    GndServiceBase.lockQi257.lock();
                    GndServiceBase.Qi257 = (String)argData;
                    GndServiceBase.lockQi257.unlock();
                    setOnGround();
                    break;
                }
                case "Qs113": {
                    GndServiceBase.lockQs113.lock();
                    GndServiceBase.Qs113 = (String)argData;
                    GndServiceBase.lockQs113.unlock();
                    break;
                }
                case "Qs114": {
                    GndServiceBase.lockQs114.lock();
                    GndServiceBase.Qs114 = (String)argData;
                    GndServiceBase.lockQs114.unlock();
                    break;
                }
                case "Qs115": {
                    GndServiceBase.lockQs115.lock();
                    GndServiceBase.Qs115 = (String)argData;
                    GndServiceBase.lockQs115.unlock();
                    break;
                }
                case "Qs121": {
                    GndServiceBase.lockQs121.lock();
                    GndServiceBase.Qs121 = (String)argData;
                    GndServiceBase.lockQs121.unlock();
                    break;
                }
                case "Qs123": {
                    GndServiceBase.lockQs123.lock();
                    GndServiceBase.Qs123 = (String)argData;
                    GndServiceBase.lockQs123.unlock();
                    break;
                }
                case "Qs429": {
                    GndServiceBase.lockQs429.lock();
                    GndServiceBase.Qs429 = (String)argData;
                    GndServiceBase.lockQs429.unlock();
                    break;
                }
                case "Qs438": {
                    GndServiceBase.lockQs438.lock();
                    GndServiceBase.Qs438 = (String)argData;
                    GndServiceBase.lockQs438.unlock();
                    break;
                }
                case "Qs483": {
                    GndServiceBase.lockQs483.lock();
                    GndServiceBase.Qs483 = (String)argData;
                    GndServiceBase.lockQs483.unlock();
                    break;
                }
                default:
                    break;
            }
        }
    }
    
    public static int getPsxDoorOpenBits() {
        GndServiceBase.lockQi180.lock();
        final String temp = GndServiceBase.Qi180.substring(GndServiceBase.Qi180.indexOf(61) + 1);
        GndServiceBase.lockQi180.unlock();
        return Integer.parseInt(temp);
    }
    
    public static void passJLabel(final JLabel argDynLabel) {
        GndServiceBase.lblDynStatus = argDynLabel;
    }
    
    public static void setDynText(final String argText) {
        GndServiceBase.lblDynStatus.setText(argText);
    }
    
    public static int getAcftVersion() {
        GndServiceBase.lockQi0.lock();
        String temp = new String(GndServiceBase.Qi0);
        GndServiceBase.lockQi0.unlock();
        temp = temp.substring(temp.indexOf(61) + 1);
        final int version = Integer.parseInt(temp);
        if (version == 0 || version == 2 || version == 4) {
            return 0;
        }
        return 1;
    }
    
    public static void setUnits(final String argUnits) {
        GndServiceBase.lockUnits.lock();
        GndServiceBase.units = argUnits;
        GndServiceBase.lockUnits.unlock();
    }
    
    public static void setPlanedFuel(final String argFuel) {
        int temp = 0;
        try {
            temp = Integer.parseInt(argFuel);
        }
        catch (NumberFormatException e) {
            if (!argFuel.equals("INOP")) {
                StatusMonitor.setGndServiceInvalidTextValues(true);
            }
            if (StatusMonitor.getGndServiceSimplified() && argFuel.isEmpty()) {
                StatusMonitor.setGndServiceInvalidTextValues(false);
            }
            return;
        }
        if (getUnits().equals("Kgs") || getUnits().equals("Hyb")) {
            temp *= (int)0.454;
        }
        GndServiceBase.lockPlanedFuel.lock();
        GndServiceBase.planedFuel = temp;
        GndServiceBase.lockPlanedFuel.unlock();
    }
    
    public static void setPlanedZfw(final String argZfw) {
        double temp = 0.0;
        try {
            temp = Double.parseDouble(argZfw);
        }
        catch (NumberFormatException e) {
            if (!argZfw.equals("INOP")) {
                StatusMonitor.setGndServiceInvalidTextValues(true);
            }
            if (StatusMonitor.getGndServiceSimplified() && argZfw.isEmpty()) {
                StatusMonitor.setGndServiceInvalidTextValues(false);
            }
            return;
        }
        if (getUnits().equals("Kgs") || getUnits().equals("Hyb")) {
            temp *= 0.454;
        }
        GndServiceBase.lockPlanedZfw.lock();
        GndServiceBase.planedZfw = (int)temp;
        GndServiceBase.lockPlanedZfw.unlock();
    }
    
    public static void setPushBackStraightLen(final String argLen) {
        int temp = 0;
        try {
            temp = Integer.parseInt(argLen);
        }
        catch (NumberFormatException e) {
            if (!argLen.equals("INOP")) {
                StatusMonitor.setGndServiceInvalidTextValues(true);
            }
            return;
        }
        GndServiceBase.lockPushBackStraightLen.lock();
        GndServiceBase.pushBackStraightLen = temp;
        GndServiceBase.lockPushBackStraightLen.unlock();
    }
    
    public static void setMinutesToBlkOut(final String argOutTime) {
        int outHours = 0;
        int outMinutes = 0;
        GndServiceBase.lockQs123.lock();
        String temp = new String(GndServiceBase.Qs123);
        GndServiceBase.lockQs123.unlock();
        temp = temp.substring(temp.indexOf(61) + 1);
        final long psxTimeSinceEpoch = Long.parseLong(temp);
        final String outTime = new String(argOutTime);
        if (outTime.length() != 4) {
            StatusMonitor.setGndServiceInvalidTextValues(true);
            return;
        }
        try {
            outHours = Integer.parseInt(outTime.substring(0, 2));
            outMinutes = Integer.parseInt(outTime.substring(2));
        }
        catch (NumberFormatException e) {
            if (!argOutTime.equals("INOP")) {
                StatusMonitor.setGndServiceInvalidTextValues(true);
            }
            return;
        }
        final Calendar psxCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        psxCalendar.clear();
        psxCalendar.setTimeInMillis(psxTimeSinceEpoch);
        final int psxYear = psxCalendar.get(1);
        final int psxMonth = psxCalendar.get(2);
        final int psxDay = psxCalendar.get(5);
        final Calendar outCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        outCalendar.clear();
        outCalendar.set(psxYear, psxMonth, psxDay, outHours, outMinutes);
        final long outTimeSinceEpoch = outCalendar.getTimeInMillis();
        int tempMinsToBlkOut = (int)((outTimeSinceEpoch - psxTimeSinceEpoch) / 60000L + 1L);
        if (tempMinsToBlkOut < 0) {
            tempMinsToBlkOut += 1440;
        }
        GndServiceBase.lockMinutesToBlkOut.lock();
        GndServiceBase.minutesToBlkOut = tempMinsToBlkOut;
        GndServiceBase.lockMinutesToBlkOut.unlock();
    }
    
    public static boolean getOnGround() {
        GndServiceBase.lockOnGround.lock();
        final boolean temp = GndServiceBase.onGround;
        GndServiceBase.lockOnGround.unlock();
        return temp;
    }
    
    protected static void sendStaticPosition() {
        final StringSplitter ss = new StringSplitter(getQs121());
        final String HEAL = ss.splitBetween(';', ';', 2, 4, false, true);
        final String POS = ss.splitFrom(';', 5, true);
        final String IMMOBILEPOS = "Qs121=0;0;" + HEAL + "0" + POS;
        SocketClientPSXMain.send(IMMOBILEPOS);
    }
    
    protected static void playSoundClip(final boolean argFltCom, final String argSoundFile) {
        try {
            final File soundFile = new File(argSoundFile);
            final AudioInputStream stream = AudioSystem.getAudioInputStream(soundFile);
            final AudioFormat format = stream.getFormat();
            final DataLine.Info info = new DataLine.Info(Clip.class, format);
            (GndServiceBase.clip = (Clip)AudioSystem.getLine(info)).open(stream);
            GndServiceBase.clip.start();
            final long CLIPENDTIME = System.currentTimeMillis() + GndServiceBase.clip.getMicrosecondLength() / 1000L;
            while (System.currentTimeMillis() < CLIPENDTIME) {
                setSoundClipVolume(argFltCom);
                Thread.sleep(100L);
            }
        }
        catch (UnsupportedAudioFileException | IOException ex2) {
            final Exception ex;
            final Exception e = ex;
            e.printStackTrace();
        }
        catch (LineUnavailableException e2) {
            e2.printStackTrace();
        }
        catch (InterruptedException e3) {
            e3.printStackTrace();
        }
    }
    
    private static void setSoundClipVolume(final boolean argFltCom) {
        String temp = "";
        if (StatusMonitor.getGndServiceVolumeAcp().equals("L")) {
            GndServiceBase.lockQs113.lock();
            temp = GndServiceBase.Qs113;
            GndServiceBase.lockQs113.unlock();
        }
        else if (StatusMonitor.getGndServiceVolumeAcp().equals("C")) {
            GndServiceBase.lockQs114.lock();
            temp = GndServiceBase.Qs114;
            GndServiceBase.lockQs114.unlock();
        }
        else {
            GndServiceBase.lockQs115.lock();
            temp = GndServiceBase.Qs115;
            GndServiceBase.lockQs115.unlock();
        }
        final StringSplitter ss = new StringSplitter(temp);
        double value = 0.0;
        if (argFltCom) {
            value = Double.parseDouble(ss.splitBetween(';', ';', 3, 4, false, false));
        }
        else {
            value = Double.parseDouble(ss.splitBetween(';', ';', 4, 5, false, false));
        }
        value /= 50.0;
        if (value < 0.0) {
            value = 0.0;
        }
        else if (value > 2.0) {
            value = 2.0;
        }
        final FloatControl gainControl = (FloatControl)GndServiceBase.clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20.0f * (float)Math.log10(value));
    }
    
    protected static boolean getBeacon() {
        GndServiceBase.lockQh178.lock();
        String temp = new String(GndServiceBase.Qh178);
        GndServiceBase.lockQh178.unlock();
        temp = temp.substring(temp.indexOf(61) + 1);
        return Integer.parseInt(temp) == 1 || Integer.parseInt(temp) == -1;
    }
    
    protected static int getPsxElecSysBits() {
        GndServiceBase.lockQi132.lock();
        String temp = new String(GndServiceBase.Qi132);
        GndServiceBase.lockQi132.unlock();
        temp = temp.substring(temp.indexOf(61) + 1);
        return Integer.parseInt(temp);
    }
    
    protected static int getPsxBleedAirBits() {
        GndServiceBase.lockQi174.lock();
        String temp = new String(GndServiceBase.Qi174);
        GndServiceBase.lockQi174.unlock();
        temp = temp.substring(temp.indexOf(61) + 1);
        return Integer.parseInt(temp);
    }
    
    protected static int getPsxDoorComBits() {
        GndServiceBase.lockQi179.lock();
        final String temp = GndServiceBase.Qi179.substring(GndServiceBase.Qi179.indexOf(61) + 1);
        GndServiceBase.lockQi179.unlock();
        return Integer.parseInt(temp);
    }
    
    protected static int getPsxDoorManBits() {
        GndServiceBase.lockQi181.lock();
        final String temp = GndServiceBase.Qi181.substring(GndServiceBase.Qi181.indexOf(61) + 1);
        GndServiceBase.lockQi181.unlock();
        return Integer.parseInt(temp);
    }
    
    protected static void setModDoorComBits(final int argBits) {
        GndServiceBase.lockModDoorComBits.lock();
        GndServiceBase.modDoorComBits = argBits;
        GndServiceBase.lockModDoorComBits.unlock();
    }
    
    protected static void setModDoorOpenBits(final int argBits) {
        GndServiceBase.lockModDoorOpenBits.lock();
        GndServiceBase.modDoorOpenBits = argBits;
        GndServiceBase.lockModDoorOpenBits.unlock();
    }
    
    protected static void setModDoorManBits(final int argBits) {
        GndServiceBase.lockModDoorManBits.lock();
        GndServiceBase.modDoorManBits = argBits;
        GndServiceBase.lockModDoorManBits.unlock();
    }
    
    protected static void sendModDoorComBits() {
        GndServiceBase.lockModDoorComBits.lock();
        final int temp = GndServiceBase.modDoorComBits;
        GndServiceBase.lockModDoorComBits.unlock();
        if (temp != 0) {
            SocketClientPSXMain.send("Qi179=" + String.valueOf(temp));
        }
    }
    
    protected static void sendModDoorOpenBits() {
        GndServiceBase.lockModDoorOpenBits.lock();
        final int temp = GndServiceBase.modDoorOpenBits;
        GndServiceBase.lockModDoorOpenBits.unlock();
        if (temp != 0) {
            SocketClientPSXMain.send("Qi180=" + String.valueOf(temp));
        }
    }
    
    protected static void sendModDoorManBits() {
        GndServiceBase.lockModDoorManBits.lock();
        final int temp = GndServiceBase.modDoorManBits;
        GndServiceBase.lockModDoorManBits.unlock();
        if (temp != 0) {
            SocketClientPSXMain.send("Qi181=" + String.valueOf(temp));
        }
    }
    
    protected static int getPsxOat() {
        GndServiceBase.lockQs483.lock();
        final String temp = new String(GndServiceBase.Qs483);
        GndServiceBase.lockQs483.unlock();
        final StringSplitter ss = new StringSplitter(temp);
        try {
            final int psxOat = Integer.parseInt(ss.splitBetween(';', ';', 1, 2, false, false));
            return psxOat / 10;
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    protected static boolean getRefuelingCompleted() {
        GndServiceBase.lockQi220.lock();
        String temp = new String(GndServiceBase.Qi220);
        GndServiceBase.lockQi220.unlock();
        temp = temp.substring(temp.indexOf(61) + 1);
        final int value = Integer.parseInt(temp);
        return value == 0;
    }
    
    protected static int getObsAudioSys() {
        GndServiceBase.lockQh288.lock();
        String temp = new String(GndServiceBase.Qh288);
        GndServiceBase.lockQh288.unlock();
        temp = temp.substring(temp.indexOf(61) + 1);
        return Integer.parseInt(temp);
    }
    
    protected static int getMicSelectL() {
        GndServiceBase.lockQi109.lock();
        String temp = new String(GndServiceBase.Qi109);
        GndServiceBase.lockQi109.unlock();
        temp = temp.substring(temp.indexOf(61) + 1);
        return Integer.parseInt(temp);
    }
    
    protected static int getMicSelectC() {
        GndServiceBase.lockQi110.lock();
        String temp = new String(GndServiceBase.Qi110);
        GndServiceBase.lockQi110.unlock();
        temp = temp.substring(temp.indexOf(61) + 1);
        return Integer.parseInt(temp);
    }
    
    protected static int getMicSelectR() {
        GndServiceBase.lockQi111.lock();
        String temp = new String(GndServiceBase.Qi111);
        GndServiceBase.lockQi111.unlock();
        temp = temp.substring(temp.indexOf(61) + 1);
        return Integer.parseInt(temp);
    }
    
    public static String getQs121() {
        GndServiceBase.lockQs121.lock();
        final String temp = new String(GndServiceBase.Qs121);
        GndServiceBase.lockQs121.unlock();
        return temp;
    }
    
    protected static String getQi0() {
        GndServiceBase.lockQi0.lock();
        final String temp = GndServiceBase.Qi0;
        GndServiceBase.lockQi0.unlock();
        return temp;
    }
    
    protected static String getUnits() {
        GndServiceBase.lockUnits.lock();
        final String temp = new String(GndServiceBase.units);
        GndServiceBase.lockUnits.unlock();
        return temp;
    }
    
    protected static String getSoundsPath(final boolean argDeparture) {
        final String fileSeparator = new String(System.getProperty("file.separator"));
        final String pathFrench = new String("sound" + fileSeparator + "gndService" + fileSeparator + "french" + fileSeparator);
        final String pathUs1 = new String("sound" + fileSeparator + "gndService" + fileSeparator + "american1" + fileSeparator);
        final String pathEnglish = new String("sound" + fileSeparator + "gndService" + fileSeparator + "english" + fileSeparator);
        final String pathGerman = new String("sound" + fileSeparator + "gndService" + fileSeparator + "german" + fileSeparator);
        String arptIcao = "";
        String path = "";
        if (argDeparture) {
            arptIcao = TabGndService.getDepIcao().toUpperCase();
        }
        else {
            arptIcao = TabGndService.getArrIcao().toUpperCase();
        }
        if (arptIcao.startsWith("K") || arptIcao.startsWith("P")) {
            path = pathUs1;
        }
        else if (arptIcao.startsWith("EG")) {
            path = pathEnglish;
        }
        else if (arptIcao.startsWith("ED") || arptIcao.startsWith("LS") || arptIcao.startsWith("LO")) {
            path = pathGerman;
        }
        else {
            path = pathFrench;
        }
        return path;
    }
    
    protected static void setConversation(final boolean argValue) {
        GndServiceBase.lockConversation.lock();
        GndServiceBase.conversation = argValue;
        GndServiceBase.lockConversation.unlock();
    }
    
    protected static boolean getConversation() {
        GndServiceBase.lockConversation.lock();
        final boolean temp = GndServiceBase.conversation;
        GndServiceBase.lockConversation.unlock();
        return temp;
    }
    
    protected static void setCockpitCallFlt(final boolean argValue) {
        GndServiceBase.lockCockpitCallFlt.lock();
        GndServiceBase.cockpitCallFlt = argValue;
        GndServiceBase.lockCockpitCallFlt.unlock();
    }
    
    protected static boolean getCockpitCallFlt() {
        GndServiceBase.lockCockpitCallFlt.lock();
        final boolean temp = GndServiceBase.cockpitCallFlt;
        GndServiceBase.lockCockpitCallFlt.unlock();
        return temp;
    }
    
    protected static void setCockpitCallCab(final boolean argValue) {
        GndServiceBase.lockCockpitCallCab.lock();
        GndServiceBase.cockpitCallCab = argValue;
        GndServiceBase.lockCockpitCallCab.unlock();
    }
    
    protected static boolean getCockpitCallCab() {
        GndServiceBase.lockCockpitCallCab.lock();
        final boolean temp = GndServiceBase.cockpitCallCab;
        GndServiceBase.lockCockpitCallCab.unlock();
        return temp;
    }
    
    protected static void setRefuelStatus(final boolean argStatus) {
        GndServiceBase.lockRefuelStatus.lock();
        GndServiceBase.refuelStatus = argStatus;
        GndServiceBase.lockRefuelStatus.unlock();
    }
    
    protected static boolean getRefuelStatus() {
        GndServiceBase.lockRefuelStatus.lock();
        final boolean temp = GndServiceBase.refuelStatus;
        GndServiceBase.lockRefuelStatus.unlock();
        return temp;
    }
    
    protected static void setRefuelCompleted(final boolean argCompleted) {
        GndServiceBase.lockRefuelCompleted.lock();
        GndServiceBase.refuelCompleted = argCompleted;
        GndServiceBase.lockRefuelCompleted.unlock();
    }
    
    protected static boolean getRefuelCompleted() {
        GndServiceBase.lockRefuelCompleted.lock();
        final boolean temp = GndServiceBase.refuelCompleted;
        GndServiceBase.lockRefuelCompleted.unlock();
        return temp;
    }
    
    protected static void sendZfw() {
        int releaseZfw = getPlanedZfw();
        if (getUnits().equals("Kgs") || getUnits().equals("Hyb")) {
            releaseZfw /= (int)0.45359;
        }
        SocketClientPSXMain.send("Qi123=" + String.valueOf(releaseZfw));
    }
    
    protected static void sendReleaseFuel() {
        GndServiceBase.lockQs438.lock();
        final StringSplitter ss = new StringSplitter(GndServiceBase.Qs438);
        String temp = GndServiceBase.Qs438;
        GndServiceBase.lockQs438.unlock();
        temp = temp.substring(temp.indexOf(61) + 2);
        final StringSplitter ss2 = new StringSplitter(temp);
        final int fuelQty = (Integer.parseInt(ss2.splitTo(';', 1, false)) + Integer.parseInt(ss2.splitBetween(';', ';', 1, 2, false, false)) + Integer.parseInt(ss2.splitBetween(';', ';', 2, 3, false, false)) + Integer.parseInt(ss2.splitBetween(';', ';', 3, 4, false, false)) + Integer.parseInt(ss2.splitBetween(';', ';', 4, 5, false, false)) + Integer.parseInt(ss2.splitBetween(';', ';', 5, 6, false, false)) + Integer.parseInt(ss2.splitBetween(';', ';', 6, 7, false, false)) + Integer.parseInt(ss2.splitBetween(';', ';', 7, 8, false, false)) + Integer.parseInt(ss2.splitBetween(';', ';', 8, 9, false, false))) / 10;
        int releaseFuel = getPlanedFuel();
        if (getUnits().equals("Kgs") || getUnits().equals("Hyb")) {
            releaseFuel /= (int)0.45359;
        }
        final String firstPart = new String(ss.splitTo(';', 9, true));
        final String lastPart = new String(ss.splitFrom(';', 10, true));
        try {
            if (releaseFuel > fuelQty) {
                final String message = String.valueOf(firstPart) + String.valueOf(releaseFuel) + lastPart;
                SocketClientPSXMain.send(message);
                Thread.sleep(500L);
                SocketClientPSXMain.send(message);
                Thread.sleep(500L);
                SocketClientPSXMain.send(message);
                Thread.sleep(500L);
                SocketClientPSXMain.send(message);
            }
            else {
                final String message = "Qs438=d0;55115;55115;0;0;0;0;0;0;" + String.valueOf(releaseFuel) + lastPart;
                SocketClientPSXMain.send(message);
                Thread.sleep(500L);
                SocketClientPSXMain.send(message);
                Thread.sleep(500L);
                SocketClientPSXMain.send(message);
                Thread.sleep(500L);
                SocketClientPSXMain.send(message);
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    protected static int getMinutesToBlkOut() {
        GndServiceBase.lockMinutesToBlkOut.lock();
        final int temp = GndServiceBase.minutesToBlkOut;
        GndServiceBase.lockMinutesToBlkOut.unlock();
        return temp;
    }
    
    protected static int getPlanedZfw() {
        GndServiceBase.lockPlanedZfw.lock();
        final int temp = GndServiceBase.planedZfw;
        GndServiceBase.lockPlanedZfw.unlock();
        return temp;
    }
    
    protected static int getPlanedFuel() {
        GndServiceBase.lockPlanedFuel.lock();
        final int temp = GndServiceBase.planedFuel;
        GndServiceBase.lockPlanedFuel.unlock();
        return temp;
    }
    
    protected static int getPushBackStraightLen() {
        GndServiceBase.lockPushBackStraightLen.lock();
        int temp = GndServiceBase.pushBackStraightLen;
        GndServiceBase.lockPushBackStraightLen.unlock();
        if (getUnits().equals("Kgs")) {
            double tempDouble = temp;
            tempDouble *= 3.28084;
            temp = (int)tempDouble;
        }
        return temp;
    }
    
    private static void setOnGround() {
        GndServiceBase.lockQi257.lock();
        String temp = new String(GndServiceBase.Qi257);
        GndServiceBase.lockQi257.unlock();
        temp = temp.substring(temp.indexOf(61) + 1);
        if (Integer.parseInt(temp) == 1) {
            if (!getOnGround()) {
                try {
                    if (StatusMonitor.getGndServiceRunning()) {
                        if (!StatusMonitor.getGndServiceSimplified()) {
                            final GndServicePostFlt gndServicePostFlt = new GndServicePostFlt();
                            final Thread gndServicePostFltThread = new Thread(gndServicePostFlt);
                            gndServicePostFltThread.start();
                        }
                        else {
                            final GndServicePostFltSimplified gndServicePostFltSimplified = new GndServicePostFltSimplified();
                            final Thread gndServicePostFltSimplifiedThread = new Thread(gndServicePostFltSimplified);
                            gndServicePostFltSimplifiedThread.start();
                        }
                    }
                }
                catch (IllegalThreadStateException ex) {}
            }
            GndServiceBase.lockOnGround.lock();
            GndServiceBase.onGround = true;
            GndServiceBase.lockOnGround.unlock();
        }
        else {
            GndServiceBase.lockOnGround.lock();
            GndServiceBase.onGround = false;
            GndServiceBase.lockOnGround.unlock();
        }
    }
    
    private static void dispatchQh410() {
        GndServiceBase.lockQh410.lock();
        String temp = new String(GndServiceBase.Qh410);
        GndServiceBase.lockQh410.unlock();
        temp = temp.substring(temp.indexOf(61) + 1);
        final int value = Integer.parseInt(temp);
        if (getObsAudioSys() == 0 || getObsAudioSys() == 1) {
            if (value == 26) {
                GndServiceRadios.setFromRtAcpL(true);
            }
            if (value == 27) {
                GndServiceRadios.setFromIntAcpL(true);
            }
            if (value == -1) {
                if (GndServiceRadios.getFromRtAcpL()) {
                    GndServiceRadios.pttDispatch(false, false, true, false, false, true);
                    GndServiceRadios.setFromRtAcpL(false);
                }
                if (GndServiceRadios.getFromIntAcpL()) {
                    GndServiceRadios.pttDispatch(false, false, true, false, false, false);
                    GndServiceRadios.setFromIntAcpL(false);
                }
            }
        }
    }
    
    private static void dispatchQh411() {
        GndServiceBase.lockQh411.lock();
        String temp = new String(GndServiceBase.Qh411);
        GndServiceBase.lockQh411.unlock();
        temp = temp.substring(temp.indexOf(61) + 1);
        final int value = Integer.parseInt(temp);
        if (value == 26) {
            GndServiceRadios.setFromRtAcpC(true);
        }
        if (value == 27) {
            GndServiceRadios.setFromIntAcpC(true);
        }
        if (value == -1) {
            if (GndServiceRadios.getFromRtAcpC()) {
                GndServiceRadios.pttDispatch(false, false, false, true, false, true);
                GndServiceRadios.setFromRtAcpC(false);
            }
            if (GndServiceRadios.getFromIntAcpC()) {
                GndServiceRadios.pttDispatch(false, false, false, true, false, false);
                GndServiceRadios.setFromIntAcpC(false);
            }
        }
    }
    
    private static void dispatchQh412() {
        GndServiceBase.lockQh412.lock();
        String temp = new String(GndServiceBase.Qh412);
        GndServiceBase.lockQh412.unlock();
        temp = temp.substring(temp.indexOf(61) + 1);
        final int value = Integer.parseInt(temp);
        if (getObsAudioSys() == 0 || getObsAudioSys() == -1) {
            if (value == 26) {
                GndServiceRadios.setFromRtAcpR(true);
            }
            if (value == 27) {
                GndServiceRadios.setFromIntAcpR(true);
            }
            if (value == -1) {
                if (GndServiceRadios.getFromRtAcpR()) {
                    GndServiceRadios.pttDispatch(false, false, false, false, true, true);
                    GndServiceRadios.setFromRtAcpR(false);
                }
                if (GndServiceRadios.getFromIntAcpR()) {
                    GndServiceRadios.pttDispatch(false, false, false, false, true, false);
                    GndServiceRadios.setFromIntAcpR(false);
                }
            }
        }
    }
    
    private static void dispatchQi220() {
        GndServiceBase.lockQi220.lock();
        String temp = new String(GndServiceBase.Qi220);
        GndServiceBase.lockQi220.unlock();
        temp = temp.substring(temp.indexOf(61) + 1);
        if (temp.equals("0")) {
            setRefuelCompleted(true);
        }
        else {
            setRefuelCompleted(false);
        }
    }
    
    protected static String getQs429() {
        GndServiceBase.lockQs429.lock();
        final String temp = GndServiceBase.Qs429;
        GndServiceBase.lockQs429.unlock();
        return temp;
    }
    
    public static void dispatchLtTaxiCutoff1() {
        try {
            GndServiceBase.lockQh177.lock();
            final String taxiLtString = new String(GndServiceBase.Qh177);
            GndServiceBase.lockQh177.unlock();
            GndServiceBase.lockQh392.lock();
            final String cutoff1String = new String(GndServiceBase.Qh392);
            GndServiceBase.lockQh392.unlock();
            if (taxiLtString.equals("Qh177=1") && cutoff1String.equals("Qh392=1")) {
                GndServiceBase.LOCK_RELEASE_SCENGEN.lock();
                GndServiceBase.releaseScenGen = false;
                GndServiceBase.LOCK_RELEASE_SCENGEN.unlock();
                if (StatusMonitor.getSimBridgeIsRunning() && StatusMonitor.getGndServiceExtPush()) {
                    BridgeSimConnect.releaseSceneryGen(false);
                }
            }
            if (cutoff1String.equals("Qh392=0")) {
                GndServiceBase.LOCK_RELEASE_SCENGEN.lock();
                GndServiceBase.releaseScenGen = true;
                GndServiceBase.LOCK_RELEASE_SCENGEN.unlock();
                if (StatusMonitor.getSimBridgeIsRunning() && StatusMonitor.getGndServiceExtPush()) {
                    BridgeSimConnect.releaseSceneryGen(true);
                }
            }
        }
        catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }
    
    public static boolean getReleaseScenGen() {
        GndServiceBase.LOCK_RELEASE_SCENGEN.lock();
        final boolean temp = GndServiceBase.releaseScenGen;
        GndServiceBase.LOCK_RELEASE_SCENGEN.unlock();
        return temp;
    }
    
    public static void setReleaseScenGen(final boolean argRelease) {
        GndServiceBase.LOCK_RELEASE_SCENGEN.lock();
        GndServiceBase.releaseScenGen = argRelease;
        GndServiceBase.LOCK_RELEASE_SCENGEN.unlock();
    }
    
    protected static boolean getIrsLNav() {
        GndServiceBase.lockQh213.lock();
        final String temp = GndServiceBase.Qh213;
        GndServiceBase.lockQh213.unlock();
        return temp.equals("Qh213=2");
    }
    
    protected static boolean getHydDem1Auto() {
        GndServiceBase.lockQh184.lock();
        final String temp = GndServiceBase.Qh184;
        GndServiceBase.lockQh184.unlock();
        return temp.equals("Qh184=2");
    }
    
    protected static boolean getParkBrkLev() {
        GndServiceBase.lockQh397.lock();
        final String temp = GndServiceBase.Qh397;
        GndServiceBase.lockQh397.unlock();
        return temp.equals("Qh397=1");
    }
    
    public static String getQh177() {
        GndServiceBase.lockQh177.lock();
        final String temp = GndServiceBase.Qh177;
        GndServiceBase.lockQh177.unlock();
        return temp;
    }
}
