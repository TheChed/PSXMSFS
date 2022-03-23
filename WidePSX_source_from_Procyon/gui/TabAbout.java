// 
// Decompiled by Procyon v0.5.36
// 

package gui;

import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.LayoutManager;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class TabAbout
{
    public TabAbout(final JTabbedPane argTabbedPane, final String argVersion) throws IOException {
        final JPanel panel_1 = new JPanel();
        argTabbedPane.addTab("About", null, panel_1, null);
        panel_1.setLayout(null);
        final JLabel lblLogo = new JLabel(new ImageIcon("Logo_WidePSX_app.png"));
        lblLogo.setBounds(500, 300, 305, 106);
        panel_1.add(lblLogo);
        final JLabel lblNewLabel = new JLabel("WidePSX version " + argVersion + ". Reverse engineering prohibited.");
        lblNewLabel.setBounds(30, 30, 710, 14);
        panel_1.add(lblNewLabel);
        final JLabel lblNewLabel_1 = new JLabel("This application uses a modified version of jSimConnect (LGPL), written by \"mharj\"  available at  :");
        lblNewLabel_1.setBounds(30, 70, 710, 14);
        panel_1.add(lblNewLabel_1);
        final JLabel lblNewLabel_2 = new JLabel("Thanks to the Beta Testers and Hardy HEINLIN from Aerowinx for their help and support.");
        lblNewLabel_2.setBounds(30, 130, 710, 14);
        panel_1.add(lblNewLabel_2);
        final JLabel lblNewLabel_3 = new JLabel("https://github.com/mharj/jsimconnect (see Lib folder for more details).");
        lblNewLabel_3.setBounds(30, 90, 710, 14);
        panel_1.add(lblNewLabel_3);
    }
}
