// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect;

public enum ClientDataPeriod
{
    NEVER("NEVER", 0), 
    ONCE("ONCE", 1), 
    VISUAL_FRAME("VISUAL_FRAME", 2), 
    ON_SET("ON_SET", 3), 
    SECOND("SECOND", 4);
    
    private ClientDataPeriod(final String name, final int ordinal) {
    }
}
