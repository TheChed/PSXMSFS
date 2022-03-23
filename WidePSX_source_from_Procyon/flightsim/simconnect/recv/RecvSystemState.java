// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.recv;

import java.nio.ByteBuffer;

public class RecvSystemState extends RecvPacket
{
    private final int requestID;
    private final int dataInteger;
    private final float dataFloat;
    private final String dataString;
    
    RecvSystemState(final ByteBuffer bf) {
        super(bf, RecvID.ID_SYSTEM_STATE);
        this.requestID = bf.getInt();
        this.dataInteger = bf.getInt();
        this.dataFloat = bf.getFloat();
        this.dataString = this.makeString(bf, 260);
    }
    
    public float getDataFloat() {
        return this.dataFloat;
    }
    
    public int getDataInteger() {
        return this.dataInteger;
    }
    
    public String getDataString() {
        return this.dataString;
    }
    
    public int getRequestID() {
        return this.requestID;
    }
}
