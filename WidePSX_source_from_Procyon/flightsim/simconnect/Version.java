// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect;

import java.awt.Component;
import javax.swing.JOptionPane;
import java.io.InputStream;
import java.util.Properties;

public class Version
{
    public static final String VERSION_STRING = "0.7";
    public static final int VERSION_MAJOR = 0;
    public static final int VERSION_MINOR = 7;
    public static final int PATCHLEVEL = 0;
    
    public static int buildNumber() {
        final InputStream is = Version.class.getResourceAsStream("/jsimconnect.build.number");
        final Properties props = new Properties();
        try {
            props.load(is);
            final String val = props.getProperty("build.number", "-1");
            return Integer.parseInt(val);
        }
        catch (Exception ex) {
            return -1;
        }
    }
    
    public static void main(final String[] args) {
        final int bn = buildNumber();
        final String ver = "0.7 (0) build " + bn + " proto " + 4;
        System.out.println("JSimConnect " + ver);
        JOptionPane.showMessageDialog(null, "JSimConnect " + ver, "JSimConnect", 1);
    }
}
