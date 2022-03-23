// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.recv;

import java.nio.ByteBuffer;

public class RecvEventFilename extends RecvEvent
{
    private final String fileName;
    private final int flags;
    
    RecvEventFilename(final ByteBuffer bf) {
        super(bf, RecvID.ID_EVENT_FILENAME);
        this.fileName = super.makeString(bf, 260);
        this.flags = bf.getInt();
    }
    
    public String getFileName() {
        return this.fileName;
    }
    
    public int getFlags() {
        return this.flags;
    }
}
