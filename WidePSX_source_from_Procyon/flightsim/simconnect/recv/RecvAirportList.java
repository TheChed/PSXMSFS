// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.recv;

import java.nio.ByteBuffer;

public class RecvAirportList extends RecvFacilitiesList
{
    private FacilityAirport[] airports;
    
    RecvAirportList(final ByteBuffer bf) {
        super(bf, RecvID.ID_AIRPORT_LIST);
        final int size = this.getArraySize();
        this.airports = new FacilityAirport[size];
        for (int i = 0; i < size; ++i) {
            this.airports[i] = new FacilityAirport(bf);
        }
    }
    
    @Override
    public FacilityAirport[] getFacilities() {
        return this.airports;
    }
}
