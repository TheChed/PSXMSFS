// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.recv;

import java.nio.ByteBuffer;

public class FacilityVOR extends FacilityNDB
{
    public static final int HAS_NAV_SIGNAL = 1;
    public static final int HAS_LOCALIZER = 2;
    public static final int HAS_GLIDE_SLOPE = 4;
    public static final int HAS_DME = 8;
    private int flags;
    private float localizer;
    private double glideLat;
    private double glideLon;
    private double glideAlt;
    private float glideSlopeAngle;
    
    FacilityVOR(final ByteBuffer bf) {
        super(bf);
        this.flags = bf.getInt();
        this.localizer = bf.getFloat();
        this.glideLat = bf.getDouble();
        this.glideLon = bf.getDouble();
        this.glideAlt = bf.getDouble();
        this.glideSlopeAngle = bf.getFloat();
    }
    
    public int getFlags() {
        return this.flags;
    }
    
    public double getGlideAlt() {
        return this.glideAlt;
    }
    
    public double getGlideLat() {
        return this.glideLat;
    }
    
    public double getGlideLon() {
        return this.glideLon;
    }
    
    public float getGlideSlopeAngle() {
        return this.glideSlopeAngle;
    }
    
    public float getLocalizer() {
        return this.localizer;
    }
    
    private boolean hasFlag(final int constant) {
        return (this.flags & constant) != 0x0;
    }
    
    public boolean hasNAVSignal() {
        return this.hasFlag(1);
    }
    
    public boolean hasLocalizer() {
        return this.hasFlag(2);
    }
    
    public boolean hasGlideSlope() {
        return this.hasFlag(4);
    }
    
    public boolean hasDME() {
        return this.hasFlag(8);
    }
    
    @Override
    public String toString() {
        return String.valueOf(super.toString()) + " localizer=" + this.localizer + " glideslope=" + this.glideSlopeAngle + " flags=0x" + Integer.toHexString(this.flags);
    }
}
