// 
// Decompiled by Procyon v0.5.36
// 

package gui;

import java.io.IOException;
import network.SocketClientPSXMain;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import util.StatusMonitor;
import files.FileSettingConfig;
import traffic.TrafficRadioXpndr;
import java.awt.Font;
import javax.swing.JLabel;
import java.awt.LayoutManager;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JCheckBox;

public class TabTrafficRadioXpndr
{
    private static JCheckBox chkSendTrueTasToVATSIM;
    
    static {
        TabTrafficRadioXpndr.chkSendTrueTasToVATSIM = new JCheckBox("Send PSX TAS to the Scenery Generator");
    }
    
    public TabTrafficRadioXpndr(final JTabbedPane argTabbedPane) throws IOException {
        final JPanel panel_2 = new JPanel();
        argTabbedPane.addTab("TrafficRadioXpndr", null, panel_2, null);
        panel_2.setLayout(null);
        final JLabel lblStaAcftListTitle = new JLabel("Closest Aircraft in the 40NM vicinity :");
        lblStaAcftListTitle.setFont(new Font("Tahoma", 1, 11));
        lblStaAcftListTitle.setBounds(30, 130, 302, 14);
        panel_2.add(lblStaAcftListTitle);
        final JLabel lblDynAi1 = new JLabel("");
        lblDynAi1.setBounds(30, 160, 710, 14);
        panel_2.add(lblDynAi1);
        final JLabel lblDynAi2 = new JLabel("");
        lblDynAi2.setBounds(30, 190, 710, 14);
        panel_2.add(lblDynAi2);
        final JLabel lblDynAi3 = new JLabel("");
        lblDynAi3.setBounds(30, 220, 710, 14);
        panel_2.add(lblDynAi3);
        final JLabel lblDynAi4 = new JLabel("");
        lblDynAi4.setBounds(30, 250, 710, 14);
        panel_2.add(lblDynAi4);
        final JLabel lblDynAi5 = new JLabel("");
        lblDynAi5.setBounds(30, 280, 710, 14);
        panel_2.add(lblDynAi5);
        final JLabel lblDynAi6 = new JLabel("");
        lblDynAi6.setBounds(30, 310, 710, 14);
        panel_2.add(lblDynAi6);
        final JLabel lblDynAi7 = new JLabel("");
        lblDynAi7.setBounds(30, 340, 710, 14);
        panel_2.add(lblDynAi7);
        TrafficRadioXpndr.passJLabel(lblDynAi1, lblDynAi2, lblDynAi3, lblDynAi4, lblDynAi5, lblDynAi6, lblDynAi7);
        final JCheckBox chkEnableTcasTfc = new JCheckBox("Send Scenery Generator traffic data to PSX");
        chkEnableTcasTfc.setFont(new Font("Tahoma", 1, 11));
        chkEnableTcasTfc.setBounds(27, 21, 342, 23);
        panel_2.add(chkEnableTcasTfc);
        chkEnableTcasTfc.setSelected(FileSettingConfig.getSimSetPsxTraffic());
        StatusMonitor.setSimSetPsxTraffic(FileSettingConfig.getSimSetPsxTraffic());
        chkEnableTcasTfc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                StatusMonitor.setSimSetPsxTraffic(chkEnableTcasTfc.isSelected());
                if (chkEnableTcasTfc.isSelected()) {
                    SocketClientPSXMain.send("bang");
                }
            }
        });
        final JCheckBox chkPsxSetComXpndr = new JCheckBox("Send PSX Altimeter, XPNDR (code only) and COM Frequencies to the Scenery Generator");
        chkPsxSetComXpndr.setFont(new Font("Tahoma", 1, 11));
        chkPsxSetComXpndr.setBounds(27, 51, 540, 23);
        panel_2.add(chkPsxSetComXpndr);
        chkPsxSetComXpndr.setSelected(FileSettingConfig.getPsxSetSimComXpndrAlt());
        StatusMonitor.setPsxSetSimComXpndrAlt(FileSettingConfig.getPsxSetSimComXpndrAlt());
        chkPsxSetComXpndr.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                StatusMonitor.setPsxSetSimComXpndrAlt(chkPsxSetComXpndr.isSelected());
                if (chkPsxSetComXpndr.isSelected()) {
                    SocketClientPSXMain.send("bang");
                }
            }
        });
        TabTrafficRadioXpndr.chkSendTrueTasToVATSIM.setFont(new Font("Tahoma", 1, 11));
        TabTrafficRadioXpndr.chkSendTrueTasToVATSIM.setBounds(27, 81, 275, 23);
        panel_2.add(TabTrafficRadioXpndr.chkSendTrueTasToVATSIM);
        TabTrafficRadioXpndr.chkSendTrueTasToVATSIM.setSelected(FileSettingConfig.getSendTrueTasToVATSIM());
        StatusMonitor.setSendTrueTasToVATSIM(FileSettingConfig.getSendTrueTasToVATSIM());
        TabTrafficRadioXpndr.chkSendTrueTasToVATSIM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                StatusMonitor.setSendTrueTasToVATSIM(TabTrafficRadioXpndr.chkSendTrueTasToVATSIM.isSelected());
            }
        });
    }
    
    public static void saveTabData() {
        try {
            FileSettingConfig.saveSendTrueTasToVATSIM(TabTrafficRadioXpndr.chkSendTrueTasToVATSIM.isSelected());
            FileSettingConfig.saveSimSetPsxTraffic(StatusMonitor.getSimSetPsxTraffic());
            FileSettingConfig.savePsxSetSimComXpndrAlt(StatusMonitor.getPsxSetSimComXpndrAlt());
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
