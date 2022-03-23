// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.recv;

import java.nio.ByteBuffer;

public class RecvVORList extends RecvFacilitiesList
{
    private FacilityVOR[] vors;
    
    RecvVORList(final ByteBuffer bf) {
        super(bf, RecvID.ID_VOR_LIST);
        final int size = this.getArraySize();
        this.vors = new FacilityVOR[size];
        for (int i = 0; i < size; ++i) {
            this.vors[i] = new FacilityVOR(bf);
        }
    }
    
    @Override
    public FacilityVOR[] getFacilities() {
        return this.vors;
    }
}
