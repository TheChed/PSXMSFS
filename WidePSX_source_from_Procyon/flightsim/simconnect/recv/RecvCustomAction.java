// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.recv;

import java.nio.ByteBuffer;

public class RecvCustomAction extends RecvEvent
{
    private final byte[] guid;
    private final int waitForCompletion;
    private final String payLoad;
    
    RecvCustomAction(final ByteBuffer bf) {
        super(bf, RecvID.ID_CUSTOM_ACTION);
        bf.get(this.guid = new byte[16]);
        this.waitForCompletion = bf.getInt();
        this.payLoad = super.makeString(bf, bf.remaining());
    }
    
    public byte[] getGuid() {
        return this.guid;
    }
    
    public String getPayLoad() {
        return this.payLoad;
    }
    
    public int getWaitForCompletion() {
        return this.waitForCompletion;
    }
}
