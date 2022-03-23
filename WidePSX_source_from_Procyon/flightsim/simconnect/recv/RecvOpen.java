// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.recv;

import java.nio.ByteBuffer;

public class RecvOpen extends RecvPacket
{
    private final String applicationName;
    private final int applicationVersionMajor;
    private final int applicationVersionMinor;
    private final int applicationBuildMajor;
    private final int applicationBuildMinor;
    private final int simConnectVersionMajor;
    private final int simConnectVersionMinor;
    private final int simConnectBuildMajor;
    private final int simConnectBuildMinor;
    private final int reserved1;
    private final int reserved2;
    
    RecvOpen(final ByteBuffer bf) {
        super(bf, RecvID.ID_OPEN);
        this.applicationName = this.makeString(bf, 256);
        this.applicationVersionMajor = bf.getInt();
        this.applicationVersionMinor = bf.getInt();
        this.applicationBuildMajor = bf.getInt();
        this.applicationBuildMinor = bf.getInt();
        this.simConnectVersionMajor = bf.getInt();
        this.simConnectVersionMinor = bf.getInt();
        this.simConnectBuildMajor = bf.getInt();
        this.simConnectBuildMinor = bf.getInt();
        this.reserved1 = bf.getInt();
        this.reserved2 = bf.getInt();
    }
    
    public int getApplicationBuildMajor() {
        return this.applicationBuildMajor;
    }
    
    public int getApplicationBuildMinor() {
        return this.applicationBuildMinor;
    }
    
    public String getApplicationName() {
        return this.applicationName;
    }
    
    public int getApplicationVersionMajor() {
        return this.applicationVersionMajor;
    }
    
    public int getApplicationVersionMinor() {
        return this.applicationVersionMinor;
    }
    
    public int getReserved1() {
        return this.reserved1;
    }
    
    public int getReserved2() {
        return this.reserved2;
    }
    
    public int getSimConnectBuildMajor() {
        return this.simConnectBuildMajor;
    }
    
    public int getSimConnectBuildMinor() {
        return this.simConnectBuildMinor;
    }
    
    public int getSimConnectVersionMajor() {
        return this.simConnectVersionMajor;
    }
    
    public int getSimConnectVersionMinor() {
        return this.simConnectVersionMinor;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.applicationName) + " ( ver " + this.applicationVersionMajor + "." + this.applicationVersionMinor + " build " + this.applicationBuildMajor + "." + this.applicationBuildMinor + " ) simconnect " + this.simConnectVersionMajor + "." + this.simConnectVersionMinor + " build " + this.simConnectBuildMajor + "." + this.simConnectBuildMinor;
    }
}
