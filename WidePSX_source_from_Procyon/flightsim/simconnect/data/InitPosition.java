// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.data;

import java.nio.ByteBuffer;
import java.io.Serializable;

public class InitPosition implements SimConnectData, Serializable
{
    private static final long serialVersionUID = -1336171966431611602L;
    public double latitude;
    public double longitude;
    public double altitude;
    public double pitch;
    public double bank;
    public double heading;
    public boolean onGround;
    public int airspeed;
    
    public InitPosition() {
    }
    
    public InitPosition(final double latitude, final double longitude, final double altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }
    
    public InitPosition(final double latitude, final double longitude, final double altitude, final double pitch, final double bank, final double heading, final boolean onGround, final int airspeed) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.pitch = pitch;
        this.bank = bank;
        this.heading = heading;
        this.onGround = onGround;
        this.airspeed = airspeed;
    }
    
    @Override
    public void read(final ByteBuffer buffer) {
        this.latitude = buffer.getDouble();
        this.longitude = buffer.getDouble();
        this.altitude = buffer.getDouble();
        this.pitch = buffer.getDouble();
        this.bank = buffer.getDouble();
        this.heading = buffer.getDouble();
        final int tmp = buffer.getInt();
        this.onGround = (tmp == 1);
        this.airspeed = buffer.getInt();
    }
    
    @Override
    public void write(final ByteBuffer buffer) {
        buffer.putDouble(this.latitude);
        buffer.putDouble(this.longitude);
        buffer.putDouble(this.altitude);
        buffer.putDouble(this.pitch);
        buffer.putDouble(this.bank);
        buffer.putDouble(this.heading);
        buffer.putInt(this.onGround ? 1 : 0);
        buffer.putInt(this.airspeed);
    }
    
    public void setLatLonAlt(final LatLonAlt lla) {
        this.latitude = lla.latitude;
        this.longitude = lla.longitude;
        this.altitude = lla.altitude / 0.3048;
    }
    
    public void setLatLonAlt(final XYZ xyz, final int latIndex, final int lonIndex, final int altIndex) {
        if (latIndex < 0 || latIndex > 2 || lonIndex < 0 || lonIndex > 2 || altIndex < 0 || altIndex > 2) {
            throw new IllegalArgumentException("Indices out of bound");
        }
        this.latitude = xyz.get(latIndex);
        this.longitude = xyz.get(lonIndex);
        this.altitude = xyz.get(altIndex) / 0.3048;
    }
    
    public void setLatLonAlt(final XYZ xyz) {
        this.setLatLonAlt(xyz, 0, 1, 2);
    }
    
    public void setPitchBankHeading(final XYZ xyz, final int pitchIndex, final int bankIndex, final int headingIndex) {
        if (pitchIndex < 0 || pitchIndex > 2 || bankIndex < 0 || bankIndex > 2 || headingIndex < 0 || headingIndex > 2) {
            throw new IllegalArgumentException("Indices out of bound");
        }
        this.pitch = xyz.get(pitchIndex);
        this.bank = xyz.get(bankIndex);
        this.heading = xyz.get(headingIndex);
    }
    
    public void setPitchBankHeading(final XYZ xyz) {
        this.setPitchBankHeading(xyz, 0, 1, 2);
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.latitude) + ", " + this.longitude + ", " + this.altitude;
    }
}
