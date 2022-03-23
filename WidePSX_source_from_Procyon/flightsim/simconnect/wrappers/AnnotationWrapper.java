// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.wrappers;

import flightsim.simconnect.DataType;
import java.util.Iterator;
import flightsim.simconnect.recv.RecvSimObjectData;
import flightsim.simconnect.SimConnectPeriod;
import java.lang.reflect.Field;
import flightsim.simconnect.data.MarkerState;
import flightsim.simconnect.data.Waypoint;
import flightsim.simconnect.data.XYZ;
import flightsim.simconnect.data.LatLonAlt;
import flightsim.simconnect.SimConnectDataType;
import java.lang.annotation.Annotation;
import java.io.IOException;
import java.util.HashMap;
import flightsim.simconnect.SimConnect;
import java.util.Map;

public class AnnotationWrapper
{
    private Map<Class<?>, Integer> definitionIds;
    private SimConnect simConnect;
    private int definitionID;
    private int requestID;
    
    public AnnotationWrapper(final SimConnect sc) {
        this(sc, 0, 0);
    }
    
    public AnnotationWrapper(final SimConnect sc, final int startDefID, final int startReqID) {
        this.definitionIds = new HashMap<Class<?>, Integer>();
        if (sc == null) {
            throw new NullPointerException("Simconnect cannot be null");
        }
        this.simConnect = sc;
        this.setStartDefinitionID(startDefID);
        this.setStartRequestID(startReqID);
    }
    
    public void setStartDefinitionID(final int startID) {
        this.definitionID = startID;
    }
    
    public void setStartRequestID(final int startID) {
        this.requestID = startID;
    }
    
    public int registerClass(final Class<?> c) throws IOException, IllegalDataDefinition {
        return this.registerClass(++this.definitionID, c);
    }
    
    public int registerClass(final Enum dataDefID, final Class<?> c) throws IOException, IllegalDataDefinition {
        return this.registerClass(dataDefID.ordinal(), c);
    }
    
    public int registerClass(final int dataDefID, final Class<?> c) throws IOException, IllegalDataDefinition {
        if (this.definitionIds.containsKey(c)) {
            return this.definitionIds.get(c);
        }
        int fieldAdded = 0;
        Field[] declaredFields;
        for (int length = (declaredFields = c.getDeclaredFields()).length, i = 0; i < length; ++i) {
            final Field f = declaredFields[i];
            if (f.isAnnotationPresent(FlightSimData.class)) {
                final FlightSimData fsd = f.getAnnotation(FlightSimData.class);
                final String variable = fsd.variable();
                final String units = fsd.units();
                SimConnectDataType type = SimConnectDataType.INVALID;
                if (f.getType().equals(Float.class) || f.getType().equals(Float.TYPE)) {
                    type = SimConnectDataType.FLOAT32;
                }
                else if (f.getType().equals(Double.class) || f.getType().equals(Double.TYPE)) {
                    type = SimConnectDataType.FLOAT64;
                }
                else if (f.getType().equals(Integer.class) || f.getType().equals(Integer.TYPE)) {
                    type = SimConnectDataType.INT32;
                }
                else if (f.getType().equals(Long.class) || f.getType().equals(Long.TYPE)) {
                    type = SimConnectDataType.INT64;
                }
                else if (f.getType().equals(Boolean.class) || f.getType().equals(Boolean.TYPE)) {
                    type = SimConnectDataType.INT32;
                }
                else if (f.getType().equals(Short.class) || f.getType().equals(Short.TYPE)) {
                    type = SimConnectDataType.INT32;
                }
                else if (f.getType().equals(LatLonAlt.class)) {
                    type = SimConnectDataType.LATLONALT;
                }
                else if (f.getType().equals(XYZ.class)) {
                    type = SimConnectDataType.XYZ;
                }
                else if (f.getType().equals(Waypoint.class)) {
                    type = SimConnectDataType.WAYPOINT;
                }
                else if (f.getType().equals(MarkerState.class)) {
                    type = SimConnectDataType.MARKERSTATE;
                }
                else if (f.getType().equals(String.class)) {
                    final int len = fsd.stringWidth();
                    switch (len) {
                        case 8: {
                            type = SimConnectDataType.STRING8;
                            break;
                        }
                        case 32: {
                            type = SimConnectDataType.STRING32;
                            break;
                        }
                        case 64: {
                            type = SimConnectDataType.STRING64;
                            break;
                        }
                        case 128: {
                            type = SimConnectDataType.STRING128;
                            break;
                        }
                        case 256: {
                            type = SimConnectDataType.STRING256;
                            break;
                        }
                        case 260: {
                            type = SimConnectDataType.STRING260;
                            break;
                        }
                        default: {
                            throw new IllegalDataDefinition("Invalid string length (" + len + ")");
                        }
                    }
                }
                if (type == SimConnectDataType.INVALID) {
                    throw new IllegalDataDefinition("Invalid field type (" + f.getType().getName() + ")");
                }
                this.simConnect.addToDataDefinition(dataDefID, variable, units, type);
                ++fieldAdded;
            }
        }
        if (fieldAdded > 0) {
            this.definitionIds.put(c, dataDefID);
        }
        return this.requestID;
    }
    
