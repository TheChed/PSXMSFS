// 
// Decompiled by Procyon v0.5.36
// 

package gui;

import java.awt.Color;
import javax.swing.AbstractButton;
import simBridge.SimBridgeBase;
import simBridge.SimBridgeOffsetPos;
import network.SocketClientPSXMain;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import simBridge.SimBridgeDoors;
import network.BridgeSimConnect;
import gndService.GndServiceBase;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import files.FileSettingConfig;
import java.awt.Font;
import util.StatusMonitor;
import java.io.IOException;
import java.awt.LayoutManager;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JTabbedPane;
import simBridge.SimBridgeSyncThread;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPanel;

public class TabSceneryGen
{
    private static JPanel panel_5;
    private static JTextField textInhibitOffsetAlt;
    private static JTextField textHeightRef;
    private static JLabel lblDynPosOffset;
    private static JLabel lblDynAltOffset;
    private static final ButtonGroup buttonGroup;
    private static final ButtonGroup buttonGroup_1;
    private static JRadioButton rdbtnDisableOffsets;
    private static JRadioButton rdbtnEnableDestOffsets;
    private static JRadioButton rdbtnEnableAllOffsets;
    private static JRadioButton rdbtnSlaveSim;
    private static JRadioButton rdbtnSlavePSX;
    private static JCheckBox chkUseBetaFSXP3DAlgos;
    private static SimBridgeSyncThread simBridgeSyncThread;
    private static Thread controlsSyncThread;
    private static boolean showSimSlaveConfirmBox;
    private static boolean showPsxSlaveConfirmBox;
    private static boolean showExtPushConfirmBox;
    
    static {
        TabSceneryGen.panel_5 = new JPanel();
        TabSceneryGen.textInhibitOffsetAlt = new JTextField();
        TabSceneryGen.textHeightRef = new JTextField();
        TabSceneryGen.lblDynPosOffset = new JLabel("");
        TabSceneryGen.lblDynAltOffset = new JLabel("");
        buttonGroup = new ButtonGroup();
        buttonGroup_1 = new ButtonGroup();
        TabSceneryGen.rdbtnDisableOffsets = new JRadioButton("Disable Position Offset (not to be used before saving PSX SITU)");
        TabSceneryGen.rdbtnEnableDestOffsets = new JRadioButton("Enable Position Offset for FMC destination runway only");
        TabSceneryGen.rdbtnEnableAllOffsets = new JRadioButton("Enable Position Offset for all airports and runways  >>>>>>>>");
        TabSceneryGen.rdbtnSlaveSim = new JRadioButton("FSX/P3D/MSFS");
        TabSceneryGen.rdbtnSlavePSX = new JRadioButton("PSX");
        TabSceneryGen.chkUseBetaFSXP3DAlgos = new JCheckBox("Use New Lift/Flare Algorithms (BETA)");
        TabSceneryGen.simBridgeSyncThread = new SimBridgeSyncThread();
        TabSceneryGen.controlsSyncThread = new Thread(TabSceneryGen.simBridgeSyncThread);
        TabSceneryGen.showSimSlaveConfirmBox = false;
        TabSceneryGen.showPsxSlaveConfirmBox = false;
        TabSceneryGen.showExtPushConfirmBox = false;
    }
    
    public TabSceneryGen(final JTabbedPane argTabbedPane) throws IOException {
        TextTabParser.passJLabel(this, TabSceneryGen.lblDynPosOffset, TabSceneryGen.lblDynAltOffset);
        argTabbedPane.addTab("SceneryGen Bridge", null, TabSceneryGen.panel_5, null);
        TabSceneryGen.panel_5.setLayout(null);
        paintTab();
    }
    
