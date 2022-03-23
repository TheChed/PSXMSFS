// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect;

public enum SimObjectType
{
    USER("USER", 0), 
    ALL("ALL", 1), 
    AIRCRAFT("AIRCRAFT", 2), 
    HELICOPTER("HELICOPTER", 3), 
    BOAT("BOAT", 4), 
    GROUND("GROUND", 5), 
    INVALID("INVALID", 6);
    
    private static final String[] NAMES;
    
    static {
        NAMES = new String[] { "User", "All", "Airplane", "Helicopter", "Boat", "GroundVehicle" };
    }
    
    private SimObjectType(final String name, final int ordinal) {
    }
    
    public static final SimObjectType type(final int i) {
        final SimObjectType[] values = values();
        if (i > values.length || i < 0) {
            return SimObjectType.ALL;
        }
        return values[i];
    }
    
    public static final SimObjectType type(final String s) {
        final SimObjectType[] values = values();
        for (int i = 0; i < SimObjectType.NAMES.length; ++i) {
            if (SimObjectType.NAMES[i].equals(s)) {
                return values[i];
            }
        }
        return SimObjectType.INVALID;
    }
    
    @Override
    public String toString() {
        return SimObjectType.NAMES[this.ordinal()];
    }
}
