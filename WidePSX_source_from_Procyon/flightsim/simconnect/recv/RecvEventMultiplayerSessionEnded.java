// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.recv;

import java.nio.ByteBuffer;

public class RecvEventMultiplayerSessionEnded extends RecvEvent
{
    RecvEventMultiplayerSessionEnded(final ByteBuffer bf) {
        super(bf, RecvID.ID_EVENT_MULTIPLAYER_SESSION_ENDED);
    }
}
