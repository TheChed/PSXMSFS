// 
// Decompiled by Procyon v0.5.36
// 

package gui;

import util.StatusMonitor;
import java.util.concurrent.locks.ReentrantLock;
import java.awt.Color;
import java.util.concurrent.locks.Lock;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;

public class TextTabParser implements Runnable
{
    private static JTabbedPane tabbedPane;
    private static JLabel lblPsxMain;
    private static JLabel lblPsxBoost;
    private static JLabel lblSimConnect;
    private static JLabel lblAloftWx;
    private static JLabel lblAloftWxTurb;
    private static JLabel lblAloftWxUplink;
    private static JLabel lblPrinter;
    private static JLabel lblGndService;
    private static JLabel lblSimBridgePosOffset;
    private static JLabel lblSimBridgeAltOffset;
    private static final Lock lockLblPsxMain;
    private static final Lock lockLblPsxBoost;
    private static final Lock lockLblSim;
    private static final Lock lockLblAloftWx;
    private static final Lock lockLblAloftWxTurb;
    private static final Lock lockLblAloftWxUplink;
    private static final Lock lockLblPrinter;
    private static final Lock lockLblGndService;
    private static final Lock lockLblSimBridgePosOffset;
    private static final Lock lockLblSimBridgeAltOffset;
    private static boolean tabNetworkWarning;
    private static boolean tabNetworkNormal;
    private static boolean tabAloftWxWarning;
    private static boolean tabAloftWxNormal;
    private static boolean tabTcasTfcNormal;
    private static boolean tabPrinterWarning;
    private static boolean tabPrinterNormal;
    private static boolean tabGndServiceWarning;
    private static boolean tabGndServiceNormal;
    private static boolean tabSimBridgeCaution;
    private static boolean tabSimBridgeNormal;
    private static String arptIdText;
    private static String rwyIdText;
    private static final Lock lockArptRwyText;
    private static final Color neutral;
    private static final Color normal;
    private static final Color caution;
    private static final Color warning;
    
    static {
        TextTabParser.tabbedPane = new JTabbedPane();
        TextTabParser.lblPsxMain = new JLabel();
        TextTabParser.lblPsxBoost = new JLabel();
        TextTabParser.lblSimConnect = new JLabel();
        TextTabParser.lblAloftWx = new JLabel();
        TextTabParser.lblAloftWxTurb = new JLabel();
        TextTabParser.lblAloftWxUplink = new JLabel();
        TextTabParser.lblPrinter = new JLabel();
        TextTabParser.lblGndService = new JLabel();
        TextTabParser.lblSimBridgePosOffset = new JLabel();
        TextTabParser.lblSimBridgeAltOffset = new JLabel();
        lockLblPsxMain = new ReentrantLock();
        lockLblPsxBoost = new ReentrantLock();
        lockLblSim = new ReentrantLock();
        lockLblAloftWx = new ReentrantLock();
        lockLblAloftWxTurb = new ReentrantLock();
        lockLblAloftWxUplink = new ReentrantLock();
        lockLblPrinter = new ReentrantLock();
        lockLblGndService = new ReentrantLock();
        lockLblSimBridgePosOffset = new ReentrantLock();
        lockLblSimBridgeAltOffset = new ReentrantLock();
        TextTabParser.tabNetworkWarning = false;
        TextTabParser.tabNetworkNormal = false;
        TextTabParser.tabAloftWxWarning = false;
        TextTabParser.tabAloftWxNormal = false;
        TextTabParser.tabTcasTfcNormal = false;
        TextTabParser.tabPrinterWarning = false;
        TextTabParser.tabPrinterNormal = false;
        TextTabParser.tabGndServiceWarning = false;
        TextTabParser.tabGndServiceNormal = false;
        TextTabParser.tabSimBridgeCaution = false;
        TextTabParser.tabSimBridgeNormal = false;
        TextTabParser.arptIdText = new String("");
        TextTabParser.rwyIdText = new String("");
        lockArptRwyText = new ReentrantLock();
        neutral = new Color(0, 0, 0);
        normal = new Color(0, 150, 0);
        caution = new Color(255, 140, 0);
        warning = new Color(255, 0, 0);
    }
    
