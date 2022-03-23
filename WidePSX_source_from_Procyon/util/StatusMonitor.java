// 
// Decompiled by Procyon v0.5.36
// 

package util;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;

public class StatusMonitor
{
    private static boolean psxMainIsConnected;
    private static boolean psxMainIsDisconnected;
    private static boolean psxMainUnableToConnect;
    private static boolean psxMainInvalidIp;
    private static boolean msfsInUse;
    private static final Lock lockPsxMainIsConnected;
    private static final Lock lockPsxMainIsDisconnected;
    private static final Lock lockPsxMainUnableToConnect;
    private static final Lock lockPsxMainInvalidIp;
    private static final Lock lockMsfsInUse;
    private static boolean simConnectIsEnabled;
    private static boolean simConnectIsConnected;
    private static boolean simConnectIsDisconnected;
    private static boolean simConnectUnableToConnect;
    private static boolean simConnectInvalidIp;
    private static final Lock lockSimConnectIsEnabled;
    private static final Lock lockSimConnectIsConnected;
    private static final Lock lockSimConnectIsDisconnected;
    private static final Lock lockSimConnectUnableToConnect;
    private static final Lock lockSimConnectInvalidIp;
    private static boolean aloftWxDynMode;
    private static boolean aloftWxStaMode;
    private static boolean aloftWxTurbMode;
    private static boolean aloftWxRunning;
    private static boolean aloftWxFileLoaded;
    private static boolean aloftWxTocNotFound;
    private static final Lock lockAloftWxDynMode;
    private static final Lock lockAloftWxStaMode;
    private static final Lock lockAloftWxTurbMode;
    private static final Lock lockAloftWxRunning;
    private static final Lock lockAloftWxFileLoaded;
    private static final Lock lockAloftWxTocNotFound;
    private static boolean turbDataInvalidLowUpLim;
    private static boolean turbWptsNotFound;
    private static boolean turbConfigIsReady;
    private static final Lock lockTurbDataInvalidLowUpLim;
    private static final Lock lockTurbWptsNotFound;
    private static final Lock lockTurbConfigIsReady;
    private static boolean fmcUplinkNoPsxRteLoaded;
    private static boolean fmcUplinkTodDestNotFound;
    private static boolean fmcUplinkNoWxFileLoaded;
    private static final Lock lockFmcUplinkNoPsxRteLoaded;
    private static final Lock lockFmcUplinkTodDestNotFound;
    private static final Lock lockFmcUplinkNoWxFileLoaded;
    private static boolean simSetPsxTraffic;
    private static boolean psxSetSimComXpndr;
    private static boolean sendTrueTasToVATSIM;
    private static final Lock lockSimSetPsxTraffic;
    private static final Lock lockPsxSetSimComXpndr;
    private static final Lock lockSendTrueTasToVATSIM;
    private static boolean printOutputEnabled;
    private static boolean printOutputFailed;
    private static final Lock lockPrintOutputEnabled;
    private static final Lock lockPrintOutputFailed;
    private static boolean gndServiceInvalidTextValues;
    private static boolean gndServiceRunning;
    private static boolean gndServiceColdDarkPhase;
    private static boolean gndServiceRefuelCompletePhase;
    private static boolean gndServicePushReadyPhase;
    private static boolean gndServiceSimplified;
    private static boolean gndServiceExtPush;
    private static String gndServiceVolumeAcp;
    private static final Lock lockGndServiceInvalidTextValues;
    private static final Lock lockGndServiceRunning;
    private static final Lock lockGndServiceColdDarkPhase;
    private static final Lock lockGndServiceRefuelCompletePhase;
    private static final Lock lockGndServicePushReadyPhase;
    private static final Lock lockGndServiceSimplified;
    private static final Lock lockGndServiceExtPush;
    private static final Lock lockGndServiceVolumeAcp;
    private static boolean psxBoostIsConnected;
    private static boolean psxBoostIsDisconnected;
    private static boolean psxBoostUnableToConnect;
    private static boolean psxBoostInvalidIp;
    private static final Lock lockPsxBoostIsConnected;
    private static final Lock lockPsxBoostIsDisconnected;
    private static final Lock lockPsxBoostUnableToConnect;
    private static final Lock lockPsxBoostInvalidIp;
    private static boolean scenGenEnabled;
    private static boolean simBridgeIsRunning;
    private static boolean simBridgeUsingDestOffsets;
    private static boolean simBridgeUsingAllOffsets;
    private static boolean simBridgePosOffsetUsed;
    private static boolean simBridgeAltOffsetUsed;
    private static boolean simBridgeOffsetsNotFound;
    private static boolean simBridgeAutoPsxDepAlign;
    private static boolean simBridgeAlignPsxWithDepRwy;
    private static boolean simBridgeSlaveIsSim;
    private static boolean simBridgeDoorsSync;
    private static boolean simBridgeHeightRefEnabled;
    private static boolean simBridgeUseBetaAlgos;
    private static final Lock lockScenGenEnabled;
    private static final Lock lockSimBridgeIsRunning;
    private static final Lock lockSimBridgeUsingDestOffsets;
    private static final Lock lockSimBridgeUsingAllOffsets;
    private static final Lock lockSimBridgePosOffsetUsed;
    private static final Lock lockSimBridgeAltOffsetUsed;
    private static final Lock lockSimBridgeOffsetsNotFound;
    private static final Lock lockSimBridgeAutoPsxDepAlign;
    private static final Lock lockSimBridgeAlignPsxWithDepRwy;
    private static final Lock lockSimBridgeSlaveIsSim;
    private static final Lock lockSimBridgeDoorsSync;
    private static final Lock lockSimBridgeHeightRefEnabled;
    private static final Lock lockSimBridgeUseBetaAlgos;
    
