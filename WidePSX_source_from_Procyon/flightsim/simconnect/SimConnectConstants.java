// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect;

public interface SimConnectConstants
{
    public static final int DATA_REQUEST_FLAG_DEFAULT = 0;
    public static final int DATA_REQUEST_FLAG_CHANGED = 1;
    public static final int DATA_REQUEST_FLAG_TAGGED = 2;
    public static final int DATA_SET_FLAG_DEFAULT = 0;
    public static final int DATA_SET_FLAG_TAGGED = 1;
    public static final int CLIENT_DATA_REQUEST_FLAG_DEFAULT = 0;
    public static final int CLIENT_DATA_REQUEST_FLAG_CHANGED = 1;
    public static final int CLIENT_DATA_REQUEST_FLAG_TAGGED = 2;
    public static final int CLIENT_DATA_SET_FLAG_DEFAULT = 0;
    public static final int CLIENT_DATA_SET_FLAG_TAGGED = 1;
    public static final int OBJECT_ID_USER = 0;
    public static final int UNUSED = -1;
    public static final int EVENT_FLAG_DEFAULT = 0;
    public static final int EVENT_FLAG_FAST_REPEAT_TIMER = 1;
    public static final int EVENT_FLAG_SLOW_REPEAT_TIMER = 2;
    public static final int EVENT_FLAG_GROUPID_IS_PRIORITY = 16;
    public static final int PROTO_VERSION = 4;
    public static final int RECEIVE_SIZE = 65536;
    public static final float CAMERA_IGNORE_FIELD = Float.MAX_VALUE;
    public static final int MAX_METAR_LENGTH = 2000;
    public static final float MAX_THERMAL_SIZE = 100000.0f;
    public static final float MAX_THERMAL_RATE = 1000.0f;
    public static final int INITPOSITION_AIRSPEED_CRUISE = -1;
    public static final int INITPOSITION_AIRSPEED_KEEP = -2;
    public static final int MAX_PATH = 260;
    public static final int WAYPOINT_SPEED_REQUESTED = 4;
    public static final int WAYPOINT_THROTTLE_REQUESTED = 8;
    public static final int WAYPOINT_COMPUTE_VERTICAL_SPEED = 16;
    public static final int WAYPOINT_ALTITUDE_IS_AGL = 32;
    public static final int WAYPOINT_ON_GROUND = 1048576;
    public static final int WAYPOINT_REVERSE = 2097152;
    public static final int WAYPOINT_WRAP_TO_FIRST = 4194304;
    public static final int MISSION_FAILED = 0;
    public static final int MISSION_CRASHED = 1;
    public static final int MISSION_SUCCEEDED = 2;
    public static final int VIEW_SYSTEM_EVENT_DATA_COCKPIT_2D = 1;
    public static final int VIEW_SYSTEM_EVENT_DATA_COCKPIT_VIRTUAL = 2;
    public static final int VIEW_SYSTEM_EVENT_DATA_ORTHOGONAL = 4;
    public static final int SOUND_SYSTEM_EVENT_DATA_MASTER = 1;
    public static final int UNKNOWN_GROUP = -1;
    public static final int CLIENTDATAOFFSET_AUTO = -1;
    public static final int TEXT_RESULT_MENU_SELECT_1 = 0;
    public static final int TEXT_RESULT_MENU_SELECT_2 = 1;
    public static final int TEXT_RESULT_MENU_SELECT_3 = 2;
    public static final int TEXT_RESULT_MENU_SELECT_4 = 3;
    public static final int TEXT_RESULT_MENU_SELECT_5 = 4;
    public static final int TEXT_RESULT_MENU_SELECT_6 = 5;
    public static final int TEXT_RESULT_MENU_SELECT_7 = 6;
    public static final int TEXT_RESULT_MENU_SELECT_8 = 7;
    public static final int TEXT_RESULT_MENU_SELECT_9 = 8;
    public static final int TEXT_RESULT_MENU_SELECT_10 = 9;
    public static final int TEXT_RESULT_DISPLAYED = 65536;
    public static final int TEXT_RESULT_QUEUED = 65537;
    public static final int TEXT_RESULT_REMOVED = 65538;
    public static final int TEXT_RESULT_REPLACED = 65539;
    public static final int TEXT_RESULT_TIMEOUT = 65540;
    public static final int CLIENT_DATA_TYPE_INT8 = -1;
    public static final int CLIENT_DATA_TYPE_INT16 = -2;
    public static final int CLIENT_DATA_TYPE_INT32 = -3;
    public static final int CLIENT_DATA_TYPE_INT64 = -4;
    public static final int CLIENT_DATA_TYPE_FLOAT32 = -5;
    public static final int CLIENT_DATA_TYPE_FLOAT64 = -6;
}
