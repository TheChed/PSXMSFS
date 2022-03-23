// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.data;

import java.nio.ByteBuffer;
import flightsim.simconnect.SimConnectConstants;
import java.io.Serializable;

public class Waypoint implements SimConnectData, Serializable, SimConnectConstants
{
    private static final long serialVersionUID = 6165789235638978423L;
    public double latitude;
    public double longitude;
    public double altitude;
    public int flags;
    public double speed;
    public double throttle;
    
    public Waypoint(final double latitude, final double longitude, final double altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }
    
    public Waypoint(final double latitude, final double longitude, final double altitude, final int flags, final double speed, final double throttle) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.flags = flags;
        this.speed = speed;
        this.throttle = throttle;
    }
    
    public Waypoint() {
    }
    
    @Override
    public void read(final ByteBuffer buffer) {
        this.latitude = buffer.getDouble();
        this.longitude = buffer.getDouble();
        this.altitude = buffer.getDouble();
        this.flags = buffer.getInt();
        this.speed = buffer.getDouble();
        this.throttle = buffer.getDouble();
    }
    
    @Override
    public void write(final ByteBuffer buffer) {
        buffer.putDouble(this.latitude);
        buffer.putDouble(this.longitude);
        buffer.putDouble(this.altitude);
        buffer.putInt(this.flags);
        buffer.putDouble(this.speed);
        buffer.putDouble(this.throttle);
    }
    
    public void setLatLonAlt(final LatLonAlt lla) {
        this.latitude = lla.latitude;
        this.longitude = lla.longitude;
        this.altitude = lla.altitude / 0.3048;
    }
}