    public TextTabParser(final JTabbedPane argTabbedPane) {
        TextTabParser.tabbedPane = argTabbedPane;
    }
    
    public static void passJLabel(final Object argSender, final JLabel... argLabel) {
        if (argSender instanceof TabNetwork) {
            TextTabParser.lblPsxMain = argLabel[0];
            TextTabParser.lblSimConnect = argLabel[1];
            TextTabParser.lblPsxBoost = argLabel[2];
        }
        else if (argSender instanceof TabAloftWx) {
            TextTabParser.lblAloftWx = argLabel[0];
            TextTabParser.lblAloftWxTurb = argLabel[1];
            TextTabParser.lblAloftWxUplink = argLabel[2];
        }
        else if (argSender instanceof TabPrinter) {
            TextTabParser.lblPrinter = argLabel[0];
        }
        else if (argSender instanceof TabGndService) {
            TextTabParser.lblGndService = argLabel[0];
        }
        else if (argSender instanceof TabSceneryGen) {
            TextTabParser.lblSimBridgePosOffset = argLabel[0];
            TextTabParser.lblSimBridgeAltOffset = argLabel[1];
        }
    }
    
    public static void setArptRwyText(final String argArptId, final String argRwyId) {
        TextTabParser.lockArptRwyText.lock();
        TextTabParser.arptIdText = argArptId;
        TextTabParser.rwyIdText = argRwyId;
        TextTabParser.lockArptRwyText.unlock();
    }
    
