// 
// Decompiled by Procyon v0.5.36
// 

package util;

public class PlaceBearingDistance
{
    private static final double DEG2RAD = 0.017453292519943295;
    private static final double TWOPI = 6.283185307179586;
    private static final double RAD2DEG = 57.29577951308232;
    
    public static double[] getPbdDestCoords(final double argOriginLat, final double argOriginLon, final double argBearing, final double argDistance) {
        final double sinD = Math.sin(argDistance);
        final double cosD = Math.cos(argDistance);
        final double cosC = Math.cos(argBearing);
        final double cosOLat = Math.cos(argOriginLat);
        final double sinOLat = Math.sin(argOriginLat);
        final double[] destCoord = { 0.0, 0.0 };
        destCoord[0] = Math.asin(sinOLat * cosD + cosOLat * sinD * cosC);
        final double dLon = Math.atan2(Math.sin(argBearing) * sinD * cosOLat, cosD - sinOLat * Math.sin(argOriginLat));
        double lon = -(-argOriginLon - dLon + 3.141592653589793) % 6.283185307179586 + 3.141592653589793;
        if (lon < -3.141592653589793) {
            lon += 6.283185307179586;
        }
        else if (lon > 3.141592653589793) {
            lon -= 6.283185307179586;
        }
        destCoord[1] = lon;
        return destCoord;
    }
    
    public static double getReverseHdg(final double argInitialHdg) {
        final double temp = argInitialHdg * 57.29577951308232;
        double reverseHeading = temp - 180.0;
        if (reverseHeading < 0.0) {
            reverseHeading = temp + 180.0;
        }
        else if (reverseHeading > 360.0) {
            reverseHeading = temp - 180.0;
        }
        else if (reverseHeading == 360.0) {
            reverseHeading = 0.0;
        }
        return reverseHeading * 0.017453292519943295;
    }
    
    public static double getDistance(final double argLatitudeWpt1, final double argLongitudeWpt1, final double argLatitudeWpt2, final double argLongitudeWpt2) {
        final double a = Math.sin((argLatitudeWpt2 - argLatitudeWpt1) / 2.0) * Math.sin((argLatitudeWpt2 - argLatitudeWpt1) / 2.0) + Math.cos(argLatitudeWpt1) * Math.cos(argLatitudeWpt2) * Math.sin((argLongitudeWpt2 - argLongitudeWpt1) / 2.0) * Math.sin((argLongitudeWpt2 - argLongitudeWpt1) / 2.0);
        final double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));
        final double d = 6371000.0 * c;
        return d * 5.39957E-4;
    }
}