    static {
        StatusMonitor.psxMainIsConnected = false;
        StatusMonitor.psxMainIsDisconnected = false;
        StatusMonitor.psxMainUnableToConnect = false;
        StatusMonitor.psxMainInvalidIp = false;
        StatusMonitor.msfsInUse = false;
        lockPsxMainIsConnected = new ReentrantLock();
        lockPsxMainIsDisconnected = new ReentrantLock();
        lockPsxMainUnableToConnect = new ReentrantLock();
        lockPsxMainInvalidIp = new ReentrantLock();
        lockMsfsInUse = new ReentrantLock();
        StatusMonitor.simConnectIsEnabled = false;
        StatusMonitor.simConnectIsConnected = false;
        StatusMonitor.simConnectIsDisconnected = false;
        StatusMonitor.simConnectUnableToConnect = false;
        StatusMonitor.simConnectInvalidIp = false;
        lockSimConnectIsEnabled = new ReentrantLock();
        lockSimConnectIsConnected = new ReentrantLock();
        lockSimConnectIsDisconnected = new ReentrantLock();
        lockSimConnectUnableToConnect = new ReentrantLock();
        lockSimConnectInvalidIp = new ReentrantLock();
        StatusMonitor.aloftWxDynMode = false;
        StatusMonitor.aloftWxStaMode = false;
        StatusMonitor.aloftWxTurbMode = false;
        StatusMonitor.aloftWxRunning = false;
        StatusMonitor.aloftWxFileLoaded = false;
        StatusMonitor.aloftWxTocNotFound = false;
        lockAloftWxDynMode = new ReentrantLock();
        lockAloftWxStaMode = new ReentrantLock();
        lockAloftWxTurbMode = new ReentrantLock();
        lockAloftWxRunning = new ReentrantLock();
        lockAloftWxFileLoaded = new ReentrantLock();
        lockAloftWxTocNotFound = new ReentrantLock();
        StatusMonitor.turbDataInvalidLowUpLim = false;
        StatusMonitor.turbWptsNotFound = false;
        StatusMonitor.turbConfigIsReady = false;
        lockTurbDataInvalidLowUpLim = new ReentrantLock();
        lockTurbWptsNotFound = new ReentrantLock();
        lockTurbConfigIsReady = new ReentrantLock();
        StatusMonitor.fmcUplinkNoPsxRteLoaded = false;
        StatusMonitor.fmcUplinkTodDestNotFound = false;
        StatusMonitor.fmcUplinkNoWxFileLoaded = false;
        lockFmcUplinkNoPsxRteLoaded = new ReentrantLock();
        lockFmcUplinkTodDestNotFound = new ReentrantLock();
        lockFmcUplinkNoWxFileLoaded = new ReentrantLock();
        StatusMonitor.simSetPsxTraffic = false;
        StatusMonitor.psxSetSimComXpndr = false;
        StatusMonitor.sendTrueTasToVATSIM = false;
        lockSimSetPsxTraffic = new ReentrantLock();
        lockPsxSetSimComXpndr = new ReentrantLock();
        lockSendTrueTasToVATSIM = new ReentrantLock();
        StatusMonitor.printOutputEnabled = false;
        StatusMonitor.printOutputFailed = false;
        lockPrintOutputEnabled = new ReentrantLock();
        lockPrintOutputFailed = new ReentrantLock();
        StatusMonitor.gndServiceInvalidTextValues = false;
        StatusMonitor.gndServiceRunning = false;
        StatusMonitor.gndServiceColdDarkPhase = false;
        StatusMonitor.gndServiceRefuelCompletePhase = false;
        StatusMonitor.gndServicePushReadyPhase = false;
        StatusMonitor.gndServiceSimplified = false;
        StatusMonitor.gndServiceExtPush = false;
        lockGndServiceInvalidTextValues = new ReentrantLock();
        lockGndServiceRunning = new ReentrantLock();
        lockGndServiceColdDarkPhase = new ReentrantLock();
        lockGndServiceRefuelCompletePhase = new ReentrantLock();
        lockGndServicePushReadyPhase = new ReentrantLock();
        lockGndServiceSimplified = new ReentrantLock();
        lockGndServiceExtPush = new ReentrantLock();
        lockGndServiceVolumeAcp = new ReentrantLock();
        StatusMonitor.psxBoostIsConnected = false;
        StatusMonitor.psxBoostIsDisconnected = false;
        StatusMonitor.psxBoostUnableToConnect = false;
        StatusMonitor.psxBoostInvalidIp = false;
        lockPsxBoostIsConnected = new ReentrantLock();
        lockPsxBoostIsDisconnected = new ReentrantLock();
        lockPsxBoostUnableToConnect = new ReentrantLock();
        lockPsxBoostInvalidIp = new ReentrantLock();
        StatusMonitor.scenGenEnabled = false;
        StatusMonitor.simBridgeIsRunning = false;
        StatusMonitor.simBridgeUsingDestOffsets = false;
        StatusMonitor.simBridgeUsingAllOffsets = false;
        StatusMonitor.simBridgePosOffsetUsed = false;
        StatusMonitor.simBridgeAltOffsetUsed = false;
        StatusMonitor.simBridgeOffsetsNotFound = false;
        StatusMonitor.simBridgeAutoPsxDepAlign = false;
        StatusMonitor.simBridgeAlignPsxWithDepRwy = false;
        StatusMonitor.simBridgeSlaveIsSim = false;
        StatusMonitor.simBridgeDoorsSync = false;
        StatusMonitor.simBridgeHeightRefEnabled = false;
        StatusMonitor.simBridgeUseBetaAlgos = false;
        lockScenGenEnabled = new ReentrantLock();
        lockSimBridgeIsRunning = new ReentrantLock();
        lockSimBridgeUsingDestOffsets = new ReentrantLock();
        lockSimBridgeUsingAllOffsets = new ReentrantLock();
        lockSimBridgePosOffsetUsed = new ReentrantLock();
        lockSimBridgeAltOffsetUsed = new ReentrantLock();
        lockSimBridgeOffsetsNotFound = new ReentrantLock();
        lockSimBridgeAutoPsxDepAlign = new ReentrantLock();
        lockSimBridgeAlignPsxWithDepRwy = new ReentrantLock();
        lockSimBridgeSlaveIsSim = new ReentrantLock();
        lockSimBridgeDoorsSync = new ReentrantLock();
        lockSimBridgeHeightRefEnabled = new ReentrantLock();
        lockSimBridgeUseBetaAlgos = new ReentrantLock();
    }
    
