import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import gui.TabAbout;
import gui.TabPrinter;
import gui.TabTrafficRadioXpndr;
import gui.TabGndService;
import gui.TabAloftWx;
import gui.TabSceneryGen;
import gui.TabNetwork;
import java.awt.BorderLayout;
import gui.TextTabParser;
import java.awt.Component;
import javax.swing.JTabbedPane;
import java.awt.LayoutManager;
import files.FileSettingConfig;
import java.awt.EventQueue;
import java.io.IOException;
import javax.swing.JFrame;

// 
// Decompiled by Procyon v0.5.36
// 

public class WidePSX
{
    private static final String version = "2.5.8";
    private static String windowTitle;
    private static JFrame frame;
    
    static {
        WidePSX.windowTitle = new String("WidePSX version 2.5.8");
        WidePSX.frame = new JFrame();
    }
    
    public WidePSX() throws IOException {
        this.initialize();
    }
    
    public static void main(final String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    final WidePSX mainWindow = new WidePSX();
                    WidePSX.frame.setVisible(true);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    private void initialize() throws IOException {
        final String OS = System.getProperty("os.name").toLowerCase();
        int w;
        int h;
        if (OS.indexOf("mac") >= 0) {
            w = 840;
            h = 490;
        }
        else {
            w = 810;
            h = 475;
        }
        try {
            final int X = (int)Double.parseDouble(FileSettingConfig.getWindowPosX());
            final int Y = (int)Double.parseDouble(FileSettingConfig.getWindowPosY());
            WidePSX.frame.setBounds(X, Y, w, h);
        }
        catch (Exception e) {
            WidePSX.frame.setBounds(100, 100, w, h);
        }
        WidePSX.frame.setDefaultCloseOperation(3);
        WidePSX.frame.getContentPane().setLayout(null);
        WidePSX.frame.setTitle(WidePSX.windowTitle);
        final JTabbedPane tabbedPane = new JTabbedPane(1);
        WidePSX.frame.getContentPane().add(tabbedPane);
        final TextTabParser textTabParser = new TextTabParser(tabbedPane);
        final Thread textTabParserThread = new Thread(textTabParser);
        textTabParserThread.start();
        WidePSX.frame.getContentPane().setLayout(new BorderLayout());
        WidePSX.frame.getContentPane().add(tabbedPane, "Center");
        final TabNetwork tabNetwork = new TabNetwork(tabbedPane);
        final TabSceneryGen tabSceneryGen = new TabSceneryGen(tabbedPane);
        final TabAloftWx tabAloftWx = new TabAloftWx(tabbedPane);
        final TabGndService tabGndService = new TabGndService(tabbedPane);
        final TabTrafficRadioXpndr tabTcasTfc = new TabTrafficRadioXpndr(tabbedPane);
        final TabPrinter tabPrinter = new TabPrinter(tabbedPane);
        final TabAbout tabAbout = new TabAbout(tabbedPane, "2.5.8");
        tabbedPane.addChangeListener(new ChangeListener() {
            boolean firstLoad = true;
            int leavingTab = 0;
            
            @Override
            public void stateChanged(final ChangeEvent e) {
                if (!this.firstLoad) {
                    if (this.leavingTab == 0) {
                        TabNetwork.saveTabData();
                    }
                    else if (this.leavingTab == 4) {
                        TabTrafficRadioXpndr.saveTabData();
                    }
                    else if (this.leavingTab == 5) {
                        TabPrinter.saveTabData();
                    }
                    else if (this.leavingTab == 3) {
                        TabGndService.saveTabData();
                    }
                    else if (this.leavingTab == 1) {
                        TabSceneryGen.saveTabData();
                    }
                    this.leavingTab = tabbedPane.getSelectedIndex();
                }
                this.firstLoad = false;
            }
        });
        WidePSX.frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent windowEvent) {
                TabNetwork.saveTabData();
                TabAloftWx.saveTabData();
                TabTrafficRadioXpndr.saveTabData();
                TabPrinter.saveTabData();
                TabGndService.saveTabData();
                TabSceneryGen.saveTabData();
                try {
                    FileSettingConfig.saveWindowPosX(Double.toString(WidePSX.frame.getLocation().getX()));
                    FileSettingConfig.saveWindowPosY(Double.toString(WidePSX.frame.getLocation().getY()));
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
