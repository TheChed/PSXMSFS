// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.recv;

import flightsim.simconnect.SimConnect;

public interface FacilitiesListHandler
{
    void handleAirportList(final SimConnect p0, final RecvAirportList p1);
    
    void handleWaypointList(final SimConnect p0, final RecvWaypointList p1);
    
    void handleVORList(final SimConnect p0, final RecvVORList p1);
    
    void handleNDBList(final SimConnect p0, final RecvNDBList p1);
}
