// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.recv;

import java.nio.ByteBuffer;

public class RecvReservedKey extends RecvPacket
{
    private final String choiceReserved;
    private final String reservedKey;
    
    RecvReservedKey(final ByteBuffer bf) {
        super(bf, RecvID.ID_RESERVED_KEY);
        this.choiceReserved = super.makeString(bf, 50);
        this.reservedKey = super.makeString(bf, 30);
    }
    
    public String getChoiceReserved() {
        return this.choiceReserved;
    }
    
    public String getReservedKey() {
        return this.reservedKey;
    }
}