    public static void setPsxMainIsConnected(final boolean argIsconnected) {
        StatusMonitor.lockPsxMainIsConnected.lock();
        StatusMonitor.psxMainIsConnected = argIsconnected;
        StatusMonitor.lockPsxMainIsConnected.unlock();
    }
    
    public static boolean getPsxMainIsConnected() {
        StatusMonitor.lockPsxMainIsConnected.lock();
        final boolean temp = StatusMonitor.psxMainIsConnected;
        StatusMonitor.lockPsxMainIsConnected.unlock();
        return temp;
    }
    
    public static void setPsxMainIsDisconnected(final boolean argDisconnected) {
        StatusMonitor.lockPsxMainIsDisconnected.lock();
        StatusMonitor.psxMainIsDisconnected = argDisconnected;
        StatusMonitor.lockPsxMainIsDisconnected.unlock();
    }
    
    public static boolean getPsxMainIsDisconnected() {
        StatusMonitor.lockPsxMainIsDisconnected.lock();
        final boolean temp = StatusMonitor.psxMainIsDisconnected;
        StatusMonitor.lockPsxMainIsDisconnected.unlock();
        return temp;
    }
    
    public static void setPsxMainUnableToConnect(final boolean argUnable) {
        StatusMonitor.lockPsxMainUnableToConnect.lock();
        StatusMonitor.psxMainUnableToConnect = argUnable;
        StatusMonitor.lockPsxMainUnableToConnect.unlock();
    }
    
    public static boolean getPsxMainUnableToConnect() {
        StatusMonitor.lockPsxMainUnableToConnect.lock();
        final boolean temp = StatusMonitor.psxMainUnableToConnect;
        StatusMonitor.lockPsxMainUnableToConnect.unlock();
        return temp;
    }
    
    public static void setPsxMainInvalidIp(final boolean argInvalid) {
        StatusMonitor.lockPsxMainInvalidIp.lock();
        StatusMonitor.psxMainInvalidIp = argInvalid;
        StatusMonitor.lockPsxMainInvalidIp.unlock();
    }
    
    public static boolean getPsxMainInvalidIp() {
        StatusMonitor.lockPsxMainInvalidIp.lock();
        final boolean temp = StatusMonitor.psxMainInvalidIp;
        StatusMonitor.lockPsxMainInvalidIp.unlock();
        return temp;
    }
    
    public static void setPsxBoostIsConnected(final boolean argIsconnected) {
        StatusMonitor.lockPsxBoostIsConnected.lock();
        StatusMonitor.psxBoostIsConnected = argIsconnected;
        StatusMonitor.lockPsxBoostIsConnected.unlock();
    }
    
    public static boolean getPsxBoostIsConnected() {
        StatusMonitor.lockPsxBoostIsConnected.lock();
        final boolean temp = StatusMonitor.psxBoostIsConnected;
        StatusMonitor.lockPsxBoostIsConnected.unlock();
        return temp;
    }
    
    public static void setPsxBoostIsDisconnected(final boolean argDisconnected) {
        StatusMonitor.lockPsxBoostIsDisconnected.lock();
        StatusMonitor.psxBoostIsDisconnected = argDisconnected;
        StatusMonitor.lockPsxBoostIsDisconnected.unlock();
    }
    
    public static boolean getPsxBoostIsDisconnected() {
        StatusMonitor.lockPsxBoostIsDisconnected.lock();
        final boolean temp = StatusMonitor.psxBoostIsDisconnected;
        StatusMonitor.lockPsxBoostIsDisconnected.unlock();
        return temp;
    }
    
    public static void setPsxBoostUnableToConnect(final boolean argUnable) {
        StatusMonitor.lockPsxBoostUnableToConnect.lock();
        StatusMonitor.psxBoostUnableToConnect = argUnable;
        StatusMonitor.lockPsxBoostUnableToConnect.unlock();
    }
    
    public static boolean getPsxBoostUnableToConnect() {
        StatusMonitor.lockPsxBoostUnableToConnect.lock();
        final boolean temp = StatusMonitor.psxBoostUnableToConnect;
        StatusMonitor.lockPsxBoostUnableToConnect.unlock();
        return temp;
    }
    
