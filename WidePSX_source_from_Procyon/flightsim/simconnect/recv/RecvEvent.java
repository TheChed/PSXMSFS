// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.recv;

import java.nio.ByteBuffer;

public class RecvEvent extends RecvPacket
{
    private final int groupID;
    private final int eventID;
    private final int data;
    
    RecvEvent(final ByteBuffer bf, final RecvID id) {
        super(bf, id);
        this.groupID = bf.getInt();
        this.eventID = bf.getInt();
        this.data = bf.getInt();
    }
    
    RecvEvent(final ByteBuffer bf) {
        this(bf, RecvID.ID_EVENT);
    }
    
    public int getData() {
        return this.data;
    }
    
    public int getEventID() {
        return this.eventID;
    }
    
    public int getGroupID() {
        return this.groupID;
    }
}
