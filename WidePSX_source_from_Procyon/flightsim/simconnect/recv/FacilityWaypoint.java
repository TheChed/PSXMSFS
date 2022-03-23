// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.recv;

import java.nio.ByteBuffer;

public class FacilityWaypoint extends FacilityAirport
{
    private float magVar;
    
    FacilityWaypoint(final ByteBuffer bf) {
        super(bf);
        this.magVar = bf.getFloat();
    }
    
    public FacilityWaypoint(final String icao, final double latitude, final double longitude, final double altitude, final float magVar) {
        super(icao, latitude, longitude, altitude);
        this.magVar = magVar;
    }
    
    public float getMagVar() {
        return this.magVar;
    }
    
    @Override
    public String toString() {
        return String.valueOf(super.toString()) + " magvar=" + this.magVar;
    }
}
