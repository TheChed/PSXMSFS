// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect;

public enum DataType
{
    INVALID("INVALID", 0), 
    INT32("INT32", 1), 
    INT64("INT64", 2), 
    FLOAT32("FLOAT32", 3), 
    FLOAT64("FLOAT64", 4), 
    STRING8("STRING8", 5), 
    STRING32("STRING32", 6), 
    STRING64("STRING64", 7), 
    STRING128("STRING128", 8), 
    STRING256("STRING256", 9), 
    STRING260("STRING260", 10), 
    STRINGV("STRINGV", 11), 
    INITPOSITION("INITPOSITION", 12), 
    MARKERSTATE("MARKERSTATE", 13), 
    WAYPOINT("WAYPOINT", 14), 
    LATLONALT("LATLONALT", 15), 
    XYZ("XYZ", 16), 
    MAX("MAX", 17);
    
    private DataType(final String name, final int ordinal) {
    }
    
    public int size() {
        switch (this) {
            case INT32:
            case FLOAT32: {
                return 4;
            }
            case INT64:
            case FLOAT64:
            case STRING8: {
                return 8;
            }
            case STRING32: {
                return 32;
            }
            case STRING64: {
                return 64;
            }
            case STRING128: {
                return 128;
            }
            case STRING256: {
                return 256;
            }
            case STRING260: {
                return 260;
            }
            case INITPOSITION: {
                return 56;
            }
            case LATLONALT: {
                return 24;
            }
            case WAYPOINT: {
                return 48;
            }
            case MARKERSTATE: {
                return 68;
            }
            case XYZ: {
                return 24;
            }
            case STRINGV: {
                return -1;
            }
            default: {
                return 0;
            }
        }
    }
}