    private static void paintTab() throws IOException {
        if (!StatusMonitor.getScenGenEnabled()) {
            final JLabel lblScenGenDisabled = new JLabel("Scenery Generator Bridge is currently disabled. Go to the Network Tab to enable.");
            lblScenGenDisabled.setFont(new Font("Tahoma", 1, 11));
            lblScenGenDisabled.setBounds(180, 180, 518, 16);
            TabSceneryGen.panel_5.add(lblScenGenDisabled);
            return;
        }
        final JCheckBox chkDoorsSync = new JCheckBox("Doors Synchronization");
        chkDoorsSync.setFont(new Font("Tahoma", 1, 11));
        TabSceneryGen.panel_5.add(chkDoorsSync);
        chkDoorsSync.setSelected(FileSettingConfig.getSimBridgeDoorsSync());
        StatusMonitor.setSimBridgeDoorsSync(FileSettingConfig.getSimBridgeDoorsSync());
        chkDoorsSync.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                StatusMonitor.setSimBridgeDoorsSync(chkDoorsSync.isSelected());
                if (StatusMonitor.getSimBridgeIsRunning()) {
                    if (!StatusMonitor.getSimBridgeDoorsSync()) {
                        try {
                            BridgeSimConnect.closeDoors(GndServiceBase.getAcftVersion());
                        }
                        catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                    else {
                        SimBridgeDoors.dispatchQi180(GndServiceBase.getAcftVersion(), GndServiceBase.getPsxDoorOpenBits());
                    }
                }
            }
        });
        TabSceneryGen.panel_5.add(TabSceneryGen.textHeightRef);
        TabSceneryGen.textHeightRef.setColumns(10);
        final JCheckBox chkSetHeightRef = new JCheckBox("Set height ref :");
        TabSceneryGen.panel_5.add(chkSetHeightRef);
        chkSetHeightRef.setSelected(FileSettingConfig.getSimBridgeHeightRefEnabled());
        StatusMonitor.setSimBridgeHeightRefEnabled(FileSettingConfig.getSimBridgeHeightRefEnabled());
        if (!StatusMonitor.getSimBridgeHeightRefEnabled()) {
            TabSceneryGen.textHeightRef.setText("INOP");
        }
        else {
            TabSceneryGen.textHeightRef.setText(FileSettingConfig.getSimBridgeHeightRef());
        }
        chkSetHeightRef.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                StatusMonitor.setSimBridgeHeightRefEnabled(chkSetHeightRef.isSelected());
                if (StatusMonitor.getSimBridgeHeightRefEnabled()) {
                    JOptionPane.showMessageDialog(null, "You are about to modify the aircraft height reference above the ground.\nSetting this data manually is at your own visual risks.", "Caution", 2);
                    if (StatusMonitor.getMsfsInUse()) {
                        TabSceneryGen.textHeightRef.setText("13.0");
                    }
                    else {
                        TabSceneryGen.textHeightRef.setText("15.1");
                    }
                }
                else {
                    TabSceneryGen.textHeightRef.setText("INOP");
                }
            }
        });
        if (FileSettingConfig.getScenGenTabSimSlaveShowConfirmDialog()) {
            TabSceneryGen.showSimSlaveConfirmBox = true;
        }
        if (FileSettingConfig.getScenGenTabPsxSlaveShowConfirmDialog()) {
            TabSceneryGen.showPsxSlaveConfirmBox = true;
        }
        if (FileSettingConfig.getScenGenTabExtPushShowConfirmDialog()) {
            TabSceneryGen.showExtPushConfirmBox = true;
        }
        final JButton btnStartSceneryGenerator = new JButton("Start Scenery Generator Bridge");
        btnStartSceneryGenerator.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (StatusMonitor.getScenGenEnabled() && StatusMonitor.getSimConnectIsConnected() && StatusMonitor.getPsxMainIsConnected() && StatusMonitor.getPsxBoostIsConnected() && !StatusMonitor.getSimBridgeIsRunning()) {
                    try {
                        if (TabSceneryGen.rdbtnSlaveSim.isSelected()) {
                            if (TabSceneryGen.showSimSlaveConfirmBox) {
                                final String message = "You are about to start the Scenery Generator Bridge with the Scenery Generator set as the startup\nsynchronization slave. If you confirm your selection,\nthe Scenery Generator aircraft will be moved to the actual PSX position. Is it what you want ?";
                                final JCheckBox checkbox = new JCheckBox("Do not show this message again");
                                final Object[] params = { message, checkbox };
                                final int dialogResult = JOptionPane.showConfirmDialog(null, params, "Startup Synchronization Slave Selection", 0);
                                if (dialogResult == 1 || dialogResult == -1) {
                                    return;
                                }
                                if (checkbox.isSelected()) {
                                    TabSceneryGen.access$3(false);
                                }
                            }
                        }
                        else if (TabSceneryGen.showPsxSlaveConfirmBox) {
                            final String message = "You are about to start the Scenery Generator Bridge with PSX set as the startup\nsynchronization slave. If you confirm your selection, PSX\nwill be moved to the actual Scenery Generator position and the Scenery Generator aircraft will\nnot move. This will only happen at the initial synchronization. All the future\nsynchronizations, for example when loading a PSX SITU will move the Scenery Generator\naircraft to the PSX position. Is it what you want ?";
                            final JCheckBox checkbox = new JCheckBox("Do not show this message again");
                            final Object[] params = { message, checkbox };
                            final int dialogResult = JOptionPane.showConfirmDialog(null, params, "Startup Synchronization Slave Selection", 0);
                            if (dialogResult == 1 || dialogResult == -1) {
                                return;
                            }
                            if (checkbox.isSelected()) {
                                TabSceneryGen.access$5(false);
                            }
                        }
                        if (StatusMonitor.getGndServiceExtPush() && GndServiceBase.getOnGround() && TabSceneryGen.showExtPushConfirmBox) {
                            final String message = "You are about to start the Scenery Generator Bridge and allow an external pushback system to be used.\nBe advisedthat PSX will not follow the Scenery Generator's aircraft movements until the taxi lights\nare turned ON. Is this what you want ?\nNote : You can disable this option by un-checking the \"Use of external pushback\" checkbox in the Ground Services Tab.";
                            final JCheckBox checkbox = new JCheckBox("Do not show this message again");
                            final Object[] params = { message, checkbox };
                            final int dialogResult = JOptionPane.showConfirmDialog(null, params, "External pushback confirmation", 0);
                            if (dialogResult == 1 || dialogResult == -1) {
                                return;
                            }
                            if (checkbox.isSelected()) {
                                TabSceneryGen.access$7(false);
                            }
                        }
                        if (!StatusMonitor.getSimBridgeSlaveIsSim()) {
                            if (!GndServiceBase.getOnGround() || !BridgeSimConnect.getSimOnGround()) {
                                JOptionPane.showMessageDialog(null, "Both PSX and Scenery Generator aircraft must be on the ground before to start the Scenery\nGenerator Bridge with PSX set as the startup synchronization slave.", "Invalid action", 0);
                                return;
                            }
                            final double elev = BridgeSimConnect.getSimGndAlt();
                            final int Qi198Elev = (int)(elev * 100.0);
                            SocketClientPSXMain.send("Qi198=" + Qi198Elev);
                            final double[] simPosAltAtt = BridgeSimConnect.getSimPosAltAtt();
                            final int hdg = (int)(simPosAltAtt[6] * 1000.0);
                            final int locElev = (int)(BridgeSimConnect.getSimGndAlt() * 10.0);
                            final int alt = locElev / 10;
                            SocketClientPSXMain.send("Qs122=1;0;0;" + String.valueOf(hdg) + ";" + String.valueOf(alt) + ";0;0;0;" + String.valueOf(simPosAltAtt[0]) + ";" + String.valueOf(simPosAltAtt[1]) + ";" + String.valueOf(locElev));
                        }
                        StatusMonitor.setSimBridgeIsRunning(true);
                        TabSceneryGen.controlsSyncThread.start();
                        if (StatusMonitor.getSimBridgeSlaveIsSim()) {
                            SimBridgeOffsetPos.setOffsets(true);
                        }
                        if (StatusMonitor.getSimBridgeDoorsSync()) {
                            SimBridgeDoors.dispatchQi180(GndServiceBase.getAcftVersion(), GndServiceBase.getPsxDoorOpenBits());
                        }
                        else {
                            BridgeSimConnect.closeDoors(0);
                        }
                        SimBridgeBase.dispatchQs122();
                    }
                    catch (IllegalThreadStateException ex) {}
                    catch (IOException ex2) {}
                    SocketClientPSXMain.send("bang");
                }
                else if (StatusMonitor.getScenGenEnabled() && !StatusMonitor.getSimBridgeIsRunning()) {
                    JOptionPane.showMessageDialog(null, "You must be connected with PSX Main, Boost servers\nand SimConnect before to start the Scenery Generator Bridge.", "Invalid action", 0);
                }
            }
        });
        TabSceneryGen.panel_5.add(btnStartSceneryGenerator);
        final JLabel lblStartupAlignmentSlave = new JLabel("Startup Synchronization Slave :");
        lblStartupAlignmentSlave.setFont(new Font("Tahoma", 1, 11));
        TabSceneryGen.panel_5.add(lblStartupAlignmentSlave);
        TabSceneryGen.rdbtnSlaveSim.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                StatusMonitor.setSimBridgeSlaveIsSim(true);
            }
        });
        TabSceneryGen.buttonGroup_1.add(TabSceneryGen.rdbtnSlaveSim);
        TabSceneryGen.panel_5.add(TabSceneryGen.rdbtnSlaveSim);
        TabSceneryGen.rdbtnSlavePSX.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                StatusMonitor.setSimBridgeSlaveIsSim(false);
            }
        });
        TabSceneryGen.buttonGroup_1.add(TabSceneryGen.rdbtnSlavePSX);
        TabSceneryGen.panel_5.add(TabSceneryGen.rdbtnSlavePSX);
        try {
            if (FileSettingConfig.getSimBridgeStartSlave().equals("PSX")) {
                StatusMonitor.setSimBridgeSlaveIsSim(false);
                TabSceneryGen.rdbtnSlavePSX.setSelected(true);
                TabSceneryGen.rdbtnSlaveSim.setSelected(false);
            }
            else {
                StatusMonitor.setSimBridgeSlaveIsSim(true);
                TabSceneryGen.rdbtnSlavePSX.setSelected(false);
                TabSceneryGen.rdbtnSlaveSim.setSelected(true);
            }
        }
        catch (NullPointerException e) {
            StatusMonitor.setSimBridgeSlaveIsSim(true);
            TabSceneryGen.rdbtnSlavePSX.setSelected(false);
            TabSceneryGen.rdbtnSlaveSim.setSelected(true);
        }
        if (!StatusMonitor.getMsfsInUse()) {
            TabSceneryGen.rdbtnSlavePSX.setBounds(696, 50, 78, 23);
            TabSceneryGen.rdbtnSlaveSim.setBounds(556, 50, 131, 23);
            lblStartupAlignmentSlave.setBounds(353, 54, 197, 14);
            btnStartSceneryGenerator.setBounds(29, 12, 305, 23);
            chkSetHeightRef.setBounds(556, 9, 124, 23);
            TabSceneryGen.textHeightRef.setBounds(692, 10, 46, 20);
            chkDoorsSync.setBounds(347, 10, 197, 23);
        }
        else {
            TabSceneryGen.rdbtnSlavePSX.setBounds(696, 80, 78, 23);
            TabSceneryGen.rdbtnSlaveSim.setBounds(556, 80, 131, 23);
            lblStartupAlignmentSlave.setBounds(353, 84, 197, 14);
            btnStartSceneryGenerator.setBounds(29, 42, 305, 23);
            chkSetHeightRef.setBounds(556, 39, 124, 23);
            TabSceneryGen.textHeightRef.setBounds(692, 40, 46, 20);
            chkDoorsSync.setBounds(347, 40, 197, 23);
        }
        if (!StatusMonitor.getMsfsInUse()) {
            TabSceneryGen.chkUseBetaFSXP3DAlgos.setFont(new Font("Tahoma", 1, 11));
            TabSceneryGen.panel_5.add(TabSceneryGen.chkUseBetaFSXP3DAlgos);
            TabSceneryGen.chkUseBetaFSXP3DAlgos.setBounds(466, 377, 300, 23);
            TabSceneryGen.chkUseBetaFSXP3DAlgos.setSelected(FileSettingConfig.getScenGenUseBetaAlgos());
            StatusMonitor.setSimBridgeUseBetaAlgos(FileSettingConfig.getScenGenUseBetaAlgos());
            TabSceneryGen.chkUseBetaFSXP3DAlgos.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (StatusMonitor.getSimBridgeIsRunning()) {
                        JOptionPane.showMessageDialog(null, "Scenery Generator Bridge is already running.\nYou must restart WidePSX for the modification take effect.", "Warning", 2);
                    }
                    else {
                        StatusMonitor.setSimBridgeUseBetaAlgos(TabSceneryGen.chkUseBetaFSXP3DAlgos.isSelected());
                    }
                }
            });
            final JButton btnCancelPosOffset = new JButton("Cancel FSX/P3D Position Offset");
            btnCancelPosOffset.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (StatusMonitor.getSimBridgeIsRunning() && StatusMonitor.getSimBridgePosOffsetUsed() && SimBridgeBase.getOnGround()) {
                        BridgeSimConnect.setCancelLdgOffset(true);
                    }
                }
            });
            btnCancelPosOffset.setBounds(29, 90, 305, 23);
            TabSceneryGen.panel_5.add(btnCancelPosOffset);
            final JLabel lblStaPosOffset = new JLabel("Position Offset Status :");
            lblStaPosOffset.setFont(new Font("Tahoma", 1, 11));
            lblStaPosOffset.setBounds(30, 224, 155, 14);
            TabSceneryGen.panel_5.add(lblStaPosOffset);
            TabSceneryGen.lblDynPosOffset.setBounds(200, 224, 518, 14);
            TabSceneryGen.panel_5.add(TabSceneryGen.lblDynPosOffset);
            final JLabel lblStaAltOffset = new JLabel("Altitude Offset Status :");
            lblStaAltOffset.setFont(new Font("Tahoma", 1, 11));
            lblStaAltOffset.setBounds(30, 264, 155, 14);
            TabSceneryGen.panel_5.add(lblStaAltOffset);
            TabSceneryGen.lblDynAltOffset.setBounds(200, 264, 518, 14);
            TabSceneryGen.panel_5.add(TabSceneryGen.lblDynAltOffset);
            final JLabel lblNewLabel = new JLabel("Must be used when fully stopped before saving SITU in PSX");
            lblNewLabel.setForeground(Color.RED);
            lblNewLabel.setFont(new Font("Tahoma", 2, 11));
            lblNewLabel.setBounds(353, 94, 314, 14);
            TabSceneryGen.panel_5.add(lblNewLabel);
            TabSceneryGen.rdbtnDisableOffsets.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    StatusMonitor.setSimBridgeUsingAllOffsets(false);
                    StatusMonitor.setSimBridgeUsingDestOffsets(false);
                    if (StatusMonitor.getSimBridgeIsRunning()) {
                        try {
                            SimBridgeOffsetPos.setOffsets(false);
                        }
                        catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                }
            });
            TabSceneryGen.buttonGroup.add(TabSceneryGen.rdbtnDisableOffsets);
            TabSceneryGen.rdbtnDisableOffsets.setBounds(27, 377, 443, 23);
            TabSceneryGen.panel_5.add(TabSceneryGen.rdbtnDisableOffsets);
            TabSceneryGen.rdbtnEnableDestOffsets.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    StatusMonitor.setSimBridgeUsingDestOffsets(true);
                    StatusMonitor.setSimBridgeUsingAllOffsets(false);
                    if (StatusMonitor.getSimBridgeIsRunning()) {
                        try {
                            SimBridgeOffsetPos.setOffsets(false);
                        }
                        catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                }
            });
            TabSceneryGen.buttonGroup.add(TabSceneryGen.rdbtnEnableDestOffsets);
            TabSceneryGen.rdbtnEnableDestOffsets.setBounds(27, 325, 557, 23);
            TabSceneryGen.panel_5.add(TabSceneryGen.rdbtnEnableDestOffsets);
            TabSceneryGen.rdbtnEnableAllOffsets.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    StatusMonitor.setSimBridgeUsingAllOffsets(true);
                    StatusMonitor.setSimBridgeUsingDestOffsets(false);
                    if (StatusMonitor.getSimBridgeIsRunning()) {
                        try {
                            SimBridgeOffsetPos.setOffsets(false);
                        }
                        catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                }
            });
            TabSceneryGen.buttonGroup.add(TabSceneryGen.rdbtnEnableAllOffsets);
            TabSceneryGen.rdbtnEnableAllOffsets.setBounds(27, 351, 443, 23);
            TabSceneryGen.panel_5.add(TabSceneryGen.rdbtnEnableAllOffsets);
            if (FileSettingConfig.getSimBridgeDestOffsetEnabled()) {
                StatusMonitor.setSimBridgeUsingDestOffsets(true);
                StatusMonitor.setSimBridgeUsingAllOffsets(false);
                TabSceneryGen.rdbtnDisableOffsets.setSelected(false);
                TabSceneryGen.rdbtnEnableAllOffsets.setSelected(false);
                TabSceneryGen.rdbtnEnableDestOffsets.setSelected(true);
            }
            else if (FileSettingConfig.getSimBridgeAllOffsetEnabled()) {
                StatusMonitor.setSimBridgeUsingAllOffsets(true);
                StatusMonitor.setSimBridgeUsingDestOffsets(false);
                TabSceneryGen.rdbtnDisableOffsets.setSelected(false);
                TabSceneryGen.rdbtnEnableAllOffsets.setSelected(true);
                TabSceneryGen.rdbtnEnableDestOffsets.setSelected(false);
            }
            else {
                StatusMonitor.setSimBridgeUsingAllOffsets(false);
                StatusMonitor.setSimBridgeUsingDestOffsets(false);
                TabSceneryGen.rdbtnDisableOffsets.setSelected(true);
                TabSceneryGen.rdbtnEnableAllOffsets.setSelected(false);
                TabSceneryGen.rdbtnEnableDestOffsets.setSelected(false);
            }
            final JButton btnForceDepRwyOffset = new JButton("Move FSX/P3D to PSX position (Ground)");
            btnForceDepRwyOffset.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (StatusMonitor.getSimBridgeIsRunning() && SimBridgeBase.getOnGround()) {
                        try {
                            if (StatusMonitor.getSimBridgeUsingAllOffsets()) {
                                SimBridgeOffsetPos.setOffsets(true);
                                BridgeSimConnect.forceDepRwyOffset();
                            }
                            else {
                                StatusMonitor.setSimBridgeUsingAllOffsets(true);
                                SimBridgeOffsetPos.setOffsets(true);
                                BridgeSimConnect.forceDepRwyOffset();
                                StatusMonitor.setSimBridgeUsingAllOffsets(false);
                            }
                        }
                        catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                }
            });
            btnForceDepRwyOffset.setBounds(29, 170, 305, 23);
            TabSceneryGen.panel_5.add(btnForceDepRwyOffset);
            final JLabel lblNewLabel_1 = new JLabel("Warning : may lead to FSX/P3D aircraft position \"jump\"");
            lblNewLabel_1.setForeground(Color.RED);
            lblNewLabel_1.setFont(new Font("Tahoma", 2, 11));
            lblNewLabel_1.setBounds(353, 174, 303, 14);
            TabSceneryGen.panel_5.add(lblNewLabel_1);
            final JLabel lblOffsetsPolicy = new JLabel("Landing Offset Policy :");
            lblOffsetsPolicy.setFont(new Font("Tahoma", 1, 11));
            lblOffsetsPolicy.setBounds(30, 304, 270, 14);
            TabSceneryGen.panel_5.add(lblOffsetsPolicy);
            final JButton btnAlignPsxDepRwyPos = new JButton("Apply FSX/P3D Position Offset for DEP RWY");
            btnAlignPsxDepRwyPos.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (StatusMonitor.getSimBridgeIsRunning() && SimBridgeBase.getOnGround() && !StatusMonitor.getSimBridgePosOffsetUsed()) {
                        SimBridgeOffsetPos.alignPsxWithDepRwy(true);
                    }
                }
            });
            btnAlignPsxDepRwyPos.setBounds(29, 130, 305, 23);
            TabSceneryGen.panel_5.add(btnAlignPsxDepRwyPos);
            final JCheckBox chkAutoAlignPSX = new JCheckBox("Allow this to be done automatically for FMC DEP RWY");
            chkAutoAlignPSX.setBounds(350, 130, 367, 23);
            TabSceneryGen.panel_5.add(chkAutoAlignPSX);
            chkAutoAlignPSX.setSelected(FileSettingConfig.getSimBridgeAutoAlignPsxPos());
            StatusMonitor.setSimBridgeAutoPsxDepAlign(FileSettingConfig.getSimBridgeAutoAlignPsxPos());
            chkAutoAlignPSX.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    StatusMonitor.setSimBridgeAutoPsxDepAlign(chkAutoAlignPSX.isSelected());
                }
            });
            TabSceneryGen.textInhibitOffsetAlt.setBounds(610, 353, 46, 20);
            TabSceneryGen.panel_5.add(TabSceneryGen.textInhibitOffsetAlt);
            TabSceneryGen.textInhibitOffsetAlt.setColumns(10);
            TabSceneryGen.textInhibitOffsetAlt.setText(FileSettingConfig.getSimBridgeOffsetInhibitAlt());
            final JLabel lblStaInhibitOffsetAlt = new JLabel("Inhibit Offset Above :");
            lblStaInhibitOffsetAlt.setBounds(475, 355, 138, 14);
            TabSceneryGen.panel_5.add(lblStaInhibitOffsetAlt);
            final JLabel lblFt = new JLabel("ft AGL");
            lblFt.setBounds(666, 356, 51, 14);
            TabSceneryGen.panel_5.add(lblFt);
        }
    }
    
    public static void saveTabData() {
        try {
            FileSettingConfig.saveSimBridgeDoorsSync(StatusMonitor.getSimBridgeDoorsSync());
            FileSettingConfig.saveSimBridgeEnableDestOffsets(TabSceneryGen.rdbtnEnableDestOffsets.isSelected());
            FileSettingConfig.saveSimBridgeEnableAllOffsets(TabSceneryGen.rdbtnEnableAllOffsets.isSelected());
            FileSettingConfig.saveSimBridgeAutoAlignPsxPos(StatusMonitor.getSimBridgeAutoPsxDepAlign());
            FileSettingConfig.saveSimBridgeOffsetInhibitAlt(TabSceneryGen.textInhibitOffsetAlt.getText());
            FileSettingConfig.saveSimBridgeHeightRef(TabSceneryGen.textHeightRef.getText());
            FileSettingConfig.saveSimBridgeHeightRefEnabled(StatusMonitor.getSimBridgeHeightRefEnabled());
            FileSettingConfig.saveScenGenUseBetaAlgos(TabSceneryGen.chkUseBetaFSXP3DAlgos.isSelected());
            if (StatusMonitor.getSimBridgeSlaveIsSim()) {
                FileSettingConfig.saveSimBridgeStartSlave("FSX/P3D");
            }
            else {
                FileSettingConfig.saveSimBridgeStartSlave("PSX");
            }
            if (TabSceneryGen.showSimSlaveConfirmBox) {
                FileSettingConfig.saveScenGenTabSimSlaveShowConfirmDialog(true);
            }
            else {
                FileSettingConfig.saveScenGenTabSimSlaveShowConfirmDialog(false);
            }
            if (TabSceneryGen.showPsxSlaveConfirmBox) {
                FileSettingConfig.saveScenGenTabPsxSlaveShowConfirmDialog(true);
            }
            else {
                FileSettingConfig.saveScenGenTabPsxSlaveShowConfirmDialog(false);
            }
            if (TabSceneryGen.showExtPushConfirmBox) {
                FileSettingConfig.saveScenGenTabExtPushShowConfirmDialog(true);
            }
            else {
                FileSettingConfig.saveScenGenTabExtPushShowConfirmDialog(false);
            }
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    
    public static int getOffsetInhibitAlt() {
        final String temp = TabSceneryGen.textInhibitOffsetAlt.getText();
        try {
            if (temp.isEmpty()) {
                return 50000;
            }
            return Integer.parseInt(temp);
        }
        catch (Exception e) {
            return 50000;
        }
    }
    
    public static double getHeightRef() {
        final String temp = TabSceneryGen.textHeightRef.getText();
        try {
            if (!temp.isEmpty()) {
                return Double.parseDouble(temp);
            }
            if (StatusMonitor.getMsfsInUse()) {
                return 13.0;
            }
            return 15.1;
        }
        catch (Exception e) {
            if (StatusMonitor.getMsfsInUse()) {
                return 13.0;
            }
            return 15.1;
        }
    }
    
    public static void refreshTab() throws IOException {
        TabSceneryGen.panel_5.removeAll();
        TabSceneryGen.panel_5.revalidate();
        TabSceneryGen.panel_5.repaint();
        paintTab();
    }
    
    static /* synthetic */ void access$3(final boolean showSimSlaveConfirmBox) {
        TabSceneryGen.showSimSlaveConfirmBox = showSimSlaveConfirmBox;
    }
    
    static /* synthetic */ void access$5(final boolean showPsxSlaveConfirmBox) {
        TabSceneryGen.showPsxSlaveConfirmBox = showPsxSlaveConfirmBox;
    }
    
    static /* synthetic */ void access$7(final boolean showExtPushConfirmBox) {
        TabSceneryGen.showExtPushConfirmBox = showExtPushConfirmBox;
    }
}
