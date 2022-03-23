// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect;

public enum FacilityListType
{
    AIRPORT("AIRPORT", 0), 
    WAYPOINT("WAYPOINT", 1), 
    NDB("NDB", 2), 
    VOR("VOR", 3), 
    COUNT("COUNT", 4);
    
    private FacilityListType(final String name, final int ordinal) {
    }
}
