// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.recv;

import java.nio.ByteBuffer;

public class RecvEventFrame extends RecvEvent
{
    private final float frameRate;
    private final float simSpeed;
    
    RecvEventFrame(final ByteBuffer bf) {
        super(bf, RecvID.ID_EVENT_FRAME);
        this.frameRate = bf.getFloat();
        this.simSpeed = bf.getFloat();
    }
    
    public float getFrameRate() {
        return this.frameRate;
    }
    
    public float getSimSpeed() {
        return this.simSpeed;
    }
}
