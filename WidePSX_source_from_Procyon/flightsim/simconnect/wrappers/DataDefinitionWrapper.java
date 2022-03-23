// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.wrappers;

import flightsim.simconnect.data.XYZ;
import flightsim.simconnect.data.Waypoint;
import flightsim.simconnect.data.MarkerState;
import flightsim.simconnect.data.LatLonAlt;
import flightsim.simconnect.data.InitPosition;
import flightsim.simconnect.data.SimConnectData;
import flightsim.simconnect.recv.RecvSimObjectData;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.text.MessageFormat;
import flightsim.simconnect.Messages;
import flightsim.simconnect.SimConnectDataType;
import java.io.IOException;
import java.util.ArrayList;
import flightsim.simconnect.SimConnect;
import java.util.List;

public class DataDefinitionWrapper extends DataWrapper implements Iterable<Object>
{
    private final List<DataDef> dataDefs;
    private final SimConnect sc;
    private int totalSize;
    private final int datadefid;
    
    public DataDefinitionWrapper(final SimConnect sc, final int dataDefinitionID) throws IOException {
        this.dataDefs = new ArrayList<DataDef>();
        this.totalSize = 0;
        this.sc = sc;
        this.datadefid = dataDefinitionID;
        this.clearDataDefinition();
    }
    
    public DataDefinitionWrapper(final SimConnect sc) throws IOException {
        this(sc, (int)(Math.random() * 8192.0));
    }
    
    public int getDataDefinitionID() {
        return this.datadefid;
    }
    
    public void addToDataDefinition(final String variable, final String units, final SimConnectDataType dataType) throws IOException {
        if (dataType.size() == -1) {
            throw new IllegalArgumentException(Messages.getString("DataDefinitionWrapper.0"));
        }
        synchronized (this) {
            this.sc.addToDataDefinition(this.datadefid, variable, units, dataType);
            final DataDef dd = new DataDef(variable, this.totalSize, dataType);
            this.dataDefs.add(dd);
            this.totalSize += dataType.size();
        }
    }
    
    public void clearDataDefinition() throws IOException {
        this.dataBuffer = null;
        synchronized (this) {
            this.sc.clearDataDefinition(this.datadefid);
            this.dataDefs.clear();
        }
    }
    
    public void setDataOnSimObject(final int objectId) throws IOException, IllegalDataDefinition {
        if (this.dataBuffer == null) {
            throw new IllegalDataDefinition(Messages.getString("DataDefinitionWrapper.1"));
        }
        this.sc.setDataOnSimObject(this.datadefid, objectId, false, 1, this.bytes());
    }
    
    public void setClientData(final int clientDataId) throws IOException, IllegalDataDefinition {
        if (this.dataBuffer == null) {
            throw new IllegalDataDefinition(Messages.getString("DataDefinitionWrapper.2"));
        }
        this.sc.setClientData(clientDataId, this.datadefid, 0, 1, this.totalSize, this.bytes());
    }
    
    int getOffset(final String variable) throws IllegalDataDefinition {
        for (final DataDef dd : this.dataDefs) {
            if (dd.name.equalsIgnoreCase(variable)) {
                return dd.offset;
            }
        }
        throw new IllegalDataDefinition(MessageFormat.format(Messages.getString("DataDefinitionWrapper.3"), variable));
    }
    
    public void fillEmptyData() {
        synchronized (this) {
            final byte[] rawData = new byte[this.totalSize];
            (super.dataBuffer = ByteBuffer.wrap(rawData)).order(ByteOrder.LITTLE_ENDIAN);
        }
    }
    
    public void fillDataFrom(final RecvSimObjectData simObjData) throws IllegalDataDefinition {
        if (simObjData.getDefineID() != this.datadefid) {
            throw new IllegalDataDefinition(MessageFormat.format(Messages.getString("DataDefinitionWrapper.4"), this.datadefid, simObjData.getDefineID()));
        }
        synchronized (this) {
            final byte[] rawData = simObjData.getData();
            if (rawData.length != this.totalSize) {
                throw new IllegalDataDefinition(MessageFormat.format(Messages.getString("DataDefinitionWrapper.5"), rawData.length));
            }
            (super.dataBuffer = ByteBuffer.wrap(rawData)).order(ByteOrder.LITTLE_ENDIAN);
        }
    }
    
    @Override
    public Iterator<Object> iterator() {
        return new DataIterator((DataIterator)null);
    }
    
    public <T extends SimConnectData> T getData(final T data, final String var) throws IllegalDataDefinition {
        return super.getData(data, this.getOffset(var));
    }
    
    public float getFloat32(final String var) throws IllegalDataDefinition {
        return super.getFloat32(this.getOffset(var));
    }
    
    public double getFloat64(final String var) throws IllegalDataDefinition {
        return super.getFloat64(this.getOffset(var));
    }
    
    public InitPosition getInitPosition(final String var) throws IllegalDataDefinition {
        return super.getInitPosition(this.getOffset(var));
    }
    
    public int getInt32(final String var) throws IllegalDataDefinition {
        return super.getInt32(this.getOffset(var));
    }
    
    public long getInt64(final String var) throws IllegalDataDefinition {
        return super.getInt64(this.getOffset(var));
    }
    
    public LatLonAlt getLatLonAlt(final String var) throws IllegalDataDefinition {
        return super.getLatLonAlt(this.getOffset(var));
    }
    
    public MarkerState getMarkerState(final String var) throws IllegalDataDefinition {
        return super.getMarkerState(this.getOffset(var));
    }
    
