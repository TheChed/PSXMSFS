// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.wrappers;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import flightsim.simconnect.data.XYZ;
import flightsim.simconnect.data.LatLonAlt;
import flightsim.simconnect.data.Waypoint;
import flightsim.simconnect.data.MarkerState;
import flightsim.simconnect.data.InitPosition;
import flightsim.simconnect.data.SimConnectData;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.io.DataOutput;
import java.io.DataInput;

public class DataWrapper implements DataInput, DataOutput
{
    protected ByteBuffer dataBuffer;
    
    public DataWrapper(final ByteBuffer bf) {
        this.dataBuffer = bf;
    }
    
    public DataWrapper(final int size) {
        this(ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN));
    }
    
    protected DataWrapper() {
    }
    
    protected String makeString(final ByteBuffer bf, final int len) {
        final byte[] tmp = new byte[len];
        bf.get(tmp);
        int fZeroPos;
        for (fZeroPos = 0; fZeroPos < len && tmp[fZeroPos] != 0; ++fZeroPos) {}
        return new String(tmp, 0, fZeroPos);
    }
    
    public float getFloat32(final int offset) {
        return this.dataBuffer.getFloat(offset);
    }
    
    public double getFloat64(final int offset) {
        return this.dataBuffer.getDouble(offset);
    }
    
    public int getInt32(final int offset) {
        return this.dataBuffer.getInt(offset);
    }
    
    public long getInt64(final int offset) {
        return this.dataBuffer.getLong(offset);
    }
    
    public String getString8(final int offset) {
        return this.getString(offset, 8);
    }
    
    public String getString32(final int offset) {
        return this.getString(offset, 32);
    }
    
    public String getString64(final int offset) {
        return this.getString(offset, 64);
    }
    
    public String getString128(final int offset) {
        return this.getString(offset, 128);
    }
    
    public String getString256(final int offset) {
        return this.getString(offset, 256);
    }
    
    public String getString260(final int offset) {
        return this.getString(offset, 260);
    }
    
    public String getString(final int offset, final int len) {
        this.dataBuffer.position(offset);
        return this.makeString(this.dataBuffer, len);
    }
    
    public String getStringV(final int offset) {
        int i;
        for (i = 0; this.dataBuffer.hasRemaining() && this.dataBuffer.get(offset + i) != 0; ++i) {}
        return this.makeString(this.dataBuffer, i);
    }
    
    public <T extends SimConnectData> T getData(final T data, final int offset) {
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
    
    public float getFloat32() {
        return this.dataBuffer.getFloat();
    }
    
    public double getFloat64() {
        return this.dataBuffer.getDouble();
    }
    
    public int getInt32() {
        return this.dataBuffer.getInt();
    }
    
    public long getInt64() {
        return this.dataBuffer.getLong();
    }
    
    public String getString8() {
        return this.getString(8);
    }
    
    public String getString32() {
        return this.getString(32);
    }
    
    public String getString64() {
        return this.getString(64);
    }
    
    public String getString128() {
        return this.getString(128);
    }
    
    public String getString256() {
        return this.getString(256);
    }
    
    public String getString260() {
        return this.getString(260);
    }
    
    public String getString(final int len) {
        return this.makeString(this.dataBuffer, len);
    }
    
    public String getStringV() {
        int i = 0;
        for (int currentOffset = this.dataBuffer.position(); this.dataBuffer.hasRemaining() && this.dataBuffer.get(currentOffset + i) != 0; ++i) {}
        return this.makeString(this.dataBuffer, i);
    }
    
    public <T extends SimConnectData> T getData(final T data) {
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
        this.dataBuffer.clear();
    }
    
    public boolean hasRemaining() {
        return this.dataBuffer.hasRemaining();
    }
    
    public int remaining() {
        return this.dataBuffer.remaining();
    }
    
    public void putString(final String s, final int len) {
        final byte[] b = s.getBytes();
        this.dataBuffer.put(b, 0, Math.min(len, b.length));
        for (int i = b.length; i < len; ++i) {
            this.dataBuffer.put((byte)0);
        }
    }
    
    public void putString(final int offset, final String s, final int len) {
        final int sp = this.dataBuffer.position();
        this.dataBuffer.position(offset);
        final byte[] b = s.getBytes();
        this.dataBuffer.put(b, 0, Math.min(len, b.length));
        for (int i = b.length; i < len; ++i) {
            this.dataBuffer.put((byte)0);
        }
        this.dataBuffer.position(sp);
    }
    
    public void putFloat64(final double value) {
        this.dataBuffer.putDouble(value);
    }
    
    public void putFloat64(final int index, final double value) {
        this.dataBuffer.putDouble(index, value);
    }
    
    public void putFloat32(final float value) {
        this.dataBuffer.putFloat(value);
    }
    
    public void putFloat32(final int index, final float value) {
        this.dataBuffer.putFloat(index, value);
    }
    
    public void putInt32(final int index, final int value) {
        this.dataBuffer.putInt(index, value);
    }
    
    public void putInt32(final int value) {
        this.dataBuffer.putInt(value);
    }
    
    public void putInt64(final int index, final long value) {
        this.dataBuffer.putLong(index, value);
    }
    
    public void putInt64(final long value) {
        this.dataBuffer.putLong(value);
    }
    
    public void putString8(final String s) {
        this.putString(s, 8);
    }
    
    public void putString32(final String s) {
        this.putString(s, 32);
    }
    
    public void putString64(final String s) {
        this.putString(s, 64);
    }
    
    public void putString128(final String s) {
        this.putString(s, 128);
    }
    
    public void putString256(final String s) {
        this.putString(s, 256);
    }
    
    public void putString260(final String s) {
        this.putString(s, 260);
    }
    
    public void putString8(final int offset, final String s) {
        this.putString(s, 8);
    }
    
    public void putString32(final int offset, final String s) {
        this.putString(s, 32);
    }
    
    public void putString64(final int offset, final String s) {
        this.putString(s, 64);
    }
    
    public void putString128(final int offset, final String s) {
        this.putString(s, 128);
    }
    
    public void putString256(final int offset, final String s) {
        this.putString(s, 256);
    }
    
    public void putString260(final int offset, final String s) {
        this.putString(s, 260);
    }
    
    public int putStringV(final String s) {
        final byte[] b = s.getBytes();
        this.dataBuffer.put(b);
        this.dataBuffer.put((byte)0);
        return b.length + 1;
    }
    
    public int putStringV(final int offset, final String s) {
        final byte[] b = s.getBytes();
        final int sp = this.dataBuffer.position();
        this.dataBuffer.position(offset);
        this.dataBuffer.put(b);
        this.dataBuffer.put((byte)0);
        this.dataBuffer.position(sp);
        return b.length + 1;
    }
    
    public <T extends SimConnectData> void putData(final int offset, final T data) {
        final int sp = this.dataBuffer.position();
        this.dataBuffer.position(offset);
        data.write(this.dataBuffer);
        this.dataBuffer.position(sp);
    }
    
    public <T extends SimConnectData> void putData(final T data) {
        data.write(this.dataBuffer);
    }
    
    public void putData(final byte[] b) {
        this.dataBuffer.put(b);
    }
    
    public void putLatLonAlt(final LatLonAlt data) {
        this.putData(data);
    }
    
    public void putInitPosition(final InitPosition data) {
        this.putData(data);
    }
    
    public void putWaypoint(final Waypoint data) {
        this.putData(data);
    }
    
    public void putMarkerState(final MarkerState data) {
        this.putData(data);
    }
    
    public void putXYZ(final XYZ data) {
        this.putData(data);
    }
    
    public void putLatLonAlt(final int offset, final LatLonAlt data) {
        this.putData(offset, data);
    }
    
    public void putInitPosition(final int offset, final InitPosition data) {
        this.putData(offset, data);
    }
    
    public void putWaypoint(final int offset, final Waypoint data) {
        this.putData(offset, data);
    }
    
    public void putMarkerState(final int offset, final MarkerState data) {
        this.putData(offset, data);
    }
    
    public void putXYZ(final int offset, final XYZ data) {
        this.putData(offset, data);
    }
    
    public byte[] bytes() {
        return this.dataBuffer.array();
    }
    
    public ByteBuffer getBuffer() {
        this.dataBuffer.flip();
        return this.dataBuffer;
    }
    
    @Override
    public boolean readBoolean() throws IOException {
        return this.dataBuffer.getInt() != 0;
    }
    
    @Override
    public byte readByte() throws IOException {
        return this.dataBuffer.get();
    }
    
    @Override
    public char readChar() throws IOException {
        return this.dataBuffer.getChar();
    }
    
    @Override
    public double readDouble() throws IOException {
        return this.dataBuffer.getDouble();
    }
    
    @Override
    public float readFloat() throws IOException {
        return this.dataBuffer.getFloat();
    }
    
    @Override
    public void readFully(final byte[] b) throws IOException {
        this.dataBuffer.get(b);
    }
    
    @Override
    public void readFully(final byte[] b, final int off, final int len) throws IOException {
        this.dataBuffer.get(b, off, len);
    }
    
    @Override
    public int readInt() throws IOException {
        return this.dataBuffer.getInt();
    }
    
    @Override
    public String readLine() throws IOException {
        return this.getStringV();
    }
    
    @Override
    public long readLong() throws IOException {
        return this.getInt64();
    }
    
    @Override
    public short readShort() throws IOException {
        return this.dataBuffer.getShort();
    }
    
    @Override
    public String readUTF() throws IOException {
        return this.readLine();
    }
    
    @Override
    public int readUnsignedByte() throws IOException {
        return this.dataBuffer.get() & 0xFF;
    }
    
    @Override
    public int readUnsignedShort() throws IOException {
        return this.dataBuffer.getShort() & 0xFFFF;
    }
    
    @Override
    public int skipBytes(final int n) throws IOException {
        this.dataBuffer.position(this.dataBuffer.position() + n);
        return n;
    }
    
    @Override
    public void write(final int b) throws IOException {
        this.dataBuffer.put((byte)b);
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        this.dataBuffer.put(b);
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.dataBuffer.put(b, off, len);
    }
    
    @Override
    public void writeBoolean(final boolean v) throws IOException {
        this.dataBuffer.putInt(v ? 1 : 0);
    }
    
    @Override
    public void writeByte(final int v) throws IOException {
        this.dataBuffer.putInt(v);
    }
    
    @Override
    public void writeBytes(final String s) throws IOException {
        this.dataBuffer.put(s.getBytes());
    }
    
    @Override
    public void writeChar(final int v) throws IOException {
        this.dataBuffer.putChar((char)v);
    }
    
    @Override
    public void writeChars(final String s) throws IOException {
        final char[] sc = s.toCharArray();
        char[] array;
        for (int length = (array = sc).length, i = 0; i < length; ++i) {
            final char c = array[i];
            this.writeChar(c);
        }
    }
    
    @Override
    public void writeDouble(final double v) throws IOException {
        this.dataBuffer.putDouble(v);
    }
    
    @Override
    public void writeFloat(final float v) throws IOException {
        this.dataBuffer.putFloat(v);
    }
    
    @Override
    public void writeInt(final int v) throws IOException {
        this.dataBuffer.putInt(v);
    }
    
    @Override
    public void writeLong(final long v) throws IOException {
        this.dataBuffer.putLong(v);
    }
    
    @Override
    public void writeShort(final int v) throws IOException {
        this.dataBuffer.putShort((short)v);
    }
    
    @Override
    public void writeUTF(final String str) throws IOException {
        this.writeChars(str);
    }
}
