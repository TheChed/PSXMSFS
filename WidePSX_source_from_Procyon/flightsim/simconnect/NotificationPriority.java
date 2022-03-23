// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect;

public enum NotificationPriority
{
    HIGHEST("HIGHEST", 0, 1), 
    HIGHEST_MASKABLE("HIGHEST_MASKABLE", 1, 10000000), 
    STANDARD("STANDARD", 2, 1900000000), 
    DEFAULT("DEFAULT", 3, 2000000000), 
    LOWEST("LOWEST", 4, -294967296);
    
    private final int value;
    
    private NotificationPriority(final String name, final int ordinal, final int value) {
        this.value = value;
    }
    
    public int value() {
        return this.value;
    }
}
