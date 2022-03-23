// 
// Decompiled by Procyon v0.5.36
// 

package network;

import flightsim.simconnect.SimObjectType;
import flightsim.simconnect.recv.RecvEvent;
import flightsim.simconnect.recv.EventHandler;
import flightsim.simconnect.recv.DispatcherTask;
import flightsim.simconnect.SimConnectDataType;
import simBridge.SimBridgeTimeSync;
import simBridge.SimBridgeOffsetPos;
import simBridge.SimBridgeBase;
import gui.TabSceneryGen;
import util.PlaceBearingDistance;
import traffic.TrafficRadioXpndr;
import flightsim.simconnect.recv.RecvSimObjectDataByType;
import flightsim.simconnect.recv.RecvException;
import java.awt.Component;
import javax.swing.JOptionPane;
import flightsim.simconnect.recv.RecvQuit;
import flightsim.simconnect.NotificationPriority;
import flightsim.simconnect.recv.RecvOpen;
import java.io.IOException;
import util.StatusMonitor;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;
import flightsim.simconnect.SimConnect;
import flightsim.simconnect.recv.ExceptionHandler;
import flightsim.simconnect.recv.SimObjectDataTypeHandler;
import flightsim.simconnect.recv.QuitHandler;
import flightsim.simconnect.recv.OpenHandler;

public class BridgeSimConnect implements OpenHandler, QuitHandler, SimObjectDataTypeHandler, ExceptionHandler
{
    private static String ip;
    private static int port;
    private static SimConnect sc;
    private static final double RAD2NM = 3437.746770784939;
    private static final double NM2RAD = 2.908882086657216E-4;
    private static final double MT2FT = 3.28084;
    private static final double DISTANCE = 0.0153412920184464;
    private static double ambientOat;
    private static double ambientWindDir;
    private static double ambientWindVel;
    private static boolean parkBrkSet;
    private static boolean simOnGround;
    private static int[] simTime;
    private static final Lock lockAmbientOat;
    private static final Lock lockAmbientWindDir;
    private static final Lock lockAmbientWindVel;
    private static final Lock lockParkBrkSet;
    private static final Lock lockSimOnGround;
    private static final Lock lockSimTime;
    private static final Lock lockSimGndAlt;
    private static int simLightState;
    private static final Lock lockSimLightState;
    private static String userAcftId;
    private static final Lock lockUserAcftId;
    private static boolean liftDecompArmed;
    private static boolean liftDecompInProgress;
    private static double staticCompRatio;
    private static double liftDecompArmedPitch;
    private static double[] simPosAltAtt;
    private static double[] offsets;
    private static double simGndAlt;
    private static boolean cancelLdgOffset;
    private static final Lock lockSimPosAltAtt;
    private static final Lock lockOffsets;
    private static final Lock lockCancelLdgOffset;
    private static final Lock lockSimGearPos;
    private static double toAltOffsetCancelAlt;
    private static double backupSimToAmslAlt;
    private static double backupPsxToAmslAlt;
    private static double[] backupLdgOffsets;
    private static int simGearPos;
    private static boolean doTakeoffAltOffset;
    private static boolean doTakeoffWithPosOffset;
    private static boolean doLandingWithPosOffset;
    private static boolean doBackupLdgOffsets;
    private static boolean smoothFlareTransition;
    private static boolean psxSlaveLoaded;
    private static boolean freezeSim;
    private static boolean takingOff;
    private static boolean landing;
    private static boolean doSaveLiftOffAltDiff;
    private static double lastAltInjectionBefore1000ft;
    private static double liftOffAltitude;
    private static double liftOffAltDiff;
    private static double lastAltInjectionBeforeLanding;
    private static boolean eng1Starter;
    private static final Lock lockEng1Starter;
    private static boolean eng2Starter;
    private static final Lock lockEng2Starter;
    private static boolean eng3Starter;
    private static final Lock lockEng3Starter;
    private static boolean eng4Starter;
    private static final Lock lockEng4Starter;
    private static boolean doorsOpened;
    private static final Lock lockDoorsOpened;
    private static boolean doorsPaxOpened;
    private static final Lock lockDoorsPaxOpened;
    private static final int FOURSECONDSEVENT = 0;
    private static final int ONESECONDEVENT = 24;
    private static final int SIXHZEVENT = 7;
    private static final int COM1ACTIVESETEVENT = 1;
    private static final int COM2ACTIVESETEVENT = 2;
    private static final int XPNDRCODESETEVENT = 3;
    private static final int FREEZELATLONSETEVENT = 4;
    private static final int FREEZEALTITUDESETEVENT = 5;
    private static final int FREEZEATTITUDESETEVENT = 6;
    private static final int ZULU_HOURS_EVENT = 8;
    private static final int ZULU_MINUTES_EVENT = 9;
    private static final int ZULU_DAY_EVENT = 10;
    private static final int ZULU_YEAR_EVENT = 11;
    private static final int GEAR_UP_EVENT = 12;
    private static final int GEAR_DN_EVENT = 13;
    private static final int LTS_LDG_SET_EVENT = 14;
    private static final int LTS_TAXI_TOGGLE_EVENT = 17;
    private static final int LTS_BCN_TOGGLE_EVENT = 18;
    private static final int LTS_NAV_TOGGLE_EVENT = 19;
    private static final int LTS_STROBE_SET_EVENT = 20;
    private static final int LTS_WING_TOGGLE_EVENT = 21;
    private static final int LTS_LOGO_TOGGLE_EVENT = 22;
    private static final int LTS_CABIN_TOGGLE_EVENT = 23;
    private static final int CTRL_ELEV_SET_EVENT = 15;
    private static final int CTRL_AILERONS_SET_EVENT = 16;
    private static final int CTRL_RUDDER_SET_EVENT = 25;
    private static final int CTRL_SPDBRK_SET_EVENT = 26;
    private static final int CTRL_FLAPS_SET_EVENT = 27;
    private static final int PARK_BRK_TOGGLE_EVENT = 31;
    private static final int CLIENT_DATA_SQUAWKBOX = 28;
    private static final int SQUAWKBOX_TRANSPONDER_MODE = 29;
    private static final int SQUAWKBOX_TRANSPONDER_IDENT = 30;
    private static final int FUEL_SELECTOR_ALL_EVENT = 38;
    private static final int MIXTURE_1_RICH_EVENT = 39;
    private static final int MIXTURE_1_LEAN_EVENT = 40;
    private static final int MIXTURE_2_RICH_EVENT = 41;
    private static final int MIXTURE_2_LEAN_EVENT = 42;
    private static final int MIXTURE_3_RICH_EVENT = 43;
    private static final int MIXTURE_3_LEAN_EVENT = 44;
    private static final int MIXTURE_4_RICH_EVENT = 45;
    private static final int MIXTURE_4_LEAN_EVENT = 46;
    private static final int ENGINE_1_START_TOGGLE_EVENT = 33;
    private static final int ENGINE_2_START_TOGGLE_EVENT = 47;
    private static final int ENGINE_3_START_TOGGLE_EVENT = 48;
    private static final int ENGINE_4_START_TOGGLE_EVENT = 49;
    private static final int THROTTLE_1_SET_EVENT = 34;
    private static final int THROTTLE_2_SET_EVENT = 35;
    private static final int THROTTLE_3_SET_EVENT = 36;
    private static final int THROTTLE_4_SET_EVENT = 37;
    private static final int DOORS_TOGGLE_EVENT = 50;
    private static final int SELECT_DOORS_CARGO_R_EVENT = 52;
    private static final int SELECT_DOORS_NOSE_L2_EVENT = 53;
    private static final int SELECT_DOOR_BULK_CARGO_EVENT = 54;
    private static final int ENGINE1_ANTI_ICE_SET = 55;
    private static final int ENGINE2_ANTI_ICE_SET = 56;
    private static final int ENGINE3_ANTI_ICE_SET = 57;
    private static final int ENGINE4_ANTI_ICE_SET = 58;
    private static final int WING_ANTI_ICE_SET = 59;
    private static final int KOHLSMAN_SET_EVENT = 62;
    
    static {
        BridgeSimConnect.ip = new String("");
        BridgeSimConnect.port = 0;
        BridgeSimConnect.ambientOat = 0.0;
        BridgeSimConnect.ambientWindDir = 0.0;
        BridgeSimConnect.ambientWindVel = 0.0;
        BridgeSimConnect.parkBrkSet = false;
        BridgeSimConnect.simOnGround = false;
        BridgeSimConnect.simTime = new int[4];
        lockAmbientOat = new ReentrantLock();
        lockAmbientWindDir = new ReentrantLock();
        lockAmbientWindVel = new ReentrantLock();
        lockParkBrkSet = new ReentrantLock();
        lockSimOnGround = new ReentrantLock();
        lockSimTime = new ReentrantLock();
        lockSimGndAlt = new ReentrantLock();
        BridgeSimConnect.simLightState = 0;
        lockSimLightState = new ReentrantLock();
        BridgeSimConnect.userAcftId = "";
        lockUserAcftId = new ReentrantLock();
        BridgeSimConnect.liftDecompArmed = false;
        BridgeSimConnect.liftDecompInProgress = false;
        BridgeSimConnect.staticCompRatio = 0.0;
        BridgeSimConnect.liftDecompArmedPitch = 0.0;
        BridgeSimConnect.simPosAltAtt = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
        BridgeSimConnect.offsets = new double[] { 0.0, 0.0, 0.0, 0.0 };
        BridgeSimConnect.simGndAlt = 0.0;
        BridgeSimConnect.cancelLdgOffset = false;
        lockSimPosAltAtt = new ReentrantLock();
        lockOffsets = new ReentrantLock();
        lockCancelLdgOffset = new ReentrantLock();
        lockSimGearPos = new ReentrantLock();
        BridgeSimConnect.toAltOffsetCancelAlt = 0.0;
        BridgeSimConnect.backupSimToAmslAlt = 0.0;
        BridgeSimConnect.backupPsxToAmslAlt = 0.0;
        BridgeSimConnect.backupLdgOffsets = new double[] { 0.0, 0.0 };
        BridgeSimConnect.simGearPos = 9;
        BridgeSimConnect.doTakeoffAltOffset = false;
        BridgeSimConnect.doTakeoffWithPosOffset = false;
        BridgeSimConnect.doLandingWithPosOffset = false;
        BridgeSimConnect.doBackupLdgOffsets = true;
        BridgeSimConnect.smoothFlareTransition = false;
        BridgeSimConnect.psxSlaveLoaded = false;
        BridgeSimConnect.freezeSim = false;
        BridgeSimConnect.takingOff = false;
        BridgeSimConnect.landing = false;
        BridgeSimConnect.doSaveLiftOffAltDiff = false;
        BridgeSimConnect.lastAltInjectionBefore1000ft = 0.0;
        BridgeSimConnect.liftOffAltitude = 0.0;
        BridgeSimConnect.liftOffAltDiff = 0.0;
        BridgeSimConnect.lastAltInjectionBeforeLanding = 0.0;
        BridgeSimConnect.eng1Starter = false;
        lockEng1Starter = new ReentrantLock();
        BridgeSimConnect.eng2Starter = false;
        lockEng2Starter = new ReentrantLock();
        BridgeSimConnect.eng3Starter = false;
        lockEng3Starter = new ReentrantLock();
        BridgeSimConnect.eng4Starter = false;
        lockEng4Starter = new ReentrantLock();
        BridgeSimConnect.doorsOpened = false;
        lockDoorsOpened = new ReentrantLock();
        BridgeSimConnect.doorsPaxOpened = false;
        lockDoorsPaxOpened = new ReentrantLock();
    }
    