    public int requestSimObjectData(final Class<?> cl, final int objectId, final SimConnectPeriod period, final boolean onlyWhenChanged) throws IOException, IllegalDataDefinition {
        return this.requestSimObjectData(cl, ++this.requestID, objectId, period, onlyWhenChanged);
    }
    
    public int requestSimObjectData(final Class<?> cl, final Enum requestId, final int objectId, final SimConnectPeriod period, final boolean onlyWhenChanged) throws IOException, IllegalDataDefinition {
        return this.requestSimObjectData(cl, requestId.ordinal(), objectId, period, onlyWhenChanged);
    }
    
    public int requestSimObjectData(final Class<?> cl, final int requestId, final int objectId, final SimConnectPeriod period, final boolean onlyWhenChanged) throws IOException, IllegalDataDefinition {
        if (!this.definitionIds.containsKey(cl)) {
            throw new IllegalDataDefinition("Class not defined. call registerClass() first");
        }
        final int defId = this.definitionIds.get(cl);
        final int flags = onlyWhenChanged ? 1 : 0;
        this.simConnect.requestDataOnSimObject(requestId, defId, objectId, period, flags, 0, 0, 0);
        return requestId;
    }
    
    public <T> void setSimObjectData(final T data, final int objectId) throws IOException, IllegalDataDefinition {
        final Class<?> cl = data.getClass();
        if (!this.definitionIds.containsKey(cl)) {
            throw new IllegalDataDefinition("Class not defined in this wrapper.");
        }
        final int dataLen = this.classDataSize(cl);
        final DataWrapper dw = new DataWrapper(dataLen);
        this.wrap(data, dw);
        final int defId = this.definitionIds.get(cl);
        this.simConnect.setDataOnSimObject(defId, objectId, false, 1, dw);
    }
    
    public Object unwrap(final RecvSimObjectData dataPacket) {
        final int defId = dataPacket.getDefineID();
        for (final Map.Entry<Class<?>, Integer> me : this.definitionIds.entrySet()) {
            if (me.getValue() == defId) {
                try {
                    return this.unwrap(me.getKey(), dataPacket);
                }
                catch (IllegalDataDefinition e) {
                    return null;
                }
            }
        }
        return null;
    }
    
    public <T> T unwrap(final Class<T> cl, final RecvSimObjectData data) throws IllegalDataDefinition {
        if (!this.definitionIds.containsKey(cl)) {
            throw new IllegalDataDefinition("Class not defined in this wrapper.");
        }
        T o;
        try {
            o = cl.newInstance();
        }
        catch (InstantiationException e) {
            return null;
        }
        catch (IllegalAccessException e2) {
            return null;
        }
        return (T)this.unwrapToObject(o, data);
    }
    
