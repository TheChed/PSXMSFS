// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.recv;

import java.nio.ByteBuffer;

public class RecvWaypointList extends RecvFacilitiesList
{
    private FacilityWaypoint[] waypoints;
    
    RecvWaypointList(final ByteBuffer bf) {
        super(bf, RecvID.ID_WAYPOINT_LIST);
        final int size = this.getArraySize();
        this.waypoints = new FacilityWaypoint[size];
        for (int i = 0; i < size; ++i) {
            this.waypoints[i] = new FacilityWaypoint(bf);
        }
    }
    
    @Override
    public FacilityWaypoint[] getFacilities() {
        return this.waypoints;
    }
}