    public BridgeSimConnect() {
        StatusMonitor.setSimConnectIsConnected(false);
        StatusMonitor.setSimConnectIsDisconnected(false);
        StatusMonitor.setSimConnectUnableToConnect(false);
    }
    
    public static void setIp(final String argIp) {
        BridgeSimConnect.ip = argIp;
    }
    
    public static boolean setPort(final String argPort) {
        try {
            BridgeSimConnect.port = Integer.valueOf(argPort);
            StatusMonitor.setSimConnectInvalidIp(false);
            return true;
        }
        catch (NumberFormatException e) {
            StatusMonitor.setSimConnectInvalidIp(true);
            return false;
        }
    }
    
    public void connect() {
        try {
            this.configureDispatch(BridgeSimConnect.sc = new SimConnect("WidePSX", BridgeSimConnect.ip, BridgeSimConnect.port));
        }
        catch (IOException e) {
            StatusMonitor.setSimConnectIsConnected(false);
            StatusMonitor.setSimConnectIsDisconnected(false);
            StatusMonitor.setSimConnectUnableToConnect(true);
        }
    }
    
    public void handleOpen(final SimConnect sender, final RecvOpen e) {
        StatusMonitor.setSimConnectIsConnected(true);
        StatusMonitor.setSimConnectIsDisconnected(false);
        StatusMonitor.setSimConnectUnableToConnect(false);
        try {
            releaseSceneryGen(false);
            BridgeSimConnect.sc.setDataOnSimObject((Enum)DATA_DEFINE_ID.DEFINITION_FUEL_TANK_CTR_LEVEL, 0, new double[] { 10000.0 });
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
    }
    
    public static void releaseSceneryGen(final boolean argRelease) {
        try {
            if (argRelease) {
                updateParkingBrake(1);
                BridgeSimConnect.sc.transmitClientEvent(0, 5, 0, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
                BridgeSimConnect.sc.transmitClientEvent(0, 6, 0, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
                BridgeSimConnect.sc.transmitClientEvent(0, 4, 0, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
            }
            else {
                BridgeSimConnect.sc.transmitClientEvent(0, 5, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
                BridgeSimConnect.sc.transmitClientEvent(0, 6, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
                BridgeSimConnect.sc.transmitClientEvent(0, 4, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void handleQuit(final SimConnect sender, final RecvQuit e) {
        StatusMonitor.setSimConnectIsConnected(false);
        StatusMonitor.setSimConnectIsDisconnected(true);
        StatusMonitor.setSimConnectUnableToConnect(false);
        StatusMonitor.setSimBridgeIsRunning(false);
        JOptionPane.showMessageDialog(null, "SimConnect has disconnected. WidePSX will close.", "Warning", 2);
        System.exit(0);
    }
    
    public void handleException(final SimConnect sender, final RecvException e) {
    }
    
    public void handleSimObjectType(final SimConnect sender, final RecvSimObjectDataByType e) {
        if (!StatusMonitor.getSimBridgeIsRunning()) {
            if (e.getRequestID() == DATA_REQUEST_ID.REQUEST_ENGINE_1_STARTER.ordinal()) {
                final int value = e.getDataInt32();
                if (value == 1) {
                    BridgeSimConnect.lockEng1Starter.lock();
                    BridgeSimConnect.eng1Starter = true;
                    BridgeSimConnect.lockEng1Starter.unlock();
                }
                else {
                    BridgeSimConnect.lockEng1Starter.lock();
                    BridgeSimConnect.eng1Starter = false;
                    BridgeSimConnect.lockEng1Starter.unlock();
                }
            }
            if (e.getRequestID() == DATA_REQUEST_ID.REQUEST_ENGINE_2_STARTER.ordinal()) {
                final int value = e.getDataInt32();
                if (value == 1) {
                    BridgeSimConnect.lockEng2Starter.lock();
                    BridgeSimConnect.eng2Starter = true;
                    BridgeSimConnect.lockEng2Starter.unlock();
                }
                else {
                    BridgeSimConnect.lockEng2Starter.lock();
                    BridgeSimConnect.eng2Starter = false;
                    BridgeSimConnect.lockEng2Starter.unlock();
                }
            }
            if (e.getRequestID() == DATA_REQUEST_ID.REQUEST_ENGINE_3_STARTER.ordinal()) {
                final int value = e.getDataInt32();
                if (value == 1) {
                    BridgeSimConnect.lockEng3Starter.lock();
                    BridgeSimConnect.eng3Starter = true;
                    BridgeSimConnect.lockEng3Starter.unlock();
                }
                else {
                    BridgeSimConnect.lockEng3Starter.lock();
                    BridgeSimConnect.eng3Starter = false;
                    BridgeSimConnect.lockEng3Starter.unlock();
                }
            }
            if (e.getRequestID() == DATA_REQUEST_ID.REQUEST_ENGINE_4_STARTER.ordinal()) {
                final int value = e.getDataInt32();
                if (value == 1) {
                    BridgeSimConnect.lockEng4Starter.lock();
                    BridgeSimConnect.eng4Starter = true;
                    BridgeSimConnect.lockEng4Starter.unlock();
                }
                else {
                    BridgeSimConnect.lockEng4Starter.lock();
                    BridgeSimConnect.eng4Starter = false;
                    BridgeSimConnect.lockEng4Starter.unlock();
                }
            }
        }
        if (e.getRequestID() == DATA_REQUEST_ID.REQUEST_DOORS.ordinal()) {
            final double value2 = e.getDataFloat64();
            if (value2 != 0.0) {
                BridgeSimConnect.lockDoorsOpened.lock();
                BridgeSimConnect.doorsOpened = true;
                BridgeSimConnect.lockDoorsOpened.unlock();
            }
            else {
                BridgeSimConnect.lockDoorsOpened.lock();
                BridgeSimConnect.doorsOpened = false;
                BridgeSimConnect.lockDoorsOpened.unlock();
            }
        }
        if (e.getRequestID() == DATA_REQUEST_ID.REQUEST_DOORS_PAX.ordinal()) {
            final double value2 = e.getDataFloat64();
            if (value2 != 0.0) {
                BridgeSimConnect.lockDoorsPaxOpened.lock();
                BridgeSimConnect.doorsPaxOpened = true;
                BridgeSimConnect.lockDoorsPaxOpened.unlock();
            }
            else {
                BridgeSimConnect.lockDoorsPaxOpened.lock();
                BridgeSimConnect.doorsPaxOpened = false;
                BridgeSimConnect.lockDoorsPaxOpened.unlock();
            }
        }
        if (e.getRequestID() == DATA_REQUEST_ID.REQUEST_FUEL_TANK_CTR_LEVEL.ordinal()) {
            try {
                BridgeSimConnect.sc.setDataOnSimObject((Enum)DATA_DEFINE_ID.DEFINITION_FUEL_TANK_CTR_LEVEL, 0, new double[] { 10000.0 });
            }
            catch (IOException ex) {}
        }
        if (e.getRequestID() == DATA_REQUEST_ID.REQUEST_GEAR_POSITION.ordinal()) {
            BridgeSimConnect.lockSimGearPos.lock();
            BridgeSimConnect.simGearPos = e.getDataInt32();
            BridgeSimConnect.lockSimGearPos.unlock();
        }
        if (e.getRequestID() == DATA_REQUEST_ID.REQUEST_SIM_POS_ALT_ATT.ordinal()) {
            BridgeSimConnect.lockSimPosAltAtt.lock();
            BridgeSimConnect.simPosAltAtt[0] = e.getDataFloat64();
            BridgeSimConnect.simPosAltAtt[1] = e.getDataFloat64();
            BridgeSimConnect.simPosAltAtt[2] = e.getDataFloat64();
            BridgeSimConnect.simPosAltAtt[3] = e.getDataFloat64();
            BridgeSimConnect.simPosAltAtt[4] = e.getDataFloat64();
            BridgeSimConnect.simPosAltAtt[5] = e.getDataFloat64();
            BridgeSimConnect.simPosAltAtt[6] = e.getDataFloat64();
            BridgeSimConnect.lockSimPosAltAtt.unlock();
        }
        if (e.getRequestID() == DATA_REQUEST_ID.REQUEST_SIM_ON_GROUND.ordinal()) {
            final int temp = e.getDataInt32();
            if (temp == 1) {
                BridgeSimConnect.lockSimOnGround.lock();
                BridgeSimConnect.simOnGround = true;
                BridgeSimConnect.lockSimOnGround.unlock();
            }
            else {
                BridgeSimConnect.lockSimOnGround.lock();
                BridgeSimConnect.simOnGround = false;
                BridgeSimConnect.lockSimOnGround.unlock();
            }
        }
        if (e.getRequestID() == DATA_REQUEST_ID.REQUEST_SIM_GMT_YEAR.ordinal()) {
            BridgeSimConnect.lockSimTime.lock();
            BridgeSimConnect.simTime[0] = e.getDataInt32();
            BridgeSimConnect.lockSimTime.unlock();
        }
        if (e.getRequestID() == DATA_REQUEST_ID.REQUEST_SIM_GMT_MONTH_OF_YEAR.ordinal()) {
            BridgeSimConnect.lockSimTime.lock();
            BridgeSimConnect.simTime[1] = e.getDataInt32() - 1;
            BridgeSimConnect.lockSimTime.unlock();
        }
        if (e.getRequestID() == DATA_REQUEST_ID.REQUEST_SIM_GMT_DAY_OF_MONTH.ordinal()) {
            BridgeSimConnect.lockSimTime.lock();
            BridgeSimConnect.simTime[2] = e.getDataInt32();
            BridgeSimConnect.lockSimTime.unlock();
        }
        if (e.getRequestID() == DATA_REQUEST_ID.REQUEST_SIM_GMT_TIME.ordinal()) {
            BridgeSimConnect.lockSimTime.lock();
            BridgeSimConnect.simTime[3] = e.getDataInt32();
            BridgeSimConnect.lockSimTime.unlock();
        }
        if (e.getRequestID() == DATA_REQUEST_ID.REQUEST_LIGHT_STATE.ordinal()) {
            BridgeSimConnect.lockSimLightState.lock();
            BridgeSimConnect.simLightState = e.getDataInt32();
            BridgeSimConnect.lockSimLightState.unlock();
        }
        if (e.getRequestID() == DATA_REQUEST_ID.REQUEST_SIM_GND_ALT.ordinal()) {
            BridgeSimConnect.lockSimGndAlt.lock();
            BridgeSimConnect.simGndAlt = e.getDataFloat64() * 3.28084;
            BridgeSimConnect.lockSimGndAlt.unlock();
        }
        if (e.getRequestID() == DATA_REQUEST_ID.REQUEST_PARK_BRK_STATE.ordinal()) {
            final int temp = e.getDataInt32();
            if (temp == 0) {
                BridgeSimConnect.lockParkBrkSet.lock();
                BridgeSimConnect.parkBrkSet = false;
                BridgeSimConnect.lockParkBrkSet.unlock();
            }
            else {
                BridgeSimConnect.lockParkBrkSet.lock();
                BridgeSimConnect.parkBrkSet = true;
                BridgeSimConnect.lockParkBrkSet.unlock();
            }
        }
        if (e.getRequestID() == DATA_REQUEST_ID.REQUEST_WINDOAT.ordinal()) {
            BridgeSimConnect.lockAmbientOat.lock();
            BridgeSimConnect.ambientOat = e.getDataFloat64();
            BridgeSimConnect.lockAmbientOat.unlock();
            BridgeSimConnect.lockAmbientWindVel.lock();
            BridgeSimConnect.ambientWindVel = e.getDataFloat64();
            BridgeSimConnect.lockAmbientWindVel.unlock();
            BridgeSimConnect.lockAmbientWindDir.lock();
            BridgeSimConnect.ambientWindDir = e.getDataFloat64();
            BridgeSimConnect.lockAmbientWindDir.unlock();
        }
        if (e.getRequestID() == DATA_REQUEST_ID.REQUEST_USER_ACFT_ATC_ID.ordinal()) {
            BridgeSimConnect.lockUserAcftId.lock();
            BridgeSimConnect.userAcftId = e.getDataString32();
            BridgeSimConnect.lockUserAcftId.unlock();
        }
        if (e.getRequestID() == DATA_REQUEST_ID.REQUEST_AITRAFFIC.ordinal()) {
            final double latitude = e.getDataFloat64();
            final double longitude = e.getDataFloat64();
            final double altitude = e.getDataFloat64();
            final double gndSpeed = e.getDataFloat64();
            final double ias = e.getDataFloat64();
            final double hdgTrue = e.getDataFloat64();
            final String atcType = new String(e.getDataString32());
            final String atcId = new String(e.getDataString32());
            BridgeSimConnect.lockUserAcftId.lock();
            final String userId = BridgeSimConnect.userAcftId;
            BridgeSimConnect.lockUserAcftId.unlock();
            if (!atcId.equals(userId) && (ias > 35.0 || gndSpeed > 35.0)) {
                TrafficRadioXpndr.newAiReceived(latitude, longitude, altitude, hdgTrue, atcType, atcId);
            }
        }
    }
    
    public static void updateAcftPos(final boolean argAirborne, double argLat, double argLon, double argAlt, final double argHdg, final double argPitch, final double argBank) throws IOException {
        final double elev = getSimGndAlt();
        final int Qi198Elev = (int)(elev * 100.0);
        final double[] pos = PlaceBearingDistance.getPbdDestCoords(argLat, argLon, PlaceBearingDistance.getReverseHdg(argHdg), 4.462600953863606E-6);
        argLat = pos[0];
        argLon = pos[1];
        argAlt -= 28.412073 + 92.5 * Math.sin(-argPitch);
        if (StatusMonitor.getMsfsInUse()) {
            SocketClientPSXMain.send("Qi198=" + Qi198Elev);
            double targetGndElev = 13.0;
            if (StatusMonitor.getSimBridgeHeightRefEnabled()) {
                targetGndElev = TabSceneryGen.getHeightRef();
            }
            double psxTas = 0.0;
            if (StatusMonitor.getSendTrueTasToVATSIM()) {
                psxTas = SimBridgeBase.getPsxTas();
                if (psxTas < 40.0) {
                    psxTas = 0.0;
                }
            }
            final double GNDPITCHHEIGHTRATIO = 33.0;
            final double FLARESMOOTHINGRATIO = 0.07;
            final double LIFTOFFALTITUDESMOOTHRATIO = 1.0;
            if (!argAirborne) {
                double gndAltInjection = elev + (targetGndElev + 33.0 * argPitch);
                if (BridgeSimConnect.landing) {
                    final double diff = gndAltInjection - BridgeSimConnect.lastAltInjectionBeforeLanding;
                    if (diff > 0.07) {
                        gndAltInjection = BridgeSimConnect.lastAltInjectionBeforeLanding + 0.07;
                        BridgeSimConnect.lastAltInjectionBeforeLanding += 0.07;
                    }
                    else if (diff < -0.07) {
                        gndAltInjection = BridgeSimConnect.lastAltInjectionBeforeLanding - 0.07;
                        BridgeSimConnect.lastAltInjectionBeforeLanding -= 0.07;
                    }
                    else {
                        BridgeSimConnect.landing = false;
                    }
                }
                try {
                    BridgeSimConnect.sc.setDataOnSimObject((Enum)DATA_DEFINE_ID.DEFINITION_AIR_POS_UPDATE, 0, new double[] { argLat, argLon, gndAltInjection, -argPitch, argBank, argHdg, psxTas });
                }
                catch (Exception ex) {}
                BridgeSimConnect.liftOffAltitude = gndAltInjection;
                BridgeSimConnect.takingOff = true;
                BridgeSimConnect.doSaveLiftOffAltDiff = true;
            }
            else {
                if (BridgeSimConnect.takingOff) {
                    if (BridgeSimConnect.doSaveLiftOffAltDiff) {
                        BridgeSimConnect.liftOffAltDiff = argAlt - BridgeSimConnect.liftOffAltitude;
                        BridgeSimConnect.doSaveLiftOffAltDiff = false;
                    }
                    if (argAlt <= BridgeSimConnect.liftOffAltitude + 1000.0) {
                        if (BridgeSimConnect.liftOffAltDiff > 0.0) {
                            argAlt -= BridgeSimConnect.liftOffAltDiff;
                        }
                        else if (BridgeSimConnect.liftOffAltDiff < 0.0) {
                            argAlt += BridgeSimConnect.liftOffAltDiff;
                        }
                        BridgeSimConnect.lastAltInjectionBefore1000ft = argAlt;
                    }
                    else if (argAlt <= BridgeSimConnect.liftOffAltitude + 1500.0) {
                        final double diff2 = argAlt - BridgeSimConnect.lastAltInjectionBefore1000ft;
                        if (diff2 > LIFTOFFALTITUDESMOOTHRATIO) {
                            argAlt = BridgeSimConnect.lastAltInjectionBefore1000ft + LIFTOFFALTITUDESMOOTHRATIO;
                            BridgeSimConnect.lastAltInjectionBefore1000ft += LIFTOFFALTITUDESMOOTHRATIO;
                        }
                        else if (diff2 < -LIFTOFFALTITUDESMOOTHRATIO) {
                            argAlt = BridgeSimConnect.lastAltInjectionBefore1000ft - LIFTOFFALTITUDESMOOTHRATIO;
                            BridgeSimConnect.lastAltInjectionBefore1000ft -= LIFTOFFALTITUDESMOOTHRATIO;
                        }
                        else {
                            BridgeSimConnect.takingOff = false;
                        }
                    }
                    else {
                        BridgeSimConnect.takingOff = false;
                    }
                }
                try {
                    BridgeSimConnect.sc.setDataOnSimObject((Enum)DATA_DEFINE_ID.DEFINITION_AIR_POS_UPDATE, 0, new double[] { argLat, argLon, argAlt, -argPitch, argBank, argHdg, psxTas });
                }
                catch (Exception ex2) {}
                BridgeSimConnect.lastAltInjectionBeforeLanding = argAlt;
                BridgeSimConnect.landing = true;
            }
        }
        else if (StatusMonitor.getSimBridgeUseBetaAlgos()) {
            SocketClientPSXMain.send("Qi198=" + Qi198Elev);
            double psxTas2 = SimBridgeBase.getPsxTas();
            double targetGndElev2 = 15.1;
            if (StatusMonitor.getSimBridgeHeightRefEnabled()) {
                targetGndElev2 = TabSceneryGen.getHeightRef();
            }
            final double GNDPITCHHEIGHTRATIO = 33.0;
            final double FLARESMOOTHINGRATIO = 0.07;
            final double LIFTOFFALTITUDESMOOTHRATIO = 1.0;
            double latitude = 0.0;
            double longitude = 0.0;
            final double[] offsetsLocal = getOffsets();
            final double[] simPosAltAttLocal = getSimPosAltAtt();
            final double psxAltAbovePsxRwy = argAlt - offsetsLocal[3];
            final double offsetsInhibitAlt = TabSceneryGen.getOffsetInhibitAlt();
            boolean inhibitOffsets = true;
            if ((!StatusMonitor.getSimBridgeUsingDestOffsets() && psxAltAbovePsxRwy < offsetsInhibitAlt) || StatusMonitor.getSimBridgeUsingDestOffsets()) {
                inhibitOffsets = false;
            }
            if (!argAirborne) {
                if (psxTas2 < 40.0) {
                    psxTas2 = 0.0;
                }
                final double targetCompRatio = SimBridgeBase.getPsxGearCompRatio();
                if (targetCompRatio < BridgeSimConnect.staticCompRatio - 0.15) {
                    BridgeSimConnect.staticCompRatio -= 0.15;
                }
                else if (targetCompRatio > BridgeSimConnect.staticCompRatio + 0.15) {
                    BridgeSimConnect.staticCompRatio += 0.15;
                }
                else {
                    BridgeSimConnect.staticCompRatio = targetCompRatio;
                }
                targetGndElev2 -= 0.021 * BridgeSimConnect.staticCompRatio;
                double gndAltInjection2 = elev + (targetGndElev2 + 33.0 * argPitch);
                if (BridgeSimConnect.landing) {
                    final double diff3 = gndAltInjection2 - BridgeSimConnect.lastAltInjectionBeforeLanding;
                    if (diff3 > 0.07) {
                        gndAltInjection2 = BridgeSimConnect.lastAltInjectionBeforeLanding + 0.07;
                        BridgeSimConnect.lastAltInjectionBeforeLanding += 0.07;
                    }
                    else if (diff3 < -0.07) {
                        gndAltInjection2 = BridgeSimConnect.lastAltInjectionBeforeLanding - 0.07;
                        BridgeSimConnect.lastAltInjectionBeforeLanding -= 0.07;
                    }
                    else {
                        BridgeSimConnect.landing = false;
                    }
                }
                if (BridgeSimConnect.doLandingWithPosOffset) {
                    BridgeSimConnect.doTakeoffWithPosOffset = true;
                    if (BridgeSimConnect.doBackupLdgOffsets) {
                        BridgeSimConnect.doBackupLdgOffsets = false;
                        BridgeSimConnect.backupLdgOffsets[0] = offsetsLocal[0];
                        BridgeSimConnect.backupLdgOffsets[1] = offsetsLocal[1];
                    }
                    latitude = argLat + BridgeSimConnect.backupLdgOffsets[0];
                    longitude = argLon + BridgeSimConnect.backupLdgOffsets[1];
                    if (getCancelLdgoffset()) {
                        BridgeSimConnect.doLandingWithPosOffset = false;
                        BridgeSimConnect.freezeSim = true;
                        setCancelLdgOffset(false);
                        SimBridgeOffsetPos.setPsxPosToSimPos(argLat, argLon, simPosAltAttLocal[0], simPosAltAttLocal[1]);
                    }
                }
                else if (!BridgeSimConnect.freezeSim) {
                    latitude = argLat;
                    longitude = argLon;
                    BridgeSimConnect.doTakeoffWithPosOffset = false;
                }
                BridgeSimConnect.sc.setDataOnSimObject((Enum)DATA_DEFINE_ID.DEFINITION_AIR_POS_UPDATE, 0, new double[] { latitude, longitude, gndAltInjection2, -argPitch, argBank, argHdg, psxTas2 });
                BridgeSimConnect.liftOffAltitude = gndAltInjection2;
                BridgeSimConnect.takingOff = true;
                BridgeSimConnect.doSaveLiftOffAltDiff = true;
            }
            else {
                if (BridgeSimConnect.takingOff) {
                    if (BridgeSimConnect.doSaveLiftOffAltDiff) {
                        BridgeSimConnect.liftOffAltDiff = argAlt - BridgeSimConnect.liftOffAltitude;
                        BridgeSimConnect.doSaveLiftOffAltDiff = false;
                    }
                    if (argAlt <= BridgeSimConnect.liftOffAltitude + 1000.0) {
                        if (BridgeSimConnect.liftOffAltDiff > 0.0) {
                            argAlt -= BridgeSimConnect.liftOffAltDiff;
                        }
                        else if (BridgeSimConnect.liftOffAltDiff < 0.0) {
                            argAlt += BridgeSimConnect.liftOffAltDiff;
                        }
                        BridgeSimConnect.lastAltInjectionBefore1000ft = argAlt;
                    }
                    else if (argAlt <= BridgeSimConnect.liftOffAltitude + 1500.0) {
                        final double diff4 = argAlt - BridgeSimConnect.lastAltInjectionBefore1000ft;
                        if (diff4 > LIFTOFFALTITUDESMOOTHRATIO) {
                            argAlt = BridgeSimConnect.lastAltInjectionBefore1000ft + LIFTOFFALTITUDESMOOTHRATIO;
                            BridgeSimConnect.lastAltInjectionBefore1000ft += LIFTOFFALTITUDESMOOTHRATIO;
                        }
                        else if (diff4 < -LIFTOFFALTITUDESMOOTHRATIO) {
                            argAlt = BridgeSimConnect.lastAltInjectionBefore1000ft - LIFTOFFALTITUDESMOOTHRATIO;
                            BridgeSimConnect.lastAltInjectionBefore1000ft -= LIFTOFFALTITUDESMOOTHRATIO;
                        }
                        else {
                            BridgeSimConnect.takingOff = false;
                        }
                    }
                    else {
                        BridgeSimConnect.takingOff = false;
                    }
                }
                if (argAlt < 20000.0) {
                    if (argAlt > BridgeSimConnect.liftOffAltitude + 1000.0) {
                        offsetsLocal[1] = (offsetsLocal[0] = 0.0);
                        BridgeSimConnect.doTakeoffWithPosOffset = false;
                        BridgeSimConnect.liftOffAltitude = 100000.0;
                        StatusMonitor.setSimBridgeAlignPsxWithDepRwy(false);
                    }
                    if (BridgeSimConnect.doTakeoffWithPosOffset) {
                        latitude = argLat + BridgeSimConnect.backupLdgOffsets[0];
                        longitude = argLon + BridgeSimConnect.backupLdgOffsets[1];
                    }
                    else if (!inhibitOffsets) {
                        BridgeSimConnect.doLandingWithPosOffset = true;
                        BridgeSimConnect.doBackupLdgOffsets = true;
                        latitude = argLat + offsetsLocal[0];
                        longitude = argLon + offsetsLocal[1];
                    }
                    else {
                        latitude = argLat;
                        longitude = argLon;
                        BridgeSimConnect.doLandingWithPosOffset = false;
                    }
                }
                else {
                    offsetsLocal[1] = (offsetsLocal[0] = 0.0);
                    latitude = argLat;
                    longitude = argLon;
                }
                BridgeSimConnect.sc.setDataOnSimObject((Enum)DATA_DEFINE_ID.DEFINITION_AIR_POS_UPDATE, 0, new double[] { latitude, longitude, argAlt, -argPitch, argBank, argHdg, psxTas2 });
                BridgeSimConnect.lastAltInjectionBeforeLanding = argAlt;
                BridgeSimConnect.landing = true;
            }
            BridgeSimConnect.freezeSim = false;
            final double latDiff = Math.abs(latitude - argLat);
            final double lonDiff = Math.abs(longitude - argLon);
            if (latDiff != 0.0 || lonDiff != 0.0) {
                StatusMonitor.setSimBridgePosOffsetUsed(true);
            }
            else {
                StatusMonitor.setSimBridgePosOffsetUsed(false);
            }
            StatusMonitor.setSimBridgeAltOffsetUsed(false);
        }
        else {
            double latitude2 = 0.0;
            double longitude2 = 0.0;
            double altToSim = 0.0;
            double targetGndElev3 = 15.1;
            if (StatusMonitor.getSimBridgeHeightRefEnabled()) {
                targetGndElev3 = TabSceneryGen.getHeightRef();
            }
            final double[] offsetsLocal2 = getOffsets();
            final double[] simPosAltAttLocal2 = getSimPosAltAtt();
            final double psxAltAbovePsxRwy2 = argAlt - offsetsLocal2[3];
            final double offsetsInhibitAlt2 = TabSceneryGen.getOffsetInhibitAlt();
            boolean inhibitOffsets2 = true;
            if ((!StatusMonitor.getSimBridgeUsingDestOffsets() && psxAltAbovePsxRwy2 < offsetsInhibitAlt2) || StatusMonitor.getSimBridgeUsingDestOffsets()) {
                inhibitOffsets2 = false;
            }
            if (!argAirborne) {
                if (BridgeSimConnect.liftDecompArmed && argPitch >= BridgeSimConnect.liftDecompArmedPitch + 0.0174533) {
                    BridgeSimConnect.liftDecompInProgress = true;
                }
                if (!BridgeSimConnect.liftDecompArmed && argPitch >= 0.0872665) {
                    BridgeSimConnect.liftDecompArmed = true;
                    BridgeSimConnect.liftDecompArmedPitch = argPitch;
                }
                if (!BridgeSimConnect.liftDecompInProgress || argPitch < 0.0174533) {
                    final double targetCompRatio2 = SimBridgeBase.getPsxGearCompRatio();
                    if (targetCompRatio2 < BridgeSimConnect.staticCompRatio - 0.15) {
                        BridgeSimConnect.staticCompRatio -= 0.15;
                    }
                    else if (targetCompRatio2 > BridgeSimConnect.staticCompRatio + 0.15) {
                        BridgeSimConnect.staticCompRatio += 0.15;
                    }
                    else {
                        BridgeSimConnect.staticCompRatio = targetCompRatio2;
                    }
                }
                else {
                    BridgeSimConnect.staticCompRatio -= 1.5;
                    if (BridgeSimConnect.staticCompRatio < 0.0) {
                        BridgeSimConnect.staticCompRatio = 0.0;
                    }
                }
                targetGndElev3 -= 0.021 * BridgeSimConnect.staticCompRatio;
                BridgeSimConnect.doTakeoffAltOffset = true;
                BridgeSimConnect.backupSimToAmslAlt = simPosAltAttLocal2[3];
                BridgeSimConnect.backupPsxToAmslAlt = argAlt;
                BridgeSimConnect.toAltOffsetCancelAlt = argAlt + 1000.0;
                if (BridgeSimConnect.doLandingWithPosOffset) {
                    if (BridgeSimConnect.doBackupLdgOffsets) {
                        BridgeSimConnect.doBackupLdgOffsets = false;
                        BridgeSimConnect.backupLdgOffsets[0] = offsetsLocal2[0];
                        BridgeSimConnect.backupLdgOffsets[1] = offsetsLocal2[1];
                    }
                    latitude2 = argLat + BridgeSimConnect.backupLdgOffsets[0];
                    longitude2 = argLon + BridgeSimConnect.backupLdgOffsets[1];
                    if (getCancelLdgoffset()) {
                        BridgeSimConnect.doLandingWithPosOffset = false;
                        BridgeSimConnect.doBackupLdgOffsets = true;
                        BridgeSimConnect.doTakeoffWithPosOffset = false;
                        BridgeSimConnect.freezeSim = true;
                        setCancelLdgOffset(false);
                        SimBridgeOffsetPos.setPsxPosToSimPos(argLat, argLon, simPosAltAttLocal2[0], simPosAltAttLocal2[1]);
                    }
                }
            }
            else if (BridgeSimConnect.doTakeoffAltOffset) {
                SocketClientPSXMain.send("Qi198=" + Qi198Elev);
                BridgeSimConnect.liftDecompInProgress = false;
                BridgeSimConnect.liftDecompArmed = false;
                BridgeSimConnect.smoothFlareTransition = true;
                updateSpdbrkPos(0);
                if (BridgeSimConnect.backupSimToAmslAlt != BridgeSimConnect.backupPsxToAmslAlt) {
                    altToSim = BridgeSimConnect.backupSimToAmslAlt + (argAlt - BridgeSimConnect.backupPsxToAmslAlt);
                    BridgeSimConnect.backupSimToAmslAlt += argAlt - BridgeSimConnect.backupPsxToAmslAlt;
                    BridgeSimConnect.backupPsxToAmslAlt += argAlt - BridgeSimConnect.backupPsxToAmslAlt;
                }
                else {
                    BridgeSimConnect.backupSimToAmslAlt = 0.0;
                    BridgeSimConnect.backupPsxToAmslAlt = 0.0;
                    altToSim = argAlt;
                }
                if (argAlt > BridgeSimConnect.toAltOffsetCancelAlt) {
                    BridgeSimConnect.backupSimToAmslAlt = 0.0;
                    BridgeSimConnect.backupPsxToAmslAlt = 0.0;
                    BridgeSimConnect.toAltOffsetCancelAlt = 0.0;
                    BridgeSimConnect.doTakeoffAltOffset = false;
                    BridgeSimConnect.doTakeoffWithPosOffset = false;
                    BridgeSimConnect.doLandingWithPosOffset = false;
                    BridgeSimConnect.doBackupLdgOffsets = true;
                    offsetsLocal2[1] = (offsetsLocal2[0] = 0.0);
                    StatusMonitor.setSimBridgeAlignPsxWithDepRwy(false);
                    altToSim = argAlt;
                }
                if (BridgeSimConnect.doTakeoffWithPosOffset) {
                    latitude2 = argLat + offsetsLocal2[0];
                    longitude2 = argLon + offsetsLocal2[1];
                }
            }
            else {
                SocketClientPSXMain.send("Qi198=" + Qi198Elev);
                BridgeSimConnect.liftDecompInProgress = false;
                BridgeSimConnect.liftDecompArmed = false;
                BridgeSimConnect.smoothFlareTransition = true;
                StatusMonitor.setSimBridgeAlignPsxWithDepRwy(false);
                if (argAlt > 20000.0) {
                    BridgeSimConnect.doLandingWithPosOffset = false;
                    BridgeSimConnect.doBackupLdgOffsets = true;
                    BridgeSimConnect.doTakeoffWithPosOffset = false;
                    offsetsLocal2[1] = (offsetsLocal2[0] = 0.0);
                }
                else if (argAlt <= 20000.0) {
                    BridgeSimConnect.doLandingWithPosOffset = true;
                    BridgeSimConnect.doBackupLdgOffsets = true;
                    BridgeSimConnect.doTakeoffWithPosOffset = true;
                    if (!inhibitOffsets2) {
                        latitude2 = argLat + offsetsLocal2[0];
                        longitude2 = argLon + offsetsLocal2[1];
                    }
                    else {
                        latitude2 = argLat;
                        longitude2 = argLon;
                    }
                }
                altToSim = argAlt;
            }
            if (!BridgeSimConnect.doLandingWithPosOffset && !BridgeSimConnect.doTakeoffWithPosOffset && !BridgeSimConnect.freezeSim) {
                latitude2 = argLat;
                longitude2 = argLon;
            }
            double gndAltToSim = 0.0;
            if (!argAirborne && getSimGearPos() == 1) {
                if (!BridgeSimConnect.smoothFlareTransition) {
                    gndAltToSim = targetGndElev3;
                }
                else {
                    final double FLAREHEIGHTTOLERANCE = 0.15;
                    if (simPosAltAttLocal2[2] - 0.15 > targetGndElev3) {
                        gndAltToSim = simPosAltAttLocal2[2] - 0.15;
                    }
                    else if (simPosAltAttLocal2[2] + 0.15 < targetGndElev3) {
                        gndAltToSim = simPosAltAttLocal2[2] + 0.15;
                    }
                    else {
                        gndAltToSim = targetGndElev3;
                        BridgeSimConnect.smoothFlareTransition = false;
                    }
                }
                BridgeSimConnect.sc.setDataOnSimObject((Enum)DATA_DEFINE_ID.DEFINITION_GND_POS_UPDATE, 0, new double[] { latitude2, longitude2, gndAltToSim, -argPitch, argBank, argHdg, 0.0 });
            }
            else {
                BridgeSimConnect.sc.setDataOnSimObject((Enum)DATA_DEFINE_ID.DEFINITION_AIR_POS_UPDATE, 0, new double[] { latitude2, longitude2, altToSim, -argPitch, argBank, argHdg, SimBridgeBase.getPsxTas() });
            }
            BridgeSimConnect.freezeSim = false;
            final double latDiff2 = Math.abs(latitude2 - argLat);
            final double lonDiff2 = Math.abs(longitude2 - argLon);
            final double amslDiff = Math.abs(altToSim - argAlt);
            if (latDiff2 != 0.0 || lonDiff2 != 0.0) {
                StatusMonitor.setSimBridgePosOffsetUsed(true);
            }
            else {
                StatusMonitor.setSimBridgePosOffsetUsed(false);
            }
            if (amslDiff != 0.0 && argAirborne) {
                StatusMonitor.setSimBridgeAltOffsetUsed(true);
            }
            else {
                StatusMonitor.setSimBridgeAltOffsetUsed(false);
            }
        }
    }
    
    public static void notifyPsxSituLoad(final int argChange) {
        final SituReloadSyncThread reloadSyncThread = new SituReloadSyncThread();
        final Thread syncThread = new Thread(reloadSyncThread);
        syncThread.start();
        try {
            BridgeSimConnect.liftDecompInProgress = false;
            BridgeSimConnect.liftDecompArmed = false;
            BridgeSimConnect.smoothFlareTransition = false;
            if (!StatusMonitor.getSimBridgeSlaveIsSim() && !BridgeSimConnect.psxSlaveLoaded) {
                BridgeSimConnect.psxSlaveLoaded = true;
                BridgeSimConnect.doLandingWithPosOffset = false;
                BridgeSimConnect.doTakeoffAltOffset = true;
                BridgeSimConnect.doTakeoffWithPosOffset = false;
                SimBridgeTimeSync.setPsxTime(getSimTime());
                return;
            }
            if (argChange == 0) {
                if (!StatusMonitor.getSimBridgeUseBetaAlgos()) {
                    BridgeSimConnect.doLandingWithPosOffset = true;
                    BridgeSimConnect.doBackupLdgOffsets = true;
                    BridgeSimConnect.doTakeoffAltOffset = false;
                }
                BridgeSimConnect.doTakeoffWithPosOffset = false;
                BridgeSimConnect.takingOff = false;
            }
            else if (argChange == 1) {
                BridgeSimConnect.doLandingWithPosOffset = false;
                BridgeSimConnect.doTakeoffAltOffset = true;
                BridgeSimConnect.doTakeoffWithPosOffset = false;
                BridgeSimConnect.smoothFlareTransition = false;
                BridgeSimConnect.landing = false;
            }
            SimBridgeOffsetPos.setOffsets(false);
            final int[] date = SimBridgeTimeSync.getPsxDate();
            forceSimTimeSync(date);
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    
    public static double getSimGndAlt() {
        BridgeSimConnect.lockSimGndAlt.lock();
        final double temp = BridgeSimConnect.simGndAlt;
        BridgeSimConnect.lockSimGndAlt.unlock();
        return temp;
    }
    
    public static boolean getSimOnGround() {
        BridgeSimConnect.lockSimOnGround.lock();
        final boolean temp = BridgeSimConnect.simOnGround;
        BridgeSimConnect.lockSimOnGround.unlock();
        return temp;
    }
    
    public static void forceSimTimeSync(final int[] argTime) throws IOException {
        BridgeSimConnect.sc.transmitClientEvent(0, 8, argTime[2], NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
        BridgeSimConnect.sc.transmitClientEvent(0, 9, argTime[3], NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
        BridgeSimConnect.sc.transmitClientEvent(0, 10, argTime[1], NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
        BridgeSimConnect.sc.transmitClientEvent(0, 11, argTime[0], NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
    }
    
    public static void setCom1ActiveFreq(final int argBcd) throws IOException {
        BridgeSimConnect.sc.transmitClientEvent(0, 1, argBcd, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
    }
    
    public static void setCom2ActiveFreq(final int argBcd) throws IOException {
        BridgeSimConnect.sc.transmitClientEvent(0, 2, argBcd, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
    }
    
    public static void setXpndrMode(final int argMode) throws IOException {
        final byte[] data = { (byte)argMode };
        BridgeSimConnect.sc.setClientData(28, 29, data);
    }
    
    public static void toggleXpndrIdent() throws IOException {
        final byte[] data = { 1 };
        BridgeSimConnect.sc.setClientData(28, 30, data);
    }
    
    public static void setXpndrCode(final int argBcd) throws IOException {
        BridgeSimConnect.sc.transmitClientEvent(0, 3, argBcd, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
    }
    
    public static void updateGearPos(final boolean argUp) throws IOException {
        if (argUp) {
            BridgeSimConnect.sc.transmitClientEvent(0, 12, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
        }
        else {
            BridgeSimConnect.sc.transmitClientEvent(0, 13, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
        }
    }
    
    public static void updateLdgLts(final boolean argOn) throws IOException {
        int temp = getSimLightState();
        if (argOn) {
            if ((temp & 0x4) == 0x0) {
                BridgeSimConnect.sc.transmitClientEvent(0, 14, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
                setSimLightState(temp |= 0x4);
            }
        }
        else if ((temp & 0x4) != 0x0) {
            BridgeSimConnect.sc.transmitClientEvent(0, 14, 0, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
            setSimLightState(temp &= 0x3FB);
        }
    }
    
    public static void updateLtsTaxi(final boolean argOn) throws IOException {
        int temp = getSimLightState();
        if (argOn) {
            if ((temp & 0x8) == 0x0) {
                BridgeSimConnect.sc.transmitClientEvent(0, 17, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
                setSimLightState(temp |= 0x8);
            }
        }
        else if ((temp & 0x8) != 0x0) {
            BridgeSimConnect.sc.transmitClientEvent(0, 17, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
            setSimLightState(temp &= 0x3F7);
        }
    }
    
    public static void updateLtsBcn(final boolean argOn) throws IOException {
        int temp = getSimLightState();
        if (argOn) {
            if ((temp & 0x2) == 0x0) {
                BridgeSimConnect.sc.transmitClientEvent(0, 18, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
                setSimLightState(temp |= 0x2);
            }
        }
        else if ((temp & 0x2) != 0x0) {
            BridgeSimConnect.sc.transmitClientEvent(0, 18, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
            setSimLightState(temp &= 0x3FD);
        }
    }
    
    public static void updateLtsNav(final boolean argOn) throws IOException {
        int temp = getSimLightState();
        if (argOn) {
            if ((temp & 0x1) == 0x0) {
                BridgeSimConnect.sc.transmitClientEvent(0, 19, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
                setSimLightState(temp |= 0x1);
            }
        }
        else if ((temp & 0x1) != 0x0) {
            BridgeSimConnect.sc.transmitClientEvent(0, 19, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
            setSimLightState(temp &= 0x3FE);
        }
    }
    
    public static void updateLtsStrobe(final boolean argOn) throws IOException {
        int temp = getSimLightState();
        if (argOn) {
            if ((temp & 0x10) == 0x0) {
                BridgeSimConnect.sc.transmitClientEvent(0, 20, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
                setSimLightState(temp |= 0x10);
            }
        }
        else if ((temp & 0x10) != 0x0) {
            BridgeSimConnect.sc.transmitClientEvent(0, 20, 0, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
            setSimLightState(temp &= 0x3EF);
        }
    }
    
    public static void updateLtsWing(final boolean argOn) throws IOException {
        int temp = getSimLightState();
        if (argOn) {
            if ((temp & 0x80) == 0x0) {
                BridgeSimConnect.sc.transmitClientEvent(0, 21, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
                setSimLightState(temp |= 0x80);
            }
        }
        else if ((temp & 0x80) != 0x0) {
            BridgeSimConnect.sc.transmitClientEvent(0, 21, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
            setSimLightState(temp &= 0x37F);
        }
    }
    
    public static void updateLtsLogo(final boolean argOn) throws IOException {
        int temp = getSimLightState();
        if (argOn) {
            if ((temp & 0x100) == 0x0) {
                BridgeSimConnect.sc.transmitClientEvent(0, 22, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
                setSimLightState(temp |= 0x100);
            }
        }
        else if ((temp & 0x100) != 0x0) {
            BridgeSimConnect.sc.transmitClientEvent(0, 22, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
            setSimLightState(temp &= 0x2FF);
        }
    }
    
    public static void updateLtsCabin(final boolean argOn) throws IOException {
        int temp = getSimLightState();
        if (argOn) {
            if ((temp & 0x200) == 0x0) {
                BridgeSimConnect.sc.transmitClientEvent(0, 23, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
                setSimLightState(temp |= 0x200);
            }
        }
        else if ((temp & 0x200) != 0x0) {
            BridgeSimConnect.sc.transmitClientEvent(0, 23, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
            setSimLightState(temp &= 0x1FF);
        }
    }
    
    public static void updateWdoHeat(final boolean argOn) throws IOException {
    }
    
    public static void updateAntiIceEng1(final boolean argOn) throws IOException {
    }
    
    public static void updateAntiIceEng2(final boolean argOn) throws IOException {
    }
    
    public static void updateAntiIceEng3(final boolean argOn) throws IOException {
    }
    
    public static void updateAntiIceEng4(final boolean argOn) throws IOException {
    }
    
    public static void updateAntiIceWing(final boolean argOn) throws IOException {
        if (argOn) {
            BridgeSimConnect.sc.transmitClientEvent(0, 59, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
        }
        else {
            BridgeSimConnect.sc.transmitClientEvent(0, 59, 0, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
        }
    }
    
    public static void updateElevPos(final int argPos) throws IOException {
        BridgeSimConnect.sc.transmitClientEvent(0, 15, argPos, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
    }
    
    public static void updateAileronsPos(final int argPos) throws IOException {
        BridgeSimConnect.sc.transmitClientEvent(0, 16, argPos, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
    }
    
    public static void updateRudderPos(final int argPos) throws IOException {
        BridgeSimConnect.sc.transmitClientEvent(0, 25, argPos, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
    }
    
    public static void updateSpdbrkPos(final int argPos) throws IOException {
        BridgeSimConnect.sc.transmitClientEvent(0, 26, argPos, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
    }
    
    public static void updateFlapsPos(final int argPos) throws IOException {
        BridgeSimConnect.sc.transmitClientEvent(0, 27, argPos, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
    }
    
    public static void updateParkingBrake(final int argValue) throws IOException {
        BridgeSimConnect.lockParkBrkSet.lock();
        final boolean temp = BridgeSimConnect.parkBrkSet;
        BridgeSimConnect.lockParkBrkSet.unlock();
        if ((argValue == 0 && temp) || (argValue == 1 && !temp)) {
            BridgeSimConnect.sc.transmitClientEvent(0, 31, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
            if (temp) {
                BridgeSimConnect.lockParkBrkSet.lock();
                BridgeSimConnect.parkBrkSet = false;
                BridgeSimConnect.lockParkBrkSet.unlock();
            }
            else {
                BridgeSimConnect.lockParkBrkSet.lock();
                BridgeSimConnect.parkBrkSet = true;
                BridgeSimConnect.lockParkBrkSet.unlock();
            }
        }
    }
    
    public static void startEngines(final int argIndex) throws IOException {
        BridgeSimConnect.sc.transmitClientEvent(0, 38, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
        if (argIndex == 1) {
            BridgeSimConnect.lockEng1Starter.lock();
            final boolean starter1 = BridgeSimConnect.eng1Starter;
            BridgeSimConnect.lockEng1Starter.unlock();
            if (!starter1) {
                BridgeSimConnect.sc.transmitClientEvent(0, 33, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
                BridgeSimConnect.lockEng1Starter.lock();
                BridgeSimConnect.eng1Starter = true;
                BridgeSimConnect.lockEng1Starter.unlock();
            }
            BridgeSimConnect.sc.transmitClientEvent(0, 39, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
        }
        else if (argIndex == 2) {
            BridgeSimConnect.lockEng2Starter.lock();
            final boolean starter2 = BridgeSimConnect.eng2Starter;
            BridgeSimConnect.lockEng2Starter.unlock();
            if (!starter2) {
                BridgeSimConnect.sc.transmitClientEvent(0, 47, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
                BridgeSimConnect.lockEng2Starter.lock();
                BridgeSimConnect.eng2Starter = true;
                BridgeSimConnect.lockEng2Starter.unlock();
            }
            BridgeSimConnect.sc.transmitClientEvent(0, 41, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
        }
        else if (argIndex == 3) {
            BridgeSimConnect.lockEng3Starter.lock();
            final boolean starter3 = BridgeSimConnect.eng3Starter;
            BridgeSimConnect.lockEng3Starter.unlock();
            if (!starter3) {
                BridgeSimConnect.sc.transmitClientEvent(0, 48, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
                BridgeSimConnect.lockEng3Starter.lock();
                BridgeSimConnect.eng3Starter = true;
                BridgeSimConnect.lockEng3Starter.unlock();
            }
            BridgeSimConnect.sc.transmitClientEvent(0, 43, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
        }
        else if (argIndex == 4) {
            BridgeSimConnect.lockEng4Starter.lock();
            final boolean starter4 = BridgeSimConnect.eng4Starter;
            BridgeSimConnect.lockEng4Starter.unlock();
            if (!starter4) {
                BridgeSimConnect.sc.transmitClientEvent(0, 49, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
                BridgeSimConnect.lockEng4Starter.lock();
                BridgeSimConnect.eng4Starter = true;
                BridgeSimConnect.lockEng4Starter.unlock();
            }
            BridgeSimConnect.sc.transmitClientEvent(0, 45, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
        }
    }
    
    public static void stopEngines(final int argIndex) throws IOException {
        if (argIndex == 1) {
            BridgeSimConnect.lockEng1Starter.lock();
            final boolean starter1 = BridgeSimConnect.eng1Starter;
            BridgeSimConnect.lockEng1Starter.unlock();
            if (starter1) {
                BridgeSimConnect.sc.transmitClientEvent(0, 33, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
                BridgeSimConnect.lockEng1Starter.lock();
                BridgeSimConnect.eng1Starter = false;
                BridgeSimConnect.lockEng1Starter.unlock();
            }
            BridgeSimConnect.sc.transmitClientEvent(0, 40, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
        }
        else if (argIndex == 2) {
            BridgeSimConnect.lockEng2Starter.lock();
            final boolean starter2 = BridgeSimConnect.eng2Starter;
            BridgeSimConnect.lockEng2Starter.unlock();
            if (starter2) {
                BridgeSimConnect.sc.transmitClientEvent(0, 47, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
                BridgeSimConnect.lockEng2Starter.lock();
                BridgeSimConnect.eng2Starter = false;
                BridgeSimConnect.lockEng2Starter.unlock();
            }
            BridgeSimConnect.sc.transmitClientEvent(0, 42, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
        }
        else if (argIndex == 3) {
            BridgeSimConnect.lockEng3Starter.lock();
            final boolean starter3 = BridgeSimConnect.eng3Starter;
            BridgeSimConnect.lockEng3Starter.unlock();
            if (starter3) {
                BridgeSimConnect.sc.transmitClientEvent(0, 48, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
                BridgeSimConnect.lockEng3Starter.lock();
                BridgeSimConnect.eng3Starter = false;
                BridgeSimConnect.lockEng3Starter.unlock();
            }
            BridgeSimConnect.sc.transmitClientEvent(0, 44, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
        }
        else if (argIndex == 4) {
            BridgeSimConnect.lockEng4Starter.lock();
            final boolean starter4 = BridgeSimConnect.eng4Starter;
            BridgeSimConnect.lockEng4Starter.unlock();
            if (starter4) {
                BridgeSimConnect.sc.transmitClientEvent(0, 49, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
                BridgeSimConnect.lockEng4Starter.lock();
                BridgeSimConnect.eng4Starter = false;
                BridgeSimConnect.lockEng4Starter.unlock();
            }
            BridgeSimConnect.sc.transmitClientEvent(0, 46, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
        }
    }
    
    public static void updateThrottles(final int argThr1, final int argThr2, final int argThr3, final int argThr4) throws IOException {
        BridgeSimConnect.sc.transmitClientEvent(0, 34, argThr1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
        BridgeSimConnect.sc.transmitClientEvent(0, 35, argThr2, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
        BridgeSimConnect.sc.transmitClientEvent(0, 36, argThr3, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
        BridgeSimConnect.sc.transmitClientEvent(0, 37, argThr4, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
    }
    
    public static void openDoors(final int argIndex) throws IOException {
        BridgeSimConnect.lockDoorsOpened.lock();
        final boolean opened = BridgeSimConnect.doorsOpened;
        BridgeSimConnect.lockDoorsOpened.unlock();
        if (!opened) {
            BridgeSimConnect.sc.transmitClientEvent(0, 50, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
            BridgeSimConnect.lockDoorsOpened.lock();
            BridgeSimConnect.doorsOpened = true;
            BridgeSimConnect.lockDoorsOpened.unlock();
        }
    }
    
    public static void closeDoors(final int argIndex) throws IOException {
        BridgeSimConnect.lockDoorsOpened.lock();
        final boolean opened = BridgeSimConnect.doorsOpened;
        BridgeSimConnect.lockDoorsOpened.unlock();
        if (opened) {
            BridgeSimConnect.sc.transmitClientEvent(0, 50, 1, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
            BridgeSimConnect.lockDoorsOpened.lock();
            BridgeSimConnect.doorsOpened = false;
            BridgeSimConnect.lockDoorsOpened.unlock();
        }
    }
    
    public static void updateAltimeter(final int argValue) throws IOException {
        BridgeSimConnect.sc.transmitClientEvent(0, 62, argValue * 16, NotificationPriority.HIGHEST_MASKABLE.ordinal(), 16);
    }
    
    public static void forceDepRwyOffset() {
        if (!SimBridgeBase.getOnGround()) {
            return;
        }
        BridgeSimConnect.doTakeoffWithPosOffset = true;
        BridgeSimConnect.doLandingWithPosOffset = true;
        BridgeSimConnect.doBackupLdgOffsets = true;
    }
    
    public static void setCancelLdgOffset(final boolean argCancel) {
        BridgeSimConnect.lockCancelLdgOffset.lock();
        BridgeSimConnect.cancelLdgOffset = argCancel;
        BridgeSimConnect.lockCancelLdgOffset.unlock();
    }
    
    public static void setFileSimOffsets(final double[] argOffsets) {
        BridgeSimConnect.lockOffsets.lock();
        BridgeSimConnect.offsets = argOffsets;
        BridgeSimConnect.lockOffsets.unlock();
    }
    
    public static double[] getSimPosAltAtt() {
        BridgeSimConnect.lockSimPosAltAtt.lock();
        final double[] temp = BridgeSimConnect.simPosAltAtt;
        BridgeSimConnect.lockSimPosAltAtt.unlock();
        return temp;
    }
    
    public static double getSimOat() {
        BridgeSimConnect.lockAmbientOat.lock();
        final double temp = BridgeSimConnect.ambientOat;
        BridgeSimConnect.lockAmbientOat.unlock();
        return temp;
    }
    
    public static double getSimWindDir() {
        BridgeSimConnect.lockAmbientWindDir.lock();
        final double temp = BridgeSimConnect.ambientWindDir;
        BridgeSimConnect.lockAmbientWindDir.unlock();
        return temp;
    }
    
    public static double getSimWindVel() {
        BridgeSimConnect.lockAmbientWindVel.lock();
        final double temp = BridgeSimConnect.ambientWindVel;
        BridgeSimConnect.lockAmbientWindVel.unlock();
        return temp;
    }
    
    public static int getSimLightState() {
        BridgeSimConnect.lockSimLightState.lock();
        final int temp = BridgeSimConnect.simLightState;
        BridgeSimConnect.lockSimLightState.unlock();
        return temp;
    }
    
    private static int[] getSimTime() {
        BridgeSimConnect.lockSimTime.lock();
        final int[] temp = BridgeSimConnect.simTime;
        BridgeSimConnect.lockSimTime.unlock();
        return temp;
    }
    
    private static boolean getCancelLdgoffset() {
        BridgeSimConnect.lockCancelLdgOffset.lock();
        final boolean temp = BridgeSimConnect.cancelLdgOffset;
        BridgeSimConnect.lockCancelLdgOffset.unlock();
        return temp;
    }
    
    private static double[] getOffsets() {
        BridgeSimConnect.lockOffsets.lock();
        final double[] temp = BridgeSimConnect.offsets;
        BridgeSimConnect.lockOffsets.unlock();
        return temp;
    }
    
    private static void setSimLightState(final int argState) {
        BridgeSimConnect.lockSimLightState.lock();
        BridgeSimConnect.simLightState = argState;
        BridgeSimConnect.lockSimLightState.unlock();
    }
    
    private static int getSimGearPos() {
        BridgeSimConnect.lockSimGearPos.lock();
        final int temp = BridgeSimConnect.simGearPos;
        BridgeSimConnect.lockSimGearPos.unlock();
        return temp;
    }
    
    private void configureDispatch(final SimConnect argSc) throws IOException {
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINTION_WINDOAT, "Ambient temperature", "celsius", SimConnectDataType.FLOAT64);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINTION_WINDOAT, "Ambient wind velocity", "knots", SimConnectDataType.FLOAT64);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINTION_WINDOAT, "Ambient wind direction", "degrees", SimConnectDataType.FLOAT64);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_AITRAFFIC, "Plane latitude", "radians", SimConnectDataType.FLOAT64);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_AITRAFFIC, "Plane longitude", "radians", SimConnectDataType.FLOAT64);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_AITRAFFIC, "Plane altitude", "feet", SimConnectDataType.FLOAT64);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_AITRAFFIC, "Ground velocity", "knots", SimConnectDataType.FLOAT64);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_AITRAFFIC, "Airspeed indicated", "knots", SimConnectDataType.FLOAT64);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_AITRAFFIC, "Plane heading degrees true", "radians", SimConnectDataType.FLOAT64);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_AITRAFFIC, "ATC MODEL", "", SimConnectDataType.STRING32);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_AITRAFFIC, "ATC ID", "", SimConnectDataType.STRING32);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_USER_ACFT_ATC_ID, "ATC ID", "", SimConnectDataType.STRING32);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_LIGHT_STATE, "LIGHT STATES", "Mask", SimConnectDataType.INT32);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_PARK_BRAKE_STATE, "BRAKE PARKING POSITION", "Position", SimConnectDataType.INT32);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_SIM_POS_ALT_ATT, "Plane latitude", "radians", SimConnectDataType.FLOAT64);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_SIM_POS_ALT_ATT, "Plane longitude", "radians", SimConnectDataType.FLOAT64);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_SIM_POS_ALT_ATT, "Plane alt above ground", "feet", SimConnectDataType.FLOAT64);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_SIM_POS_ALT_ATT, "Plane altitude", "feet", SimConnectDataType.FLOAT64);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_SIM_POS_ALT_ATT, "Plane pitch degrees", "radians", SimConnectDataType.FLOAT64);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_SIM_POS_ALT_ATT, "Plane bank degrees", "radians", SimConnectDataType.FLOAT64);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_SIM_POS_ALT_ATT, "Plane heading degrees true", "radians", SimConnectDataType.FLOAT64);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_SIM_ON_GROUND, "Sim on ground", "bool", SimConnectDataType.INT32);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_SIM_GND_ALT, "Ground altitude", "meters", SimConnectDataType.FLOAT64);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_SIM_GMT_YEAR, "Zulu year", "number", SimConnectDataType.INT32);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_SIM_GMT_MONTH_OF_YEAR, "Zulu month of year", "number", SimConnectDataType.INT32);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_SIM_GMT_DAY_OF_MONTH, "Zulu day of month", "number", SimConnectDataType.INT32);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_SIM_GMT_TIME, "Zulu time", "seconds", SimConnectDataType.INT32);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_GEAR_POSITION, "Gear position", "number", SimConnectDataType.INT32);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_FUEL_TANK_CTR_LEVEL, "FUEL TANK CENTER QUANTITY", "gallons", SimConnectDataType.FLOAT64);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_TAS, "AIRSPEED TRUE", "knots", SimConnectDataType.FLOAT64);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_ENGINE_1_STARTER, "GENERAL ENG STARTER:1", "bool", SimConnectDataType.INT32);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_ENGINE_2_STARTER, "GENERAL ENG STARTER:2", "bool", SimConnectDataType.INT32);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_ENGINE_3_STARTER, "GENERAL ENG STARTER:3", "bool", SimConnectDataType.INT32);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_ENGINE_4_STARTER, "GENERAL ENG STARTER:4", "bool", SimConnectDataType.INT32);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_DOORS, "EXIT OPEN:0", "percent", SimConnectDataType.FLOAT64);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_DOORS_PAX, "EXIT OPEN:3", "percent", SimConnectDataType.FLOAT64);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_AIR_POS_UPDATE, "Plane latitude", "radians", SimConnectDataType.FLOAT64);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_AIR_POS_UPDATE, "Plane longitude", "radians", SimConnectDataType.FLOAT64);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_AIR_POS_UPDATE, "Plane altitude", "feet", SimConnectDataType.FLOAT64);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_AIR_POS_UPDATE, "Plane pitch degrees", "radians", SimConnectDataType.FLOAT64);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_AIR_POS_UPDATE, "Plane bank degrees", "radians", SimConnectDataType.FLOAT64);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_AIR_POS_UPDATE, "Plane heading degrees true", "radians", SimConnectDataType.FLOAT64);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_AIR_POS_UPDATE, "AIRSPEED TRUE", "knots", SimConnectDataType.FLOAT64);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_GND_POS_UPDATE, "Plane latitude", "radians", SimConnectDataType.FLOAT64);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_GND_POS_UPDATE, "Plane longitude", "radians", SimConnectDataType.FLOAT64);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_GND_POS_UPDATE, "Plane alt above ground", "feet", SimConnectDataType.FLOAT64);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_GND_POS_UPDATE, "Plane pitch degrees", "radians", SimConnectDataType.FLOAT64);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_GND_POS_UPDATE, "Plane bank degrees", "radians", SimConnectDataType.FLOAT64);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_GND_POS_UPDATE, "Plane heading degrees true", "radians", SimConnectDataType.FLOAT64);
        argSc.addToDataDefinition((Enum)DATA_DEFINE_ID.DEFINITION_GND_POS_UPDATE, "AIRSPEED TRUE", "knots", SimConnectDataType.FLOAT64);
        argSc.mapClientDataNameToID("SquawkBox Data", 28);
        argSc.addToClientDataDefinition(29, 17, 1, 0.0f, 0);
        argSc.addToClientDataDefinition(30, 19, 1, 0.0f, 0);
        argSc.mapClientEventToSimEvent(1, "COM_RADIO_SET");
        argSc.mapClientEventToSimEvent(2, "COM2_RADIO_SET");
        argSc.mapClientEventToSimEvent(3, "XPNDR_SET");
        argSc.mapClientEventToSimEvent(4, "FREEZE_LATITUDE_LONGITUDE_SET");
        argSc.mapClientEventToSimEvent(5, "FREEZE_ALTITUDE_SET");
        argSc.mapClientEventToSimEvent(6, "FREEZE_ATTITUDE_SET");
        argSc.mapClientEventToSimEvent(8, "ZULU_HOURS_SET");
        argSc.mapClientEventToSimEvent(9, "ZULU_MINUTES_SET");
        argSc.mapClientEventToSimEvent(10, "ZULU_DAY_SET");
        argSc.mapClientEventToSimEvent(11, "ZULU_YEAR_SET");
        argSc.mapClientEventToSimEvent(12, "GEAR_UP");
        argSc.mapClientEventToSimEvent(13, "GEAR_DOWN");
        argSc.mapClientEventToSimEvent(14, "LANDING_LIGHTS_SET");
        argSc.mapClientEventToSimEvent(17, "TOGGLE_TAXI_LIGHTS");
        argSc.mapClientEventToSimEvent(18, "TOGGLE_BEACON_LIGHTS");
        argSc.mapClientEventToSimEvent(19, "TOGGLE_NAV_LIGHTS");
        argSc.mapClientEventToSimEvent(20, "STROBES_SET");
        argSc.mapClientEventToSimEvent(21, "TOGGLE_WING_LIGHTS");
        argSc.mapClientEventToSimEvent(22, "TOGGLE_LOGO_LIGHTS");
        argSc.mapClientEventToSimEvent(23, "TOGGLE_CABIN_LIGHTS");
        argSc.mapClientEventToSimEvent(15, "ELEVATOR_SET");
        argSc.mapClientEventToSimEvent(16, "AILERON_SET");
        argSc.mapClientEventToSimEvent(25, "RUDDER_SET");
        argSc.mapClientEventToSimEvent(26, "SPOILERS_SET");
        argSc.mapClientEventToSimEvent(27, "FLAPS_SET");
        argSc.mapClientEventToSimEvent(31, "PARKING_BRAKES");
        argSc.mapClientEventToSimEvent(38, "FUEL_SELECTOR_ALL");
        argSc.mapClientEventToSimEvent(39, "MIXTURE1_RICH");
        argSc.mapClientEventToSimEvent(40, "MIXTURE1_LEAN");
        argSc.mapClientEventToSimEvent(41, "MIXTURE2_RICH");
        argSc.mapClientEventToSimEvent(42, "MIXTURE2_LEAN");
        argSc.mapClientEventToSimEvent(43, "MIXTURE3_RICH");
        argSc.mapClientEventToSimEvent(44, "MIXTURE3_LEAN");
        argSc.mapClientEventToSimEvent(45, "MIXTURE4_RICH");
        argSc.mapClientEventToSimEvent(46, "MIXTURE4_LEAN");
        argSc.mapClientEventToSimEvent(33, "TOGGLE_STARTER1");
        argSc.mapClientEventToSimEvent(47, "TOGGLE_STARTER2");
        argSc.mapClientEventToSimEvent(48, "TOGGLE_STARTER3");
        argSc.mapClientEventToSimEvent(49, "TOGGLE_STARTER4");
        argSc.mapClientEventToSimEvent(34, "AXIS_THROTTLE1_SET");
        argSc.mapClientEventToSimEvent(35, "AXIS_THROTTLE2_SET");
        argSc.mapClientEventToSimEvent(36, "AXIS_THROTTLE3_SET");
        argSc.mapClientEventToSimEvent(37, "AXIS_THROTTLE4_SET");
        argSc.mapClientEventToSimEvent(50, "TOGGLE_AIRCRAFT_EXIT");
        argSc.mapClientEventToSimEvent(52, "SELECT_2");
        argSc.mapClientEventToSimEvent(53, "SELECT_3");
        argSc.mapClientEventToSimEvent(54, "SELECT_4");
        argSc.mapClientEventToSimEvent(55, "ANTI_ICE_SET_ENG1");
        argSc.mapClientEventToSimEvent(56, "ANTI_ICE_SET_ENG2");
        argSc.mapClientEventToSimEvent(57, "ANTI_ICE_SET_ENG3");
        argSc.mapClientEventToSimEvent(58, "ANTI_ICE_SET_ENG4");
        argSc.mapClientEventToSimEvent(59, "ANTI_ICE_SET");
        argSc.mapClientEventToSimEvent(62, "KOHLSMAN_SET");
        argSc.subscribeToSystemEvent(0, "4sec");
        argSc.subscribeToSystemEvent(24, "1sec");
        argSc.subscribeToSystemEvent(7, "6Hz");
        final DispatcherTask dt = new DispatcherTask(argSc);
        dt.addOpenHandler((OpenHandler)this);
        dt.addQuitHandler((QuitHandler)this);
        dt.addSimObjectDataTypeHandler((SimObjectDataTypeHandler)this);
        dt.addExceptionHandler((ExceptionHandler)this);
        dt.addEventHandler((EventHandler)new EventHandler() {
            public void handleEvent(final SimConnect sender, final RecvEvent e) {
                if (e.getEventID() == 0) {
                    try {
                        sender.requestDataOnSimObjectType((Enum)DATA_REQUEST_ID.REQUEST_WINDOAT, (Enum)DATA_DEFINE_ID.DEFINTION_WINDOAT, 0, SimObjectType.USER);
                        sender.requestDataOnSimObjectType((Enum)DATA_REQUEST_ID.REQUEST_AITRAFFIC, (Enum)DATA_DEFINE_ID.DEFINITION_AITRAFFIC, 75000, SimObjectType.AIRCRAFT);
                        sender.requestDataOnSimObjectType((Enum)DATA_REQUEST_ID.REQUEST_USER_ACFT_ATC_ID, (Enum)DATA_DEFINE_ID.DEFINITION_USER_ACFT_ATC_ID, 0, SimObjectType.USER);
                        sender.requestDataOnSimObjectType((Enum)DATA_REQUEST_ID.REQUEST_FUEL_TANK_CTR_LEVEL, (Enum)DATA_DEFINE_ID.DEFINITION_FUEL_TANK_CTR_LEVEL, 0, SimObjectType.USER);
                    }
                    catch (IOException ex) {}
                }
                if (e.getEventID() == 24) {
                    try {
                        sender.requestDataOnSimObjectType((Enum)DATA_REQUEST_ID.REQUEST_LIGHT_STATE, (Enum)DATA_DEFINE_ID.DEFINITION_LIGHT_STATE, 0, SimObjectType.USER);
                        sender.requestDataOnSimObjectType((Enum)DATA_REQUEST_ID.REQUEST_PARK_BRK_STATE, (Enum)DATA_DEFINE_ID.DEFINITION_PARK_BRAKE_STATE, 0, SimObjectType.USER);
                        sender.requestDataOnSimObjectType((Enum)DATA_REQUEST_ID.REQUEST_SIM_ON_GROUND, (Enum)DATA_DEFINE_ID.DEFINITION_SIM_ON_GROUND, 0, SimObjectType.USER);
                        sender.requestDataOnSimObjectType((Enum)DATA_REQUEST_ID.REQUEST_SIM_GMT_YEAR, (Enum)DATA_DEFINE_ID.DEFINITION_SIM_GMT_YEAR, 0, SimObjectType.USER);
                        sender.requestDataOnSimObjectType((Enum)DATA_REQUEST_ID.REQUEST_SIM_GMT_MONTH_OF_YEAR, (Enum)DATA_DEFINE_ID.DEFINITION_SIM_GMT_MONTH_OF_YEAR, 0, SimObjectType.USER);
                        sender.requestDataOnSimObjectType((Enum)DATA_REQUEST_ID.REQUEST_SIM_GMT_DAY_OF_MONTH, (Enum)DATA_DEFINE_ID.DEFINITION_SIM_GMT_DAY_OF_MONTH, 0, SimObjectType.USER);
                        sender.requestDataOnSimObjectType((Enum)DATA_REQUEST_ID.REQUEST_SIM_GMT_TIME, (Enum)DATA_DEFINE_ID.DEFINITION_SIM_GMT_TIME, 0, SimObjectType.USER);
                        sender.requestDataOnSimObjectType((Enum)DATA_REQUEST_ID.REQUEST_GEAR_POSITION, (Enum)DATA_DEFINE_ID.DEFINITION_GEAR_POSITION, 0, SimObjectType.USER);
                        sender.requestDataOnSimObjectType((Enum)DATA_REQUEST_ID.REQUEST_ENGINE_1_STARTER, (Enum)DATA_DEFINE_ID.DEFINITION_ENGINE_1_STARTER, 0, SimObjectType.USER);
                        sender.requestDataOnSimObjectType((Enum)DATA_REQUEST_ID.REQUEST_ENGINE_2_STARTER, (Enum)DATA_DEFINE_ID.DEFINITION_ENGINE_2_STARTER, 0, SimObjectType.USER);
                        sender.requestDataOnSimObjectType((Enum)DATA_REQUEST_ID.REQUEST_ENGINE_3_STARTER, (Enum)DATA_DEFINE_ID.DEFINITION_ENGINE_3_STARTER, 0, SimObjectType.USER);
                        sender.requestDataOnSimObjectType((Enum)DATA_REQUEST_ID.REQUEST_ENGINE_4_STARTER, (Enum)DATA_DEFINE_ID.DEFINITION_ENGINE_4_STARTER, 0, SimObjectType.USER);
                        sender.requestDataOnSimObjectType((Enum)DATA_REQUEST_ID.REQUEST_DOORS, (Enum)DATA_DEFINE_ID.DEFINITION_DOORS, 0, SimObjectType.USER);
                        sender.requestDataOnSimObjectType((Enum)DATA_REQUEST_ID.REQUEST_DOORS_PAX, (Enum)DATA_DEFINE_ID.DEFINITION_DOORS_PAX, 0, SimObjectType.USER);
                    }
                    catch (IOException ex2) {}
                }
                if (e.getEventID() == 7) {
                    try {
                        sender.requestDataOnSimObjectType((Enum)DATA_REQUEST_ID.REQUEST_SIM_POS_ALT_ATT, (Enum)DATA_DEFINE_ID.DEFINITION_SIM_POS_ALT_ATT, 0, SimObjectType.USER);
                        sender.requestDataOnSimObjectType((Enum)DATA_REQUEST_ID.REQUEST_SIM_GND_ALT, (Enum)DATA_DEFINE_ID.DEFINITION_SIM_GND_ALT, 0, SimObjectType.USER);
                    }
                    catch (IOException ex3) {}
                }
            }
        });
        dt.createThread().start();
    }
    
    enum DATA_DEFINE_ID
    {
        DEFINTION_WINDOAT("DEFINTION_WINDOAT", 0), 
        DEFINITION_AITRAFFIC("DEFINITION_AITRAFFIC", 1), 
        DEFINITION_USER_ACFT_ATC_ID("DEFINITION_USER_ACFT_ATC_ID", 2), 
        DEFINITION_LIGHT_STATE("DEFINITION_LIGHT_STATE", 3), 
        DEFINITION_PARK_BRAKE_STATE("DEFINITION_PARK_BRAKE_STATE", 4), 
        DEFINITION_SIM_POS_ALT_ATT("DEFINITION_SIM_POS_ALT_ATT", 5), 
        DEFINITION_SIM_ON_GROUND("DEFINITION_SIM_ON_GROUND", 6), 
        DEFINITION_SIM_GMT_YEAR("DEFINITION_SIM_GMT_YEAR", 7), 
        DEFINITION_SIM_GMT_MONTH_OF_YEAR("DEFINITION_SIM_GMT_MONTH_OF_YEAR", 8), 
        DEFINITION_SIM_GMT_DAY_OF_MONTH("DEFINITION_SIM_GMT_DAY_OF_MONTH", 9), 
        DEFINITION_SIM_GMT_TIME("DEFINITION_SIM_GMT_TIME", 10), 
        DEFINITION_SIM_GND_ALT("DEFINITION_SIM_GND_ALT", 11), 
        DEFINITION_GEAR_POSITION("DEFINITION_GEAR_POSITION", 12), 
        DEFINITION_FUEL_TANK_CTR_LEVEL("DEFINITION_FUEL_TANK_CTR_LEVEL", 13), 
        DEFINITION_TAS("DEFINITION_TAS", 14), 
        DEFINITION_ENGINE_1_STARTER("DEFINITION_ENGINE_1_STARTER", 15), 
        DEFINITION_ENGINE_2_STARTER("DEFINITION_ENGINE_2_STARTER", 16), 
        DEFINITION_ENGINE_3_STARTER("DEFINITION_ENGINE_3_STARTER", 17), 
        DEFINITION_ENGINE_4_STARTER("DEFINITION_ENGINE_4_STARTER", 18), 
        DEFINITION_DOORS("DEFINITION_DOORS", 19), 
        DEFINITION_DOORS_PAX("DEFINITION_DOORS_PAX", 20), 
        DEFINITION_GND_POS_UPDATE("DEFINITION_GND_POS_UPDATE", 21), 
        DEFINITION_AIR_POS_UPDATE("DEFINITION_AIR_POS_UPDATE", 22);
        
        private DATA_DEFINE_ID(final String name, final int ordinal) {
        }
    }
    
    enum DATA_REQUEST_ID
    {
        REQUEST_WINDOAT("REQUEST_WINDOAT", 0), 
        REQUEST_AITRAFFIC("REQUEST_AITRAFFIC", 1), 
        REQUEST_USER_ACFT_ATC_ID("REQUEST_USER_ACFT_ATC_ID", 2), 
        REQUEST_LIGHT_STATE("REQUEST_LIGHT_STATE", 3), 
        REQUEST_PARK_BRK_STATE("REQUEST_PARK_BRK_STATE", 4), 
        REQUEST_SIM_POS_ALT_ATT("REQUEST_SIM_POS_ALT_ATT", 5), 
        REQUEST_SIM_ON_GROUND("REQUEST_SIM_ON_GROUND", 6), 
        REQUEST_SIM_GMT_YEAR("REQUEST_SIM_GMT_YEAR", 7), 
        REQUEST_SIM_GMT_MONTH_OF_YEAR("REQUEST_SIM_GMT_MONTH_OF_YEAR", 8), 
        REQUEST_SIM_GMT_DAY_OF_MONTH("REQUEST_SIM_GMT_DAY_OF_MONTH", 9), 
        REQUEST_SIM_GMT_TIME("REQUEST_SIM_GMT_TIME", 10), 
        REQUEST_SIM_GND_ALT("REQUEST_SIM_GND_ALT", 11), 
        REQUEST_GEAR_POSITION("REQUEST_GEAR_POSITION", 12), 
        REQUEST_FUEL_TANK_CTR_LEVEL("REQUEST_FUEL_TANK_CTR_LEVEL", 13), 
        REQUEST_ENGINE_1_STARTER("REQUEST_ENGINE_1_STARTER", 14), 
        REQUEST_ENGINE_2_STARTER("REQUEST_ENGINE_2_STARTER", 15), 
        REQUEST_ENGINE_3_STARTER("REQUEST_ENGINE_3_STARTER", 16), 
        REQUEST_ENGINE_4_STARTER("REQUEST_ENGINE_4_STARTER", 17), 
        REQUEST_DOORS("REQUEST_DOORS", 18), 
        REQUEST_DOORS_PAX("REQUEST_DOORS_PAX", 19);
        
        private DATA_REQUEST_ID(final String name, final int ordinal) {
        }
    }
}
