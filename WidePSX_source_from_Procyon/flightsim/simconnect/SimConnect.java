// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect;

import flightsim.simconnect.data.InitPosition;
import flightsim.simconnect.wrappers.DataWrapper;
import flightsim.simconnect.data.SimConnectData;
import java.nio.ByteOrder;
import java.net.SocketAddress;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import flightsim.simconnect.config.ConfigurationNotFoundException;
import flightsim.simconnect.config.ConfigurationManager;
import flightsim.simconnect.config.Configuration;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SimConnect implements SimConnectConstants
{
    private SocketChannel sc;
    private ByteBuffer readBuffer;
    private ByteBuffer writeBuffer;
    private final String appName;
    private final int bufferSize;
    private final InetSocketAddress remoteAddress;
    private final int ourProtocol;
    private int currentIndex;
    private int packetsReceived;
    private int packetsSent;
    private int bytesReceived;
    private int bytesSent;
    private static final int SIMCONNECT_BUILD_SP0 = 60905;
    private static final int SIMCONNECT_BUILD_SP1 = 61355;
    private static final int SIMCONNECT_BUILD_SP2_XPACK = 61259;
    
    public SimConnect(final String appName) throws IOException {
        this(appName, makeAutoConfiguration());
    }
    
    private static Configuration makeAutoConfiguration() {
        Configuration cfg = null;
        try {
            cfg = ConfigurationManager.getConfiguration(0);
        }
        catch (ConfigurationNotFoundException cnfe) {
            cfg = new Configuration();
        }
        int port = cfg.getInt("Port", -1);
        if (port == -1) {
            port = Configuration.findSimConnectPortIPv4();
            if (port <= 0) {
                port = Configuration.findSimConnectPortIPv6();
                cfg.setProtocol(6);
            }
            else {
                cfg.setProtocol(4);
            }
            cfg.setPort(port);
        }
        final String host = cfg.get("Address", null);
        if (host == null) {
            if (cfg.getInt("Protocol", 4) == 6) {
                cfg.setAddress("::1");
            }
            else {
                cfg.setAddress("localhost");
            }
        }
        return cfg;
    }
    
    public SimConnect(final String appName, final Configuration config, final int simConnectProtocol) throws IOException {
        this.currentIndex = 1;
        this.packetsReceived = 0;
        this.packetsSent = 0;
        this.bytesReceived = 0;
        this.bytesSent = 0;
        this.appName = appName;
        InetAddress inAddr = null;
        final String host = config.get("Address", "localhost");
        final InetAddress[] addrs = InetAddress.getAllByName(host);
        final String proto = config.get("Protocol", "");
        InetAddress[] array;
        for (int length = (array = addrs).length, i = 0; i < length; ++i) {
            final InetAddress in = array[i];
            if ("IPv6".equalsIgnoreCase(proto) && in instanceof Inet6Address) {
                inAddr = in;
            }
            if (!"IPv6".equalsIgnoreCase(proto) && in instanceof Inet4Address) {
                inAddr = in;
            }
        }
        if (inAddr == null && addrs.length > 0) {
            inAddr = addrs[0];
        }
        if (inAddr == null) {
            throw new IOException(Messages.get("SimConnect.Unknown_host"));
        }
        final int port = config.getInt("Port", 8002);
        this.remoteAddress = new InetSocketAddress(host, port);
        this.bufferSize = config.getInt("MaxReceiveSize", 65536);
        this.ourProtocol = simConnectProtocol;
        if (this.ourProtocol != 2 && this.ourProtocol != 3 && this.ourProtocol != 4) {
            throw new IllegalArgumentException(Messages.getString("SimConnect.VersionMismatch"));
        }
        this.openNetworkConnection();
        if (config.getBoolean("DisableNagle", false)) {
            this.sc.socket().setTcpNoDelay(false);
        }
        this.open();
    }
    
    public SimConnect(final String appName, final Configuration config) throws IOException {
        this(appName, config, config.getInt("SimConnect", 4));
    }
    
    public SimConnect(final String appName, final String host, final int port, final int simConnectProtocol) throws IOException {
        this(appName, buildConfiguration(host, port), simConnectProtocol);
    }
    
    public SimConnect(final String appName, final String host, final int port) throws IOException {
        this(appName, buildConfiguration(host, port), 4);
        this.sc.socket().setTcpNoDelay(true);
    }
    
    public SimConnect(final String appName, final int configNumber, final int simConnectProtocol) throws IOException, ConfigurationNotFoundException {
        this(appName, ConfigurationManager.getConfiguration(configNumber), simConnectProtocol);
    }
    
    public SimConnect(final String appName, final int configNumber) throws IOException, ConfigurationNotFoundException {
        this(appName, ConfigurationManager.getConfiguration(configNumber), 4);
    }
    
    private static Configuration buildConfiguration(final String host, final int port) {
        final Configuration c = new Configuration();
        c.put("Address", host);
        c.put("Port", Integer.toString(port));
        return c;
    }
    
    private void openNetworkConnection() throws IOException {
        this.sc = SocketChannel.open(this.remoteAddress);
        (this.readBuffer = ByteBuffer.allocateDirect(65536)).order(ByteOrder.LITTLE_ENDIAN);
        (this.writeBuffer = ByteBuffer.allocateDirect(65536)).order(ByteOrder.LITTLE_ENDIAN);
    }
    
    public void close() throws IOException {
        this.sc.close();
    }
    
    public boolean isClosed() {
        return !this.sc.isConnected();
    }
    
    public SocketAddress remoteAddress() {
        return this.sc.socket().getRemoteSocketAddress();
    }
    
    public SocketAddress localAddress() {
        return this.sc.socket().getLocalSocketAddress();
    }
    
    private synchronized void sendPacket(final int type) throws IOException {
        final int packetSize = this.writeBuffer.position();
        this.writeBuffer.putInt(0, packetSize);
        this.writeBuffer.putInt(4, this.ourProtocol);
        this.writeBuffer.putInt(8, 0xF0000000 | type);
        this.writeBuffer.putInt(12, this.currentIndex++);
        this.writeBuffer.flip();
        this.sc.write(this.writeBuffer);
        ++this.packetsSent;
        this.bytesSent += packetSize;
    }
    
    private void clean(final ByteBuffer bf) {
        bf.clear();
        this.writeBuffer.position(16);
    }
    
    private void putString(final ByteBuffer bf, String s, final int fixed) {
        if (s == null) {
            s = "";
        }
        final byte[] b = s.getBytes();
        bf.put(b, 0, Math.min(b.length, fixed));
        if (b.length < fixed) {
            for (int i = 0; i < fixed - b.length; ++i) {
                bf.put((byte)0);
            }
        }
    }
    
    private synchronized void open() throws IOException {
        this.clean(this.writeBuffer);
        this.putString(this.writeBuffer, this.appName, 256);
        this.writeBuffer.putInt(0);
        this.writeBuffer.put((byte)0);
        this.writeBuffer.put((byte)88);
        this.writeBuffer.put((byte)83);
        this.writeBuffer.put((byte)70);
        if (this.ourProtocol == 2) {
            this.writeBuffer.putInt(0);
            this.writeBuffer.putInt(0);
            this.writeBuffer.putInt(60905);
            this.writeBuffer.putInt(0);
        }
        else if (this.ourProtocol == 3) {
            this.writeBuffer.putInt(10);
            this.writeBuffer.putInt(0);
            this.writeBuffer.putInt(61355);
            this.writeBuffer.putInt(0);
        }
        else {
            if (this.ourProtocol != 4) {
                throw new IllegalArgumentException(Messages.getString("SimConnect.InvalidProtocol"));
            }
            this.writeBuffer.putInt(10);
            this.writeBuffer.putInt(0);
            this.writeBuffer.putInt(61259);
            this.writeBuffer.putInt(0);
        }
        this.sendPacket(1);
    }
    
    public synchronized void addToDataDefinition(final int dataDefinitionId, final String datumName, final String unitsName, final SimConnectDataType dataType) throws IOException {
        this.addToDataDefinition(dataDefinitionId, datumName, unitsName, dataType, 0.0f, -1);
    }
    
    public synchronized void addToDataDefinition(final Enum dataDefinitionId, final String datumName, final String unitsName, final SimConnectDataType dataType) throws IOException {
        this.addToDataDefinition(dataDefinitionId.ordinal(), datumName, unitsName, dataType);
    }
    
    public synchronized void addToDataDefinition(final int dataDefinitionId, final String datumName, final String unitsName, final SimConnectDataType dataType, final float epsilon, final int datumId) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(dataDefinitionId);
        this.putString(this.writeBuffer, datumName, 256);
        this.putString(this.writeBuffer, unitsName, 256);
        this.writeBuffer.putInt(dataType.ordinal());
        this.writeBuffer.putFloat(epsilon);
        this.writeBuffer.putInt(datumId);
        this.sendPacket(12);
    }
    
    public synchronized void addToDataDefinition(final Enum dataDefinitionId, final String datumName, final String unitsName, final SimConnectDataType dataType, final float epsilon, final int datumId) throws IOException {
        this.addToDataDefinition(dataDefinitionId.ordinal(), datumName, unitsName, dataType, epsilon, datumId);
    }
    
    public synchronized void requestDataOnSimObject(final int dataRequestId, final int dataDefinitionId, final int objectId, final SimConnectPeriod period) throws IOException {
        this.requestDataOnSimObject(dataRequestId, dataDefinitionId, objectId, period, 0, 0, 0, 0);
    }
    
    public synchronized void requestDataOnSimObject(final Enum dataRequestId, final Enum dataDefinitionId, final int objectId, final SimConnectPeriod period) throws IOException {
        this.requestDataOnSimObject(dataRequestId.ordinal(), dataDefinitionId.ordinal(), objectId, period);
    }
    
    public synchronized void requestDataOnSimObject(final int dataRequestId, final int dataDefinitionId, final int objectId, final SimConnectPeriod period, final int flags, final int origin, final int interval, final int limit) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(dataRequestId);
        this.writeBuffer.putInt(dataDefinitionId);
        this.writeBuffer.putInt(objectId);
        this.writeBuffer.putInt(period.ordinal());
        this.writeBuffer.putInt(flags);
        this.writeBuffer.putInt(origin);
        this.writeBuffer.putInt(interval);
        this.writeBuffer.putInt(limit);
        this.sendPacket(14);
    }
    
    public synchronized void requestDataOnSimObject(final Enum dataRequestId, final Enum dataDefinitionId, final int objectId, final SimConnectPeriod period, final int flags, final int origin, final int interval, final int limit) throws IOException {
        this.requestDataOnSimObject(dataRequestId.ordinal(), dataDefinitionId.ordinal(), objectId, period, flags, origin, interval, limit);
    }
    
    public synchronized void clearDataDefinition(final int dataDefinitionId) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(dataDefinitionId);
        this.sendPacket(13);
    }
    
    public synchronized void clearDataDefinition(final Enum dataDefinitionId) throws IOException {
        this.clearDataDefinition(dataDefinitionId.ordinal());
    }
    
    public synchronized void requestDataOnSimObjectType(final int dataRequestId, final int dataDefinitionId, final int radiusMeters, final SimObjectType type) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(dataRequestId);
        this.writeBuffer.putInt(dataDefinitionId);
        this.writeBuffer.putInt(radiusMeters);
        this.writeBuffer.putInt(type.ordinal());
        this.sendPacket(15);
    }
    
    public synchronized void requestDataOnSimObjectType(final Enum dataRequestId, final Enum dataDefinitionId, final int radiusMeters, final SimObjectType type) throws IOException {
        this.requestDataOnSimObjectType(dataRequestId.ordinal(), dataDefinitionId.ordinal(), radiusMeters, type);
    }
    
    public synchronized void subscribeToSystemEvent(final int clientEventID, final String eventName) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(clientEventID);
        this.putString(this.writeBuffer, eventName, 256);
        this.sendPacket(23);
    }
    
    public synchronized void subscribeToSystemEvent(final Enum clientEventID, final String eventName) throws IOException {
        this.subscribeToSystemEvent(clientEventID.ordinal(), eventName);
    }
    
    public synchronized void unsubscribeFromSystemEvent(final int clientEventID) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(clientEventID);
        this.sendPacket(24);
    }
    
    public synchronized void unsubscribeFromSystemEvent(final Enum clientEventID) throws IOException {
        this.unsubscribeFromSystemEvent(clientEventID.ordinal());
    }
    
    public synchronized void requestSystemState(final int dataRequestID, final String state) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(dataRequestID);
        this.putString(this.writeBuffer, state, 256);
        this.sendPacket(53);
    }
    
    public synchronized void requestSystemState(final Enum dataRequestID, final String state) throws IOException {
        this.requestSystemState(dataRequestID.ordinal(), state);
    }
    
    public synchronized void setSystemState(final String state, final int paramInt, final float paramFloat, final String paramString) throws IOException {
        this.clean(this.writeBuffer);
        this.putString(this.writeBuffer, state, 256);
        this.writeBuffer.putInt(paramInt);
        this.writeBuffer.putFloat(paramFloat);
        this.putString(this.writeBuffer, paramString, 256);
        this.writeBuffer.putInt(0);
        this.sendPacket(54);
    }
    
    public synchronized void addClientEventToNotificationGroup(final int notificationGroupID, final int clientEventID) throws IOException {
        this.addClientEventToNotificationGroup(notificationGroupID, clientEventID, false);
    }
    
    public synchronized void addClientEventToNotificationGroup(final Enum notificationGroupID, final Enum clientEventID) throws IOException {
        this.addClientEventToNotificationGroup(notificationGroupID.ordinal(), clientEventID.ordinal(), false);
    }
    
    public synchronized void addClientEventToNotificationGroup(final int notificationGroupID, final int clientEventID, final boolean maskable) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(notificationGroupID);
        this.writeBuffer.putInt(clientEventID);
        this.writeBuffer.putInt(maskable ? 1 : 0);
        this.sendPacket(7);
    }
    
    public synchronized void addClientEventToNotificationGroup(final Enum notificationGroupID, final Enum clientEventID, final boolean maskable) throws IOException {
        this.addClientEventToNotificationGroup(notificationGroupID.ordinal(), clientEventID.ordinal(), maskable);
    }
    
    public synchronized void mapClientEventToSimEvent(final int clientEventId) throws IOException {
        this.mapClientEventToSimEvent(clientEventId, "");
    }
    
    public synchronized void mapClientEventToSimEvent(final Enum clientEventId) throws IOException {
        this.mapClientEventToSimEvent(clientEventId.ordinal(), "");
    }
    
    public synchronized void mapClientEventToSimEvent(final int clientEventId, final String eventName) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(clientEventId);
        this.putString(this.writeBuffer, eventName, 256);
        this.sendPacket(4);
    }
    
    public synchronized void mapClientEventToSimEvent(final Enum clientEventId, final String eventName) throws IOException {
        this.mapClientEventToSimEvent(clientEventId.ordinal(), eventName);
    }
    
    public synchronized void transmitClientEvent(final int objectID, final int eventID, final int data, final int groupID, final int flags) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(objectID);
        this.writeBuffer.putInt(eventID);
        this.writeBuffer.putInt(data);
        this.writeBuffer.putInt(groupID);
        this.writeBuffer.putInt(flags);
        this.sendPacket(5);
    }
    
    public synchronized void transmitClientEvent(final int objectID, final Enum eventID, final int data, final Enum groupID, final int flags) throws IOException {
        this.transmitClientEvent(objectID, eventID.ordinal(), data, groupID.ordinal(), flags);
    }
    
    public synchronized void setSystemEventState(final int eventID, final boolean state) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(eventID);
        this.writeBuffer.putInt(state ? 1 : 0);
        this.sendPacket(6);
    }
    
    public synchronized void setSystemEventState(final Enum eventID, final boolean state) throws IOException {
        this.setSystemEventState(eventID.ordinal(), state);
    }
    
    public synchronized void removeClientEvent(final int groupID, final int eventID) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(eventID);
        this.writeBuffer.putInt(eventID);
        this.sendPacket(8);
    }
    
    public synchronized void removeClientEvent(final Enum groupID, final Enum eventID) throws IOException {
        this.removeClientEvent(groupID.ordinal(), eventID.ordinal());
    }
    
    public synchronized void setNotificationGroupPriority(final int groupID, final NotificationPriority priority) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(groupID);
        this.writeBuffer.putInt(priority.value());
        this.sendPacket(9);
    }
    
    public synchronized void setNotificationGroupPriority(final Enum groupID, final NotificationPriority priority) throws IOException {
        this.setNotificationGroupPriority(groupID.ordinal(), priority);
    }
    
    public synchronized void clearNotificationGroup(final int groupID) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(groupID);
        this.sendPacket(10);
    }
    
    public synchronized void requestNotificationGroup(final int groupID) throws IOException {
        this.requestNotificationGroup(groupID, 0, 0);
    }
    
    public synchronized void requestNotificationGroup(final int groupID, final int reserved, final int flags) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(groupID);
        this.writeBuffer.putInt(reserved);
        this.writeBuffer.putInt(flags);
        this.sendPacket(11);
    }
    
    public synchronized void setDataOnSimObject(final int dataDefinitionID, final int objectID, final boolean tagged, int arrayCount, final byte[] data) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(dataDefinitionID);
        this.writeBuffer.putInt(objectID);
        this.writeBuffer.putInt(tagged ? 1 : 0);
        if (arrayCount == 0) {
            arrayCount = 1;
        }
        this.writeBuffer.putInt(arrayCount);
        this.writeBuffer.putInt(data.length);
        this.writeBuffer.put(data);
        this.sendPacket(16);
    }
    
    public synchronized void setDataOnSimObject(final Enum dataDefinitionID, final int objectID, final boolean tagged, final int arrayCount, final byte[] data) throws IOException {
        this.setDataOnSimObject(dataDefinitionID.ordinal(), objectID, tagged, arrayCount, data);
    }
    
    public synchronized void setDataOnSimObject(final int dataDefinitionID, final int objectID, final boolean tagged, int arrayCount, final ByteBuffer data) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(dataDefinitionID);
        this.writeBuffer.putInt(objectID);
        this.writeBuffer.putInt(tagged ? 1 : 0);
        if (arrayCount == 0) {
            arrayCount = 1;
        }
        this.writeBuffer.putInt(arrayCount);
        this.writeBuffer.putInt(data.remaining());
        this.writeBuffer.put(data);
        this.sendPacket(16);
    }
    
    public synchronized void setDataOnSimObject(final int dataDefinitionID, final int objectID, final SimConnectData[] data) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(dataDefinitionID);
        this.writeBuffer.putInt(objectID);
        this.writeBuffer.putInt(0);
        this.writeBuffer.putInt(data.length);
        this.writeBuffer.putInt(0);
        for (final SimConnectData sd : data) {
            sd.write(this.writeBuffer);
        }
        this.writeBuffer.putInt(32, this.writeBuffer.position() - 36);
        this.sendPacket(16);
    }
    
    public synchronized void setDataOnSimObject(final Enum dataDefinitionID, final int objectID, final double... data) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(dataDefinitionID.ordinal());
        this.writeBuffer.putInt(objectID);
        this.writeBuffer.putInt(0);
        this.writeBuffer.putInt(1);
        this.writeBuffer.putInt(8 * data.length);
        for (final double d : data) {
            this.writeBuffer.putDouble(d);
        }
        this.sendPacket(16);
    }
    
    public synchronized void setDataOnSimObject(final int dataDefinitionID, final int objectID, final double... data) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(dataDefinitionID);
        this.writeBuffer.putInt(objectID);
        this.writeBuffer.putInt(0);
        this.writeBuffer.putInt(1);
        this.writeBuffer.putInt(8 * data.length);
        for (final double d : data) {
            this.writeBuffer.putDouble(d);
        }
        this.sendPacket(16);
    }
    
    public synchronized void setDataOnSimObject(final Enum dataDefinitionID, final int objectID, final SimConnectData[] data) throws IOException {
        this.setDataOnSimObject(dataDefinitionID.ordinal(), objectID, data);
    }
    
    public synchronized void setDataOnSimObject(final int dataDefinitionID, final int objectID, final boolean tagged, final int arrayCount, final DataWrapper data) throws IOException {
        this.setDataOnSimObject(dataDefinitionID, objectID, tagged, arrayCount, data.getBuffer());
    }
    
    public synchronized void setDataOnSimObject(final Enum dataDefinitionID, final int objectID, final boolean tagged, final int arrayCount, final DataWrapper data) throws IOException {
        this.setDataOnSimObject(dataDefinitionID.ordinal(), objectID, tagged, arrayCount, data.getBuffer());
    }
    
    public synchronized void mapInputEventToClientEvent(final int inputGroupID, final String inputDefinition, final int clientEventDownID) throws IOException {
        this.mapInputEventToClientEvent(inputGroupID, inputDefinition, clientEventDownID, 0, -1, 0, false);
    }
    
    public synchronized void mapInputEventToClientEvent(final Enum inputGroupID, final String inputDefinition, final Enum clientEventDownID) throws IOException {
        this.mapInputEventToClientEvent(inputGroupID.ordinal(), inputDefinition, clientEventDownID.ordinal(), 0, -1, 0, false);
    }
    
    public synchronized void mapInputEventToClientEvent(final int inputGroupID, final String inputDefinition, final int clientEventDownID, final int downValue, final int clientEventUpID, final int UpValue, final boolean maskable) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(inputGroupID);
        this.putString(this.writeBuffer, inputDefinition, 256);
        this.writeBuffer.putInt(clientEventDownID);
        this.writeBuffer.putInt(downValue);
        this.writeBuffer.putInt(clientEventUpID);
        this.writeBuffer.putInt(UpValue);
        this.writeBuffer.putInt(maskable ? 1 : 0);
        this.sendPacket(17);
    }
    
    public synchronized void mapInputEventToClientEvent(final Enum inputGroupID, final String inputDefinition, final Enum clientEventDownID, final int downValue, final Enum clientEventUpID, final int UpValue, final boolean maskable) throws IOException {
        this.mapInputEventToClientEvent(inputGroupID.ordinal(), inputDefinition, clientEventDownID.ordinal(), downValue, clientEventUpID.ordinal(), UpValue, maskable);
    }
    
    public synchronized void setInputGroupPriority(final int inputGroupID, final NotificationPriority priority) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(inputGroupID);
        this.writeBuffer.putInt(priority.value());
        this.sendPacket(18);
    }
    
    public synchronized void setInputGroupPriority(final Enum inputGroupID, final NotificationPriority priority) throws IOException {
        this.setInputGroupPriority(inputGroupID.ordinal(), priority);
    }
    
    public synchronized void removeInputEvent(final int inputGroupID, final String inputDefinition) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(inputGroupID);
        this.putString(this.writeBuffer, inputDefinition, 256);
        this.sendPacket(19);
    }
    
    public synchronized void removeInputEvent(final Enum inputGroupID, final String inputDefinition) throws IOException {
        this.removeInputEvent(inputGroupID.ordinal(), inputDefinition);
    }
    
    public synchronized void clearInputGroup(final int inputGroupID) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(inputGroupID);
        this.sendPacket(20);
    }
    
    public synchronized void clearInputGroup(final Enum inputGroupID) throws IOException {
        this.clearInputGroup(inputGroupID.ordinal());
    }
    
    public synchronized void setInputGroupState(final int inputGroupID, final boolean state) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(inputGroupID);
        this.writeBuffer.putInt(state ? 1 : 0);
        this.sendPacket(21);
    }
    
    public synchronized void setInputGroupState(final Enum inputGroupID, final boolean state) throws IOException {
        this.setInputGroupState(inputGroupID.ordinal(), state);
    }
    
    public synchronized void requestReservedKey(final int eventID, final String keyChoice1) throws IOException {
        this.requestReservedKey(eventID, keyChoice1, "", "");
    }
    
    public synchronized void requestReservedKey(final int eventID, final String keyChoice1, final String keyChoice2) throws IOException {
        this.requestReservedKey(eventID, keyChoice1, keyChoice2, "");
    }
    
    public synchronized void requestReservedKey(final int eventID, final String keyChoice1, final String keyChoice2, final String keyChoice3) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(eventID);
        this.putString(this.writeBuffer, (keyChoice1 == null) ? "" : keyChoice1, 30);
        this.putString(this.writeBuffer, (keyChoice2 == null) ? "" : keyChoice2, 30);
        this.putString(this.writeBuffer, (keyChoice3 == null) ? "" : keyChoice3, 30);
        this.sendPacket(22);
    }
    
    public synchronized void weatherRequestInterpolatedObservation(final int dataRequestID, final float lat, final float lon, final float alt) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(dataRequestID);
        this.writeBuffer.putFloat(lat);
        this.writeBuffer.putFloat(lon);
        this.writeBuffer.putFloat(alt);
        this.sendPacket(25);
    }
    
    public synchronized void weatherRequestObservationAtStation(final int dataRequestID, final String ICAO) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(dataRequestID);
        this.putString(this.writeBuffer, ICAO, 5);
        this.sendPacket(26);
    }
    
    public synchronized void weatherRequestObservationAtNearestStation(final int dataRequestID, final float lat, final float lon) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(dataRequestID);
        this.writeBuffer.putFloat(lat);
        this.writeBuffer.putFloat(lon);
        this.sendPacket(27);
    }
    
    public synchronized void weatherRequestObservationAtNearestStation(final Enum dataRequestID, final float lat, final float lon) throws IOException {
        this.weatherRequestObservationAtNearestStation(dataRequestID.ordinal(), lat, lon);
    }
    
    public synchronized void weatherCreateStation(final int dataRequestID, final String ICAO, final String name, final float lat, final float lon, final float alt) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(dataRequestID);
        this.putString(this.writeBuffer, ICAO, 5);
        this.putString(this.writeBuffer, name, 256);
        this.writeBuffer.putFloat(lat);
        this.writeBuffer.putFloat(lon);
        this.writeBuffer.putFloat(alt);
        this.sendPacket(28);
    }
    
    public synchronized void weatherRemoveStation(final int dataRequestID, final String ICAO) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(dataRequestID);
        this.putString(this.writeBuffer, ICAO, 5);
        this.sendPacket(29);
    }
    
    public synchronized void weatherSetObservation(final int seconds, final String metar) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(seconds);
        final byte[] metarData = metar.getBytes();
        this.writeBuffer.put(metarData);
        this.writeBuffer.put((byte)0);
        this.sendPacket(30);
    }
    
    public synchronized void weatherSetModeServer(final int port, final int seconds) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(port);
        this.writeBuffer.putInt(seconds);
        this.sendPacket(31);
    }
    
    public synchronized void weatherSetModeTheme(final String themeName) throws IOException {
        this.clean(this.writeBuffer);
        this.putString(this.writeBuffer, themeName, 256);
        this.sendPacket(32);
    }
    
    public synchronized void weatherSetModeGlobal() throws IOException {
        this.clean(this.writeBuffer);
        this.sendPacket(33);
    }
    
    public synchronized void weatherSetModeCustom() throws IOException {
        this.clean(this.writeBuffer);
        this.sendPacket(34);
    }
    
    public synchronized void weatherSetDynamicUpdateRate(final int rate) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(rate);
        this.sendPacket(35);
    }
    
    public synchronized void weatherRequestCloudState(final int dataRequestID, final float minLat, final float minLon, final float minAlt, final float maxLat, final float maxLon, final float maxAlt, final int flags) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(dataRequestID);
        this.writeBuffer.putFloat(minLat);
        this.writeBuffer.putFloat(minLon);
        this.writeBuffer.putFloat(minAlt);
        this.writeBuffer.putFloat(maxLat);
        this.writeBuffer.putFloat(maxLon);
        this.writeBuffer.putFloat(maxAlt);
        this.writeBuffer.putInt(flags);
        this.sendPacket(36);
    }
    
    public synchronized void weatherRequestCloudState(final int dataRequestID, final float minLat, final float minLon, final float minAlt, final float maxLat, final float maxLon, final float maxAlt) throws IOException {
        this.weatherRequestCloudState(dataRequestID, minLat, minLon, minAlt, maxLat, maxLon, maxAlt, 0);
    }
    
    public synchronized void weatherCreateThermal(final int dataRequestID, final float lat, final float lon, final float alt, final float radius, final float height) throws IOException {
        this.weatherCreateThermal(dataRequestID, lat, lon, alt, radius, height, 3.0f, 0.05f, 3.0f, 0.2f, 0.4f, 0.1f, 0.4f, 0.1f);
    }
    
    public synchronized void weatherCreateThermal(final int dataRequestID, final float lat, final float lon, final float alt, final float radius, final float height, final float coreRate, final float coreTurbulence, final float sinkRate, final float sinkTurbulence, final float coreSize, final float coreTransitionSize, final float sinkLayerSize, final float sinkTransitionSize) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(dataRequestID);
        this.writeBuffer.putFloat(lat);
        this.writeBuffer.putFloat(lon);
        this.writeBuffer.putFloat(alt);
        this.writeBuffer.putFloat(radius);
        this.writeBuffer.putFloat(height);
        this.writeBuffer.putFloat(coreRate);
        this.writeBuffer.putFloat(coreTurbulence);
        this.writeBuffer.putFloat(sinkRate);
        this.writeBuffer.putFloat(sinkTurbulence);
        this.writeBuffer.putFloat(coreSize);
        this.writeBuffer.putFloat(coreTransitionSize);
        this.writeBuffer.putFloat(sinkLayerSize);
        this.writeBuffer.putFloat(sinkTransitionSize);
        this.sendPacket(37);
    }
    
    public synchronized void weatherRemoveThermal(final int objectID) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(objectID);
        this.sendPacket(38);
    }
    
    public synchronized void aICreateParkedATCAircraft(final String containerTitle, final String tailNumber, final String airportID, final int dataRequestID) throws IOException {
        this.clean(this.writeBuffer);
        this.putString(this.writeBuffer, containerTitle, 256);
        this.putString(this.writeBuffer, tailNumber, 12);
        this.putString(this.writeBuffer, airportID, 5);
        this.writeBuffer.putInt(dataRequestID);
        this.sendPacket(39);
    }
    
    public synchronized void aICreateEnrouteATCAircraft(final String containerTitle, final String tailNumber, final int flightNumber, final String flightPlanPath, final double flightPlanPosition, final boolean touchAndGo, final int dataRequestID) throws IOException {
        this.clean(this.writeBuffer);
        this.putString(this.writeBuffer, containerTitle, 256);
        this.putString(this.writeBuffer, tailNumber, 12);
        this.writeBuffer.putInt(flightNumber);
        this.putString(this.writeBuffer, flightPlanPath, 260);
        this.writeBuffer.putDouble(flightPlanPosition);
        this.writeBuffer.putInt(touchAndGo ? 1 : 0);
        this.writeBuffer.putInt(dataRequestID);
        this.sendPacket(40);
    }
    
    public synchronized void aICreateNonATCAircraft(final String containerTitle, final String tailNumber, final InitPosition initPos, final int dataRequestID) throws IOException {
        this.clean(this.writeBuffer);
        this.putString(this.writeBuffer, containerTitle, 256);
        this.putString(this.writeBuffer, tailNumber, 12);
        initPos.write(this.writeBuffer);
        this.writeBuffer.putInt(dataRequestID);
        this.sendPacket(41);
    }
    
    public synchronized void aICreateNonATCAircraft(final String containerTitle, final String tailNumber, final InitPosition initPos, final Enum dataRequestID) throws IOException {
        this.aICreateNonATCAircraft(containerTitle, tailNumber, initPos, dataRequestID.ordinal());
    }
    
    public synchronized void aICreateSimulatedObject(final String containerTitle, final InitPosition initPos, final int dataRequestID) throws IOException {
        this.clean(this.writeBuffer);
        this.putString(this.writeBuffer, containerTitle, 256);
        initPos.write(this.writeBuffer);
        this.writeBuffer.putInt(dataRequestID);
        this.sendPacket(42);
    }
    
    public synchronized void aICreateSimulatedObject(final String containerTitle, final InitPosition initPos, final Enum dataRequestID) throws IOException {
        this.aICreateSimulatedObject(containerTitle, initPos, dataRequestID.ordinal());
    }
    
    public synchronized void aIReleaseControl(final int objectID, final int dataRequestID) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(objectID);
        this.writeBuffer.putInt(dataRequestID);
        this.sendPacket(43);
    }
    
    public synchronized void aIReleaseControl(final int objectID, final Enum dataRequestID) throws IOException {
        this.aIReleaseControl(objectID, dataRequestID.ordinal());
    }
    
    public synchronized void aIRemoveObject(final int objectID, final int dataRequestID) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(objectID);
        this.writeBuffer.putInt(dataRequestID);
        this.sendPacket(44);
    }
    
    public synchronized void aIRemoveObject(final int objectID, final Enum dataRequestID) throws IOException {
        this.aIRemoveObject(objectID, dataRequestID.ordinal());
    }
    
    public synchronized void aISetAircraftFlightPlan(final int objectID, final String flightPlanPath, final int dataRequestID) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(objectID);
        this.putString(this.writeBuffer, flightPlanPath, 260);
        this.writeBuffer.putInt(dataRequestID);
        this.sendPacket(45);
    }
    
    public synchronized void executeMissionAction(final byte[] guidInstanceId) throws IOException {
        if (guidInstanceId.length != 16) {
            throw new IllegalArgumentException(Messages.get("SimConnect.GUID_invalid_size"));
        }
        this.clean(this.writeBuffer);
        this.writeBuffer.put(guidInstanceId);
        this.sendPacket(46);
    }
    
    public synchronized void completeCustomMissionAction(final byte[] guidInstanceId) throws IOException {
        if (guidInstanceId.length != 16) {
            throw new IllegalArgumentException(Messages.get("SimConnect.GUID_invalid_size"));
        }
        this.clean(this.writeBuffer);
        this.writeBuffer.put(guidInstanceId);
        this.sendPacket(47);
    }
    
    public synchronized int getLastSentPacketID() {
        return this.currentIndex - 1;
    }
    
    public synchronized float[] requestResponseTimes(final int nCount) throws IOException {
        throw new UnsupportedOperationException(Messages.get("SimConnect.Unimplemented"));
    }
    
    public synchronized void cameraSetRelative6DOF(final float deltaX, final float deltaY, final float deltaZ, final float pitchDeg, final float bankDeg, final float headingDeg) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putFloat(deltaX);
        this.writeBuffer.putFloat(deltaY);
        this.writeBuffer.putFloat(deltaZ);
        this.writeBuffer.putFloat(pitchDeg);
        this.writeBuffer.putFloat(bankDeg);
        this.writeBuffer.putFloat(headingDeg);
        this.sendPacket(48);
    }
    
    public synchronized void menuAddItem(final String menuItem, final int clientMenuEventID, final int data) throws IOException {
        this.clean(this.writeBuffer);
        this.putString(this.writeBuffer, menuItem, 256);
        this.writeBuffer.putInt(clientMenuEventID);
        this.writeBuffer.putInt(data);
        this.sendPacket(49);
    }
    
    public synchronized void menuAddItem(final String menuItem, final Enum clientMenuEventID, final int data) throws IOException {
        this.menuAddItem(menuItem, clientMenuEventID.ordinal(), data);
    }
    
    public synchronized void menuDeleteItem(final int clientMenuEventID) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(clientMenuEventID);
        this.sendPacket(50);
    }
    
    public synchronized void menuDeleteItem(final Enum clientMenuEventID) throws IOException {
        this.menuDeleteItem(clientMenuEventID.ordinal());
    }
    
    public synchronized void menuAddSubItem(final int clientMenuEventID, final String menuItem, final int clientSubMenuEventID, final int data) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(clientMenuEventID);
        this.putString(this.writeBuffer, menuItem, 256);
        this.writeBuffer.putInt(clientSubMenuEventID);
        this.writeBuffer.putInt(data);
        this.sendPacket(51);
    }
    
    public synchronized void menuAddSubItem(final Enum clientMenuEventID, final String menuItem, final Enum clientSubMenuEventID, final int data) throws IOException {
        this.menuAddSubItem(clientMenuEventID.ordinal(), menuItem, clientSubMenuEventID.ordinal(), data);
    }
    
    public synchronized void menuDeleteSubItem(final int clientMenuEventID, final int clientSubMenuEventID) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(clientMenuEventID);
        this.writeBuffer.putInt(clientSubMenuEventID);
        this.sendPacket(52);
    }
    
    public synchronized void menuDeleteSubItem(final Enum clientMenuEventID, final Enum clientSubMenuEventID) throws IOException {
        this.menuDeleteSubItem(clientMenuEventID.ordinal(), clientSubMenuEventID.ordinal());
    }
    
    public synchronized void mapClientDataNameToID(final String clientDataName, final int clientDataID) throws IOException {
        this.clean(this.writeBuffer);
        this.putString(this.writeBuffer, clientDataName, 256);
        this.writeBuffer.putInt(clientDataID);
        this.sendPacket(55);
    }
    
    public synchronized void mapClientDataNameToID(final String clientDataName, final Enum clientDataID) throws IOException {
        this.mapClientDataNameToID(clientDataName, clientDataID.ordinal());
    }
    
    public synchronized void createClientData(final int clientDataID, final int size, final boolean readOnly) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(clientDataID);
        this.writeBuffer.putInt(size);
        this.writeBuffer.putInt(readOnly ? 1 : 0);
        this.sendPacket(56);
    }
    
    public synchronized void createClientData(final Enum clientDataID, final int size, final boolean readOnly) throws IOException {
        this.createClientData(clientDataID.ordinal(), size, readOnly);
    }
    
    public synchronized void addToClientDataDefinition(final int dataDefineID, final int offset, final int size) throws IOException {
        this.addToClientDataDefinition(dataDefineID, offset, size, 0);
    }
    
    public synchronized void addToClientDataDefinition(final int dataDefineID, final int offset, final int size, final int reserved) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(dataDefineID);
        this.writeBuffer.putInt(offset);
        this.writeBuffer.putInt(size);
        this.writeBuffer.putInt(reserved);
        if (this.ourProtocol > 3) {
            this.writeBuffer.putInt(-1);
        }
        this.sendPacket(57);
    }
    
    public synchronized void addToClientDataDefinition(final int dataDefineID, final int offset, final int sizeOrType, final float epsilon, final int datumId) throws IOException {
        if (this.ourProtocol < 3) {
            throw new UnsupportedOperationException(Messages.getString("SimConnect.badversion"));
        }
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(dataDefineID);
        this.writeBuffer.putInt(offset);
        this.writeBuffer.putInt(sizeOrType);
        this.writeBuffer.putFloat(epsilon);
        this.writeBuffer.putInt(datumId);
        this.sendPacket(57);
    }
    
    public synchronized void addToClientDataDefinition(final Enum dataDefineID, final int offset, final int sizeOrType, final float epsilon, final int datumId) throws IOException {
        this.addToClientDataDefinition(dataDefineID.ordinal(), offset, sizeOrType, epsilon, datumId);
    }
    
    public synchronized void addToClientDataDefinition(final Enum dataDefineID, final int sizeOrType) throws IOException {
        this.addToClientDataDefinition(dataDefineID.ordinal(), -1, sizeOrType, 0.0f, 0);
    }
    
    public synchronized void clearClientDataDefinition(final int dataDefineID) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(dataDefineID);
        this.sendPacket(58);
    }
    
    public synchronized void requestClientData(final int clientDataID, final int dataRequestID, final int clientDataDefineID) throws IOException {
        this.requestClientData(clientDataID, dataRequestID, clientDataDefineID, -1, 0);
    }
    
    public synchronized void requestClientData(final int clientDataID, final int dataRequestID, final int clientDataDefineID, final int reserved1, final int reserved2) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(clientDataID);
        this.writeBuffer.putInt(dataRequestID);
        this.writeBuffer.putInt(clientDataDefineID);
        if (this.ourProtocol >= 3) {
            this.writeBuffer.putInt(SimConnectPeriod.ONCE.ordinal());
            this.writeBuffer.putInt(0);
            this.writeBuffer.putInt(0);
            this.writeBuffer.putInt(0);
            this.writeBuffer.putInt(0);
        }
        else {
            this.writeBuffer.putInt(reserved1);
            this.writeBuffer.putInt(reserved2);
        }
        this.sendPacket(59);
    }
    
    public synchronized void requestClientData(final int clientDataID, final int dataRequestID, final int clientDataDefineID, final ClientDataPeriod period, final int flags, final int origin, final int interval, final int limit) throws IOException {
        if (this.ourProtocol < 3) {
            throw new UnsupportedOperationException(Messages.getString("SimConnect.badversion"));
        }
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(clientDataID);
        this.writeBuffer.putInt(dataRequestID);
        this.writeBuffer.putInt(clientDataDefineID);
        this.writeBuffer.putInt(period.ordinal());
        this.writeBuffer.putInt(flags);
        this.writeBuffer.putInt(origin);
        this.writeBuffer.putInt(interval);
        this.writeBuffer.putInt(limit);
        this.sendPacket(59);
    }
    
    public synchronized void requestClientData(final Enum clientDataID, final Enum dataRequestID, final Enum clientDataDefineID, final ClientDataPeriod period, final int flags, final int origin, final int interval, final int limit) throws IOException {
        this.requestClientData(clientDataID.ordinal(), dataRequestID.ordinal(), clientDataDefineID.ordinal(), period, flags, origin, interval, limit);
    }
    
    public synchronized void setClientData(final int clientDataID, final int clientDataDefineID, final int reserved, final int arrayCount, final int unitSize, final byte[] data) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(clientDataID);
        this.writeBuffer.putInt(clientDataDefineID);
        this.writeBuffer.putInt(0);
        this.writeBuffer.putInt(1);
        this.writeBuffer.putInt(unitSize);
        this.writeBuffer.put(data);
        this.sendPacket(60);
    }
    
    public synchronized void setClientData(final int clientDataID, final int clientDataDefineID, final byte[] data) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(clientDataID);
        this.writeBuffer.putInt(clientDataDefineID);
        this.writeBuffer.putInt(0);
        this.writeBuffer.putInt(1);
        this.writeBuffer.putInt(data.length);
        this.writeBuffer.put(data);
        this.sendPacket(60);
    }
    
    public synchronized void setClientData(final int clientDataID, final int clientDataDefineID, final int reserved, final int arrayCount, final int unitSize, final ByteBuffer data) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(clientDataID);
        this.writeBuffer.putInt(clientDataDefineID);
        this.writeBuffer.putInt(0);
        this.writeBuffer.putInt(1);
        this.writeBuffer.putInt(unitSize);
        this.writeBuffer.put(data);
        this.sendPacket(60);
    }
    
    public synchronized void setClientData(final int clientDataID, final int clientDataDefineID, final int reserved, final int arrayCount, final int unitSize, final DataWrapper data) throws IOException {
        this.setClientData(clientDataID, clientDataDefineID, reserved, arrayCount, unitSize, data.getBuffer());
    }
    
    public synchronized void setClientData(final int clientDataID, final int clientDataDefineID, final ByteBuffer data) throws IOException {
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(clientDataID);
        this.writeBuffer.putInt(clientDataDefineID);
        this.writeBuffer.putInt(0);
        this.writeBuffer.putInt(1);
        this.writeBuffer.putInt(data.remaining());
        this.writeBuffer.put(data);
        this.sendPacket(60);
    }
    
    public synchronized void setClientData(final int clientDataID, final int clientDataDefineID, final DataWrapper data) throws IOException {
        this.setClientData(clientDataID, clientDataDefineID, data.getBuffer());
    }
    
    public synchronized void flightLoad(final String fileName) throws IOException {
        this.clean(this.writeBuffer);
        this.putString(this.writeBuffer, fileName, 260);
        this.sendPacket(61);
    }
    
    public synchronized void flightSave(final String fileName, final String description, final int flags) throws IOException {
        if (this.ourProtocol >= 4) {
            this.flightSave(fileName, fileName, description, flags);
        }
        else {
            this.clean(this.writeBuffer);
            this.putString(this.writeBuffer, fileName, 260);
            this.putString(this.writeBuffer, description, 2048);
            this.writeBuffer.putInt(-1);
            this.sendPacket(62);
        }
    }
    
    public synchronized void flightSave(final String fileName, String title, final String description, final int flags) throws IOException, UnsupportedOperationException {
        if (this.ourProtocol < 4) {
            throw new UnsupportedOperationException(Messages.getString("SimConnect.badversion"));
        }
        if (title == null) {
            title = fileName;
        }
        this.clean(this.writeBuffer);
        this.putString(this.writeBuffer, fileName, 260);
        this.putString(this.writeBuffer, title, 260);
        this.putString(this.writeBuffer, description, 2048);
        this.writeBuffer.putInt(-1);
        this.sendPacket(62);
    }
    
    public synchronized void flightPlanLoad(final String fileName) throws IOException {
        this.clean(this.writeBuffer);
        this.putString(this.writeBuffer, fileName, 260);
        this.sendPacket(63);
    }
    
    public synchronized void text(final int type, final float timeSeconds, final int eventId, final String message) throws IOException, UnsupportedOperationException {
        if (this.ourProtocol < 3) {
            throw new UnsupportedOperationException(Messages.getString("SimConnect.badversion"));
        }
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(type);
        this.writeBuffer.putFloat(timeSeconds);
        this.writeBuffer.putInt(eventId);
        if (message != null && message.length() > 0) {
            final byte[] messageBytes = message.getBytes();
            this.writeBuffer.putInt(messageBytes.length + 1);
            this.writeBuffer.put(messageBytes);
        }
        else {
            this.writeBuffer.putInt(1);
        }
        this.writeBuffer.put((byte)0);
        this.sendPacket(64);
    }
    
    public synchronized void text(final TextType type, final float timeSeconds, final int eventId, final String message) throws IOException, UnsupportedOperationException {
        this.text(type.value(), timeSeconds, eventId, message);
    }
    
    public synchronized void text(final TextType type, final float timeSeconds, final Enum eventId, final String message) throws IOException, UnsupportedOperationException {
        this.text(type.value(), timeSeconds, eventId.ordinal(), message);
    }
    
    public synchronized void menu(final float timeSeconds, final int eventId, final String title, final String prompt, final String... items) throws IOException, UnsupportedOperationException {
        if (this.ourProtocol < 3) {
            throw new UnsupportedOperationException(Messages.getString("SimConnect.badversion"));
        }
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(TextType.MENU.value());
        this.writeBuffer.putFloat(timeSeconds);
        this.writeBuffer.putInt(eventId);
        this.writeBuffer.putInt(0);
        if (title == null && prompt == null && items == null) {
            this.writeBuffer.put((byte)0);
        }
        else {
            this.writeBuffer.put(title.getBytes());
            this.writeBuffer.put((byte)0);
            this.writeBuffer.put(prompt.getBytes());
            this.writeBuffer.put((byte)0);
            for (final String s : items) {
                if (s != null) {
                    final byte[] itemBytes = s.getBytes();
                    this.writeBuffer.put(itemBytes);
                    this.writeBuffer.put((byte)0);
                }
            }
        }
        this.writeBuffer.putInt(28, this.writeBuffer.position() - 32);
        this.sendPacket(64);
    }
    
    public synchronized void menu(final float timeSeconds, final Enum eventId, final String title, final String prompt, final String... items) throws IOException, UnsupportedOperationException {
        this.menu(timeSeconds, eventId.ordinal(), title, prompt, items);
    }
    
    public synchronized void requestFacilitiesList(final FacilityListType type, final int eventId) throws IOException, UnsupportedOperationException {
        if (this.ourProtocol < 3) {
            throw new UnsupportedOperationException(Messages.getString("SimConnect.badversion"));
        }
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(type.ordinal());
        this.writeBuffer.putInt(eventId);
        this.sendPacket(67);
    }
    
    public synchronized void requestFacilitiesList(final FacilityListType type, final Enum eventId) throws IOException, UnsupportedOperationException {
        this.requestFacilitiesList(type, eventId.ordinal());
    }
    
    public synchronized void subscribeToFacilities(final FacilityListType type, final int eventId) throws IOException, UnsupportedOperationException {
        if (this.ourProtocol < 3) {
            throw new UnsupportedOperationException(Messages.getString("SimConnect.badversion"));
        }
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(type.ordinal());
        this.writeBuffer.putInt(eventId);
        this.sendPacket(65);
    }
    
    public synchronized void subscribeToFacilities(final FacilityListType type, final Enum eventId) throws IOException, UnsupportedOperationException {
        this.subscribeToFacilities(type, eventId.ordinal());
    }
    
    public synchronized void unSubscribeToFacilities(final FacilityListType type) throws IOException, UnsupportedOperationException {
        if (this.ourProtocol < 3) {
            throw new UnsupportedOperationException(Messages.getString("SimConnect.badversion"));
        }
        this.clean(this.writeBuffer);
        this.writeBuffer.putInt(type.ordinal());
        this.sendPacket(66);
    }
    
    public void callDispatch(final Dispatcher dispatcher) throws IOException {
        this.pumpNextData();
        dispatcher.dispatch(this, this.readBuffer);
    }
    
    private void pumpNextData() throws IOException {
        this.readBuffer.clear();
        this.readBuffer.limit(4);
        int rlen = this.sc.read(this.readBuffer);
        if (rlen != 4) {
            throw new IOException(Messages.get("SimConnect.Invalid_read"));
        }
        final int dlen = this.readBuffer.getInt(0);
        if (dlen > this.readBuffer.capacity()) {
            throw new IOException(Messages.getString("SimConnect.PacketTooLarge"));
        }
        this.readBuffer.position(4);
        this.readBuffer.limit(dlen);
        for (rlen = 4; rlen < dlen; rlen += this.sc.read(this.readBuffer)) {}
        ++this.packetsReceived;
        this.bytesReceived += dlen;
        if (rlen != dlen) {
            throw new IOException(String.valueOf(Messages.get("SimConnect.Short_read")) + " (" + Messages.get("SimConnect.expected") + " " + dlen + " " + Messages.get("SimConnect.got") + " " + rlen + ")");
        }
        this.readBuffer.position(0);
    }
    
    public ByteBuffer getNextData() throws IOException {
        this.pumpNextData();
        return this.readBuffer;
    }
    
    @Override
    protected void finalize() throws Throwable {
        if (this.sc.isConnected()) {
            this.sc.close();
        }
    }
    
    public int getReceivedBytes() {
        return this.bytesReceived;
    }
    
    public int getSentBytes() {
        return this.bytesSent;
    }
    
    public int getReceivedPackets() {
        return this.packetsReceived;
    }
    
    public int getSentPackets() {
        return this.packetsSent;
    }
    
    public int getProtocolVersion() {
        return this.ourProtocol;
    }
}
