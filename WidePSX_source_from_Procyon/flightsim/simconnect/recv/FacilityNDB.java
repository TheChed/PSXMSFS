// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.recv;

import java.nio.ByteBuffer;

public class FacilityNDB extends FacilityWaypoint
{
    private int frequency;
    
    FacilityNDB(final ByteBuffer bf) {
        super(bf);
        this.frequency = bf.getInt();
    }
    
    public int getFrequency() {
        return this.frequency;
    }
    
    @Override
    public String toString() {
        return String.valueOf(super.toString()) + " freq=" + this.frequency + "Hz";
    }
}
