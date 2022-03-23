// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.recv;

import java.nio.BufferUnderflowException;
import flightsim.simconnect.data.XYZ;
import flightsim.simconnect.data.LatLonAlt;
import flightsim.simconnect.data.Waypoint;
import flightsim.simconnect.data.MarkerState;
import flightsim.simconnect.data.InitPosition;
import flightsim.simconnect.data.SimConnectData;
import java.nio.ByteOrder;
import flightsim.simconnect.wrappers.DataWrapper;
import java.nio.ByteBuffer;

public class RecvSimObjectData extends RecvPacket
{
    private final int requestID;
    private final int objectID;
    private final int defineID;
    private final int flags;
    private final int entryNumber;
    private final int outOf;
    private final int defineCount;
    private final byte[] data;
    private ByteBuffer dataBuffer;
    
    RecvSimObjectData(final ByteBuffer bf) {
        this(bf, RecvID.ID_SIMOBJECT_DATA);
    }
    
    RecvSimObjectData(final ByteBuffer bf, final RecvID id) {
        super(bf, id);
        this.requestID = bf.getInt();
        this.objectID = bf.getInt();
        this.defineID = bf.getInt();
        this.flags = bf.getInt();
        this.entryNumber = bf.getInt();
        this.outOf = bf.getInt();
        this.defineCount = bf.getInt();
        bf.get(this.data = new byte[bf.remaining()]);
    }
    
    public byte[] getData() {
        return this.data;
    }
    
    public int getDataSize() {
        return this.data.length;
    }
    
    public DataWrapper getDataWrapper() {
        this.asByteBuffer();
        return new DataWrapper(this.dataBuffer);
    }
    
    public int getDefineCount() {
        return this.defineCount;
    }
    
    public int getDefineID() {
        return this.defineID;
    }
    
    public int getEntryNumber() {
        return this.entryNumber;
    }
    
    public int getFlags() {
        return this.flags;
    }
    
    public int getObjectID() {
        return this.objectID;
    }
    
    public int getOutOf() {
        return this.outOf;
    }
    
    public int getRequestID() {
        return this.requestID;
    }
    
    private ByteBuffer asByteBuffer() {
        if (this.dataBuffer == null) {
            (this.dataBuffer = ByteBuffer.wrap(this.data)).order(ByteOrder.LITTLE_ENDIAN);
        }
        return this.dataBuffer;
    }
    
    public float getDataFloat32(final int offset) {
        this.asByteBuffer();
        return this.dataBuffer.getFloat(offset);
    }
    
    public double getDataFloat64(final int offset) {
        this.asByteBuffer();
        return this.dataBuffer.getDouble(offset);
    }
    
    public int getDataInt32(final int offset) {
        this.asByteBuffer();
        return this.dataBuffer.getInt(offset);
    }
    
    public long getDataInt64(final int offset) {
        this.asByteBuffer();
        return this.dataBuffer.getLong(offset);
    }
    
    public String getDataString8(final int offset) {
        return this.getDataString(offset, 8);
    }
    
    public String getDataString32(final int offset) {
        return this.getDataString(offset, 32);
    }
    
    public String getDataString64(final int offset) {
        return this.getDataString(offset, 64);
    }
    
    public String getDataString128(final int offset) {
        return this.getDataString(offset, 128);
    }
    
    public String getDataString256(final int offset) {
        return this.getDataString(offset, 256);
    }
    
    public String getDataString260(final int offset) {
        return this.getDataString(offset, 260);
    }
    
    public String getDataString(final int offset, final int len) {
        this.asByteBuffer();
        this.dataBuffer.position(offset);
        return super.makeString(this.dataBuffer, len);
    }
    
    public String getDataStringV(final int offset) {
        this.asByteBuffer();
        int i;
        for (i = 0; this.dataBuffer.hasRemaining() && this.dataBuffer.get(offset + i) != 0; ++i) {}
        return super.makeString(this.dataBuffer, i);
    }
    
    public <T extends SimConnectData> T getData(final T data, final int offset) {
        this.asByteBuffer();
        final int current = this.dataBuffer.position();
        this.dataBuffer.position(offset);
        data.read(this.dataBuffer);
        this.dataBuffer.position(current);
        return data;
    }
    
    public InitPosition getInitPosition(final int offset) {
        return this.getData(new InitPosition(), offset);
    }
    
    public MarkerState getMarkerState(final int offset) {
        return this.getData(new MarkerState(), offset);
    }
    
    public Waypoint getWaypoint(final int offset) {
        return this.getData(new Waypoint(), offset);
    }
    
    public LatLonAlt getLatLonAlt(final int offset) {
        return this.getData(new LatLonAlt(), offset);
    }
    
    public XYZ getXYZ(final int offset) {
        return this.getData(new XYZ(), offset);
    }
    
    public float getDataFloat32() {
        this.asByteBuffer();
        return this.dataBuffer.getFloat();
    }
    
    public double getDataFloat64() {
        this.asByteBuffer();
        return this.dataBuffer.getDouble();
    }
    
    public int getDataInt32() {
        this.asByteBuffer();
        return this.dataBuffer.getInt();
    }
    
    public long getDataInt64() {
        this.asByteBuffer();
        return this.dataBuffer.getLong();
    }
    
    public String getDataString8() {
        return this.getDataString(8);
    }
    
    public String getDataString32() {
        return this.getDataString(32);
    }
    
    public String getDataString64() {
        return this.getDataString(64);
    }
    
    public String getDataString128() {
        return this.getDataString(128);
    }
    
    public String getDataString256() {
        return this.getDataString(256);
    }
    
    public String getDataString260() {
        return this.getDataString(260);
    }
    
    public String getDataString(final int len) {
        this.asByteBuffer();
        return super.makeString(this.dataBuffer, len);
    }
    
    public String getDataStringV() {
        this.asByteBuffer();
        int i = 0;
        for (int currentOffset = this.dataBuffer.position(); this.dataBuffer.hasRemaining() && this.dataBuffer.get(currentOffset + i) != 0; ++i) {}
        return super.makeString(this.dataBuffer, i);
    }
    
    public <T extends SimConnectData> T getData(final T data) {
        this.asByteBuffer();
        data.read(this.dataBuffer);
        return data;
    }
    
    public InitPosition getInitPosition() {
        return this.getData(new InitPosition());
    }
    
    public MarkerState getMarkerState() {
        return this.getData(new MarkerState());
    }
    
    public Waypoint getWaypoint() {
        return this.getData(new Waypoint());
    }
    
    public LatLonAlt getLatLonAlt() {
        return this.getData(new LatLonAlt());
    }
    
    public XYZ getXYZ() throws BufferUnderflowException {
        return this.getData(new XYZ());
    }
    
    public void reset() {
        this.asByteBuffer();
        this.dataBuffer.clear();
    }
    
    public boolean hasRemaining() {
        this.asByteBuffer();
        return this.dataBuffer.hasRemaining();
    }
    
    public int remaining() {
        this.asByteBuffer();
        return this.dataBuffer.remaining();
    }
}