    public static void setPsxBoostInvalidIp(final boolean argInvalid) {
        StatusMonitor.lockPsxBoostInvalidIp.lock();
        StatusMonitor.psxBoostInvalidIp = argInvalid;
        StatusMonitor.lockPsxBoostInvalidIp.unlock();
    }
    
    public static boolean getPsxBoostInvalidIp() {
        StatusMonitor.lockPsxBoostInvalidIp.lock();
        final boolean temp = StatusMonitor.psxBoostInvalidIp;
        StatusMonitor.lockPsxBoostInvalidIp.unlock();
        return temp;
    }
    
    public static void setSimConnectEnabled(final boolean argUsingSimConnect) {
        StatusMonitor.lockSimConnectIsEnabled.lock();
        StatusMonitor.simConnectIsEnabled = argUsingSimConnect;
        StatusMonitor.lockSimConnectIsEnabled.unlock();
    }
    
    public static boolean getSimConnectEnabled() {
        StatusMonitor.lockSimConnectIsEnabled.lock();
        final boolean temp = StatusMonitor.simConnectIsEnabled;
        StatusMonitor.lockSimConnectIsEnabled.unlock();
        return temp;
    }
    
    public static void setSimConnectIsConnected(final boolean argIsConnected) {
        StatusMonitor.lockSimConnectIsConnected.lock();
        StatusMonitor.simConnectIsConnected = argIsConnected;
        StatusMonitor.lockSimConnectIsConnected.unlock();
    }
    
    public static boolean getSimConnectIsConnected() {
        StatusMonitor.lockSimConnectIsConnected.lock();
        final boolean temp = StatusMonitor.simConnectIsConnected;
        StatusMonitor.lockSimConnectIsConnected.unlock();
        return temp;
    }
    
    public static void setSimConnectIsDisconnected(final boolean argDisconnected) {
        StatusMonitor.lockSimConnectIsDisconnected.lock();
        StatusMonitor.simConnectIsDisconnected = argDisconnected;
        StatusMonitor.lockSimConnectIsDisconnected.unlock();
    }
    
    public static boolean getSimConnectIsDisconnected() {
        StatusMonitor.lockSimConnectIsDisconnected.lock();
        final boolean temp = StatusMonitor.simConnectIsDisconnected;
        StatusMonitor.lockSimConnectIsDisconnected.unlock();
        return temp;
    }
    
    public static void setSimConnectUnableToConnect(final boolean argUnable) {
        StatusMonitor.lockSimConnectUnableToConnect.lock();
        StatusMonitor.simConnectUnableToConnect = argUnable;
        StatusMonitor.lockSimConnectUnableToConnect.unlock();
    }
    
    public static boolean getSimConnectUnableToConnect() {
        StatusMonitor.lockSimConnectUnableToConnect.lock();
        final boolean temp = StatusMonitor.simConnectUnableToConnect;
        StatusMonitor.lockSimConnectUnableToConnect.unlock();
        return temp;
    }
    
    public static void setSimConnectInvalidIp(final boolean argInvalid) {
        StatusMonitor.lockSimConnectInvalidIp.lock();
        StatusMonitor.simConnectInvalidIp = argInvalid;
        StatusMonitor.lockSimConnectInvalidIp.unlock();
    }
    
    public static boolean getSimConnectInvalidIp() {
        StatusMonitor.lockSimConnectInvalidIp.lock();
        final boolean temp = StatusMonitor.simConnectInvalidIp;
        StatusMonitor.lockSimConnectInvalidIp.unlock();
        return temp;
    }
    
    public static void setMsfsInUse(final boolean argInUse) {
        StatusMonitor.lockMsfsInUse.lock();
        StatusMonitor.msfsInUse = argInUse;
        StatusMonitor.lockMsfsInUse.unlock();
    }
    
    public static boolean getMsfsInUse() {
        StatusMonitor.lockMsfsInUse.lock();
        final boolean temp = StatusMonitor.msfsInUse;
        StatusMonitor.lockMsfsInUse.unlock();
        return temp;
    }
    
    public static void setAloftWxStaMode(final boolean argUsingAloftWxSta) {
        StatusMonitor.lockAloftWxStaMode.lock();
        StatusMonitor.aloftWxStaMode = argUsingAloftWxSta;
        StatusMonitor.lockAloftWxStaMode.unlock();
    }
    
    public static boolean getAloftWxStaMode() {
        StatusMonitor.lockAloftWxStaMode.lock();
        final boolean temp = StatusMonitor.aloftWxStaMode;
        StatusMonitor.lockAloftWxStaMode.unlock();
        return temp;
    }
    
    public static void setAloftWxTurbMode(final boolean argUsingAloftWxTurb) {
        StatusMonitor.lockAloftWxTurbMode.lock();
        StatusMonitor.aloftWxTurbMode = argUsingAloftWxTurb;
        StatusMonitor.lockAloftWxTurbMode.unlock();
    }
    
    public static boolean getAloftWxTurbMode() {
        StatusMonitor.lockAloftWxTurbMode.lock();
        final boolean temp = StatusMonitor.aloftWxTurbMode;
        StatusMonitor.lockAloftWxTurbMode.unlock();
        return temp;
    }
    
    public static void setAloftWxDynMode(final boolean argUsingAloftWxDyn) {
        StatusMonitor.lockAloftWxDynMode.lock();
        StatusMonitor.aloftWxDynMode = argUsingAloftWxDyn;
        StatusMonitor.lockAloftWxDynMode.unlock();
    }
    
