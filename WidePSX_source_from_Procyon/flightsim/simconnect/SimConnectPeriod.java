// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect;

public enum SimConnectPeriod
{
    NEVER("NEVER", 0), 
    ONCE("ONCE", 1), 
    VISUAL_FRAME("VISUAL_FRAME", 2), 
    SIM_FRAME("SIM_FRAME", 3), 
    SECOND("SECOND", 4);
    
    private SimConnectPeriod(final String name, final int ordinal) {
    }
}
