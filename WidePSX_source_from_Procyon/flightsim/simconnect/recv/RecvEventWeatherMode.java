// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.recv;

import java.nio.ByteBuffer;
import flightsim.simconnect.WeatherMode;

public class RecvEventWeatherMode extends RecvEvent
{
    private WeatherMode mode;
    
    RecvEventWeatherMode(final ByteBuffer bf) {
        super(bf, RecvID.ID_EVENT_WEATHER_MODE);
        this.mode = WeatherMode.type(this.getData());
    }
    
    public WeatherMode getWeatherMode() {
        return this.mode;
    }
}
