// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.recv;

import java.nio.ByteBuffer;

public class RecvException extends RecvPacket
{
    private final SimConnectException exception;
    private int sendID;
    private int index;
    
    RecvException(final ByteBuffer bf) {
        super(bf, RecvID.ID_EXCEPTION);
        this.exception = SimConnectException.type(bf.getInt());
        this.sendID = bf.getInt();
        this.index = bf.getInt();
    }
    
    public SimConnectException getException() {
        return this.exception;
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public int getSendID() {
        return this.sendID;
    }
}