    public static boolean getAloftWxDynMode() {
        StatusMonitor.lockAloftWxDynMode.lock();
        final boolean temp = StatusMonitor.aloftWxDynMode;
        StatusMonitor.lockAloftWxDynMode.unlock();
        return temp;
    }
    
    public static void setAloftWxRunning(final boolean argRunning) {
        StatusMonitor.lockAloftWxRunning.lock();
        StatusMonitor.aloftWxRunning = argRunning;
        StatusMonitor.lockAloftWxRunning.unlock();
    }
    
    public static boolean getAloftWxRunning() {
        StatusMonitor.lockAloftWxRunning.lock();
        final boolean temp = StatusMonitor.aloftWxRunning;
        StatusMonitor.lockAloftWxRunning.unlock();
        return temp;
    }
    
    public static void setAloftWxFileLoaded(final boolean argLoaded) {
        StatusMonitor.lockAloftWxFileLoaded.lock();
        StatusMonitor.aloftWxFileLoaded = argLoaded;
        StatusMonitor.lockAloftWxFileLoaded.unlock();
    }
    
    public static boolean getAloftWxFileLoaded() {
        StatusMonitor.lockAloftWxFileLoaded.lock();
        final boolean temp = StatusMonitor.aloftWxFileLoaded;
        StatusMonitor.lockAloftWxFileLoaded.unlock();
        return temp;
    }
    
    public static void setAloftWxTocNotFound(final boolean argNotFound) {
        StatusMonitor.lockAloftWxTocNotFound.lock();
        StatusMonitor.aloftWxTocNotFound = argNotFound;
        StatusMonitor.lockAloftWxTocNotFound.unlock();
    }
    
    public static boolean getAloftWxTocNotFound() {
        StatusMonitor.lockAloftWxTocNotFound.lock();
        final boolean temp = StatusMonitor.aloftWxTocNotFound;
        StatusMonitor.lockAloftWxTocNotFound.unlock();
        return temp;
    }
    
    public static void setTurbDataInvalidLowUpLim(final boolean argInvalid) {
        StatusMonitor.lockTurbDataInvalidLowUpLim.lock();
        StatusMonitor.turbDataInvalidLowUpLim = argInvalid;
        StatusMonitor.lockTurbDataInvalidLowUpLim.unlock();
    }
    
    public static boolean getTurbDataInvalidLowUpLim() {
        StatusMonitor.lockTurbDataInvalidLowUpLim.lock();
        final boolean temp = StatusMonitor.turbDataInvalidLowUpLim;
        StatusMonitor.lockTurbDataInvalidLowUpLim.unlock();
        return temp;
    }
    
    public static void setTurbWptsNotFound(final boolean argNotFound) {
        StatusMonitor.lockTurbWptsNotFound.lock();
        StatusMonitor.turbWptsNotFound = argNotFound;
        StatusMonitor.lockTurbWptsNotFound.unlock();
    }
    
    public static boolean getTurbWptsNotFound() {
        StatusMonitor.lockTurbWptsNotFound.lock();
        final boolean temp = StatusMonitor.turbWptsNotFound;
        StatusMonitor.lockTurbWptsNotFound.unlock();
        return temp;
    }
    
    public static void setTurbConfigIsReady(final boolean argReady) {
        StatusMonitor.lockTurbConfigIsReady.lock();
        StatusMonitor.turbConfigIsReady = argReady;
        StatusMonitor.lockTurbConfigIsReady.unlock();
    }
    
    public static boolean getTurbConfigIsReady() {
        StatusMonitor.lockTurbConfigIsReady.lock();
        final boolean temp = StatusMonitor.turbConfigIsReady;
        StatusMonitor.lockTurbConfigIsReady.unlock();
        return temp;
    }
    
    public static void setFmcUplinkNoPsxRteLoaded(final boolean argLoaded) {
        StatusMonitor.lockFmcUplinkNoPsxRteLoaded.lock();
        StatusMonitor.fmcUplinkNoPsxRteLoaded = argLoaded;
        StatusMonitor.lockFmcUplinkNoPsxRteLoaded.unlock();
    }
    
    public static boolean getFmcUplinkNoPsxRteLoaded() {
        StatusMonitor.lockFmcUplinkNoPsxRteLoaded.lock();
        final boolean temp = StatusMonitor.fmcUplinkNoPsxRteLoaded;
        StatusMonitor.lockFmcUplinkNoPsxRteLoaded.unlock();
        return temp;
    }
    
    public static void setFmcUplinkTodDestNotFound(final boolean argNotFound) {
        StatusMonitor.lockFmcUplinkTodDestNotFound.lock();
        StatusMonitor.fmcUplinkTodDestNotFound = argNotFound;
        StatusMonitor.lockFmcUplinkTodDestNotFound.unlock();
    }
    
    public static boolean getFmcUplinkTodDestNotFound() {
        StatusMonitor.lockFmcUplinkTodDestNotFound.lock();
        final boolean temp = StatusMonitor.fmcUplinkTodDestNotFound;
        StatusMonitor.lockFmcUplinkTodDestNotFound.unlock();
        return temp;
    }
    
    public static void setFmcUplinkNoWxFileLoaded(final boolean argNotLoaded) {
        StatusMonitor.lockFmcUplinkNoWxFileLoaded.lock();
        StatusMonitor.fmcUplinkNoWxFileLoaded = argNotLoaded;
        StatusMonitor.lockFmcUplinkNoWxFileLoaded.unlock();
    }
    
