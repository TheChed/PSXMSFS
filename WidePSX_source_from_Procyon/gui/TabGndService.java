// 
// Decompiled by Procyon v0.5.36
// 

package gui;

import java.io.IOException;
import files.FileSettingConfig;
import javax.swing.AbstractButton;
import gndService.GndServicePreFltSimplified;
import gndService.GndServicePreFltConv;
import simBridge.SimBridgeBase;
import javax.swing.JOptionPane;
import gndService.GndServiceBase;
import util.StatusMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.Color;
import java.awt.LayoutManager;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JCheckBox;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class TabGndService
{
    private static JTextField textOutTime;
    private static JLabel lblDynStatus;
    private static JLabel lblDynRefuelInop;
    private static JLabel lblZfwInop;
    private static JTextField textPlanedZfw;
    private static JTextField textReleaseFuel;
    private static JTextField textPushHdg;
    private static JTextField textPushDist;
    private static JTextField textArrIcao;
    private static JTextField textDepIcao;
    private static JRadioButton rdbtKgs;
    private static JRadioButton rdbtLbs;
    private static JRadioButton rdbtnHybrid;
    private static JRadioButton rdbtC_Dark;
    private static JRadioButton rdbtRefBoaComplete;
    private static JRadioButton rdbtPushReady;
    private static JRadioButton rdbtSimplified;
    private static JRadioButton rdbtnAcpR;
    private static JRadioButton rdbtnAcpC;
    private static JRadioButton rdbtnAcpL;
    private static final ButtonGroup buttonGroup;
    private static final ButtonGroup buttonGroup_1;
    private static final ButtonGroup buttonGroup_2;
    private static JCheckBox chkbxExtPush;
    
    static {
        TabGndService.lblDynStatus = new JLabel("Select the units, fill the empty boxes, and select the current PSX aircraft status");
        TabGndService.lblDynRefuelInop = new JLabel("");
        TabGndService.lblZfwInop = new JLabel("");
        TabGndService.rdbtKgs = new JRadioButton("Kgs / Meters");
        TabGndService.rdbtLbs = new JRadioButton("Lbs / Feet");
        TabGndService.rdbtnHybrid = new JRadioButton("Hybrid (Kgs / Feet)");
        TabGndService.rdbtC_Dark = new JRadioButton("Full preflight");
        TabGndService.rdbtRefBoaComplete = new JRadioButton("Refueling + boarding completed");
        TabGndService.rdbtPushReady = new JRadioButton("Ready for pushback");
        TabGndService.rdbtSimplified = new JRadioButton("Simplified operations");
        TabGndService.rdbtnAcpR = new JRadioButton("R");
        TabGndService.rdbtnAcpC = new JRadioButton("C");
        TabGndService.rdbtnAcpL = new JRadioButton("L");
        buttonGroup = new ButtonGroup();
        buttonGroup_1 = new ButtonGroup();
        buttonGroup_2 = new ButtonGroup();
        TabGndService.chkbxExtPush = new JCheckBox("Use of external pushback");
    }
    
    public TabGndService(final JTabbedPane argTabbedPane) throws IOException {
        TextTabParser.passJLabel(this, TabGndService.lblDynStatus);
        final JPanel panel_4 = new JPanel();
        argTabbedPane.addTab("GndServices", null, panel_4, null);
        panel_4.setLayout(null);
        TabGndService.lblDynStatus.setForeground(Color.RED);
        TabGndService.lblDynStatus.setBackground(new Color(255, 255, 255));
        TabGndService.lblDynStatus.setFont(new Font("Tahoma", 3, 11));
        final JLabel lblStaOutTime = new JLabel("Scheduled OUT time :");
        lblStaOutTime.setFont(new Font("Tahoma", 1, 11));
        lblStaOutTime.setBounds(30, 103, 136, 14);
        panel_4.add(lblStaOutTime);
        (TabGndService.textOutTime = new JTextField()).setBounds(196, 100, 86, 20);
        panel_4.add(TabGndService.textOutTime);
        TabGndService.textOutTime.setColumns(10);
        final JButton btnStart = new JButton("Start Module");
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (StatusMonitor.getPsxMainIsConnected()) {
                    if (!StatusMonitor.getGndServiceRunning()) {
                        StatusMonitor.setGndServiceInvalidTextValues(false);
                        if (GndServiceBase.getOnGround()) {
                            GndServiceBase.setMinutesToBlkOut(TabGndService.textOutTime.getText());
                            GndServiceBase.setPlanedFuel(TabGndService.textReleaseFuel.getText());
                            GndServiceBase.setPlanedZfw(TabGndService.textPlanedZfw.getText());
                            GndServiceBase.setPushBackStraightLen(TabGndService.textPushDist.getText());
                        }
                        if (TabGndService.rdbtKgs.isSelected()) {
                            GndServiceBase.setUnits("Kgs");
                        }
                        else if (TabGndService.rdbtLbs.isSelected()) {
                            GndServiceBase.setUnits("Lbs");
                        }
                        else {
                            GndServiceBase.setUnits("Hyb");
                        }
                        if (StatusMonitor.getGndServiceInvalidTextValues()) {
                            JOptionPane.showMessageDialog(null, "One or more edit box value is/are invalid and/or incomplete.", "Invalid action", 0);
                            return;
                        }
                        try {
                            if ((StatusMonitor.getGndServiceRefuelCompletePhase() || StatusMonitor.getGndServicePushReadyPhase()) && SimBridgeBase.getOnGround()) {
                                JOptionPane.showMessageDialog(null, "You are about to start the Ground Services module from an intermediate status.\nYou must set the fuel on board manually in the PSX Instructor/Service page.", "Caution", 2);
                            }
                            if (!StatusMonitor.getGndServiceSimplified()) {
                                GndServiceBase.setDynText("Do preflight actions and wait for a Ground Engineer call...");
                                final GndServicePreFltConv gndServicePreFltConv = new GndServicePreFltConv(TabGndService.lblDynStatus);
                                final Thread gndServicePreFltConvThread = new Thread(gndServicePreFltConv);
                                gndServicePreFltConvThread.start();
                            }
                            else {
                                GndServiceBase.setDynText("Refueling will start automatically when the IRS L Selector is set to \"NAV\".");
                                final GndServicePreFltSimplified gnsServiceSimplified = new GndServicePreFltSimplified(TabGndService.lblDynStatus);
                                final Thread gndServiceSimplifiedThread = new Thread(gnsServiceSimplified);
                                gndServiceSimplifiedThread.start();
                            }
                            StatusMonitor.setGndServiceRunning(true);
                        }
                        catch (IllegalThreadStateException ex) {}
                    }
                }
                else {
                    JOptionPane.showMessageDialog(null, "You must be connected with PSX Main Server before\nto start the Ground Services module.", "Invalid action", 0);
                }
            }
        });
        btnStart.setBounds(30, 10, 200, 23);
        panel_4.add(btnStart);
        TabGndService.lblDynStatus.setBounds(87, 63, 653, 14);
        panel_4.add(TabGndService.lblDynStatus);
        GndServiceBase.passJLabel(TabGndService.lblDynStatus);
        final JLabel lblUtcHours = new JLabel("UTC 24 hours format (Ex : 2245)");
        lblUtcHours.setFont(new Font("Tahoma", 2, 11));
        lblUtcHours.setBounds(341, 103, 199, 14);
        panel_4.add(lblUtcHours);
        final JLabel lblStaPlanedZfw = new JLabel("Planned ZFW :");
        lblStaPlanedZfw.setFont(new Font("Tahoma", 1, 11));
        lblStaPlanedZfw.setBounds(30, 143, 136, 14);
        panel_4.add(lblStaPlanedZfw);
        (TabGndService.textPlanedZfw = new JTextField()).setBounds(196, 140, 86, 20);
        panel_4.add(TabGndService.textPlanedZfw);
        TabGndService.textPlanedZfw.setColumns(10);
        final JLabel lblStaReleaseFuel = new JLabel("Planned release fuel :");
        lblStaReleaseFuel.setFont(new Font("Tahoma", 1, 11));
        lblStaReleaseFuel.setBounds(30, 183, 136, 14);
        panel_4.add(lblStaReleaseFuel);
        (TabGndService.textReleaseFuel = new JTextField()).setBounds(196, 180, 86, 20);
        panel_4.add(TabGndService.textReleaseFuel);
        TabGndService.textReleaseFuel.setColumns(10);
        final JLabel lblStaPushHdg = new JLabel("Target pushback HDG :");
        lblStaPushHdg.setFont(new Font("Tahoma", 1, 11));
        lblStaPushHdg.setBounds(30, 223, 136, 14);
        panel_4.add(lblStaPushHdg);
        (TabGndService.textPushHdg = new JTextField()).setBounds(196, 220, 86, 20);
        panel_4.add(TabGndService.textPushHdg);
        TabGndService.textPushHdg.setColumns(10);
        final JLabel lblStaPushDist = new JLabel("Pushback straight distance :");
        lblStaPushDist.setFont(new Font("Tahoma", 1, 11));
        lblStaPushDist.setBounds(30, 263, 161, 14);
        panel_4.add(lblStaPushDist);
        (TabGndService.textPushDist = new JTextField()).setBounds(196, 260, 86, 20);
        panel_4.add(TabGndService.textPushDist);
        TabGndService.textPushDist.setColumns(10);
        final JLabel lblMeters = new JLabel("");
        lblMeters.setForeground(Color.RED);
        lblMeters.setFont(new Font("Tahoma", 2, 11));
        lblMeters.setBounds(341, 183, 315, 14);
        panel_4.add(lblMeters);
        TabGndService.buttonGroup.add(TabGndService.rdbtKgs);
        TabGndService.rdbtKgs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (StatusMonitor.getGndServiceRunning()) {
                    JOptionPane.showMessageDialog(null, "WidePSX Ground Services Module is already running.\nYou must restart WidePSX for the modification take effect.", "Caution", 2);
                }
            }
        });
        TabGndService.rdbtKgs.setBounds(81, 308, 120, 23);
        panel_4.add(TabGndService.rdbtKgs);
        TabGndService.buttonGroup.add(TabGndService.rdbtLbs);
        TabGndService.rdbtLbs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (StatusMonitor.getGndServiceRunning()) {
                    JOptionPane.showMessageDialog(null, "WidePSX Ground Services Module is already running.\nYou must restart WidePSX for the modification take effect.", "Caution", 2);
                }
            }
        });
        TabGndService.rdbtLbs.setBounds(81, 334, 170, 23);
        panel_4.add(TabGndService.rdbtLbs);
        TabGndService.buttonGroup.add(TabGndService.rdbtnHybrid);
        TabGndService.rdbtnHybrid.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (StatusMonitor.getGndServiceRunning()) {
                    JOptionPane.showMessageDialog(null, "WidePSX Ground Services Module is already running.\nYou must restart WidePSX for the modification take effect.", "Caution", 2);
                }
            }
        });
        TabGndService.rdbtnHybrid.setBounds(81, 360, 154, 23);
        panel_4.add(TabGndService.rdbtnHybrid);
        try {
            if (FileSettingConfig.getGndServiceUnits().equals("Kgs")) {
                TabGndService.rdbtKgs.setSelected(true);
                TabGndService.rdbtLbs.setSelected(false);
                TabGndService.rdbtnHybrid.setSelected(false);
            }
            else if (FileSettingConfig.getGndServiceUnits().equals("Lbs")) {
                TabGndService.rdbtKgs.setSelected(false);
                TabGndService.rdbtLbs.setSelected(true);
                TabGndService.rdbtnHybrid.setSelected(false);
            }
            else {
                TabGndService.rdbtKgs.setSelected(false);
                TabGndService.rdbtLbs.setSelected(false);
                TabGndService.rdbtnHybrid.setSelected(true);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        final JLabel lblUnits = new JLabel("Units :");
        lblUnits.setFont(new Font("Tahoma", 1, 11));
        lblUnits.setBounds(30, 312, 59, 14);
        panel_4.add(lblUnits);
        final JLabel lblStaStatus = new JLabel("STATUS :");
        lblStaStatus.setFont(new Font("Tahoma", 1, 11));
        lblStaStatus.setBounds(30, 63, 59, 14);
        panel_4.add(lblStaStatus);
        final JLabel lblPreflightPhase = new JLabel("Scenario :");
        lblPreflightPhase.setFont(new Font("Tahoma", 1, 11));
        lblPreflightPhase.setBounds(480, 312, 71, 14);
        panel_4.add(lblPreflightPhase);
        (TabGndService.textArrIcao = new JTextField()).setHorizontalAlignment(0);
        TabGndService.textArrIcao.setBounds(611, 260, 50, 20);
        panel_4.add(TabGndService.textArrIcao);
        TabGndService.textArrIcao.setColumns(10);
        (TabGndService.textDepIcao = new JTextField()).setHorizontalAlignment(0);
        TabGndService.textDepIcao.setBounds(611, 220, 50, 20);
        panel_4.add(TabGndService.textDepIcao);
        TabGndService.textDepIcao.setColumns(10);
        final JLabel lblDeparture = new JLabel("Departure airport ICAO identifier (optional) :");
        lblDeparture.setFont(new Font("Tahoma", 1, 11));
        lblDeparture.setBounds(341, 223, 276, 14);
        panel_4.add(lblDeparture);
        final JLabel lblArrival = new JLabel("Arrival airport ICAO identifier (optional) :");
        lblArrival.setFont(new Font("Tahoma", 1, 11));
        lblArrival.setBounds(341, 263, 276, 14);
        panel_4.add(lblArrival);
        try {
            if (FileSettingConfig.getGndServiceScenario() == 1) {
                TabGndService.rdbtC_Dark.setSelected(false);
                TabGndService.rdbtRefBoaComplete.setSelected(true);
                TabGndService.rdbtPushReady.setSelected(false);
                TabGndService.rdbtSimplified.setSelected(false);
                StatusMonitor.setGndServiceColdDarkPhase(false);
                StatusMonitor.setGndServiceRefuelCompletePhase(true);
                StatusMonitor.setGndServicePushReadyPhase(false);
                StatusMonitor.setGndServiceSimplified(false);
                TabGndService.lblDynRefuelInop.setText("Automatic refueling inoperative");
                TabGndService.textReleaseFuel.setText("INOP");
                TabGndService.textOutTime.setText("");
                TabGndService.textArrIcao.setText("");
                TabGndService.textDepIcao.setText("");
                TabGndService.textPlanedZfw.setText("");
            }
            else if (FileSettingConfig.getGndServiceScenario() == 2) {
                TabGndService.rdbtC_Dark.setSelected(false);
                TabGndService.rdbtRefBoaComplete.setSelected(false);
                TabGndService.rdbtPushReady.setSelected(true);
                TabGndService.rdbtSimplified.setSelected(false);
                StatusMonitor.setGndServiceColdDarkPhase(false);
                StatusMonitor.setGndServiceRefuelCompletePhase(false);
                StatusMonitor.setGndServicePushReadyPhase(true);
                StatusMonitor.setGndServiceSimplified(false);
                TabGndService.lblDynRefuelInop.setText("Automatic refueling inoperative");
                TabGndService.textReleaseFuel.setText("INOP");
                TabGndService.textOutTime.setText("INOP");
                TabGndService.textArrIcao.setText("");
                TabGndService.textDepIcao.setText("");
                TabGndService.textPlanedZfw.setText("");
            }
            else if (FileSettingConfig.getGndServiceScenario() == 0) {
                TabGndService.rdbtC_Dark.setSelected(true);
                TabGndService.rdbtRefBoaComplete.setSelected(false);
                TabGndService.rdbtPushReady.setSelected(false);
                TabGndService.rdbtSimplified.setSelected(false);
                StatusMonitor.setGndServiceColdDarkPhase(true);
                StatusMonitor.setGndServiceRefuelCompletePhase(false);
                StatusMonitor.setGndServicePushReadyPhase(false);
                StatusMonitor.setGndServiceSimplified(false);
                TabGndService.lblDynRefuelInop.setText("");
                TabGndService.textReleaseFuel.setText("");
                TabGndService.textOutTime.setText("");
                TabGndService.textArrIcao.setText("");
                TabGndService.textDepIcao.setText("");
                TabGndService.textPlanedZfw.setText("");
            }
            else {
                TabGndService.rdbtC_Dark.setSelected(false);
                TabGndService.rdbtRefBoaComplete.setSelected(false);
                TabGndService.rdbtPushReady.setSelected(false);
                TabGndService.rdbtSimplified.setSelected(true);
                StatusMonitor.setGndServiceColdDarkPhase(false);
                StatusMonitor.setGndServiceRefuelCompletePhase(false);
                StatusMonitor.setGndServicePushReadyPhase(false);
                StatusMonitor.setGndServiceSimplified(true);
                TabGndService.lblDynRefuelInop.setText("Optional");
                TabGndService.lblZfwInop.setText("Optional");
                TabGndService.textReleaseFuel.setText("");
                TabGndService.textOutTime.setText("INOP");
                TabGndService.textArrIcao.setText("INOP");
                TabGndService.textDepIcao.setText("INOP");
                TabGndService.textPlanedZfw.setText("");
            }
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
        TabGndService.rdbtC_Dark.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (StatusMonitor.getGndServiceRunning()) {
                    JOptionPane.showMessageDialog(null, "WidePSX Ground Services Module is already running.\nYou must restart WidePSX for the modification take effect.", "Caution", 2);
                }
                else {
                    StatusMonitor.setGndServiceColdDarkPhase(true);
                    StatusMonitor.setGndServiceRefuelCompletePhase(false);
                    StatusMonitor.setGndServicePushReadyPhase(false);
                    StatusMonitor.setGndServiceSimplified(false);
                    TabGndService.lblDynRefuelInop.setText("");
                    TabGndService.lblZfwInop.setText("");
                    TabGndService.textReleaseFuel.setText("");
                    TabGndService.textOutTime.setText("");
                    TabGndService.textArrIcao.setText("");
                    TabGndService.textDepIcao.setText("");
                    TabGndService.textPlanedZfw.setText("");
                }
            }
        });
        TabGndService.buttonGroup_1.add(TabGndService.rdbtC_Dark);
        TabGndService.rdbtC_Dark.setBounds(563, 308, 211, 23);
        panel_4.add(TabGndService.rdbtC_Dark);
        TabGndService.rdbtRefBoaComplete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (StatusMonitor.getGndServiceRunning()) {
                    JOptionPane.showMessageDialog(null, "WidePSX Ground Services Module is already running.\nYou must restart WidePSX for the modification take effect.", "Caution", 2);
                }
                else {
                    StatusMonitor.setGndServiceColdDarkPhase(false);
                    StatusMonitor.setGndServiceRefuelCompletePhase(true);
                    StatusMonitor.setGndServicePushReadyPhase(false);
                    StatusMonitor.setGndServiceSimplified(false);
                    TabGndService.lblDynRefuelInop.setText("Automatic refueling inoperative");
                    TabGndService.lblZfwInop.setText("");
                    TabGndService.textReleaseFuel.setText("INOP");
                    TabGndService.textOutTime.setText("");
                    TabGndService.textArrIcao.setText("");
                    TabGndService.textDepIcao.setText("");
                    TabGndService.textPlanedZfw.setText("");
                }
            }
        });
        TabGndService.buttonGroup_1.add(TabGndService.rdbtRefBoaComplete);
        TabGndService.rdbtRefBoaComplete.setBounds(563, 334, 262, 23);
        panel_4.add(TabGndService.rdbtRefBoaComplete);
        TabGndService.rdbtPushReady.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (StatusMonitor.getGndServiceRunning()) {
                    JOptionPane.showMessageDialog(null, "WidePSX Ground Services Module is already running.\nYou must restart WidePSX for the modification take effect.", "Caution", 2);
                }
                else {
                    StatusMonitor.setGndServiceColdDarkPhase(false);
                    StatusMonitor.setGndServiceRefuelCompletePhase(false);
                    StatusMonitor.setGndServicePushReadyPhase(true);
                    StatusMonitor.setGndServiceSimplified(false);
                    TabGndService.lblDynRefuelInop.setText("Automatic refueling inoperative");
                    TabGndService.lblZfwInop.setText("");
                    TabGndService.textReleaseFuel.setText("INOP");
                    TabGndService.textOutTime.setText("INOP");
                    TabGndService.textArrIcao.setText("");
                    TabGndService.textDepIcao.setText("");
                    TabGndService.textPlanedZfw.setText("");
                }
            }
        });
        TabGndService.buttonGroup_1.add(TabGndService.rdbtPushReady);
        TabGndService.rdbtPushReady.setBounds(563, 360, 213, 23);
        panel_4.add(TabGndService.rdbtPushReady);
        TabGndService.rdbtSimplified.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                if (StatusMonitor.getGndServiceRunning()) {
                    JOptionPane.showMessageDialog(null, "WidePSX Ground Services Module is already running.\nYou must restart WidePSX for the modification take effect.", "Caution", 2);
                }
                else {
                    StatusMonitor.setGndServiceColdDarkPhase(false);
                    StatusMonitor.setGndServiceRefuelCompletePhase(false);
                    StatusMonitor.setGndServicePushReadyPhase(false);
                    StatusMonitor.setGndServiceSimplified(true);
                    TabGndService.lblDynRefuelInop.setText("Optional");
                    TabGndService.lblZfwInop.setText("Optional");
                    TabGndService.textReleaseFuel.setText("");
                    TabGndService.textOutTime.setText("INOP");
                    TabGndService.textArrIcao.setText("INOP");
                    TabGndService.textDepIcao.setText("INOP");
                    TabGndService.textPlanedZfw.setText("");
                }
            }
        });
        TabGndService.rdbtSimplified.setBounds(563, 386, 211, 23);
        TabGndService.buttonGroup_1.add(TabGndService.rdbtSimplified);
        panel_4.add(TabGndService.rdbtSimplified);
        TabGndService.lblDynRefuelInop.setForeground(Color.RED);
        TabGndService.lblDynRefuelInop.setFont(new Font("Tahoma", 2, 11));
        TabGndService.lblDynRefuelInop.setBounds(341, 183, 351, 14);
        panel_4.add(TabGndService.lblDynRefuelInop);
        TabGndService.lblZfwInop.setForeground(Color.RED);
        TabGndService.lblZfwInop.setFont(new Font("Tahoma", 2, 11));
        TabGndService.lblZfwInop.setBounds(341, 141, 276, 16);
        panel_4.add(TabGndService.lblZfwInop);
        final JLabel lblStaAcpVol = new JLabel("Volume control ACP :");
        lblStaAcpVol.setFont(new Font("Tahoma", 1, 11));
        lblStaAcpVol.setBounds(224, 312, 145, 14);
        panel_4.add(lblStaAcpVol);
        TabGndService.rdbtnAcpL.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (StatusMonitor.getGndServiceRunning()) {
                    JOptionPane.showMessageDialog(null, "WidePSX Ground Services Module is already running.\nYou must restart WidePSX for the modification take effect.", "Caution", 2);
                }
                else {
                    StatusMonitor.setGndServiceVolumeAcp("L");
                }
            }
        });
        TabGndService.buttonGroup_2.add(TabGndService.rdbtnAcpL);
        TabGndService.rdbtnAcpL.setBounds(365, 308, 59, 23);
        panel_4.add(TabGndService.rdbtnAcpL);
        TabGndService.rdbtnAcpC.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (StatusMonitor.getGndServiceRunning()) {
                    JOptionPane.showMessageDialog(null, "WidePSX Ground Services Module is already running.\nYou must restart WidePSX for the modification take effect.", "Caution", 2);
                }
                else {
                    StatusMonitor.setGndServiceVolumeAcp("C");
                }
            }
        });
        TabGndService.buttonGroup_2.add(TabGndService.rdbtnAcpC);
        TabGndService.rdbtnAcpC.setBounds(365, 334, 59, 23);
        panel_4.add(TabGndService.rdbtnAcpC);
        TabGndService.rdbtnAcpR.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (StatusMonitor.getGndServiceRunning()) {
                    JOptionPane.showMessageDialog(null, "WidePSX Ground Services Module is already running.\nYou must restart WidePSX for the modification take effect.", "Caution", 2);
                }
                else {
                    StatusMonitor.setGndServiceVolumeAcp("R");
                }
            }
        });
        TabGndService.buttonGroup_2.add(TabGndService.rdbtnAcpR);
        TabGndService.rdbtnAcpR.setBounds(365, 360, 59, 23);
        panel_4.add(TabGndService.rdbtnAcpR);
        TabGndService.chkbxExtPush.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                if (StatusMonitor.getSimBridgeIsRunning()) {
                    JOptionPane.showMessageDialog(null, "Scenery Generator Bridge is already running.\nYou must restart WidePSX for the modification take effect.", "Caution", 2);
                }
                else if (TabGndService.chkbxExtPush.isSelected()) {
                    StatusMonitor.setGndServiceExtPush(true);
                    TabGndService.textPushDist.setText("INOP");
                    TabGndService.textPushHdg.setText("INOP");
                }
                else {
                    StatusMonitor.setGndServiceExtPush(false);
                    TabGndService.textPushDist.setText("");
                    TabGndService.textPushHdg.setText("");
                }
            }
        });
        TabGndService.chkbxExtPush.setBounds(335, 8, 251, 23);
        panel_4.add(TabGndService.chkbxExtPush);
        try {
            if (FileSettingConfig.getGndServiceExtPush()) {
                TabGndService.chkbxExtPush.setSelected(true);
                StatusMonitor.setGndServiceExtPush(true);
                TabGndService.textPushDist.setText("INOP");
                TabGndService.textPushHdg.setText("INOP");
            }
            else {
                TabGndService.chkbxExtPush.setSelected(false);
                StatusMonitor.setGndServiceExtPush(false);
                TabGndService.textPushDist.setText("");
                TabGndService.textPushHdg.setText("");
            }
        }
        catch (Exception e3) {
            TabGndService.chkbxExtPush.setSelected(false);
            StatusMonitor.setGndServiceExtPush(false);
            TabGndService.textPushDist.setText("");
            TabGndService.textPushHdg.setText("");
        }
        try {
            if (FileSettingConfig.getGndServiceVolumeAcp().equals("L")) {
                TabGndService.rdbtnAcpL.setSelected(true);
                TabGndService.rdbtnAcpC.setSelected(false);
                TabGndService.rdbtnAcpR.setSelected(false);
                StatusMonitor.setGndServiceVolumeAcp("L");
            }
            else if (FileSettingConfig.getGndServiceVolumeAcp().equals("C")) {
                TabGndService.rdbtnAcpL.setSelected(false);
                TabGndService.rdbtnAcpC.setSelected(true);
                TabGndService.rdbtnAcpR.setSelected(false);
                StatusMonitor.setGndServiceVolumeAcp("C");
            }
            else {
                TabGndService.rdbtnAcpL.setSelected(false);
                TabGndService.rdbtnAcpC.setSelected(false);
                TabGndService.rdbtnAcpR.setSelected(true);
                StatusMonitor.setGndServiceVolumeAcp("R");
            }
        }
        catch (Exception e3) {
            TabGndService.rdbtnAcpL.setSelected(true);
            TabGndService.rdbtnAcpC.setSelected(false);
            TabGndService.rdbtnAcpR.setSelected(false);
            StatusMonitor.setGndServiceVolumeAcp("L");
        }
    }
    
    public static void saveTabData() {
        try {
            if (TabGndService.rdbtKgs.isSelected()) {
                FileSettingConfig.saveGndServiceUnits("Kgs");
            }
            else if (TabGndService.rdbtLbs.isSelected()) {
                FileSettingConfig.saveGndServiceUnits("Lbs");
            }
            else {
                FileSettingConfig.saveGndServiceUnits("Hyb");
            }
            if (TabGndService.chkbxExtPush.isSelected()) {
                FileSettingConfig.saveGndServiceExtPush(true);
            }
            else {
                FileSettingConfig.saveGndServiceExtPush(false);
            }
            if (TabGndService.rdbtC_Dark.isSelected()) {
                FileSettingConfig.saveGndServiceScenario(0);
            }
            else if (TabGndService.rdbtRefBoaComplete.isSelected()) {
                FileSettingConfig.saveGndServiceScenario(1);
            }
            else if (TabGndService.rdbtPushReady.isSelected()) {
                FileSettingConfig.saveGndServiceScenario(2);
            }
            else {
                FileSettingConfig.saveGndServiceScenario(3);
            }
            if (TabGndService.rdbtnAcpL.isSelected()) {
                FileSettingConfig.saveGndServiceVolumeAcp("L");
            }
            else if (TabGndService.rdbtnAcpC.isSelected()) {
                FileSettingConfig.saveGndServiceVolumeAcp("C");
            }
            else {
                FileSettingConfig.saveGndServiceVolumeAcp("R");
            }
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    
    public static double getPushBackHdg() {
        double temp = 0.0;
        try {
            temp = Double.parseDouble(TabGndService.textPushHdg.getText());
        }
        catch (NumberFormatException e) {
            StatusMonitor.setGndServiceInvalidTextValues(true);
            return 0.0;
        }
        return temp;
    }
    
    public static String getDepIcao() {
        return TabGndService.textDepIcao.getText();
    }
    
    public static String getArrIcao() {
        return TabGndService.textArrIcao.getText();
    }
}
