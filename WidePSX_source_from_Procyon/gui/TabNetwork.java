// 
// Decompiled by Procyon v0.5.36
// 

package gui;

import javax.swing.AbstractButton;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import util.StatusMonitor;
import files.FileSettingConfig;
import java.awt.Font;
import javax.swing.ImageIcon;
import java.io.IOException;
import java.awt.LayoutManager;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JTabbedPane;
import network.BridgeSimConnect;
import network.SocketClientPSXBoost;
import network.SocketClientPSXMain;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPanel;

public class TabNetwork
{
    private static JPanel panel_0;
    private static JTextField textPsxIp;
    private static JTextField textPsxPort;
    private static JTextField textSimIp;
    private static JTextField textSimPort;
    private static JTextField textPsxBoostIp;
    private static JTextField textPsxBoostPort;
    private static JLabel lblDynPsxMainStatus;
    private static JLabel lblDynSimStatus;
    private static JLabel lblDynPsxBoostStatus;
    private static JCheckBox chkEnableSimConnect;
    private static JCheckBox chkEnableScenGen;
    private static final ButtonGroup groupRdbtnScenGen;
    private static JRadioButton rdbtnFsxP3d;
    private static JRadioButton rdbtnMsfs;
    private static SocketClientPSXMain socketClientPsxMain;
    private static SocketClientPSXBoost socketClientPsxBoost;
    private static BridgeSimConnect bridgeSimConnect;
    private static boolean chkEnableScenGenPrevStatus;
    private static boolean chkEnableSimConnectPrevStatus;
    private static boolean firstClickInPsxBoostPortText;
    
    static {
        TabNetwork.panel_0 = new JPanel();
        TabNetwork.lblDynPsxMainStatus = new JLabel("");
        TabNetwork.lblDynSimStatus = new JLabel("");
        TabNetwork.lblDynPsxBoostStatus = new JLabel("");
        TabNetwork.chkEnableSimConnect = new JCheckBox("Enable SimConnect Bridge");
        TabNetwork.chkEnableScenGen = new JCheckBox("Enable Scenery Generator Bridge");
        groupRdbtnScenGen = new ButtonGroup();
        TabNetwork.rdbtnFsxP3d = new JRadioButton("FSX/P3D");
        TabNetwork.rdbtnMsfs = new JRadioButton("MSFS");
        TabNetwork.socketClientPsxMain = new SocketClientPSXMain();
        TabNetwork.socketClientPsxBoost = new SocketClientPSXBoost();
        TabNetwork.bridgeSimConnect = new BridgeSimConnect();
        TabNetwork.chkEnableScenGenPrevStatus = false;
        TabNetwork.chkEnableSimConnectPrevStatus = false;
        TabNetwork.firstClickInPsxBoostPortText = true;
    }
    
    public TabNetwork(final JTabbedPane argTabbedPane) throws IOException {
        TextTabParser.passJLabel(this, TabNetwork.lblDynPsxMainStatus, TabNetwork.lblDynSimStatus, TabNetwork.lblDynPsxBoostStatus);
        argTabbedPane.addTab("Network", null, TabNetwork.panel_0, null);
        TabNetwork.panel_0.setLayout(null);
        paintTab();
    }
    