    @Override
    public void run() {
        try {
            Thread.sleep(3000L);
        }
        catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        while (true) {
            TextTabParser.tabNetworkWarning = false;
            TextTabParser.tabNetworkNormal = false;
            TextTabParser.tabAloftWxWarning = false;
            TextTabParser.tabAloftWxNormal = false;
            TextTabParser.tabTcasTfcNormal = false;
            TextTabParser.tabPrinterWarning = false;
            TextTabParser.tabPrinterNormal = false;
            TextTabParser.tabGndServiceWarning = false;
            TextTabParser.tabGndServiceNormal = false;
            TextTabParser.tabSimBridgeCaution = false;
            TextTabParser.tabSimBridgeNormal = false;
            if (StatusMonitor.getPsxMainIsDisconnected()) {
                setTextAndForeground(TextTabParser.lblPsxMain, "Disconnected from server", TextTabParser.warning);
                TextTabParser.tabNetworkWarning = true;
            }
            else if (StatusMonitor.getPsxMainInvalidIp()) {
                setTextAndForeground(TextTabParser.lblPsxMain, "Invalid IP and/or Port", TextTabParser.warning);
                TextTabParser.tabNetworkWarning = true;
            }
            else if (StatusMonitor.getPsxMainUnableToConnect()) {
                setTextAndForeground(TextTabParser.lblPsxMain, "Unable to establish connection", TextTabParser.warning);
                TextTabParser.tabNetworkWarning = true;
            }
            else if (StatusMonitor.getPsxMainIsConnected()) {
                setTextAndForeground(TextTabParser.lblPsxMain, "Connected", TextTabParser.normal);
                TextTabParser.tabNetworkNormal = true;
            }
            if (StatusMonitor.getScenGenEnabled()) {
                if (StatusMonitor.getPsxBoostIsDisconnected()) {
                    setTextAndForeground(TextTabParser.lblPsxBoost, "Disconnected from server", TextTabParser.warning);
                    TextTabParser.tabNetworkWarning = true;
                }
                else if (StatusMonitor.getPsxBoostInvalidIp()) {
                    setTextAndForeground(TextTabParser.lblPsxBoost, "Invalid IP and/or Port", TextTabParser.warning);
                    TextTabParser.tabNetworkWarning = true;
                }
                else if (StatusMonitor.getPsxBoostUnableToConnect()) {
                    setTextAndForeground(TextTabParser.lblPsxBoost, "Unable to establish connection", TextTabParser.warning);
                    TextTabParser.tabNetworkWarning = true;
                }
                else if (StatusMonitor.getPsxBoostIsConnected()) {
                    setTextAndForeground(TextTabParser.lblPsxBoost, "Connected", TextTabParser.normal);
                    TextTabParser.tabNetworkNormal = true;
                }
                else {
                    setTextAndForeground(TextTabParser.lblPsxBoost, "", TextTabParser.neutral);
                }
            }
            else {
                setTextAndForeground(TextTabParser.lblPsxBoost, "Scenery Generator Bridge Disabled", TextTabParser.caution);
            }
            if (StatusMonitor.getSimConnectEnabled()) {
                if (StatusMonitor.getSimConnectIsDisconnected()) {
                    setTextAndForeground(TextTabParser.lblSimConnect, "Disconnected from server", TextTabParser.warning);
                    TextTabParser.tabNetworkWarning = true;
                }
                else if (StatusMonitor.getSimConnectInvalidIp()) {
                    setTextAndForeground(TextTabParser.lblSimConnect, "Invalid IP and/or Port", TextTabParser.warning);
                    TextTabParser.tabNetworkWarning = true;
                }
                else if (StatusMonitor.getSimConnectUnableToConnect()) {
                    setTextAndForeground(TextTabParser.lblSimConnect, "Unable to establish connection", TextTabParser.warning);
                    TextTabParser.tabNetworkWarning = true;
                }
                else if (StatusMonitor.getSimConnectIsConnected()) {
                    setTextAndForeground(TextTabParser.lblSimConnect, "Connected", TextTabParser.normal);
                    TextTabParser.tabNetworkNormal = true;
                }
                else {
                    setTextAndForeground(TextTabParser.lblSimConnect, "", TextTabParser.neutral);
                }
            }
            else {
                setTextAndForeground(TextTabParser.lblSimConnect, "SimConnect Bridge Disabled", TextTabParser.caution);
            }
            if (StatusMonitor.getAloftWxStaMode() && StatusMonitor.getAloftWxRunning()) {
                if (!StatusMonitor.getPsxMainIsConnected()) {
                    setTextAndForeground(TextTabParser.lblAloftWx, "", TextTabParser.neutral);
                }
                else if (StatusMonitor.getAloftWxTocNotFound()) {
                    setTextAndForeground(TextTabParser.lblAloftWx, "TOC not found in WX file, please check file", TextTabParser.warning);
                    TextTabParser.tabAloftWxWarning = true;
                }
                else {
                    setTextAndForeground(TextTabParser.lblAloftWx, "Running in static mode", TextTabParser.normal);
                    TextTabParser.tabAloftWxNormal = true;
                }
            }
            else if (StatusMonitor.getAloftWxDynMode() && StatusMonitor.getAloftWxRunning()) {
                if (!StatusMonitor.getPsxMainIsConnected() || !StatusMonitor.getSimConnectIsConnected()) {
                    setTextAndForeground(TextTabParser.lblAloftWx, "", TextTabParser.neutral);
                }
                else {
                    setTextAndForeground(TextTabParser.lblAloftWx, "Running in dynamic mode", TextTabParser.normal);
                    TextTabParser.tabAloftWxNormal = true;
                }
            }
            else if (StatusMonitor.getAloftWxFileLoaded()) {
                setTextAndForeground(TextTabParser.lblAloftWx, "Wx file loaded, waiting for module to be started by user", TextTabParser.caution);
            }
            else {
                setTextAndForeground(TextTabParser.lblAloftWx, "", TextTabParser.neutral);
            }
            if (StatusMonitor.getAloftWxRunning()) {
                if (StatusMonitor.getTurbConfigIsReady() && StatusMonitor.getPsxMainIsConnected()) {
                    setTextAndForeground(TextTabParser.lblAloftWxTurb, "Ready", TextTabParser.normal);
                    TextTabParser.tabAloftWxNormal = true;
                }
                else if (StatusMonitor.getTurbDataInvalidLowUpLim()) {
                    setTextAndForeground(TextTabParser.lblAloftWxTurb, "Invalid turbulence zone(s) limits", TextTabParser.warning);
                    TextTabParser.tabAloftWxWarning = true;
                }
                else if (StatusMonitor.getTurbWptsNotFound()) {
                    setTextAndForeground(TextTabParser.lblAloftWxTurb, "Turbulence waypoints not found in the FMC", TextTabParser.warning);
                    TextTabParser.tabAloftWxWarning = true;
                }
                else {
                    setTextAndForeground(TextTabParser.lblAloftWxTurb, "", TextTabParser.neutral);
                }
            }
            else {
                setTextAndForeground(TextTabParser.lblAloftWxTurb, "", TextTabParser.neutral);
            }
            if (StatusMonitor.getFmcUplinkNoPsxRteLoaded()) {
                setTextAndForeground(TextTabParser.lblAloftWxUplink, "Uplink failed : no PSX route is loaded in the FMC", TextTabParser.warning);
                TextTabParser.tabAloftWxWarning = true;
            }
            else if (StatusMonitor.getFmcUplinkNoWxFileLoaded()) {
                setTextAndForeground(TextTabParser.lblAloftWxUplink, "Uplink failed : no AS Wx file is loaded", TextTabParser.warning);
                TextTabParser.tabAloftWxWarning = true;
            }
            else if (StatusMonitor.getFmcUplinkTodDestNotFound()) {
                setTextAndForeground(TextTabParser.lblAloftWxUplink, "Uplink failed : TOD and/or destination not found in the Wx file", TextTabParser.warning);
                TextTabParser.tabAloftWxWarning = true;
            }
            else if (StatusMonitor.getAloftWxDynMode() && StatusMonitor.getAloftWxRunning() && !StatusMonitor.getAloftWxFileLoaded() && StatusMonitor.getPsxMainIsConnected() && StatusMonitor.getSimConnectIsConnected()) {
                setTextAndForeground(TextTabParser.lblAloftWxUplink, "Load a valid AS Wx file if you want to use the FMC winds uplink feature", TextTabParser.caution);
            }
            else if (StatusMonitor.getAloftWxStaMode() && StatusMonitor.getAloftWxRunning()) {
                if (StatusMonitor.getAloftWxFileLoaded() && StatusMonitor.getPsxMainIsConnected()) {
                    setTextAndForeground(TextTabParser.lblAloftWxUplink, "Ready", TextTabParser.normal);
                    TextTabParser.tabAloftWxNormal = true;
                }
                else {
                    setTextAndForeground(TextTabParser.lblAloftWxUplink, "", TextTabParser.normal);
                }
            }
            else if (StatusMonitor.getAloftWxDynMode() && StatusMonitor.getAloftWxRunning()) {
                if (StatusMonitor.getAloftWxFileLoaded() && StatusMonitor.getPsxMainIsConnected() && StatusMonitor.getSimConnectIsConnected()) {
                    setTextAndForeground(TextTabParser.lblAloftWxUplink, "Ready", TextTabParser.normal);
                    TextTabParser.tabAloftWxNormal = true;
                }
                else {
                    setTextAndForeground(TextTabParser.lblAloftWxUplink, "", TextTabParser.neutral);
                }
            }
            else {
                setTextAndForeground(TextTabParser.lblAloftWxUplink, "", TextTabParser.neutral);
            }
            if (StatusMonitor.getSimBridgeIsRunning() && StatusMonitor.getPsxMainIsConnected() && StatusMonitor.getSimConnectIsConnected()) {
                TextTabParser.tabTcasTfcNormal = true;
            }
            else {
                TextTabParser.tabTcasTfcNormal = false;
            }
            if (StatusMonitor.getPrintOutputEnabled() && StatusMonitor.getPsxMainIsConnected()) {
                if (StatusMonitor.getPrintOutputFailed()) {
                    setTextAndForeground(TextTabParser.lblPrinter, "Printer output error", TextTabParser.warning);
                    TextTabParser.tabPrinterWarning = true;
                }
                else {
                    setTextAndForeground(TextTabParser.lblPrinter, "", TextTabParser.neutral);
                    TextTabParser.tabPrinterNormal = true;
                }
            }
            else {
                setTextAndForeground(TextTabParser.lblPrinter, "", TextTabParser.neutral);
            }
            if (StatusMonitor.getGndServiceRunning() && StatusMonitor.getPsxMainIsDisconnected()) {
                TextTabParser.tabGndServiceWarning = true;
            }
            else if (StatusMonitor.getGndServiceRunning() && StatusMonitor.getPsxMainIsConnected()) {
                TextTabParser.tabGndServiceNormal = true;
            }
            TextTabParser.lockArptRwyText.lock();
            final String arptId = TextTabParser.arptIdText;
            final String rwyId = TextTabParser.rwyIdText;
            TextTabParser.lockArptRwyText.unlock();
            if (StatusMonitor.getSimBridgeIsRunning()) {
                if (StatusMonitor.getSimBridgeAltOffsetUsed()) {
                    TextTabParser.lblSimBridgeAltOffset.setText("Enabled for " + arptId + " runway " + rwyId);
                    TextTabParser.lblSimBridgeAltOffset.setForeground(TextTabParser.caution);
                }
                else {
                    TextTabParser.lblSimBridgeAltOffset.setText("Disabled");
                    TextTabParser.lblSimBridgeAltOffset.setForeground(TextTabParser.normal);
                }
            }
            else {
                TextTabParser.lblSimBridgeAltOffset.setText("");
            }
            if (StatusMonitor.getMsfsInUse()) {
                TextTabParser.lblSimBridgeAltOffset.setText("");
            }
            if (StatusMonitor.getSimBridgeIsRunning()) {
                if (StatusMonitor.getSimBridgeOffsetsNotFound()) {
                    TextTabParser.lblSimBridgePosOffset.setText("Offset not found for " + arptId + " runway " + rwyId);
                    TextTabParser.lblSimBridgePosOffset.setForeground(TextTabParser.caution);
                }
                else if (StatusMonitor.getSimBridgePosOffsetUsed()) {
                    TextTabParser.lblSimBridgePosOffset.setText("Enabled for " + arptId + " runway " + rwyId);
                    TextTabParser.lblSimBridgePosOffset.setForeground(TextTabParser.caution);
                }
                else {
                    TextTabParser.lblSimBridgePosOffset.setText("Disabled");
                    TextTabParser.lblSimBridgePosOffset.setForeground(TextTabParser.normal);
                }
            }
            else {
                TextTabParser.lblSimBridgePosOffset.setText("");
            }
            if (StatusMonitor.getMsfsInUse()) {
                TextTabParser.lblSimBridgePosOffset.setText("");
            }
            if (StatusMonitor.getSimBridgeIsRunning() && StatusMonitor.getSimBridgeOffsetsNotFound() && !StatusMonitor.getMsfsInUse()) {
                TextTabParser.tabSimBridgeCaution = true;
                TextTabParser.tabSimBridgeNormal = false;
            }
            else if (StatusMonitor.getSimBridgeIsRunning() && (!StatusMonitor.getSimBridgeOffsetsNotFound() || StatusMonitor.getMsfsInUse()) && StatusMonitor.getSimConnectIsConnected() && StatusMonitor.getPsxBoostIsConnected() && StatusMonitor.getPsxMainIsConnected()) {
                TextTabParser.tabSimBridgeNormal = true;
            }
            else {
                TextTabParser.tabSimBridgeNormal = false;
            }
            if (TextTabParser.tabNetworkWarning) {
                setTabForeground(0, TextTabParser.warning);
            }
            else if (TextTabParser.tabNetworkNormal) {
                setTabForeground(0, TextTabParser.normal);
            }
            else {
                setTabForeground(0, TextTabParser.neutral);
            }
            if (TextTabParser.tabAloftWxWarning) {
                setTabForeground(2, TextTabParser.warning);
            }
            else if (TextTabParser.tabAloftWxNormal) {
                setTabForeground(2, TextTabParser.normal);
            }
            else {
                setTabForeground(2, TextTabParser.neutral);
            }
            if (TextTabParser.tabTcasTfcNormal) {
                setTabForeground(4, TextTabParser.normal);
            }
            else {
                setTabForeground(4, TextTabParser.neutral);
            }
            if (TextTabParser.tabPrinterWarning) {
                setTabForeground(5, TextTabParser.warning);
            }
            else if (TextTabParser.tabPrinterNormal) {
                setTabForeground(5, TextTabParser.normal);
            }
            else {
                setTabForeground(5, TextTabParser.neutral);
            }
            if (TextTabParser.tabGndServiceWarning) {
                setTabForeground(3, TextTabParser.warning);
            }
            else if (TextTabParser.tabGndServiceNormal) {
                setTabForeground(3, TextTabParser.normal);
            }
            else {
                setTabForeground(3, TextTabParser.neutral);
            }
            if (TextTabParser.tabSimBridgeCaution) {
                setTabForeground(1, TextTabParser.caution);
            }
            else if (TextTabParser.tabSimBridgeNormal) {
                setTabForeground(1, TextTabParser.normal);
            }
            else {
                setTabForeground(1, TextTabParser.neutral);
            }
            try {
                Thread.sleep(1000L);
            }
            catch (InterruptedException e2) {
                e2.printStackTrace();
            }
        }
    }
    
