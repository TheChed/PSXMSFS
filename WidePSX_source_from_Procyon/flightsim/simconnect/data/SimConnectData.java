// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.data;

import java.nio.ByteBuffer;

public interface SimConnectData
{
    void write(final ByteBuffer p0);
    
    void read(final ByteBuffer p0);
}
