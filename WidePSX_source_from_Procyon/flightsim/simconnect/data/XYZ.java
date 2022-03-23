// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.data;

import java.nio.ByteBuffer;
import java.io.Serializable;

public class XYZ implements SimConnectData, Serializable
{
    private static final long serialVersionUID = -2922269039547967440L;
    public double x;
    public double y;
    public double z;
    
    public XYZ(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public XYZ() {
    }
    
    @Override
    public void read(final ByteBuffer buffer) {
        this.x = buffer.getDouble();
        this.y = buffer.getDouble();
        this.z = buffer.getDouble();
    }
    
    @Override
    public void write(final ByteBuffer buffer) {
        buffer.putDouble(this.x);
        buffer.putDouble(this.y);
        buffer.putDouble(this.z);
    }
    
    double get(final int index) {
        if (index == 0) {
            return this.x;
        }
        if (index == 1) {
            return this.y;
        }
        if (index == 2) {
            return this.z;
        }
        return -1.0;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.x) + ", " + this.y + ", " + this.z;
    }
    
    public void setFromSpherical(final double lat, final double lon, final double alt) {
        this.x = alt * Math.sin(lat) * Math.cos(lon);
        this.y = alt * Math.sin(lat) * Math.sin(lon);
        this.z = alt * Math.cos(lat);
    }
    
    public void setFromSpherical(final LatLonAlt lla, final double earthRadius) {
        final double lat = Math.toRadians(lla.latitude);
        final double lon = Math.toRadians(lla.longitude);
        final double alt = earthRadius + lla.altitude;
        this.x = alt * Math.sin(lat) * Math.cos(lon);
        this.y = alt * Math.sin(lat) * Math.sin(lon);
        this.z = alt * Math.cos(lat);
    }
    
    public double dist() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }
    
    public double dist(final double dx, final double dy, final double dz) {
        return Math.sqrt((this.x - dx) * (this.x - dx) + (this.y - dy) * (this.y - dy) + (this.z - dz) * (this.z - dz));
    }
    
    public double dist(final XYZ p) {
        return this.dist(p.x, p.y, p.z);
    }
    
    public void translate(final double xx, final double yy, final double zz) {
        this.x += xx;
        this.y += yy;
        this.z += zz;
    }
    
    public void rotateX(final double a) {
        final double newx = this.x;
        final double newy = Math.cos(a) * this.y - Math.sin(a) * this.z;
        final double newz = Math.sin(a) * this.y + Math.cos(a) * this.z;
        this.x = newx;
        this.y = newy;
        this.z = newz;
    }
    
    public void rotateY(final double a) {
        final double newx = Math.cos(a) * this.x + Math.sin(a) * this.z;
        final double newy = this.y;
        final double newz = -Math.sin(a) * this.x + Math.cos(a) * this.z;
        this.x = newx;
        this.y = newy;
        this.z = newz;
    }
    
    public void rotateZ(final double a) {
        final double newx = Math.cos(a) * this.x - Math.sin(a) * this.y;
        final double newy = Math.sin(a) * this.x + Math.cos(a) * this.y;
        final double newz = this.z;
        this.x = newx;
        this.y = newy;
        this.z = newz;
    }
    
    public void projectionFrustum(final double l, final double r, final double b, final double t, final double n, final double f) {
        final double newx = 2.0 * n / (r - l) * this.x + (r + l) / (r - l) * this.z;
        final double newy = 2.0 * n / (t - b) * this.y + (t + b) / (t - b) * this.z;
        final double newz = -(f + n) / (f - n) * this.z;
        this.x = newx;
        this.y = newy;
        this.z = newz;
    }
    
    public void projection2(final double xf, final double yf, final double xzf, final double yzf) {
        final double newx = xf * this.x + xzf * this.z;
        final double newy = yf * this.y + yzf * this.z;
        final double newz = this.z;
        this.x = newx;
        this.y = newy;
        this.z = newz;
    }
    
    public XYZ clone() {
        return new XYZ(this.x, this.y, this.z);
    }
}
