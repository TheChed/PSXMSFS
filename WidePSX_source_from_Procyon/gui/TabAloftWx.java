// 
// Decompiled by Procyon v0.5.36
// 

package gui;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.AbstractButton;
import javax.swing.JScrollPane;
import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;
import util.StatusMonitor;
import files.FileAsnWx;
import javax.swing.JFileChooser;
import files.FileSettingConfig;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import aloftWx.TurbZoneDetect;
import aloftWx.AloftWxDyn;
import aloftWx.TurbWptData;
import aloftWx.AloftWxSta;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;

public class TabAloftWx
{
    private static JLabel lblDynAloftWx;
    private static JLabel lblDynTurbStatus;
    private static JLabel lblDynUplinkStatus;
    private static final ButtonGroup groupRdbtnWxMode;
    private static JRadioButton rdbtnDynamicMode;
    private static JRadioButton rdbtnStaticMode;
    private static JRadioButton rdbtnTurbMode;
    private static AloftWxSta aloftWxSta;
    private static TurbWptData turbWptData;
    private static Thread aloftWxStaThread;
    private static AloftWxDyn aloftWxDyn;
    private static Thread aloftWxDynThread;
    private static TurbZoneDetect turbZoneDetect;
    private static Thread turbZoneDetectThread;
    private JTable table;
    
    static {
        TabAloftWx.lblDynAloftWx = new JLabel("");
        TabAloftWx.lblDynTurbStatus = new JLabel("");
        TabAloftWx.lblDynUplinkStatus = new JLabel("");
        groupRdbtnWxMode = new ButtonGroup();
        TabAloftWx.rdbtnDynamicMode = new JRadioButton("Dynamic Mode");
        TabAloftWx.rdbtnStaticMode = new JRadioButton("Static Mode");
        TabAloftWx.rdbtnTurbMode = new JRadioButton("Turb. Only");
        TabAloftWx.aloftWxSta = new AloftWxSta();
        TabAloftWx.aloftWxStaThread = new Thread(TabAloftWx.aloftWxSta);
        TabAloftWx.aloftWxDyn = new AloftWxDyn();
        TabAloftWx.aloftWxDynThread = new Thread(TabAloftWx.aloftWxDyn);
        TabAloftWx.turbZoneDetect = new TurbZoneDetect();
        TabAloftWx.turbZoneDetectThread = new Thread(TabAloftWx.turbZoneDetect);
    }
    
