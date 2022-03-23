// 
// Decompiled by Procyon v0.5.36
// 

package gui;

import java.io.IOException;
import misc.PrinterService;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import util.StatusMonitor;
import files.FileSettingConfig;
import java.awt.Font;
import javax.swing.JCheckBox;
import java.awt.LayoutManager;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class TabPrinter
{
    private static JTextField textLeftAlign;
    private static JTextField textHeightAlign;
    private static JTextField textEmptyLines;
    private static JLabel lblDynError;
    
    static {
        TabPrinter.lblDynError = new JLabel("");
    }
    
    public TabPrinter(final JTabbedPane argTabbedPane) throws IOException {
        TextTabParser.passJLabel(this, TabPrinter.lblDynError);
        final JPanel panel_3 = new JPanel();
        argTabbedPane.addTab("Printer", null, panel_3, null);
        panel_3.setLayout(null);
        final JCheckBox chkEnable = new JCheckBox("Enable printer output");
        chkEnable.setFont(new Font("Tahoma", 1, 11));
        chkEnable.setBounds(27, 10, 170, 23);
        panel_3.add(chkEnable);
        chkEnable.setSelected(FileSettingConfig.getPrintOutputEnabled());
        StatusMonitor.setPrintOutputEnabled(FileSettingConfig.getPrintOutputEnabled());
        chkEnable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                StatusMonitor.setPrintOutputEnabled(chkEnable.isSelected());
            }
        });
        final JLabel lblStaHeightAlign = new JLabel("Left alignment :");
        lblStaHeightAlign.setFont(new Font("Tahoma", 1, 11));
        lblStaHeightAlign.setBounds(30, 75, 113, 14);
        panel_3.add(lblStaHeightAlign);
        final JLabel lblHeightAlignment = new JLabel("Height alignment : ");
        lblHeightAlignment.setFont(new Font("Tahoma", 1, 11));
        lblHeightAlignment.setBounds(30, 115, 113, 14);
        panel_3.add(lblHeightAlignment);
        (TabPrinter.textLeftAlign = new JTextField()).setBounds(154, 72, 86, 20);
        panel_3.add(TabPrinter.textLeftAlign);
        TabPrinter.textLeftAlign.setColumns(10);
        TabPrinter.textLeftAlign.setText(String.valueOf(FileSettingConfig.getPrintLeftAlign()));
        (TabPrinter.textHeightAlign = new JTextField()).setBounds(154, 112, 86, 20);
        panel_3.add(TabPrinter.textHeightAlign);
        TabPrinter.textHeightAlign.setColumns(10);
        TabPrinter.textHeightAlign.setText(String.valueOf(FileSettingConfig.getPrintHeightAlign()));
        final JLabel lblStaEmptyLines = new JLabel("Empty end lines :");
        lblStaEmptyLines.setFont(new Font("Tahoma", 1, 11));
        lblStaEmptyLines.setBounds(31, 155, 113, 14);
        panel_3.add(lblStaEmptyLines);
        (TabPrinter.textEmptyLines = new JTextField()).setBounds(154, 152, 86, 20);
        panel_3.add(TabPrinter.textEmptyLines);
        TabPrinter.textEmptyLines.setColumns(10);
        TabPrinter.textEmptyLines.setText(String.valueOf(FileSettingConfig.getPrintEndLines()));
        TabPrinter.lblDynError.setBounds(31, 219, 409, 14);
        panel_3.add(TabPrinter.lblDynError);
        PrinterService.passJTextFields(TabPrinter.textEmptyLines, TabPrinter.textLeftAlign, TabPrinter.textHeightAlign);
    }
    
    public static void saveTabData() {
        try {
            FileSettingConfig.savePrintEndLines(TabPrinter.textEmptyLines.getText());
            FileSettingConfig.savePrintLeftAlign(TabPrinter.textLeftAlign.getText());
            FileSettingConfig.savePrintHeightAlign(TabPrinter.textHeightAlign.getText());
            FileSettingConfig.savePrintOutputEnabled(StatusMonitor.getPrintOutputEnabled());
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