    public static boolean getFmcUplinkNoWxFileLoaded() {
        StatusMonitor.lockFmcUplinkNoWxFileLoaded.lock();
        final boolean temp = StatusMonitor.fmcUplinkNoWxFileLoaded;
        StatusMonitor.lockFmcUplinkNoWxFileLoaded.unlock();
        return temp;
    }
    
    public static void setSimSetPsxTraffic(final boolean argSet) {
        StatusMonitor.lockSimSetPsxTraffic.lock();
        StatusMonitor.simSetPsxTraffic = argSet;
        StatusMonitor.lockSimSetPsxTraffic.unlock();
    }
    
    public static boolean getSimSetPsxTraffic() {
        StatusMonitor.lockSimSetPsxTraffic.lock();
        final boolean temp = StatusMonitor.simSetPsxTraffic;
        StatusMonitor.lockSimSetPsxTraffic.unlock();
        return temp;
    }
    
    public static void setPsxSetSimComXpndrAlt(final boolean argSet) {
        StatusMonitor.lockPsxSetSimComXpndr.lock();
        StatusMonitor.psxSetSimComXpndr = argSet;
        StatusMonitor.lockPsxSetSimComXpndr.unlock();
    }
    
    public static boolean getPsxSetSimComXpndrAlt() {
        StatusMonitor.lockPsxSetSimComXpndr.lock();
        final boolean temp = StatusMonitor.psxSetSimComXpndr;
        StatusMonitor.lockPsxSetSimComXpndr.unlock();
        return temp;
    }
    
    public static void setSendTrueTasToVATSIM(final boolean argSet) {
        StatusMonitor.lockSendTrueTasToVATSIM.lock();
        StatusMonitor.sendTrueTasToVATSIM = argSet;
        StatusMonitor.lockSendTrueTasToVATSIM.unlock();
    }
    
    public static boolean getSendTrueTasToVATSIM() {
        StatusMonitor.lockSendTrueTasToVATSIM.lock();
        final boolean temp = StatusMonitor.sendTrueTasToVATSIM;
        StatusMonitor.lockSendTrueTasToVATSIM.unlock();
        return temp;
    }
    
    public static void setPrintOutputEnabled(final boolean argEnabled) {
        StatusMonitor.lockPrintOutputEnabled.lock();
        StatusMonitor.printOutputEnabled = argEnabled;
        StatusMonitor.lockPrintOutputEnabled.unlock();
    }
    
    public static boolean getPrintOutputEnabled() {
        StatusMonitor.lockPrintOutputEnabled.lock();
        final boolean temp = StatusMonitor.printOutputEnabled;
        StatusMonitor.lockPrintOutputEnabled.unlock();
        return temp;
    }
    
    public static void setPrintOutputFailed(final boolean argFailed) {
        StatusMonitor.lockPrintOutputFailed.lock();
        StatusMonitor.printOutputFailed = argFailed;
        StatusMonitor.lockPrintOutputFailed.unlock();
    }
    
    public static boolean getPrintOutputFailed() {
        StatusMonitor.lockPrintOutputFailed.lock();
        final boolean temp = StatusMonitor.printOutputFailed;
        StatusMonitor.lockPrintOutputFailed.unlock();
        return temp;
    }
    
    public static void setGndServiceInvalidTextValues(final boolean argInvalid) {
        StatusMonitor.lockGndServiceInvalidTextValues.lock();
        StatusMonitor.gndServiceInvalidTextValues = argInvalid;
        StatusMonitor.lockGndServiceInvalidTextValues.unlock();
    }
    
    public static boolean getGndServiceInvalidTextValues() {
        StatusMonitor.lockGndServiceInvalidTextValues.lock();
        final boolean temp = StatusMonitor.gndServiceInvalidTextValues;
        StatusMonitor.lockGndServiceInvalidTextValues.unlock();
        return temp;
    }
    
    public static void setGndServiceRunning(final boolean argRunning) {
        StatusMonitor.lockGndServiceRunning.lock();
        StatusMonitor.gndServiceRunning = argRunning;
        StatusMonitor.lockGndServiceRunning.unlock();
    }
    
    public static boolean getGndServiceRunning() {
        StatusMonitor.lockGndServiceRunning.lock();
        final boolean temp = StatusMonitor.gndServiceRunning;
        StatusMonitor.lockGndServiceRunning.unlock();
        return temp;
    }
    
    public static void setScenGenEnabled(final boolean argEnabled) {
        StatusMonitor.lockScenGenEnabled.lock();
        StatusMonitor.scenGenEnabled = argEnabled;
        StatusMonitor.lockScenGenEnabled.unlock();
    }
    
    public static boolean getScenGenEnabled() {
        StatusMonitor.lockScenGenEnabled.lock();
        final boolean temp = StatusMonitor.scenGenEnabled;
        StatusMonitor.lockScenGenEnabled.unlock();
        return temp;
    }
    
    public static void setSimBridgeIsRunning(final boolean argRunning) {
        StatusMonitor.lockSimBridgeIsRunning.lock();
        StatusMonitor.simBridgeIsRunning = argRunning;
        StatusMonitor.lockSimBridgeIsRunning.unlock();
    }
    
    public static boolean getSimBridgeIsRunning() {
        StatusMonitor.lockSimBridgeIsRunning.lock();
        final boolean temp = StatusMonitor.simBridgeIsRunning;
        StatusMonitor.lockSimBridgeIsRunning.unlock();
        return temp;
    }
    
