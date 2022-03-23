// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.recv;

import java.nio.ByteBuffer;

public class RecvWeatherObservation extends RecvPacket
{
    private final int requestID;
    private final String metar;
    
    RecvWeatherObservation(final ByteBuffer bf) {
        super(bf, RecvID.ID_WEATHER_OBSERVATION);
        this.requestID = bf.getInt();
        this.metar = super.makeString(bf, bf.remaining());
    }
    
    public int getRequestID() {
        return this.requestID;
    }
    
    public String getMetar() {
        return this.metar;
    }
}