    public Object unwrapToObject(final Object o, final RecvSimObjectData data) throws IllegalDataDefinition {
        final Class<?> cl = o.getClass();
        if (!this.definitionIds.containsKey(cl)) {
            throw new IllegalDataDefinition("Class not defined in this wrapper.");
        }
        Field[] declaredFields;
        for (int length = (declaredFields = cl.getDeclaredFields()).length, i = 0; i < length; ++i) {
            final Field f = declaredFields[i];
            if (f.isAnnotationPresent(FlightSimData.class)) {
                try {
                    this.unwrapField(o, f, data);
                }
                catch (IllegalAccessException ex) {}
            }
        }
        return o;
    }
    
    private void unwrapField(final Object o, final Field f, final RecvSimObjectData ro) throws IllegalArgumentException, IllegalAccessException {
        final FlightSimData fsd = f.getAnnotation(FlightSimData.class);
        final boolean accStatus = f.isAccessible();
        f.setAccessible(true);
        if (f.getType().equals(Float.class) || f.getType().equals(Float.TYPE)) {
            final Float val = new Float(ro.getDataFloat32());
            f.set(o, val);
        }
        else if (f.getType().equals(Double.class) || f.getType().equals(Double.TYPE)) {
            final Double val2 = new Double(ro.getDataFloat64());
            f.set(o, val2);
        }
        else if (f.getType().equals(Integer.class) || f.getType().equals(Integer.TYPE)) {
            final Integer val3 = new Integer(ro.getDataInt32());
            f.set(o, val3);
        }
        else if (f.getType().equals(Long.class) || f.getType().equals(Long.TYPE)) {
            final Long val4 = new Long(ro.getDataInt64());
            f.set(o, val4);
        }
        else if (f.getType().equals(Boolean.class) || f.getType().equals(Boolean.TYPE)) {
            final Boolean val5 = new Boolean(ro.getDataInt32() != 0);
            f.set(o, val5);
        }
        else if (f.getType().equals(Short.class) || f.getType().equals(Short.TYPE)) {
            final Short val6 = new Short((short)ro.getDataInt32());
            f.set(o, val6);
        }
        else if (f.getType().equals(LatLonAlt.class)) {
            final LatLonAlt val7 = ro.getLatLonAlt();
            f.set(o, val7);
        }
        else if (f.getType().equals(XYZ.class)) {
            final XYZ val8 = ro.getXYZ();
            f.set(o, val8);
        }
        else if (f.getType().equals(Waypoint.class)) {
            final Waypoint val9 = ro.getWaypoint();
            f.set(o, val9);
        }
        else if (f.getType().equals(MarkerState.class)) {
            final MarkerState val10 = ro.getMarkerState();
            f.set(o, val10);
        }
        else if (f.getType().equals(String.class)) {
            final int len = fsd.stringWidth();
            String val11 = null;
            switch (len) {
                case 8: {
                    val11 = ro.getDataString8();
                    break;
                }
                case 32: {
                    val11 = ro.getDataString32();
                    break;
                }
                case 64: {
                    val11 = ro.getDataString64();
                    break;
                }
                case 128: {
                    val11 = ro.getDataString128();
                    break;
                }
                case 256: {
                    val11 = ro.getDataString256();
                    break;
                }
                case 260: {
                    val11 = ro.getDataString260();
                    break;
                }
            }
            f.set(o, val11);
        }
        f.setAccessible(accStatus);
    }
    
    private int classDataSize(final Class<?> cl) {
        int len = 0;
        Field[] declaredFields;
        for (int length = (declaredFields = cl.getDeclaredFields()).length, i = 0; i < length; ++i) {
            final Field f = declaredFields[i];
            if (f.isAnnotationPresent(FlightSimData.class)) {
                len += this.fieldSize(f);
            }
        }
        return len;
    }
    
