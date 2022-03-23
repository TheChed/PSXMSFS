// 
// Decompiled by Procyon v0.5.36
// 

package traffic;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;

public class TrafficAiAcft
{
    private double distance;
    private double latitude;
    private double longitude;
    private long altitude;
    private long track;
    private boolean hasData;
    private String atcType;
    private String atcId;
    private final Lock lockDistance;
    private final Lock lockLatitude;
    private final Lock lockLongitude;
    private final Lock lockAltitude;
    private final Lock lockTrack;
    private final Lock lockHasData;
    private final Lock lockAtcType;
    private final Lock lockAtcId;
    
    public TrafficAiAcft(final double argDistance) {
        this.distance = 0.0;
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.altitude = 0L;
        this.track = 0L;
        this.atcType = new String("");
        this.atcId = new String("");
        this.lockDistance = new ReentrantLock();
        this.lockLatitude = new ReentrantLock();
        this.lockLongitude = new ReentrantLock();
        this.lockAltitude = new ReentrantLock();
        this.lockTrack = new ReentrantLock();
        this.lockHasData = new ReentrantLock();
        this.lockAtcType = new ReentrantLock();
        this.lockAtcId = new ReentrantLock();
        this.distance = argDistance;
    }
    
    protected void reset() {
        this.setLatitude(0.0);
        this.setLongitude(0.0);
        this.setAltitude(0.0);
        this.setTrack(0.0);
        this.setHasData(false);
        this.setAtcType("");
        this.setAtcId("");
    }
    
    protected double getDistance() {
        this.lockDistance.lock();
        final double temp = this.distance;
        this.lockDistance.unlock();
        return temp;
    }
    
    protected void setDistance(final double argDistance) {
        this.lockDistance.lock();
        this.distance = argDistance;
        this.lockDistance.unlock();
    }
    
    protected double getLatitude() {
        this.lockLatitude.lock();
        final double temp = this.latitude;
        this.lockLatitude.unlock();
        return temp;
    }
    
    protected void setLatitude(final double argLatitude) {
        this.lockLatitude.lock();
        this.latitude = argLatitude;
        this.lockLatitude.unlock();
    }
    
    protected double getLongitude() {
        this.lockLongitude.lock();
        final double temp = this.longitude;
        this.lockLongitude.unlock();
        return temp;
    }
    
    protected void setLongitude(final double argLongitude) {
        this.lockLongitude.lock();
        this.longitude = argLongitude;
        this.lockLongitude.unlock();
    }
    
    protected long getAltitude() {
        this.lockAltitude.lock();
        final long temp = this.altitude;
        this.lockAltitude.unlock();
        return temp;
    }
    
    protected void setAltitude(final double argAltitude) {
        this.lockAltitude.lock();
        this.altitude = (long)(Math.floor(argAltitude * 10.0) / 10.0 * 10.0);
        this.lockAltitude.unlock();
    }
    
    protected long getTrack() {
        this.lockTrack.lock();
        final long temp = this.track;
        this.lockTrack.unlock();
        return temp;
    }
    
    protected void setTrack(final double argTrack) {
        this.lockTrack.lock();
        this.track = (long)(Math.floor(argTrack * 180.0 / 3.141592654 * 100.0) / 100.0 * 100.0);
        this.lockTrack.unlock();
    }
    
    protected void setHasData(final boolean argHasData) {
        this.lockHasData.lock();
        this.hasData = argHasData;
        this.lockHasData.unlock();
    }
    
    protected boolean getHasData() {
        this.lockHasData.lock();
        final boolean temp = this.hasData;
        this.lockHasData.unlock();
        return temp;
    }
    
    protected void setAtcType(final String argType) {
        this.lockAtcType.lock();
        this.atcType = argType;
        this.lockAtcType.unlock();
    }
    
    protected String getAtcType() {
        this.lockAtcType.lock();
        final String temp = new String(this.atcType);
        this.lockAtcType.unlock();
        return temp;
    }
    
    protected void setAtcId(final String argId) {
        this.lockAtcId.lock();
        this.atcId = argId;
        this.lockAtcId.unlock();
    }
    
    protected String getAtcId() {
        this.lockAtcId.lock();
        final String temp = new String(this.atcId);
        this.lockAtcId.unlock();
        return temp;
    }
}
