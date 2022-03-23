// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.recv;

import java.nio.ByteBuffer;

public class RecvQuit extends RecvPacket
{
    RecvQuit(final ByteBuffer bf) {
        super(bf, RecvID.ID_QUIT);
    }
}
