// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.recv;

import java.nio.ByteBuffer;

public class FacilityAirport
{
    private String icao;
    private double latitude;
    private double longitude;
    private double altitude;
    
    FacilityAirport(final ByteBuffer bf) {
        this.icao = this.makeString(bf, 9);
        this.latitude = bf.getDouble();
        this.longitude = bf.getDouble();
        this.altitude = bf.getDouble();
    }
    
    public FacilityAirport(final String icao, final double latitude, final double longitude, final double altitude) {
        this.icao = icao;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }
    
    public double getAltitude() {
        return this.altitude;
    }
    
    public String getIcao() {
        return this.icao;
    }
    
    public double getLatitude() {
        return this.latitude;
    }
    
    public double getLongitude() {
        return this.longitude;
    }
    
    String makeString(final ByteBuffer bf, final int len) {
        final byte[] tmp = new byte[len];
        bf.get(tmp);
        int fZeroPos;
        for (fZeroPos = 0; fZeroPos < len && tmp[fZeroPos] != 0; ++fZeroPos) {}
        return new String(tmp, 0, fZeroPos);
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.icao) + " (" + this.latitude + ", " + this.longitude + ", " + this.altitude + ")";
    }
}