    public static void setSimBridgeUsingDestOffsets(final boolean argUsing) {
        StatusMonitor.lockSimBridgeUsingDestOffsets.lock();
        StatusMonitor.simBridgeUsingDestOffsets = argUsing;
        StatusMonitor.lockSimBridgeUsingDestOffsets.unlock();
    }
    
    public static boolean getSimBridgeUsingDestOffsets() {
        StatusMonitor.lockSimBridgeUsingDestOffsets.lock();
        final boolean temp = StatusMonitor.simBridgeUsingDestOffsets;
        StatusMonitor.lockSimBridgeUsingDestOffsets.unlock();
        return temp;
    }
    
    public static void setSimBridgeUsingAllOffsets(final boolean argUsing) {
        StatusMonitor.lockSimBridgeUsingAllOffsets.lock();
        StatusMonitor.simBridgeUsingAllOffsets = argUsing;
        StatusMonitor.lockSimBridgeUsingAllOffsets.unlock();
    }
    
    public static boolean getSimBridgeUsingAllOffsets() {
        StatusMonitor.lockSimBridgeUsingAllOffsets.lock();
        final boolean temp = StatusMonitor.simBridgeUsingAllOffsets;
        StatusMonitor.lockSimBridgeUsingAllOffsets.unlock();
        return temp;
    }
    
    public static void setSimBridgePosOffsetUsed(final boolean argPosOffset) {
        StatusMonitor.lockSimBridgePosOffsetUsed.lock();
        StatusMonitor.simBridgePosOffsetUsed = argPosOffset;
        StatusMonitor.lockSimBridgePosOffsetUsed.unlock();
    }
    
    public static boolean getSimBridgePosOffsetUsed() {
        StatusMonitor.lockSimBridgePosOffsetUsed.lock();
        final boolean temp = StatusMonitor.simBridgePosOffsetUsed;
        StatusMonitor.lockSimBridgePosOffsetUsed.unlock();
        return temp;
    }
    
    public static void setSimBridgeAltOffsetUsed(final boolean argAltOffset) {
        StatusMonitor.lockSimBridgeAltOffsetUsed.lock();
        StatusMonitor.simBridgeAltOffsetUsed = argAltOffset;
        StatusMonitor.lockSimBridgeAltOffsetUsed.unlock();
    }
    
    public static boolean getSimBridgeAltOffsetUsed() {
        StatusMonitor.lockSimBridgeAltOffsetUsed.lock();
        final boolean temp = StatusMonitor.simBridgeAltOffsetUsed;
        StatusMonitor.lockSimBridgeAltOffsetUsed.unlock();
        return temp;
    }
    
    public static void setSimBridgeOffsetsNotFound(final boolean argNotFound) {
        StatusMonitor.lockSimBridgeOffsetsNotFound.lock();
        StatusMonitor.simBridgeOffsetsNotFound = argNotFound;
        StatusMonitor.lockSimBridgeOffsetsNotFound.unlock();
    }
    
    public static boolean getSimBridgeOffsetsNotFound() {
        StatusMonitor.lockSimBridgeOffsetsNotFound.lock();
        final boolean temp = StatusMonitor.simBridgeOffsetsNotFound;
        StatusMonitor.lockSimBridgeOffsetsNotFound.unlock();
        return temp;
    }
    
    public static void setSimBridgeAutoPsxDepAlign(final boolean argAuto) {
        StatusMonitor.lockSimBridgeAutoPsxDepAlign.lock();
        StatusMonitor.simBridgeAutoPsxDepAlign = argAuto;
        StatusMonitor.lockSimBridgeAutoPsxDepAlign.unlock();
    }
    
    public static boolean getSimBridgeAutoPsxDepAlign() {
        StatusMonitor.lockSimBridgeAutoPsxDepAlign.lock();
        final boolean temp = StatusMonitor.simBridgeAutoPsxDepAlign;
        StatusMonitor.lockSimBridgeAutoPsxDepAlign.unlock();
        return temp;
    }
    
    public static void setSimBridgeAlignPsxWithDepRwy(final boolean argAlign) {
        StatusMonitor.lockSimBridgeAlignPsxWithDepRwy.lock();
        StatusMonitor.simBridgeAlignPsxWithDepRwy = argAlign;
        StatusMonitor.lockSimBridgeAlignPsxWithDepRwy.unlock();
    }
    
    public static boolean getSimBridgeAlignPsxWithDepRwy() {
        StatusMonitor.lockSimBridgeAlignPsxWithDepRwy.lock();
        final boolean temp = StatusMonitor.simBridgeAlignPsxWithDepRwy;
        StatusMonitor.lockSimBridgeAlignPsxWithDepRwy.unlock();
        return temp;
    }
    
    public static void setSimBridgeUseBetaAlgos(final boolean argUse) {
        StatusMonitor.lockSimBridgeUseBetaAlgos.lock();
        StatusMonitor.simBridgeUseBetaAlgos = argUse;
        StatusMonitor.lockSimBridgeUseBetaAlgos.unlock();
    }
    
    public static boolean getSimBridgeUseBetaAlgos() {
        StatusMonitor.lockSimBridgeUseBetaAlgos.lock();
        final boolean temp = StatusMonitor.simBridgeUseBetaAlgos;
        StatusMonitor.lockSimBridgeUseBetaAlgos.unlock();
        return temp;
    }
    