    private static void paintTab() throws IOException {
        final JLabel lblLogo = new JLabel(new ImageIcon("Logo_WidePSX_app.png"));
        lblLogo.setBounds(500, 300, 305, 106);
        TabNetwork.panel_0.add(lblLogo);
        final JLabel lblStaPsxIp = new JLabel("PSX Main IP : ");
        lblStaPsxIp.setFont(new Font("Tahoma", 1, 11));
        lblStaPsxIp.setBounds(30, 10, 94, 14);
        TabNetwork.panel_0.add(lblStaPsxIp);
        final JLabel lblStaPsxPort = new JLabel("PSX Main port : ");
        lblStaPsxPort.setFont(new Font("Tahoma", 1, 11));
        lblStaPsxPort.setBounds(30, 47, 94, 14);
        TabNetwork.panel_0.add(lblStaPsxPort);
        (TabNetwork.textPsxIp = new JTextField()).setBounds(160, 8, 120, 20);
        TabNetwork.panel_0.add(TabNetwork.textPsxIp);
        TabNetwork.textPsxIp.setColumns(10);
        TabNetwork.textPsxIp.setText(FileSettingConfig.getPsxIp());
        (TabNetwork.textPsxPort = new JTextField()).setBounds(160, 44, 120, 20);
        TabNetwork.panel_0.add(TabNetwork.textPsxPort);
        TabNetwork.textPsxPort.setColumns(10);
        TabNetwork.textPsxPort.setText(FileSettingConfig.getPsxPort());
        TabNetwork.chkEnableScenGen.setFont(new Font("Tahoma", 1, 11));
        TabNetwork.chkEnableScenGen.setBounds(346, 168, 259, 23);
        TabNetwork.panel_0.add(TabNetwork.chkEnableScenGen);
        TabNetwork.chkEnableScenGen.setSelected(FileSettingConfig.getScenGenEnabled());
        TabNetwork.chkEnableScenGenPrevStatus = TabNetwork.chkEnableScenGen.isSelected();
        StatusMonitor.setScenGenEnabled(FileSettingConfig.getScenGenEnabled());
        TabNetwork.chkEnableScenGen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (TabNetwork.chkEnableScenGenPrevStatus != TabNetwork.chkEnableScenGen.isSelected()) {
                    if (StatusMonitor.getSimBridgeIsRunning()) {
                        if (!TabNetwork.chkEnableScenGen.isSelected()) {
                            JOptionPane.showMessageDialog(null, "Scenery Generator Bridge is already running.\nYou must restart WidePSX for the modification take effect.", "Caution", 2);
                        }
                        else {
                            StatusMonitor.setScenGenEnabled(TabNetwork.chkEnableScenGen.isSelected());
                        }
                    }
                    else {
                        StatusMonitor.setScenGenEnabled(TabNetwork.chkEnableScenGen.isSelected());
                        TabNetwork.saveTabData();
                        try {
                            TabNetwork.refreshTab();
                            TabSceneryGen.refreshTab();
                        }
                        catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                    TabNetwork.access$2(TabNetwork.chkEnableScenGen.isSelected());
                }
            }
        });
        TabNetwork.chkEnableSimConnect.setHorizontalAlignment(2);
        TabNetwork.chkEnableSimConnect.setFont(new Font("Tahoma", 1, 11));
        TabNetwork.chkEnableSimConnect.setBounds(23, 169, 190, 23);
        TabNetwork.panel_0.add(TabNetwork.chkEnableSimConnect);
        TabNetwork.chkEnableSimConnect.setSelected(FileSettingConfig.getSimConnectEnabled());
        StatusMonitor.setSimConnectEnabled(FileSettingConfig.getSimConnectEnabled());
        if (StatusMonitor.getScenGenEnabled()) {
            TabNetwork.chkEnableSimConnect.setSelected(true);
            StatusMonitor.setSimConnectEnabled(true);
        }
        TabNetwork.chkEnableSimConnectPrevStatus = TabNetwork.chkEnableSimConnect.isSelected();
        TabNetwork.chkEnableSimConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (TabNetwork.chkEnableSimConnectPrevStatus != TabNetwork.chkEnableSimConnect.isSelected()) {
                    if (StatusMonitor.getSimConnectIsConnected() || StatusMonitor.getPsxMainIsConnected()) {
                        if (!TabNetwork.chkEnableScenGen.isSelected() && !TabNetwork.chkEnableSimConnect.isSelected() && StatusMonitor.getSimConnectIsConnected()) {
                            JOptionPane.showMessageDialog(null, "WidePSX is already connected.\nYou must restart WidePSX for the modification take effect.", "Caution", 2);
                        }
                        else {
                            StatusMonitor.setSimConnectEnabled(TabNetwork.chkEnableSimConnect.isSelected());
                            TabNetwork.saveTabData();
                            try {
                                TabNetwork.refreshTab();
                            }
                            catch (IOException e2) {
                                e2.printStackTrace();
                            }
                        }
                    }
                    else {
                        StatusMonitor.setSimConnectEnabled(TabNetwork.chkEnableSimConnect.isSelected());
                        TabNetwork.saveTabData();
                        try {
                            TabNetwork.refreshTab();
                        }
                        catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                    TabNetwork.access$5(TabNetwork.chkEnableSimConnect.isSelected());
                }
            }
        });
        final JLabel lblStaSimIp = new JLabel("SimConnect Host IP : ");
        lblStaSimIp.setFont(new Font("Tahoma", 1, 11));
        lblStaSimIp.setBounds(30, 217, 140, 14);
        TabNetwork.panel_0.add(lblStaSimIp);
        final JLabel lblStaSimPort = new JLabel("SimConnect Host Port : ");
        lblStaSimPort.setFont(new Font("Tahoma", 1, 11));
        lblStaSimPort.setBounds(30, 256, 140, 14);
        TabNetwork.panel_0.add(lblStaSimPort);
        (TabNetwork.textSimIp = new JTextField()).setBounds(180, 214, 120, 20);
        TabNetwork.panel_0.add(TabNetwork.textSimIp);
        TabNetwork.textSimIp.setColumns(10);
        TabNetwork.textSimIp.setText(FileSettingConfig.getSimIp());
        (TabNetwork.textSimPort = new JTextField()).setBounds(180, 253, 120, 20);
        TabNetwork.panel_0.add(TabNetwork.textSimPort);
        TabNetwork.textSimPort.setColumns(10);
        TabNetwork.textSimPort.setText(FileSettingConfig.getSimPort());
        if (!StatusMonitor.getSimConnectEnabled()) {
            TabNetwork.textSimIp.setText("DISABLED");
            TabNetwork.textSimPort.setText("DISABLED");
            TabNetwork.textSimIp.setEditable(false);
            TabNetwork.textSimPort.setEditable(false);
            TabNetwork.textSimIp.setForeground(UIManager.getColor("Button.disabledText"));
            TabNetwork.textSimPort.setForeground(UIManager.getColor("Button.disabledText"));
        }
        final JButton btnConnect = new JButton("Connect");
        btnConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (StatusMonitor.getPsxMainIsConnected() && (StatusMonitor.getPsxBoostIsConnected() || !StatusMonitor.getScenGenEnabled()) && (StatusMonitor.getSimConnectIsConnected() || !StatusMonitor.getSimConnectEnabled())) {
                    return;
                }
                if (StatusMonitor.getSimConnectEnabled()) {
                    if (!StatusMonitor.getSimConnectIsConnected() && BridgeSimConnect.setPort(TabNetwork.textSimPort.getText())) {
                        BridgeSimConnect.setIp(TabNetwork.textSimIp.getText());
                        TabNetwork.bridgeSimConnect.connect();
                    }
                    if (SocketClientPSXMain.setPort(TabNetwork.textPsxPort.getText())) {
                        SocketClientPSXMain.setIp(TabNetwork.textPsxIp.getText());
                        if (!StatusMonitor.getPsxMainIsConnected()) {
                            SocketClientPSXMain.connect();
                        }
                    }
                    if (StatusMonitor.getScenGenEnabled() && SocketClientPSXBoost.setIp(TabNetwork.textPsxBoostIp.getText()) && SocketClientPSXBoost.setPort(TabNetwork.textPsxBoostPort.getText()) && !StatusMonitor.getPsxBoostIsConnected()) {
                        SocketClientPSXBoost.connect();
                    }
                }
                else if (SocketClientPSXMain.setPort(TabNetwork.textPsxPort.getText())) {
                    SocketClientPSXMain.setIp(TabNetwork.textPsxIp.getText());
                    if (!StatusMonitor.getPsxMainIsConnected()) {
                        SocketClientPSXMain.connect();
                    }
                }
            }
        });
        btnConnect.setBounds(30, 336, 236, 23);
        TabNetwork.panel_0.add(btnConnect);
        final JLabel lblStaPsxStatus = new JLabel("PSX Main Connection Status : ");
        lblStaPsxStatus.setFont(new Font("Tahoma", 1, 11));
        lblStaPsxStatus.setBounds(30, 85, 183, 14);
        TabNetwork.panel_0.add(lblStaPsxStatus);
        final JLabel lblStaSimStatus = new JLabel("SimConnect Connection Status : ");
        lblStaSimStatus.setFont(new Font("Tahoma", 1, 11));
        lblStaSimStatus.setBounds(30, 296, 200, 14);
        TabNetwork.panel_0.add(lblStaSimStatus);
        TabNetwork.lblDynSimStatus.setBounds(233, 296, 467, 14);
        TabNetwork.panel_0.add(TabNetwork.lblDynSimStatus);
        TabNetwork.lblDynPsxMainStatus.setBounds(223, 85, 467, 14);
        TabNetwork.panel_0.add(TabNetwork.lblDynPsxMainStatus);
        final JLabel lblStaPsxBoostIp = new JLabel("PSX Boost IP :");
        lblStaPsxBoostIp.setFont(new Font("Tahoma", 1, 11));
        lblStaPsxBoostIp.setBounds(328, 10, 105, 14);
        TabNetwork.panel_0.add(lblStaPsxBoostIp);
        final JLabel lblStaPsxBoostPort = new JLabel("PSX Boost Port :");
        lblStaPsxBoostPort.setFont(new Font("Tahoma", 1, 11));
        lblStaPsxBoostPort.setBounds(328, 47, 105, 14);
        TabNetwork.panel_0.add(lblStaPsxBoostPort);
        (TabNetwork.textPsxBoostIp = new JTextField()).setBounds(464, 7, 120, 20);
        TabNetwork.panel_0.add(TabNetwork.textPsxBoostIp);
        TabNetwork.textPsxBoostIp.setColumns(10);
        TabNetwork.textPsxBoostIp.setText(FileSettingConfig.getPsxBoostIp());
        (TabNetwork.textPsxBoostPort = new JTextField()).addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                if (TabNetwork.firstClickInPsxBoostPortText) {
                    JOptionPane.showMessageDialog(null, "PSX Boost Server Port is 10749 by default. This field must\nbe edited by advanced users only for very specific usages.", "Caution", 2);
                    TabNetwork.access$14(false);
                }
            }
        });
        TabNetwork.textPsxBoostPort.setForeground(UIManager.getColor("Button.disabledText"));
        TabNetwork.textPsxBoostPort.setBounds(464, 44, 120, 20);
        TabNetwork.panel_0.add(TabNetwork.textPsxBoostPort);
        TabNetwork.textPsxBoostPort.setColumns(10);
        TabNetwork.textPsxBoostPort.setText("10749");
        TabNetwork.textPsxBoostPort.setText(FileSettingConfig.getPsxBoostPort());
        if (TabNetwork.textPsxBoostPort.getText().equals("")) {
            TabNetwork.textPsxBoostPort.setText("10749");
        }
        final JLabel lblStaPsxBoostSta = new JLabel("PSX Boost Connection Status :");
        lblStaPsxBoostSta.setFont(new Font("Tahoma", 1, 11));
        lblStaPsxBoostSta.setBounds(30, 123, 183, 14);
        TabNetwork.panel_0.add(lblStaPsxBoostSta);
        TabNetwork.lblDynPsxBoostStatus.setBounds(223, 123, 467, 14);
        TabNetwork.panel_0.add(TabNetwork.lblDynPsxBoostStatus);
        if (StatusMonitor.getScenGenEnabled()) {
            final JLabel lblNewLabel = new JLabel("Scenery Generator :");
            lblNewLabel.setFont(new Font("Tahoma", 1, 11));
            lblNewLabel.setBounds(357, 216, 131, 16);
            TabNetwork.panel_0.add(lblNewLabel);
            TabNetwork.rdbtnFsxP3d.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (!StatusMonitor.getSimBridgeIsRunning()) {
                        StatusMonitor.setMsfsInUse(false);
                        try {
                            TabSceneryGen.refreshTab();
                        }
                        catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "Scenery Generator Bridge is already running.\nYou must restart WidePSX for the modification take effect.", "Caution", 2);
                    }
                }
            });
            TabNetwork.groupRdbtnScenGen.add(TabNetwork.rdbtnFsxP3d);
            TabNetwork.rdbtnFsxP3d.setFont(new Font("Tahoma", 1, 11));
            TabNetwork.rdbtnFsxP3d.setBounds(500, 212, 105, 23);
            TabNetwork.panel_0.add(TabNetwork.rdbtnFsxP3d);
            TabNetwork.rdbtnMsfs.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (!StatusMonitor.getSimBridgeIsRunning()) {
                        StatusMonitor.setMsfsInUse(true);
                        try {
                            TabSceneryGen.refreshTab();
                        }
                        catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "Scenery Generator Bridge is already running.\nYou must restart WidePSX for the modification take effect.", "Caution", 2);
                    }
                }
            });
            TabNetwork.groupRdbtnScenGen.add(TabNetwork.rdbtnMsfs);
            TabNetwork.rdbtnMsfs.setFont(new Font("Tahoma", 1, 11));
            TabNetwork.rdbtnMsfs.setBounds(500, 251, 81, 23);
            TabNetwork.panel_0.add(TabNetwork.rdbtnMsfs);
        }
        try {
            if (FileSettingConfig.getScenGenInUse().equals("FSX/P3D")) {
                TabNetwork.rdbtnFsxP3d.setSelected(true);
                TabNetwork.rdbtnMsfs.setSelected(false);
                StatusMonitor.setMsfsInUse(false);
            }
            else if (FileSettingConfig.getScenGenInUse().equals("MSFS")) {
                TabNetwork.rdbtnFsxP3d.setSelected(false);
                TabNetwork.rdbtnMsfs.setSelected(true);
                StatusMonitor.setMsfsInUse(true);
            }
        }
        catch (Exception e) {
            TabNetwork.rdbtnFsxP3d.setSelected(true);
            TabNetwork.rdbtnMsfs.setSelected(false);
            StatusMonitor.setMsfsInUse(false);
        }
    }
    
    public static void saveTabData() {
        try {
            FileSettingConfig.savePsxIp(TabNetwork.textPsxIp.getText());
            FileSettingConfig.savePsxPort(TabNetwork.textPsxPort.getText());
            FileSettingConfig.savePsxBoostIp(TabNetwork.textPsxBoostIp.getText());
            FileSettingConfig.savePsxBoostPort(TabNetwork.textPsxBoostPort.getText());
            FileSettingConfig.saveScenGenEnabled(TabNetwork.chkEnableScenGen.isSelected());
            if (!TabNetwork.textSimIp.getText().equals("DISABLED") || !TabNetwork.textSimPort.getText().equals("DISABLED")) {
                FileSettingConfig.saveSimIp(TabNetwork.textSimIp.getText());
                FileSettingConfig.saveSimPort(TabNetwork.textSimPort.getText());
            }
            FileSettingConfig.saveSimConnectEnabled(TabNetwork.chkEnableSimConnect.isSelected());
            if (TabNetwork.rdbtnFsxP3d.isSelected()) {
                FileSettingConfig.saveScenGenInUse("FSX/P3D");
            }
            else if (TabNetwork.rdbtnMsfs.isSelected()) {
                FileSettingConfig.saveScenGenInUse("MSFS");
            }
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    
    public static void refreshTab() throws IOException {
        TabNetwork.panel_0.removeAll();
        TabNetwork.panel_0.revalidate();
        TabNetwork.panel_0.repaint();
        paintTab();
    }
    
    static /* synthetic */ void access$2(final boolean chkEnableScenGenPrevStatus) {
        TabNetwork.chkEnableScenGenPrevStatus = chkEnableScenGenPrevStatus;
    }
    
    static /* synthetic */ void access$5(final boolean chkEnableSimConnectPrevStatus) {
        TabNetwork.chkEnableSimConnectPrevStatus = chkEnableSimConnectPrevStatus;
    }
    
    static /* synthetic */ void access$14(final boolean firstClickInPsxBoostPortText) {
        TabNetwork.firstClickInPsxBoostPortText = firstClickInPsxBoostPortText;
    }
}
