// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.data;

import java.nio.ByteBuffer;
import java.io.Serializable;

public class LatLonAlt implements SimConnectData, Serializable, Cloneable
{
    private static final long serialVersionUID = 7598871346462898633L;
    public double latitude;
    public double longitude;
    public double altitude;
    
    public LatLonAlt(final double latitude, final double longitude, final double altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }
    
    public LatLonAlt() {
    }
    
    @Override
    public void read(final ByteBuffer buffer) {
        this.latitude = buffer.getDouble();
        this.longitude = buffer.getDouble();
        this.altitude = buffer.getDouble();
    }
    
    @Override
    public void write(final ByteBuffer buffer) {
        buffer.putDouble(this.latitude);
        buffer.putDouble(this.longitude);
        buffer.putDouble(this.altitude);
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.latitude) + ", " + this.longitude + ", " + this.altitude;
    }
    
    public LatLonAlt clone() {
        return new LatLonAlt(this.latitude, this.longitude, this.altitude);
    }
}
