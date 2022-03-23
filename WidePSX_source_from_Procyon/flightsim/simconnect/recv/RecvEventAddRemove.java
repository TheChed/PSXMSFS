// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.recv;

import java.nio.ByteBuffer;
import flightsim.simconnect.SimObjectType;

public class RecvEventAddRemove extends RecvEvent
{
    private final SimObjectType type;
    
    RecvEventAddRemove(final ByteBuffer bf) {
        super(bf, RecvID.ID_EVENT_OBJECT_ADDREMOVE);
        this.type = SimObjectType.type(bf.getInt());
    }
    
    public SimObjectType getType() {
        return this.type;
    }
    
    @Override
    public int getData() {
        return super.getData();
    }
}