    private int fieldSize(final Field f) {
        final FlightSimData fsd = f.getAnnotation(FlightSimData.class);
        if (f.getType().equals(Float.class) || f.getType().equals(Float.TYPE) || f.getType().equals(Integer.class) || f.getType().equals(Integer.TYPE) || f.getType().equals(Boolean.class) || f.getType().equals(Boolean.TYPE) || f.getType().equals(Short.class) || f.getType().equals(Short.TYPE)) {
            return 4;
        }
        if (f.getType().equals(Double.class) || f.getType().equals(Double.TYPE) || f.getType().equals(Long.class) || f.getType().equals(Long.TYPE)) {
            return 8;
        }
        if (f.getType().equals(XYZ.class)) {
            return DataType.XYZ.size();
        }
        if (f.getType().equals(MarkerState.class)) {
            return DataType.MARKERSTATE.size();
        }
        if (f.getType().equals(Waypoint.class)) {
            return DataType.WAYPOINT.size();
        }
        if (f.getType().equals(LatLonAlt.class)) {
            return DataType.LATLONALT.size();
        }
        if (f.getType().equals(String.class)) {
            final int len = fsd.stringWidth();
            return len;
        }
        return 0;
    }
    
    private void wrap(final Object o, final DataWrapper dw) throws IllegalDataDefinition {
        final Class<?> cl = o.getClass();
        if (!this.definitionIds.containsKey(cl)) {
            throw new IllegalDataDefinition("Class not defined in this wrapper.");
        }
        Field[] declaredFields;
        for (int length = (declaredFields = cl.getDeclaredFields()).length, i = 0; i < length; ++i) {
            final Field f = declaredFields[i];
            if (f.isAnnotationPresent(FlightSimData.class)) {
                try {
                    this.wrapField(o, f, dw);
                }
                catch (IllegalAccessException ex) {}
            }
        }
    }
    
    private void wrapField(final Object o, final Field f, final DataWrapper dw) throws IllegalDataDefinition, IllegalAccessException {
        final FlightSimData fsd = f.getAnnotation(FlightSimData.class);
        final boolean accStatus = f.isAccessible();
        f.setAccessible(true);
        if (f.getType().equals(Float.class) || f.getType().equals(Float.TYPE)) {
            final Float val = (Float)f.get(o);
            dw.putFloat32(val);
        }
        else if (f.getType().equals(Double.class) || f.getType().equals(Double.TYPE)) {
            final Double val2 = (Double)f.get(o);
            dw.putFloat64(val2);
        }
        else if (f.getType().equals(Integer.class) || f.getType().equals(Integer.TYPE)) {
            final Integer val3 = (Integer)f.get(o);
            dw.putInt32(val3);
        }
        else if (f.getType().equals(Long.class) || f.getType().equals(Long.TYPE)) {
            final Long val4 = (Long)f.get(o);
            dw.putInt64(val4);
        }
        else if (f.getType().equals(Boolean.class) || f.getType().equals(Boolean.TYPE)) {
            final Boolean val5 = (Boolean)f.get(o);
            dw.putInt32(((boolean)val5) ? 1 : 0);
        }
        else if (f.getType().equals(Short.class) || f.getType().equals(Short.TYPE)) {
            final Short val6 = (Short)f.get(o);
            dw.putInt32(val6);
        }
        else if (f.getType().equals(LatLonAlt.class)) {
            final LatLonAlt val7 = (LatLonAlt)f.get(o);
            dw.putData(val7);
        }
        else if (f.getType().equals(XYZ.class)) {
            final XYZ val8 = (XYZ)f.get(o);
            dw.putData(val8);
        }
        else if (f.getType().equals(Waypoint.class)) {
            final Waypoint val9 = (Waypoint)f.get(o);
            dw.putData(val9);
        }
        else if (f.getType().equals(MarkerState.class)) {
            final MarkerState val10 = (MarkerState)f.get(o);
            dw.putData(val10);
        }
        else if (f.getType().equals(String.class)) {
            String s = (String)f.get(o);
            if (s == null) {
                s = "";
            }
            final int len = fsd.stringWidth();
            dw.putString(s, len);
        }
        f.setAccessible(accStatus);
    }
}
