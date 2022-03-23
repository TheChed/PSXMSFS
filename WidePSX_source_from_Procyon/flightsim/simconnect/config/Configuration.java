// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.config;

import java.io.InputStream;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Hashtable;

public class Configuration extends Hashtable<String, String>
{
    private static final long serialVersionUID = 4120183786070819349L;
    public static final String ADDRESS = "Address";
    public static final String PROTOCOL = "Protocol";
    public static final String PROTOCOL_IPv4 = "IPv4";
    public static final String PROTOCOL_IPv6 = "IPv6";
    public static final String PORT = "Port";
    public static final String MAX_RECEIVE_SIZE = "MaxReceiveSize";
    public static final String DISABLE_NAGLE = "DisableNagle";
    
    public String get(final String key, final String def) {
        final String s = this.get(key.toLowerCase());
        if (s == null) {
            return def;
        }
        return s;
    }
    
    public String get(final String key) {
        return super.get(key.toLowerCase());
    }
    
    public int getInt(final String key, final int def) {
        final String s = this.get(key.toLowerCase());
        if (s == null) {
            return def;
        }
        try {
            return Integer.parseInt(s);
        }
        catch (NumberFormatException nfe) {
            return def;
        }
    }
    
    public boolean getBoolean(final String key, final boolean def) {
        final int val = this.getInt(key, def ? 1 : 0);
        return val == 1;
    }
    
    @Override
    public synchronized String put(final String key, final String value) {
        return super.put(key.toLowerCase(), value);
    }
    
    public void setAddress(final String address) {
        this.put("Address", address);
    }
    
    public void setPort(final int port) {
        this.put("Port", Integer.toString(port));
    }
    
    public void setProtocol(final int protocol) {
        if (protocol != 4 && protocol != 6) {
            throw new IllegalArgumentException("Bad protocol version (" + protocol + ")");
        }
        this.put("Protocol", (protocol == 4) ? "IPv4" : "IPv6");
    }
    
    public static int findSimConnectPortIPv4() {
        return readRegistryValue("SimConnect_Port_IPv4");
    }
    
    public static int findSimConnectPortIPv6() {
        return readRegistryValue("SimConnect_Port_IPv6");
    }
    
    public static int findSimConnectPort() {
        final int port4 = findSimConnectPortIPv4();
        if (port4 <= 0) {
            return findSimConnectPortIPv6();
        }
        return port4;
    }
    
    private static int readRegistryValue(final String key) {
        try {
            final Process p = Runtime.getRuntime().exec("reg query \"HKCU\\Software\\Microsoft\\Microsoft Games\\Flight Simulator\" /v " + key);
            final InputStream is = p.getInputStream();
            final BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                final StringTokenizer toker = new StringTokenizer(line);
                try {
                    final String regKey = toker.nextToken().trim();
                    final String regType = toker.nextToken().trim();
                    final String regValue = toker.nextToken().trim();
                    if (regKey.equalsIgnoreCase(key) && "REG_SZ".equalsIgnoreCase(regType)) {
                        return Integer.parseInt(regValue);
                    }
                    continue;
                }
                catch (NoSuchElementException ex) {}
                catch (NumberFormatException ex2) {}
            }
            is.close();
        }
        catch (IOException ex3) {}
        return -1;
    }
}
