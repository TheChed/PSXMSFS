// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.config;

public class ConfigurationNotFoundException extends Exception
{
    private final int number;
    private static final long serialVersionUID = -7402875474831801952L;
    
    ConfigurationNotFoundException(final int number) {
        this.number = number;
    }
    
    public int getNumber() {
        return this.number;
    }
}
