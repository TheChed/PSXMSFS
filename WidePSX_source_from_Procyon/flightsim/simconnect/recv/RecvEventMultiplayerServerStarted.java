// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.recv;

import java.nio.ByteBuffer;

public class RecvEventMultiplayerServerStarted extends RecvEvent
{
    RecvEventMultiplayerServerStarted(final ByteBuffer bf) {
        super(bf, RecvID.ID_EVENT_MULTIPLAYER_SERVER_STARTED);
    }
}