    public TabAloftWx(final JTabbedPane argTabbedPane) throws IOException {
        TextTabParser.passJLabel(this, TabAloftWx.lblDynAloftWx, TabAloftWx.lblDynTurbStatus, TabAloftWx.lblDynUplinkStatus);
        final JPanel panel_1 = new JPanel();
        argTabbedPane.addTab("AloftWx", null, panel_1, null);
        panel_1.setLayout(null);
        final JLabel lblStaAloftWx = new JLabel("AloftWx Status :");
        lblStaAloftWx.setFont(new Font("Tahoma", 1, 11));
        lblStaAloftWx.setBounds(30, 133, 193, 14);
        panel_1.add(lblStaAloftWx);
        TabAloftWx.lblDynAloftWx.setBounds(233, 133, 456, 14);
        panel_1.add(TabAloftWx.lblDynAloftWx);
        final JButton btnSelectFp = new JButton("Select AS Flight Plan file");
        btnSelectFp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                try {
                    String defaultLocation = "";
                    boolean skip = false;
                    String temp = null;
                    temp = FileSettingConfig.getAsnFileDefaultLoc();
                    if (temp == null) {
                        skip = true;
                    }
                    if (!skip && !FileSettingConfig.getAsnFileDefaultLoc().isEmpty()) {
                        defaultLocation = FileSettingConfig.getAsnFileDefaultLoc();
                    }
                    final JFileChooser fc = new JFileChooser(defaultLocation);
                    final int returnVal = fc.showOpenDialog(fc);
                    if (returnVal == 0) {
                        final File file = fc.getSelectedFile();
                        if (FileAsnWx.checkFileName(file.getPath()) == 1) {
                            FileSettingConfig.saveAsnFileDefaultLoc(file.getPath());
                            FileAsnWx.setPath(file.getPath());
                            StatusMonitor.setAloftWxFileLoaded(true);
                            StatusMonitor.setAloftWxTocNotFound(false);
                            StatusMonitor.setFmcUplinkNoWxFileLoaded(false);
                            StatusMonitor.setFmcUplinkTodDestNotFound(false);
                        }
                        else {
                            StatusMonitor.setAloftWxFileLoaded(false);
                            JOptionPane.showMessageDialog(null, "Invalid WX file. Please select a valid ASN \"activeflightplanwx.txt\" file", "Invalid WX file.", 0);
                        }
                    }
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        });
        btnSelectFp.setBounds(30, 45, 200, 23);
        panel_1.add(btnSelectFp);
        final JButton btnStartAloftWx = new JButton("Start Module");
        btnStartAloftWx.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (StatusMonitor.getAloftWxRunning()) {
                    return;
                }
                if (StatusMonitor.getAloftWxDynMode()) {
                    if (StatusMonitor.getSimConnectIsConnected() && StatusMonitor.getPsxMainIsConnected()) {
                        StatusMonitor.setAloftWxRunning(true);
                        try {
                            TabAloftWx.aloftWxDynThread.start();
                            TabAloftWx.turbZoneDetectThread.start();
                            return;
                        }
                        catch (IllegalThreadStateException e2) {
                            return;
                        }
                    }
                    JOptionPane.showMessageDialog(null, "You must be connected with PSX Main Server and SimConnect\nbefore to start the AloftWx module in dynamic mode.", "Invalid action", 0);
                }
                else if (StatusMonitor.getAloftWxTurbMode()) {
                    if (StatusMonitor.getPsxMainIsConnected()) {
                        StatusMonitor.setAloftWxRunning(true);
                        try {
                            TabAloftWx.turbZoneDetectThread.start();
                            return;
                        }
                        catch (IllegalThreadStateException e2) {
                            return;
                        }
                    }
                    JOptionPane.showMessageDialog(null, "You must be connected with PSX Main Server before\nto start the AloftWx module in Turb. Only mode.", "Invalid action", 0);
                }
                else if (StatusMonitor.getPsxMainIsConnected()) {
                    if (StatusMonitor.getAloftWxFileLoaded()) {
                        StatusMonitor.setAloftWxRunning(true);
                        try {
                            TabAloftWx.aloftWxStaThread.start();
                            TabAloftWx.turbZoneDetectThread.start();
                            return;
                        }
                        catch (IllegalThreadStateException e2) {
                            return;
                        }
                    }
                    JOptionPane.showMessageDialog(null, "You must load a valid ASN activeflightplanwx.txt file\nbefore to start the AloftWx module in static mode.", "Invalid action", 0);
                }
                else {
                    JOptionPane.showMessageDialog(null, "You must be connected with PSX Main Server before\nto start the AloftWx module in static mode.", "Invalid action", 0);
                }
            }
        });
        btnStartAloftWx.setBounds(30, 10, 200, 23);
        panel_1.add(btnStartAloftWx);
        final JButton btnClearTurbTable = new JButton("Clear Turbulence Table");
        btnClearTurbTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                for (int i = 0; i < 5; ++i) {
                    for (int c = 0; c < 5; ++c) {
                        TabAloftWx.this.table.setValueAt("", i, c);
                    }
                }
            }
        });
        btnClearTurbTable.setBounds(30, 80, 200, 23);
        panel_1.add(btnClearTurbTable);
        final JLabel lblStaTurbStatus = new JLabel("Turbulence Generation Status : ");
        lblStaTurbStatus.setFont(new Font("Tahoma", 1, 11));
        lblStaTurbStatus.setBounds(30, 158, 193, 14);
        panel_1.add(lblStaTurbStatus);
        TabAloftWx.lblDynTurbStatus.setBounds(234, 158, 456, 14);
        panel_1.add(TabAloftWx.lblDynTurbStatus);
        final JLabel lblStaUplinkStatus = new JLabel("FMC Winds Uplink Status : ");
        lblStaUplinkStatus.setFont(new Font("Tahoma", 1, 11));
        lblStaUplinkStatus.setBounds(30, 183, 193, 14);
        panel_1.add(lblStaUplinkStatus);
        TabAloftWx.lblDynUplinkStatus.setBounds(234, 183, 456, 14);
        panel_1.add(TabAloftWx.lblDynUplinkStatus);
        final JScrollPane scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(31);
        scrollPane.setVerticalScrollBarPolicy(21);
        scrollPane.setBounds(30, 260, 630, 102);
        panel_1.add(scrollPane);
        final String[] culomns = { "Intensity (L, M, S)", "From (WPT)", "To (WPT)", "Lower lim (feet)", "Upper lim (feet)" };
        final String[][] data = { { "", "", "", "", "" }, { "", "", "", "", "" }, { "", "", "", "", "" }, { "", "", "", "", "" }, { "", "", "", "", "" } };
        scrollPane.setViewportView(this.table = new JTable(data, culomns));
        final JLabel lblCatConfigurationTable = new JLabel("CAT Configuration Table");
        lblCatConfigurationTable.setFont(new Font("Tahoma", 1, 11));
        lblCatConfigurationTable.setBounds(277, 230, 237, 14);
        panel_1.add(lblCatConfigurationTable);
        TabAloftWx.groupRdbtnWxMode.add(TabAloftWx.rdbtnDynamicMode);
        TabAloftWx.rdbtnDynamicMode.setFont(new Font("Tahoma", 1, 11));
        TabAloftWx.rdbtnDynamicMode.setBounds(277, 8, 118, 23);
        panel_1.add(TabAloftWx.rdbtnDynamicMode);
        TabAloftWx.rdbtnDynamicMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (StatusMonitor.getAloftWxRunning() && StatusMonitor.getAloftWxStaMode()) {
                    JOptionPane.showMessageDialog(null, "AloftWx Module is already running in Static Mode.\nYou must restart WidePSX for the modification take effect.", "Warning", 2);
                }
                else {
                    StatusMonitor.setAloftWxDynMode(true);
                    StatusMonitor.setAloftWxStaMode(false);
                    StatusMonitor.setAloftWxTurbMode(false);
                }
            }
        });
        TabAloftWx.groupRdbtnWxMode.add(TabAloftWx.rdbtnStaticMode);
        TabAloftWx.rdbtnStaticMode.setFont(new Font("Tahoma", 1, 11));
        TabAloftWx.rdbtnStaticMode.setBounds(397, 9, 104, 23);
        panel_1.add(TabAloftWx.rdbtnStaticMode);
        TabAloftWx.groupRdbtnWxMode.add(TabAloftWx.rdbtnTurbMode);
        TabAloftWx.rdbtnTurbMode.setFont(new Font("Tahoma", 1, 11));
        TabAloftWx.rdbtnTurbMode.setBounds(505, 9, 141, 23);
        panel_1.add(TabAloftWx.rdbtnTurbMode);
        TabAloftWx.rdbtnStaticMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (StatusMonitor.getAloftWxRunning() && StatusMonitor.getAloftWxDynMode()) {
                    JOptionPane.showMessageDialog(null, "AloftWx Module is already running in Dynamic Mode.\nYou must restart WidePSX for the modification take effect.", "Warning", 2);
                }
                else {
                    StatusMonitor.setAloftWxDynMode(false);
                    StatusMonitor.setAloftWxTurbMode(false);
                    StatusMonitor.setAloftWxStaMode(true);
                }
            }
        });
        TabAloftWx.rdbtnTurbMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (StatusMonitor.getAloftWxRunning() && StatusMonitor.getAloftWxDynMode()) {
                    JOptionPane.showMessageDialog(null, "AloftWx Module is already running in Dynamic Mode.\nYou must restart WidePSX for the modification take effect.", "Warning", 2);
                }
                else {
                    StatusMonitor.setAloftWxDynMode(false);
                    StatusMonitor.setAloftWxTurbMode(true);
                    StatusMonitor.setAloftWxStaMode(false);
                }
            }
        });
        try {
            if (FileSettingConfig.getAloftWxDynMode()) {
                TabAloftWx.rdbtnTurbMode.setSelected(false);
                TabAloftWx.rdbtnDynamicMode.setSelected(true);
                TabAloftWx.rdbtnStaticMode.setSelected(false);
                StatusMonitor.setAloftWxDynMode(true);
                StatusMonitor.setAloftWxStaMode(false);
                StatusMonitor.setAloftWxTurbMode(false);
            }
            else if (FileSettingConfig.getAloftWxTurbMode()) {
                TabAloftWx.rdbtnTurbMode.setSelected(true);
                TabAloftWx.rdbtnDynamicMode.setSelected(false);
                TabAloftWx.rdbtnStaticMode.setSelected(false);
                StatusMonitor.setAloftWxTurbMode(true);
                StatusMonitor.setAloftWxDynMode(false);
                StatusMonitor.setAloftWxStaMode(false);
            }
            else {
                TabAloftWx.rdbtnTurbMode.setSelected(false);
                TabAloftWx.rdbtnDynamicMode.setSelected(false);
                TabAloftWx.rdbtnStaticMode.setSelected(true);
                StatusMonitor.setAloftWxTurbMode(false);
                StatusMonitor.setAloftWxDynMode(false);
                StatusMonitor.setAloftWxStaMode(true);
            }
        }
        catch (Exception ex) {}
        final DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(0);
        for (int i = 0; i < culomns.length; ++i) {
            this.table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        this.table.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(final TableModelEvent evt) {
                TurbWptData.updateTableData(data);
                TurbWptData.checkTableRteMatch();
            }
        });
        TabAloftWx.turbWptData = new TurbWptData(data);
    }
    
    public static void saveTabData() {
        try {
            if (TabAloftWx.rdbtnDynamicMode.isSelected()) {
                FileSettingConfig.saveAloftWxDynMode(true);
                FileSettingConfig.saveAloftWxTurbMode(false);
            }
            else if (TabAloftWx.rdbtnTurbMode.isSelected()) {
                FileSettingConfig.saveAloftWxTurbMode(true);
                FileSettingConfig.saveAloftWxDynMode(false);
            }
            else {
                FileSettingConfig.saveAloftWxDynMode(false);
                FileSettingConfig.saveAloftWxTurbMode(false);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
