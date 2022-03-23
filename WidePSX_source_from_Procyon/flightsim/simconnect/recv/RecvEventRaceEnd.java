// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.recv;

import java.nio.ByteBuffer;
import flightsim.simconnect.wrappers.GUID;

public class RecvEventRaceEnd extends RecvEvent
{
    private int racerNumber;
    private int numberRacers;
    private GUID missionGUID;
    private String playerName;
    private String sessionType;
    private String aircraft;
    private String playerRole;
    private double totalTime;
    private double penaltyTime;
    private boolean disqualified;
    
    RecvEventRaceEnd(final ByteBuffer bf) {
        super(bf, RecvID.ID_EVENT_RACE_END);
        this.racerNumber = bf.getInt();
        this.numberRacers = bf.getInt();
        (this.missionGUID = new GUID()).read(bf);
        this.playerName = this.makeString(bf, 260);
        this.sessionType = this.makeString(bf, 260);
        this.aircraft = this.makeString(bf, 260);
        this.playerRole = this.makeString(bf, 260);
        this.totalTime = bf.getDouble();
        this.penaltyTime = bf.getDouble();
        this.disqualified = (bf.getInt() == 1);
    }
    
    public String getAircraft() {
        return this.aircraft;
    }
    
    public boolean isDisqualified() {
        return this.disqualified;
    }
    
    public GUID getMissionGUID() {
        return this.missionGUID;
    }
    
    public int getNumberRacers() {
        return this.numberRacers;
    }
    
    public double getPenaltyTime() {
        return this.penaltyTime;
    }
    
    public String getPlayerName() {
        return this.playerName;
    }
    
    public String getPlayerRole() {
        return this.playerRole;
    }
    
    public int getRacerNumber() {
        return this.racerNumber;
    }
    
    public String getSessionType() {
        return this.sessionType;
    }
    
    public double getTotalTime() {
        return this.totalTime;
    }
}