    public String getString128(final String var) throws IllegalDataDefinition {
        return super.getString128(this.getOffset(var));
    }
    
    public String getString256(final String var) throws IllegalDataDefinition {
        return super.getString256(this.getOffset(var));
    }
    
    public String getString260(final String var) throws IllegalDataDefinition {
        return super.getString260(this.getOffset(var));
    }
    
    public String getString32(final String var) throws IllegalDataDefinition {
        return super.getString32(this.getOffset(var));
    }
    
    public String getString64(final String var) throws IllegalDataDefinition {
        return super.getString64(this.getOffset(var));
    }
    
    public String getString8(final String var) throws IllegalDataDefinition {
        return super.getString8(this.getOffset(var));
    }
    
    public Waypoint getWaypoint(final String var) throws IllegalDataDefinition {
        return super.getWaypoint(this.getOffset(var));
    }
    
    public XYZ getXYZ(final String var) throws IllegalDataDefinition {
        return super.getXYZ(this.getOffset(var));
    }
    
    public void putFloat32(final String var, final float value) throws IllegalDataDefinition {
        super.putFloat32(this.getOffset(var), value);
    }
    
    public void putFloat64(final String var, final double value) throws IllegalDataDefinition {
        super.putFloat64(this.getOffset(var), value);
    }
    
    public void putInitPosition(final String var, final InitPosition data) throws IllegalDataDefinition {
        super.putInitPosition(this.getOffset(var), data);
    }
    
    public void putInt32(final String var, final int value) throws IllegalDataDefinition {
        super.putInt32(this.getOffset(var), value);
    }
    
    public void putInt64(final String var, final long value) throws IllegalDataDefinition {
        super.putInt64(this.getOffset(var), value);
    }
    
    public void putLatLonAlt(final String var, final LatLonAlt data) throws IllegalDataDefinition {
        super.putLatLonAlt(this.getOffset(var), data);
    }
    
    public void putMarkerState(final String var, final MarkerState data) throws IllegalDataDefinition {
        super.putMarkerState(this.getOffset(var), data);
    }
    
    public void putString128(final String var, final String s) throws IllegalDataDefinition {
        super.putString128(this.getOffset(var), s);
    }
    
    public void putString256(final String var, final String s) throws IllegalDataDefinition {
        super.putString256(this.getOffset(var), s);
    }
    
    public void putString260(final String var, final String s) throws IllegalDataDefinition {
        super.putString260(this.getOffset(var), s);
    }
    
    public void putString32(final String var, final String s) throws IllegalDataDefinition {
        super.putString32(this.getOffset(var), s);
    }
    
    public void putString64(final String var, final String s) throws IllegalDataDefinition {
        super.putString64(this.getOffset(var), s);
    }
    
    public void putString8(final String var, final String s) throws IllegalDataDefinition {
        super.putString8(this.getOffset(var), s);
    }
    
    public void putWaypoint(final String var, final Waypoint data) throws IllegalDataDefinition {
        super.putWaypoint(this.getOffset(var), data);
    }
    
    public void putXYZ(final String var, final XYZ data) throws IllegalDataDefinition {
        super.putXYZ(this.getOffset(var), data);
    }
    
    private class DataDef implements Comparable<DataDef>
    {
        String name;
        int offset;
        SimConnectDataType type;
        
        @Override
        public int compareTo(final DataDef o) {
            return this.offset - o.offset;
        }
        
        DataDef(final String name, final int offset, final SimConnectDataType type) {
            this.name = name;
            this.offset = offset;
            this.type = type;
        }
    }
    
    private class DataIterator implements Iterator<Object>
    {
        private Iterator<DataDef> ddIterator;
        
        @Override
        public boolean hasNext() {
            return this.ddIterator.hasNext();
        }
        
        @Override
        public Object next() {
            final DataDef dd = this.ddIterator.next();
            switch (dd.type) {
                case FLOAT32: {
                    return DataDefinitionWrapper.this.getFloat32(dd.offset);
                }
                case FLOAT64: {
                    return DataDefinitionWrapper.this.getFloat64(dd.offset);
                }
                case INITPOSITION: {
                    return DataDefinitionWrapper.this.getInitPosition(dd.offset);
                }
                case INT32: {
                    return DataDefinitionWrapper.this.getInt32(dd.offset);
                }
                case INT64: {
                    return DataDefinitionWrapper.this.getInt64(dd.offset);
                }
                case LATLONALT: {
                    return DataDefinitionWrapper.this.getLatLonAlt(dd.offset);
                }
                case MARKERSTATE: {
                    return DataDefinitionWrapper.this.getMarkerState(dd.offset);
                }
                case STRING128: {
                    return DataDefinitionWrapper.this.getString128(dd.offset);
                }
                case STRING256: {
                    return DataDefinitionWrapper.this.getString256(dd.offset);
                }
                case STRING260: {
                    return DataDefinitionWrapper.this.getString260(dd.offset);
                }
                case STRING32: {
                    return DataDefinitionWrapper.this.getString32(dd.offset);
                }
                case STRING64: {
                    return DataDefinitionWrapper.this.getString64(dd.offset);
                }
                case STRING8: {
                    return DataDefinitionWrapper.this.getString8(dd.offset);
                }
                case WAYPOINT: {
                    return DataDefinitionWrapper.this.getWaypoint(dd.offset);
                }
                case XYZ: {
                    return DataDefinitionWrapper.this.getXYZ(dd.offset);
                }
                default: {
                    return null;
                }
            }
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }
}