    private static void setTextAndForeground(final JLabel argLabel, final String argText, final Color argColor) {
        if (argLabel == TextTabParser.lblPsxMain) {
            TextTabParser.lockLblPsxMain.lock();
            TextTabParser.lblPsxMain.setText(argText);
            TextTabParser.lblPsxMain.setForeground(argColor);
            TextTabParser.lockLblPsxMain.unlock();
        }
        else if (argLabel == TextTabParser.lblPsxBoost) {
            TextTabParser.lockLblPsxBoost.lock();
            TextTabParser.lblPsxBoost.setText(argText);
            TextTabParser.lblPsxBoost.setForeground(argColor);
            TextTabParser.lockLblPsxBoost.unlock();
        }
        else if (argLabel == TextTabParser.lblSimConnect) {
            TextTabParser.lockLblSim.lock();
            TextTabParser.lblSimConnect.setText(argText);
            TextTabParser.lblSimConnect.setForeground(argColor);
            TextTabParser.lockLblSim.unlock();
        }
        else if (argLabel == TextTabParser.lblAloftWx) {
            TextTabParser.lockLblAloftWx.lock();
            TextTabParser.lblAloftWx.setText(argText);
            TextTabParser.lblAloftWx.setForeground(argColor);
            TextTabParser.lockLblAloftWx.unlock();
        }
        else if (argLabel == TextTabParser.lblAloftWxTurb) {
            TextTabParser.lockLblAloftWxTurb.lock();
            TextTabParser.lblAloftWxTurb.setText(argText);
            TextTabParser.lblAloftWxTurb.setForeground(argColor);
            TextTabParser.lockLblAloftWxTurb.unlock();
        }
        else if (argLabel == TextTabParser.lblAloftWxUplink) {
            TextTabParser.lockLblAloftWxUplink.lock();
            TextTabParser.lblAloftWxUplink.setText(argText);
            TextTabParser.lblAloftWxUplink.setForeground(argColor);
            TextTabParser.lockLblAloftWxUplink.unlock();
        }
        else if (argLabel == TextTabParser.lblPrinter) {
            TextTabParser.lockLblPrinter.lock();
            TextTabParser.lblPrinter.setText(argText);
            TextTabParser.lblPrinter.setForeground(argColor);
            TextTabParser.lockLblPrinter.unlock();
        }
        else if (argLabel == TextTabParser.lblGndService) {
            TextTabParser.lockLblGndService.lock();
            TextTabParser.lblGndService.setText(argText);
            TextTabParser.lblGndService.setForeground(argColor);
            TextTabParser.lockLblGndService.unlock();
        }
        else if (argLabel == TextTabParser.lblSimBridgePosOffset) {
            TextTabParser.lockLblSimBridgePosOffset.lock();
            TextTabParser.lblSimBridgePosOffset.setText(argText);
            TextTabParser.lblSimBridgePosOffset.setForeground(argColor);
            TextTabParser.lockLblSimBridgePosOffset.unlock();
        }
        else if (argLabel == TextTabParser.lblSimBridgeAltOffset) {
            TextTabParser.lockLblSimBridgeAltOffset.lock();
            TextTabParser.lblSimBridgeAltOffset.setText(argText);
            TextTabParser.lblSimBridgeAltOffset.setForeground(argColor);
            TextTabParser.lockLblSimBridgeAltOffset.unlock();
        }
    }
    
    private static void setTabForeground(final int argTab, final Color argColor) {
        try {
            TextTabParser.tabbedPane.setForegroundAt(argTab, argColor);
        }
        catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }
}
