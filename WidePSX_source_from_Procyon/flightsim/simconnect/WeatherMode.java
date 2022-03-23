// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect;

public enum WeatherMode
{
    THEME("THEME", 0), 
    RWW("RWW", 1), 
    CUSTOM("CUSTOM", 2), 
    GLOBAL("GLOBAL", 3);
    
    private WeatherMode(final String name, final int ordinal) {
    }
    
    public static WeatherMode type(final int i) {
        final WeatherMode[] values = values();
        if (i < 0 || i > values.length) {
            return WeatherMode.THEME;
        }
        return values[i];
    }
}
