// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.recv;

import flightsim.simconnect.SimConnect;

public interface EventHandler
{
    void handleEvent(final SimConnect p0, final RecvEvent p1);
}
