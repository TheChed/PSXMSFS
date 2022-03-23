// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.recv;

import java.nio.ByteBuffer;

public class RecvCloudState extends RecvPacket
{
    private final int requestID;
    private final int arraySize;
    private final byte[][] data;
    
    RecvCloudState(final ByteBuffer bf) {
        super(bf, RecvID.ID_CLOUD_STATE);
        this.requestID = bf.getInt();
        this.arraySize = bf.getInt();
        this.data = new byte[64][64];
        for (int i = 0; i < 64; ++i) {
            if (bf.remaining() >= 64) {
                bf.get(this.data[i]);
            }
        }
    }
    
    public int getArraySize() {
        return this.arraySize;
    }
    
    public byte[][] getData() {
        return this.data;
    }
    
    public int getRequestID() {
        return this.requestID;
    }
}
