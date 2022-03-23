// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.recv;

import flightsim.simconnect.Messages;

public enum SimConnectException
{
    NONE("NONE", 0), 
    ERROR("ERROR", 1), 
    SIZE_MISMATCH("SIZE_MISMATCH", 2), 
    UNRECOGNIZED_ID("UNRECOGNIZED_ID", 3), 
    UNOPENED("UNOPENED", 4), 
    VERSION_MISMATCH("VERSION_MISMATCH", 5), 
    TOO_MANY_GROUPS("TOO_MANY_GROUPS", 6), 
    NAME_UNRECOGNIZED("NAME_UNRECOGNIZED", 7), 
    TOO_MANY_EVENT_NAMES("TOO_MANY_EVENT_NAMES", 8), 
    EVENT_ID_DUPLICATE("EVENT_ID_DUPLICATE", 9), 
    TOO_MANY_MAPS("TOO_MANY_MAPS", 10), 
    TOO_MANY_OBJECTS("TOO_MANY_OBJECTS", 11), 
    TOO_MANY_REQUESTS("TOO_MANY_REQUESTS", 12), 
    WEATHER_INVALID_PORT("WEATHER_INVALID_PORT", 13), 
    WEATHER_INVALID_METAR("WEATHER_INVALID_METAR", 14), 
    WEATHER_UNABLE_TO_GET_OBSERVATION("WEATHER_UNABLE_TO_GET_OBSERVATION", 15), 
    WEATHER_UNABLE_TO_CREATE_STATION("WEATHER_UNABLE_TO_CREATE_STATION", 16), 
    WEATHER_UNABLE_TO_REMOVE_STATION("WEATHER_UNABLE_TO_REMOVE_STATION", 17), 
    INVALID_DATA_TYPE("INVALID_DATA_TYPE", 18), 
    INVALID_DATA_SIZE("INVALID_DATA_SIZE", 19), 
    DATA_ERROR("DATA_ERROR", 20), 
    INVALID_ARRAY("INVALID_ARRAY", 21), 
    CREATE_OBJECT_FAILED("CREATE_OBJECT_FAILED", 22), 
    LOAD_FLIGHTPLAN_FAILED("LOAD_FLIGHTPLAN_FAILED", 23), 
    OPERATION_INVALID_FOR_OJBECT_TYPE("OPERATION_INVALID_FOR_OJBECT_TYPE", 24), 
    ILLEGAL_OPERATION("ILLEGAL_OPERATION", 25), 
    ALREADY_SUBSCRIBED("ALREADY_SUBSCRIBED", 26), 
    INVALID_ENUM("INVALID_ENUM", 27), 
    DEFINITION_ERROR("DEFINITION_ERROR", 28), 
    DUPLICATE_ID("DUPLICATE_ID", 29), 
    DATUM_ID("DATUM_ID", 30), 
    OUT_OF_BOUNDS("OUT_OF_BOUNDS", 31), 
    ALREADY_CREATED("ALREADY_CREATED", 32), 
    OBJECT_OUTSIDE_REALITY_BUBBLE("OBJECT_OUTSIDE_REALITY_BUBBLE", 33), 
    OBJECT_CONTAINER("OBJECT_CONTAINER", 34), 
    OBJECT_AI("OBJECT_AI", 35), 
    OBJECT_ATC("OBJECT_ATC", 36), 
    OBJECT_SCHEDULE("OBJECT_SCHEDULE", 37);
    
    private SimConnectException(final String name, final int ordinal) {
    }
    
    public static final SimConnectException type(final int i) {
        final SimConnectException[] values = values();
        if (i > values.length || i < 0) {
            return SimConnectException.NONE;
        }
        return values[i];
    }
    
    public String getLocalisedMessage() {
        return Messages.get("Simconnect_exception_" + this.ordinal());
    }
    
    public String getMessage() {
        return Messages.getDefault("Simconnect_exception_" + this.ordinal());
    }
}
