// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect;

public enum ClientDataType
{
    INT8("INT8", 0, -1), 
    INT16("INT16", 1, -2), 
    INT32("INT32", 2, -3), 
    INT64("INT64", 3, -4), 
    FLOAT32("FLOAT32", 4, -5), 
    FLOAT64("FLOAT64", 5, -6);
    
    private final int value;
    
    private ClientDataType(final String name, final int ordinal, final int value) {
        this.value = value;
    }
    
    public int value() {
        return this.value;
    }
}
