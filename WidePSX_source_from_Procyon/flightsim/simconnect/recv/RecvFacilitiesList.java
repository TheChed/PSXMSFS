// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.recv;

import java.nio.ByteBuffer;

public abstract class RecvFacilitiesList extends RecvPacket
{
    private final int requestID;
    private final int arraySize;
    private final int entryNumber;
    private final int outOf;
    
    RecvFacilitiesList(final ByteBuffer bf, final RecvID id) {
        super(bf, id);
        this.requestID = bf.getInt();
        this.arraySize = bf.getInt();
        this.entryNumber = bf.getInt();
        this.outOf = bf.getInt();
    }
    
    public abstract FacilityAirport[] getFacilities();
    
    public int getArraySize() {
        return this.arraySize;
    }
    
    public int getEntryNumber() {
        return this.entryNumber;
    }
    
    public int getOutOf() {
        return this.outOf;
    }
    
    public int getRequestID() {
        return this.requestID;
    }
}
