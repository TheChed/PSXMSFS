// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.recv;

import java.nio.ByteBuffer;

public class RecvNDBList extends RecvFacilitiesList
{
    private FacilityNDB[] ndbs;
    
    RecvNDBList(final ByteBuffer bf) {
        super(bf, RecvID.ID_NDB_LIST);
        final int size = this.getArraySize();
        this.ndbs = new FacilityNDB[size];
        for (int i = 0; i < size; ++i) {
            this.ndbs[i] = new FacilityNDB(bf);
        }
    }
    
    @Override
    public FacilityNDB[] getFacilities() {
        return this.ndbs;
    }
}
