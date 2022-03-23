// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.recv;

import java.nio.ByteBuffer;

public class RecvPacket
{
    private int size;
    private int version;
    private int id;
    private RecvID recognisedID;
    private byte[] unparsed;
    
    RecvPacket(final ByteBuffer bf) {
        this.size = bf.getInt();
        this.version = bf.getInt();
        this.id = bf.getInt();
        bf.get(this.unparsed = new byte[bf.remaining()]);
    }
    
    protected RecvPacket(final ByteBuffer bf, final RecvID id) {
        this.size = bf.getInt();
        this.version = bf.getInt();
        this.id = bf.getInt();
        this.recognisedID = id;
    }
    
    protected String makeString(final ByteBuffer bf, final int len) {
        final byte[] tmp = new byte[len];
        bf.get(tmp);
        int fZeroPos;
        for (fZeroPos = 0; fZeroPos < len && tmp[fZeroPos] != 0; ++fZeroPos) {}
        return new String(tmp, 0, fZeroPos);
    }
    
    public int getSize() {
        return this.size;
    }
    
    public RecvID getID() {
        return this.recognisedID;
    }
    
    public int getRawID() {
        return this.id;
    }
    
    public RecvID getRecognisedID() {
        return this.recognisedID;
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public byte[] getUnparsedData() {
        return this.unparsed;
    }
}
