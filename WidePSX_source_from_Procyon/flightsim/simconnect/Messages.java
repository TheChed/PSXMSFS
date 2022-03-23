// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect;

import java.util.MissingResourceException;
import java.util.Locale;
import java.util.ResourceBundle;

public class Messages
{
    private static final String BUNDLE_NAME = "flightsim.simconnect.messages";
    private static final ResourceBundle RESOURCE_BUNDLE;
    private static final ResourceBundle RESOURCE_BUNDLE_DEF;
    
    static {
        RESOURCE_BUNDLE = ResourceBundle.getBundle("flightsim.simconnect.messages");
        RESOURCE_BUNDLE_DEF = ResourceBundle.getBundle("flightsim.simconnect.messages", Locale.ENGLISH);
    }
    
    private Messages() {
    }
    
    public static String get(final String key) {
        try {
            return Messages.RESOURCE_BUNDLE.getString(key);
        }
        catch (MissingResourceException e) {
            return String.valueOf('!') + key + '!';
        }
    }
    
    public static String getString(final String key) {
        try {
            return Messages.RESOURCE_BUNDLE.getString(key);
        }
        catch (MissingResourceException e) {
            return String.valueOf('!') + key + '!';
        }
    }
    
    public static String getDefault(final String key) {
        try {
            return Messages.RESOURCE_BUNDLE_DEF.getString(key);
        }
        catch (MissingResourceException e) {
            return String.valueOf('!') + key + '!';
        }
    }
}
