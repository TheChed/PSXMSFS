// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.recv;

import java.nio.ByteBuffer;

public class RecvSimObjectDataByType extends RecvSimObjectData
{
    RecvSimObjectDataByType(final ByteBuffer bf) {
        super(bf, RecvID.ID_SIMOBJECT_DATA_BYTYPE);
    }
}
