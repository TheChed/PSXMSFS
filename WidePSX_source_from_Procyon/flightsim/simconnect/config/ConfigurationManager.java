// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.config;

import java.util.regex.Matcher;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.filechooser.FileSystemView;
import java.io.IOException;
import java.io.File;
import java.util.regex.Pattern;
import java.util.Vector;

public class ConfigurationManager
{
    private static Vector<Configuration> configs;
    private static boolean inited;
    private static final Pattern HDR_PATTERN;
    private static final Pattern LINE_PATTERN;
    
    static {
        ConfigurationManager.configs = new Vector<Configuration>();
        ConfigurationManager.inited = false;
        HDR_PATTERN = Pattern.compile("^\\s*\\[SimConnect(.(\\d+))?\\].*", 2);
        LINE_PATTERN = Pattern.compile("^\\s*([^#]*)=([^#]*).*", 2);
    }
    
    private static void readConfiguration() {
        if (ConfigurationManager.inited) {
            return;
        }
        File f = new File("SimConnect.cfg");
        if (f.exists()) {
            try {
                parse(f);
                ConfigurationManager.inited = true;
                return;
            }
            catch (IOException ex) {}
        }
        f = new File(System.getProperty("user.home"), "SimConnect.cfg");
        if (f.exists()) {
            try {
                parse(f);
                ConfigurationManager.inited = true;
                return;
            }
            catch (IOException ex2) {}
        }
        f = new File(FileSystemView.getFileSystemView().getDefaultDirectory(), "SimConnect.cfg");
        if (f.exists()) {
            try {
                parse(f);
                ConfigurationManager.inited = true;
            }
            catch (IOException ex3) {}
        }
    }
    
    private static void parse(final File f) throws IOException {
        final FileReader fr = new FileReader(f);
        final BufferedReader br = new BufferedReader(fr);
        Configuration current = null;
        String line;
        while ((line = br.readLine()) != null) {
            Matcher m = ConfigurationManager.HDR_PATTERN.matcher(line);
            if (m.matches()) {
                current = new Configuration();
                try {
                    int cfgNo = 0;
                    if (m.group(2) != null) {
                        cfgNo = Integer.parseInt(m.group(2));
                    }
                    if (ConfigurationManager.configs.size() - 1 < cfgNo) {
                        ConfigurationManager.configs.setSize(cfgNo + 1);
                    }
                    ConfigurationManager.configs.set(cfgNo, current);
                }
                catch (NumberFormatException ex) {}
            }
            m = ConfigurationManager.LINE_PATTERN.matcher(line);
            if (m.matches() && current != null) {
                final String key = m.group(1).trim().toLowerCase();
                final String val = m.group(2).trim();
                current.put(key, val);
            }
        }
        fr.close();
    }
    
    public static Configuration getConfiguration(final int number) throws ConfigurationNotFoundException {
        if (!ConfigurationManager.inited) {
            readConfiguration();
        }
        if (number > ConfigurationManager.configs.size()) {
            throw new ConfigurationNotFoundException(number);
        }
        final Configuration cfg = ConfigurationManager.configs.get(number);
        if (cfg == null) {
            throw new ConfigurationNotFoundException(number);
        }
        return cfg;
    }
}
