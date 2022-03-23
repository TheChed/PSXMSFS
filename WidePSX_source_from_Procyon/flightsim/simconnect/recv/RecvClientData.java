// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.recv;

import java.nio.ByteBuffer;

public class RecvClientData extends RecvSimObjectData
{
    RecvClientData(final ByteBuffer bf) {
        super(bf, RecvID.ID_CLIENT_DATA);
    }
}
