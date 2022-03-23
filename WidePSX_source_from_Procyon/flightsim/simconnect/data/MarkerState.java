// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.data;

import java.nio.ByteBuffer;
import java.io.Serializable;

public class MarkerState implements SimConnectData, Serializable
{
    private static final long serialVersionUID = -9147497378014027101L;
    public String markerName;
    public boolean markerState;
    
    @Override
    public void read(final ByteBuffer buffer) {
        final byte[] tmp = new byte[64];
        buffer.get(tmp);
        int fZeroPos;
        for (fZeroPos = 0; fZeroPos < 64 && tmp[fZeroPos] != 0; ++fZeroPos) {}
        this.markerName = new String(tmp, 0, fZeroPos);
        final int i = buffer.getInt();
        this.markerState = (i != 0);
    }
    
    @Override
    public void write(final ByteBuffer buffer) {
        this.putString(buffer, this.markerName, 64);
        buffer.putInt(this.markerState ? 1 : 0);
    }
    
    private void putString(final ByteBuffer bf, String s, final int fixed) {
        if (s == null) {
            s = "";
        }
        final byte[] b = s.getBytes();
        bf.put(b, 0, Math.min(b.length, fixed));
        if (b.length < fixed) {
            for (int i = 0; i < fixed - b.length; ++i) {
                bf.put((byte)0);
            }
        }
    }
}
