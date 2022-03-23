// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.recv;

import java.nio.ByteBuffer;

public class RecvAssignedObjectID extends RecvPacket
{
    private final int requestID;
    private final int objectID;
    
    RecvAssignedObjectID(final ByteBuffer bf) {
        super(bf, RecvID.ID_ASSIGNED_OBJECT_ID);
        this.requestID = bf.getInt();
        this.objectID = bf.getInt();
    }
    
    public int getObjectID() {
        return this.objectID;
    }
    
    public int getRequestID() {
        return this.requestID;
    }
}