    public static void setGndServiceColdDarkPhase(final boolean argPhase) {
        StatusMonitor.lockGndServiceColdDarkPhase.lock();
        StatusMonitor.gndServiceColdDarkPhase = argPhase;
        StatusMonitor.lockGndServiceColdDarkPhase.unlock();
    }
    
    public static boolean getGndServiceColdDarkPhase() {
        StatusMonitor.lockGndServiceColdDarkPhase.lock();
        final boolean temp = StatusMonitor.gndServiceColdDarkPhase;
        StatusMonitor.lockGndServiceColdDarkPhase.unlock();
        return temp;
    }
    
    public static void setGndServiceRefuelCompletePhase(final boolean argPhase) {
        StatusMonitor.lockGndServiceRefuelCompletePhase.lock();
        StatusMonitor.gndServiceRefuelCompletePhase = argPhase;
        StatusMonitor.lockGndServiceRefuelCompletePhase.unlock();
    }
    
    public static boolean getGndServiceRefuelCompletePhase() {
        StatusMonitor.lockGndServiceRefuelCompletePhase.lock();
        final boolean temp = StatusMonitor.gndServiceRefuelCompletePhase;
        StatusMonitor.lockGndServiceRefuelCompletePhase.unlock();
        return temp;
    }
    
    public static void setGndServicePushReadyPhase(final boolean argPhase) {
        StatusMonitor.lockGndServicePushReadyPhase.lock();
        StatusMonitor.gndServicePushReadyPhase = argPhase;
        StatusMonitor.lockGndServicePushReadyPhase.unlock();
    }
    
    public static boolean getGndServicePushReadyPhase() {
        StatusMonitor.lockGndServicePushReadyPhase.lock();
        final boolean temp = StatusMonitor.gndServicePushReadyPhase;
        StatusMonitor.lockGndServicePushReadyPhase.unlock();
        return temp;
    }
    
    public static void setGndServiceSimplified(final boolean argPhase) {
        StatusMonitor.lockGndServiceSimplified.lock();
        StatusMonitor.gndServiceSimplified = argPhase;
        StatusMonitor.lockGndServiceSimplified.unlock();
    }
    
    public static boolean getGndServiceSimplified() {
        StatusMonitor.lockGndServiceSimplified.lock();
        final boolean temp = StatusMonitor.gndServiceSimplified;
        StatusMonitor.lockGndServiceSimplified.unlock();
        return temp;
    }
    
    public static void setGndServiceExtPush(final boolean argPush) {
        StatusMonitor.lockGndServiceExtPush.lock();
        StatusMonitor.gndServiceExtPush = argPush;
        StatusMonitor.lockGndServiceExtPush.unlock();
    }
    
    public static boolean getGndServiceExtPush() {
        StatusMonitor.lockGndServiceExtPush.lock();
        final boolean temp = StatusMonitor.gndServiceExtPush;
        StatusMonitor.lockGndServiceExtPush.unlock();
        return temp;
    }
    
    public static void setSimBridgeSlaveIsSim(final boolean argSlaveIsSim) {
        StatusMonitor.lockSimBridgeSlaveIsSim.lock();
        StatusMonitor.simBridgeSlaveIsSim = argSlaveIsSim;
        StatusMonitor.lockSimBridgeSlaveIsSim.unlock();
    }
    
    public static boolean getSimBridgeSlaveIsSim() {
        StatusMonitor.lockSimBridgeSlaveIsSim.lock();
        final boolean temp = StatusMonitor.simBridgeSlaveIsSim;
        StatusMonitor.lockSimBridgeSlaveIsSim.unlock();
        return temp;
    }
    
    public static void setSimBridgeDoorsSync(final boolean argSync) {
        StatusMonitor.lockSimBridgeDoorsSync.lock();
        StatusMonitor.simBridgeDoorsSync = argSync;
        StatusMonitor.lockSimBridgeDoorsSync.unlock();
    }
    
    public static boolean getSimBridgeDoorsSync() {
        StatusMonitor.lockSimBridgeDoorsSync.lock();
        final boolean temp = StatusMonitor.simBridgeDoorsSync;
        StatusMonitor.lockSimBridgeDoorsSync.unlock();
        return temp;
    }
    
    public static void setSimBridgeHeightRefEnabled(final boolean argEnabled) {
        StatusMonitor.lockSimBridgeHeightRefEnabled.lock();
        StatusMonitor.simBridgeHeightRefEnabled = argEnabled;
        StatusMonitor.lockSimBridgeHeightRefEnabled.unlock();
    }
    
    public static boolean getSimBridgeHeightRefEnabled() {
        StatusMonitor.lockSimBridgeHeightRefEnabled.lock();
        final boolean temp = StatusMonitor.simBridgeHeightRefEnabled;
        StatusMonitor.lockSimBridgeHeightRefEnabled.unlock();
        return temp;
    }
    
    public static void setGndServiceVolumeAcp(final String argAcp) {
        StatusMonitor.lockGndServiceVolumeAcp.lock();
        StatusMonitor.gndServiceVolumeAcp = argAcp;
        StatusMonitor.lockGndServiceVolumeAcp.unlock();
    }
    
    public static String getGndServiceVolumeAcp() {
        StatusMonitor.lockGndServiceVolumeAcp.lock();
        final String temp = StatusMonitor.gndServiceVolumeAcp;
        StatusMonitor.lockGndServiceVolumeAcp.unlock();
        return temp;
    }
}
