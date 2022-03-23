// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.recv;

import java.util.ArrayList;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.List;
import flightsim.simconnect.SimConnect;
import flightsim.simconnect.Dispatcher;

public class DispatcherTask implements Runnable, Dispatcher
{
    protected final SimConnect sc;
    private boolean cont;
    private int nListeners;
    private List<AssignedObjectHandler> AssignedObjectHandlerList;
    private List<ClientDataHandler> ClientDataHandlerList;
    private List<CloudStateHandler> CloudStateHandlerList;
    private List<CustomActionHandler> CustomActionHandlerList;
    private List<EventFilenameHandler> EventFilenameHandlerList;
    private List<EventFrameHandler> EventFrameHandlerList;
    private List<EventHandler> EventHandlerList;
    private List<EventObjectHandler> EventObjectHandlerList;
    private List<ExceptionHandler> ExceptionHandlerList;
    private List<OpenHandler> OpenHandlerList;
    private List<QuitHandler> QuitHandlerList;
    private List<ReservedKeyHandler> ReservedKeyHandlerList;
    private List<SimObjectDataHandler> SimObjectDataHandlerList;
    private List<SimObjectDataTypeHandler> SimObjectDataTypeHandlerList;
    private List<SystemStateHandler> SystemStateHandlerList;
    private List<WeatherObservationHandler> WeatherObservationHandlerList;
    private List<EventWeatherModeHandler> EventWeatherModeList;
    private List<FacilitiesListHandler> FacilitiesListHandlerList;
    private List<MultiplayerClientStartedHandler> MultiplayerClientStartedHandlerList;
    private List<MultiplayerServerStartedHandler> MultiplayerServerStartedHandlerList;
    private List<MultiplayerSessionEndedHandler> MultiplayerSessionEndedHandlerList;
    private List<RaceEndHandler> RaceEndHandlerList;
    private List<RaceLapHandler> RaceLapHandlerList;
    private Queue<LateProcessItem> queueds;
    
    public int getListenersCount() {
        return this.nListeners;
    }
    
    public DispatcherTask(final SimConnect sc) {
        this.cont = true;
        this.nListeners = 0;
        this.queueds = new LinkedList<LateProcessItem>();
        this.sc = sc;
    }
    
    @Override
    public void run() {
        this.cont = true;
        while (this.cont) {
            try {
                this.sc.callDispatch(this);
            }
            catch (IOException e) {
                this.cont = false;
            }
        }
    }
    
    public void tryStop() {
        this.cont = false;
    }
    
    @Override
    public void dispatch(final SimConnect simConnect, final ByteBuffer data) {
        final int id = data.getInt(8);
        final RecvID rid = RecvID.type(id);
        this.processQueuedListeners();
        Label_2428: {
            switch (rid) {
                case ID_EVENT: {
                    if (this.EventHandlerList != null && this.EventHandlerList.size() > 0) {
                        synchronized (this.EventHandlerList) {
                            final RecvEvent re = new RecvEvent(data);
                            for (final EventHandler ev : this.EventHandlerList) {
                                ev.handleEvent(simConnect, re);
                            }
                            // monitorexit(this.EventHandlerList)
                            break;
                        }
                        break Label_2428;
                    }
                    break;
                }
                case ID_EXCEPTION: {
                    if (this.ExceptionHandlerList != null && this.ExceptionHandlerList.size() > 0) {
                        synchronized (this.ExceptionHandlerList) {
                            final RecvException rx = new RecvException(data);
                            for (final ExceptionHandler ev2 : this.ExceptionHandlerList) {
                                ev2.handleException(simConnect, rx);
                            }
                            // monitorexit(this.ExceptionHandlerList)
                            break;
                        }
                        break Label_2428;
                    }
                    break;
                }
                case ID_OPEN: {
                    if (this.OpenHandlerList != null && this.OpenHandlerList.size() > 0) {
                        synchronized (this.OpenHandlerList) {
                            final RecvOpen ro = new RecvOpen(data);
                            for (final OpenHandler ev3 : this.OpenHandlerList) {
                                ev3.handleOpen(simConnect, ro);
                            }
                            // monitorexit(this.OpenHandlerList)
                            break;
                        }
                        break Label_2428;
                    }
                    break;
                }
                case ID_EVENT_FILENAME: {
                    if (this.EventFilenameHandlerList != null && this.EventFilenameHandlerList.size() > 0) {
                        synchronized (this.EventFilenameHandlerList) {
                            final RecvEventFilename ref = new RecvEventFilename(data);
                            for (final EventFilenameHandler ev4 : this.EventFilenameHandlerList) {
                                ev4.handleFilename(simConnect, ref);
                            }
                            // monitorexit(this.EventFilenameHandlerList)
                            break;
                        }
                        break Label_2428;
                    }
                    break;
                }
                case ID_CUSTOM_ACTION: {
                    if (this.CustomActionHandlerList != null && this.CustomActionHandlerList.size() > 0) {
                        synchronized (this.CustomActionHandlerList) {
                            final RecvCustomAction rca = new RecvCustomAction(data);
                            for (final CustomActionHandler ev5 : this.CustomActionHandlerList) {
                                ev5.handleCustomAction(simConnect, rca);
                            }
                            // monitorexit(this.CustomActionHandlerList)
                            break;
                        }
                        break Label_2428;
                    }
                    break;
                }
                case ID_EVENT_FRAME: {
                    if (this.EventFrameHandlerList != null && this.EventFrameHandlerList.size() > 0) {
                        synchronized (this.EventFrameHandlerList) {
                            final RecvEventFrame rf = new RecvEventFrame(data);
                            for (final EventFrameHandler ev6 : this.EventFrameHandlerList) {
                                ev6.handleEventFrame(simConnect, rf);
                            }
                            // monitorexit(this.EventFrameHandlerList)
                            break;
                        }
                        break Label_2428;
                    }
                    break;
                }
                case ID_SIMOBJECT_DATA: {
                    if (this.SimObjectDataHandlerList != null && this.SimObjectDataHandlerList.size() > 0) {
                        synchronized (this.SimObjectDataHandlerList) {
                            final RecvSimObjectData rod = new RecvSimObjectData(data);
                            for (final SimObjectDataHandler ev7 : this.SimObjectDataHandlerList) {
                                ev7.handleSimObject(simConnect, rod);
                                rod.reset();
                            }
                            // monitorexit(this.SimObjectDataHandlerList)
                            break;
                        }
                        break Label_2428;
                    }
                    break;
                }
                case ID_EVENT_OBJECT_ADDREMOVE: {
                    if (this.EventObjectHandlerList != null && this.EventObjectHandlerList.size() > 0) {
                        synchronized (this.EventObjectHandlerList) {
                            final RecvEventAddRemove rear = new RecvEventAddRemove(data);
                            for (final EventObjectHandler ev8 : this.EventObjectHandlerList) {
                                ev8.handleEventObject(simConnect, rear);
                            }
                            // monitorexit(this.EventObjectHandlerList)
                            break;
                        }
                        break Label_2428;
                    }
                    break;
                }
                case ID_SIMOBJECT_DATA_BYTYPE: {
                    if (this.SimObjectDataTypeHandlerList != null && this.SimObjectDataTypeHandlerList.size() > 0) {
                        final RecvSimObjectDataByType rot = new RecvSimObjectDataByType(data);
                        for (final SimObjectDataTypeHandler ev9 : this.SimObjectDataTypeHandlerList) {
                            ev9.handleSimObjectType(simConnect, rot);
                            rot.reset();
                        }
                        break;
                    }
                    break;
                }
                case ID_QUIT: {
                    if (this.QuitHandlerList != null && this.QuitHandlerList.size() > 0) {
                        final RecvQuit rq = new RecvQuit(data);
                        for (final QuitHandler ev10 : this.QuitHandlerList) {
                            ev10.handleQuit(simConnect, rq);
                        }
                        break;
                    }
                    break;
                }
                case ID_SYSTEM_STATE: {
                    if (this.SystemStateHandlerList != null && this.SystemStateHandlerList.size() > 0) {
                        final RecvSystemState ry = new RecvSystemState(data);
                        for (final SystemStateHandler ev11 : this.SystemStateHandlerList) {
                            ev11.handleSystemState(simConnect, ry);
                        }
                        break;
                    }
                    break;
                }
                case ID_CLIENT_DATA: {
                    if (this.ClientDataHandlerList != null && this.ClientDataHandlerList.size() > 0) {
                        final RecvClientData rcd = new RecvClientData(data);
                        for (final ClientDataHandler ev12 : this.ClientDataHandlerList) {
                            ev12.handleClientData(simConnect, rcd);
                            rcd.reset();
                        }
                        break;
                    }
                    break;
                }
                case ID_ASSIGNED_OBJECT_ID: {
                    if (this.AssignedObjectHandlerList != null && this.AssignedObjectHandlerList.size() > 0) {
                        synchronized (this.AssignedObjectHandlerList) {
                            final RecvAssignedObjectID rai = new RecvAssignedObjectID(data);
                            for (final AssignedObjectHandler ev13 : this.AssignedObjectHandlerList) {
                                ev13.handleAssignedObject(simConnect, rai);
                            }
                            // monitorexit(this.AssignedObjectHandlerList)
                            break;
                        }
                        break Label_2428;
                    }
                    break;
                }
                case ID_CLOUD_STATE: {
                    if (this.CloudStateHandlerList != null && this.CloudStateHandlerList.size() > 0) {
                        synchronized (this.CloudStateHandlerList) {
                            final RecvCloudState rcl = new RecvCloudState(data);
                            for (final CloudStateHandler ev14 : this.CloudStateHandlerList) {
                                ev14.handleCloudState(simConnect, rcl);
                            }
                            // monitorexit(this.CloudStateHandlerList)
                            break;
                        }
                        break Label_2428;
                    }
                    break;
                }
                case ID_RESERVED_KEY: {
                    if (this.ReservedKeyHandlerList != null && this.ReservedKeyHandlerList.size() > 0) {
                        synchronized (this.ReservedKeyHandlerList) {
                            final RecvReservedKey rrk = new RecvReservedKey(data);
                            for (final ReservedKeyHandler ev15 : this.ReservedKeyHandlerList) {
                                ev15.handleReservedKey(simConnect, rrk);
                            }
                            // monitorexit(this.ReservedKeyHandlerList)
                            break;
                        }
                        break Label_2428;
                    }
                    break;
                }
                case ID_WEATHER_OBSERVATION: {
                    if (this.WeatherObservationHandlerList != null && this.WeatherObservationHandlerList.size() > 0) {
                        synchronized (this.WeatherObservationHandlerList) {
                            final RecvWeatherObservation rwo = new RecvWeatherObservation(data);
                            for (final WeatherObservationHandler ev16 : this.WeatherObservationHandlerList) {
                                ev16.handleWeatherObservation(simConnect, rwo);
                            }
                            // monitorexit(this.WeatherObservationHandlerList)
                            break;
                        }
                        break Label_2428;
                    }
                    break;
                }
                case ID_EVENT_WEATHER_MODE: {
                    if (this.EventWeatherModeList != null && this.EventWeatherModeList.size() > 0) {
                        synchronized (this.EventWeatherModeList) {
                            final RecvEventWeatherMode ev17 = new RecvEventWeatherMode(data);
                            for (final EventWeatherModeHandler hndler : this.EventWeatherModeList) {
                                hndler.handleWeatherMode(simConnect, ev17);
                            }
                            // monitorexit(this.EventWeatherModeList)
                            break;
                        }
                        break Label_2428;
                    }
                    break;
                }
                case ID_AIRPORT_LIST: {
                    if (this.FacilitiesListHandlerList != null && this.FacilitiesListHandlerList.size() > 0) {
                        synchronized (this.FacilitiesListHandlerList) {
                            final RecvAirportList list = new RecvAirportList(data);
                            for (final FacilitiesListHandler hndle : this.FacilitiesListHandlerList) {
                                hndle.handleAirportList(simConnect, list);
                            }
                            // monitorexit(this.FacilitiesListHandlerList)
                            break;
                        }
                        break Label_2428;
                    }
                    break;
                }
                case ID_VOR_LIST: {
                    if (this.FacilitiesListHandlerList != null && this.FacilitiesListHandlerList.size() > 0) {
                        synchronized (this.FacilitiesListHandlerList) {
                            final RecvVORList list2 = new RecvVORList(data);
                            for (final FacilitiesListHandler hndle : this.FacilitiesListHandlerList) {
                                hndle.handleVORList(simConnect, list2);
                            }
                            // monitorexit(this.FacilitiesListHandlerList)
                            break;
                        }
                        break Label_2428;
                    }
                    break;
                }
                case ID_NDB_LIST: {
                    if (this.FacilitiesListHandlerList != null && this.FacilitiesListHandlerList.size() > 0) {
                        synchronized (this.FacilitiesListHandlerList) {
                            final RecvNDBList list3 = new RecvNDBList(data);
                            for (final FacilitiesListHandler hndle : this.FacilitiesListHandlerList) {
                                hndle.handleNDBList(simConnect, list3);
                            }
                            // monitorexit(this.FacilitiesListHandlerList)
                            break;
                        }
                        break Label_2428;
                    }
                    break;
                }
                case ID_WAYPOINT_LIST: {
                    if (this.FacilitiesListHandlerList != null && this.FacilitiesListHandlerList.size() > 0) {
                        synchronized (this.FacilitiesListHandlerList) {
                            final RecvWaypointList list4 = new RecvWaypointList(data);
                            for (final FacilitiesListHandler hndle : this.FacilitiesListHandlerList) {
                                hndle.handleWaypointList(simConnect, list4);
                            }
                            // monitorexit(this.FacilitiesListHandlerList)
                            break;
                        }
                        break Label_2428;
                    }
                    break;
                }
                case ID_EVENT_MULTIPLAYER_CLIENT_STARTED: {
                    if (this.MultiplayerClientStartedHandlerList != null && this.MultiplayerClientStartedHandlerList.size() > 0) {
                        synchronized (this.MultiplayerClientStartedHandlerList) {
                            final RecvEventMultiplayerClientStarted ev18 = new RecvEventMultiplayerClientStarted(data);
                            for (final MultiplayerClientStartedHandler hndle2 : this.MultiplayerClientStartedHandlerList) {
                                hndle2.handleMultiplayerClientStarted(simConnect, ev18);
                            }
                            // monitorexit(this.MultiplayerClientStartedHandlerList)
                            break;
                        }
                        break Label_2428;
                    }
                    break;
                }
                case ID_EVENT_MULTIPLAYER_SERVER_STARTED: {
                    if (this.MultiplayerServerStartedHandlerList != null && this.MultiplayerServerStartedHandlerList.size() > 0) {
                        synchronized (this.MultiplayerServerStartedHandlerList) {
                            final RecvEventMultiplayerServerStarted ev19 = new RecvEventMultiplayerServerStarted(data);
                            for (final MultiplayerServerStartedHandler hndle3 : this.MultiplayerServerStartedHandlerList) {
                                hndle3.handleMultiplayerServerStarted(simConnect, ev19);
                            }
                            // monitorexit(this.MultiplayerServerStartedHandlerList)
                            break;
                        }
                        break Label_2428;
                    }
                    break;
                }
                case ID_EVENT_MULTIPLAYER_SESSION_ENDED: {
                    if (this.MultiplayerSessionEndedHandlerList != null && this.MultiplayerSessionEndedHandlerList.size() > 0) {
                        synchronized (this.MultiplayerSessionEndedHandlerList) {
                            final RecvEventMultiplayerSessionEnded ev20 = new RecvEventMultiplayerSessionEnded(data);
                            for (final MultiplayerSessionEndedHandler hndle4 : this.MultiplayerSessionEndedHandlerList) {
                                hndle4.handleMultiplayerSessionEnded(simConnect, ev20);
                            }
                            // monitorexit(this.MultiplayerSessionEndedHandlerList)
                            break;
                        }
                        break Label_2428;
                    }
                    break;
                }
                case ID_EVENT_RACE_END: {
                    if (this.RaceEndHandlerList != null && this.RaceEndHandlerList.size() > 0) {
                        synchronized (this.RaceEndHandlerList) {
                            final RecvEventRaceEnd ev21 = new RecvEventRaceEnd(data);
                            for (final RaceEndHandler hndle5 : this.RaceEndHandlerList) {
                                hndle5.handleRaceEnd(simConnect, ev21);
                            }
                            // monitorexit(this.RaceEndHandlerList)
                            break;
                        }
                        break Label_2428;
                    }
                    break;
                }
                case ID_EVENT_RACE_LAP: {
                    if (this.RaceLapHandlerList != null && this.RaceLapHandlerList.size() > 0) {
                        synchronized (this.RaceLapHandlerList) {
                            final RecvEventRaceLap ev22 = new RecvEventRaceLap(data);
                            for (final RaceLapHandler hndle6 : this.RaceLapHandlerList) {
                                hndle6.handleRaceLap(simConnect, ev22);
                            }
                        }
                        // monitorexit(this.RaceLapHandlerList)
                        break;
                    }
                    break;
                }
            }
        }
        this.processQueuedListeners();
    }
    
    public void addAssignedObjectHandler(final AssignedObjectHandler ev) {
        if (this.AssignedObjectHandlerList == null) {
            this.AssignedObjectHandlerList = new ArrayList<AssignedObjectHandler>();
        }
        this.queueds.add(new LateAdd(this.AssignedObjectHandlerList, ev));
    }
    
    public void removeAssignedObjectHandler(final AssignedObjectHandler ev) {
        this.queueds.add(new LateRemoval(this.AssignedObjectHandlerList, ev));
    }
    
    public void addClientDataHandler(final ClientDataHandler ev) {
        if (this.ClientDataHandlerList == null) {
            this.ClientDataHandlerList = new ArrayList<ClientDataHandler>();
        }
        this.queueds.add(new LateAdd(this.ClientDataHandlerList, ev));
    }
    
    public void removeClientDataHandler(final ClientDataHandler ev) {
        this.queueds.add(new LateRemoval(this.ClientDataHandlerList, ev));
    }
    
    public void addCloudStateHandler(final CloudStateHandler ev) {
        if (this.CloudStateHandlerList == null) {
            this.CloudStateHandlerList = new ArrayList<CloudStateHandler>();
        }
        this.queueds.add(new LateAdd(this.CloudStateHandlerList, ev));
    }
    
    public void removeCloudStateHandler(final CloudStateHandler ev) {
        this.queueds.add(new LateRemoval(this.CloudStateHandlerList, ev));
    }
    
    public void addCustomActionHandler(final CustomActionHandler ev) {
        if (this.CustomActionHandlerList == null) {
            this.CustomActionHandlerList = new ArrayList<CustomActionHandler>();
        }
        this.queueds.add(new LateAdd(this.CustomActionHandlerList, ev));
    }
    
    public void removeCustomActionHandler(final CustomActionHandler ev) {
        this.queueds.add(new LateRemoval(this.CustomActionHandlerList, ev));
    }
    
    public void addEventFilenameHandler(final EventFilenameHandler ev) {
        if (this.EventFilenameHandlerList == null) {
            this.EventFilenameHandlerList = new ArrayList<EventFilenameHandler>();
        }
        this.queueds.add(new LateAdd(this.EventFilenameHandlerList, ev));
    }
    
    public void removeEventFilenameHandler(final EventFilenameHandler ev) {
        this.queueds.add(new LateRemoval(this.EventFilenameHandlerList, ev));
    }
    
    public void addEventFrameHandler(final EventFrameHandler ev) {
        if (this.EventFrameHandlerList == null) {
            this.EventFrameHandlerList = new ArrayList<EventFrameHandler>();
        }
        this.queueds.add(new LateAdd(this.EventFrameHandlerList, ev));
    }
    
    public void removeEventFrameHandler(final EventFrameHandler ev) {
        this.queueds.add(new LateRemoval(this.EventFrameHandlerList, ev));
    }
    
    public void addEventHandler(final EventHandler ev) {
        if (this.EventHandlerList == null) {
            this.EventHandlerList = new ArrayList<EventHandler>();
        }
        this.queueds.add(new LateAdd(this.EventHandlerList, ev));
    }
    
    public void removeEventHandler(final EventHandler ev) {
        this.queueds.add(new LateRemoval(this.EventHandlerList, ev));
    }
    
    public void addEventObjectHandler(final EventObjectHandler ev) {
        if (this.EventObjectHandlerList == null) {
            this.EventObjectHandlerList = new ArrayList<EventObjectHandler>();
        }
        this.queueds.add(new LateAdd(this.EventObjectHandlerList, ev));
    }
    
    public void removeEventObjectHandler(final EventObjectHandler ev) {
        this.queueds.add(new LateRemoval(this.EventObjectHandlerList, ev));
    }
    
    public void addExceptionHandler(final ExceptionHandler ev) {
        if (this.ExceptionHandlerList == null) {
            this.ExceptionHandlerList = new ArrayList<ExceptionHandler>();
        }
        this.queueds.add(new LateAdd(this.ExceptionHandlerList, ev));
    }
    
    public void removeExceptionHandler(final ExceptionHandler ev) {
        this.queueds.add(new LateRemoval(this.ExceptionHandlerList, ev));
    }
    
    public void addOpenHandler(final OpenHandler ev) {
        if (this.OpenHandlerList == null) {
            this.OpenHandlerList = new ArrayList<OpenHandler>();
        }
        this.queueds.add(new LateAdd(this.OpenHandlerList, ev));
    }
    
    public void removeOpenHandler(final OpenHandler ev) {
        this.queueds.add(new LateRemoval(this.OpenHandlerList, ev));
    }
    
    public void addQuitHandler(final QuitHandler ev) {
        if (this.QuitHandlerList == null) {
            this.QuitHandlerList = new ArrayList<QuitHandler>();
        }
        this.queueds.add(new LateAdd(this.QuitHandlerList, ev));
    }
    
    public void removeQuitHandler(final QuitHandler ev) {
        this.queueds.add(new LateRemoval(this.QuitHandlerList, ev));
    }
    
    public void addReservedKeyHandler(final ReservedKeyHandler ev) {
        if (this.ReservedKeyHandlerList == null) {
            this.ReservedKeyHandlerList = new ArrayList<ReservedKeyHandler>();
        }
        this.queueds.add(new LateAdd(this.ReservedKeyHandlerList, ev));
    }
    
    public void removeReservedKeyHandler(final ReservedKeyHandler ev) {
        this.queueds.add(new LateRemoval(this.ReservedKeyHandlerList, ev));
    }
    
    public void addSimObjectDataHandler(final SimObjectDataHandler ev) {
        if (this.SimObjectDataHandlerList == null) {
            this.SimObjectDataHandlerList = new ArrayList<SimObjectDataHandler>();
        }
        this.queueds.add(new LateAdd(this.SimObjectDataHandlerList, ev));
    }
    
    public void removeSimObjectDataHandler(final SimObjectDataHandler ev) {
        this.queueds.add(new LateRemoval(this.SimObjectDataHandlerList, ev));
    }
    
    public void addSimObjectDataTypeHandler(final SimObjectDataTypeHandler ev) {
        if (this.SimObjectDataTypeHandlerList == null) {
            this.SimObjectDataTypeHandlerList = new ArrayList<SimObjectDataTypeHandler>();
        }
        this.queueds.add(new LateAdd(this.SimObjectDataTypeHandlerList, ev));
    }
    
    public void removeSimObjectDataTypeHandler(final SimObjectDataTypeHandler ev) {
        this.queueds.add(new LateRemoval(this.SimObjectDataTypeHandlerList, ev));
    }
    
    public void addSystemStateHandler(final SystemStateHandler ev) {
        if (this.SystemStateHandlerList == null) {
            this.SystemStateHandlerList = new ArrayList<SystemStateHandler>();
        }
        this.queueds.add(new LateAdd(this.SystemStateHandlerList, ev));
    }
    
    public void removeSystemStateHandler(final SystemStateHandler ev) {
        this.queueds.add(new LateRemoval(this.SystemStateHandlerList, ev));
    }
    
    public void addWeatherObservationHandler(final WeatherObservationHandler ev) {
        if (this.WeatherObservationHandlerList == null) {
            this.WeatherObservationHandlerList = new ArrayList<WeatherObservationHandler>();
        }
        this.queueds.add(new LateAdd(this.WeatherObservationHandlerList, ev));
    }
    
    public void removeWeatherObservationHandler(final WeatherObservationHandler ev) {
        this.queueds.add(new LateRemoval(this.WeatherObservationHandlerList, ev));
    }
    
    public void addEventWeatherModeHandler(final EventWeatherModeHandler ev) {
        if (this.EventWeatherModeList == null) {
            this.EventWeatherModeList = new ArrayList<EventWeatherModeHandler>();
        }
        this.queueds.add(new LateAdd(this.EventWeatherModeList, ev));
    }
    
    public void removeEventWeatherModeHandler(final EventWeatherModeHandler ev) {
        this.queueds.add(new LateRemoval(this.EventWeatherModeList, ev));
    }
    
    public void addFacilitiesListHandler(final FacilitiesListHandler ev) {
        if (this.FacilitiesListHandlerList == null) {
            this.FacilitiesListHandlerList = new ArrayList<FacilitiesListHandler>();
        }
        this.queueds.add(new LateAdd(this.FacilitiesListHandlerList, ev));
    }
    
    public void removeFacilitiesListHandler(final FacilitiesListHandler ev) {
        this.queueds.add(new LateRemoval(this.FacilitiesListHandlerList, ev));
    }
    
    public void addMultiplayerClientStartedHandler(final MultiplayerClientStartedHandler ev) {
        if (this.MultiplayerClientStartedHandlerList == null) {
            this.MultiplayerClientStartedHandlerList = new ArrayList<MultiplayerClientStartedHandler>();
        }
        this.queueds.add(new LateAdd(this.MultiplayerClientStartedHandlerList, ev));
    }
    
    public void removeMultiplayerClientStartedHandler(final MultiplayerClientStartedHandler ev) {
        this.queueds.add(new LateRemoval(this.MultiplayerClientStartedHandlerList, ev));
    }
    
    public void addMultiplayerServerStartedHandler(final MultiplayerServerStartedHandler ev) {
        if (this.MultiplayerServerStartedHandlerList == null) {
            this.MultiplayerServerStartedHandlerList = new ArrayList<MultiplayerServerStartedHandler>();
        }
        this.queueds.add(new LateAdd(this.MultiplayerServerStartedHandlerList, ev));
    }
    
    public void removeMultiplayerServerStartedHandler(final MultiplayerServerStartedHandler ev) {
        this.queueds.add(new LateRemoval(this.MultiplayerServerStartedHandlerList, ev));
    }
    
    public void addMultiplayerSessionEndedHandler(final MultiplayerSessionEndedHandler ev) {
        if (this.MultiplayerSessionEndedHandlerList == null) {
            this.MultiplayerSessionEndedHandlerList = new ArrayList<MultiplayerSessionEndedHandler>();
        }
        this.queueds.add(new LateAdd(this.MultiplayerSessionEndedHandlerList, ev));
    }
    
    public void removeMultiplayerSessionEndedHandler(final MultiplayerSessionEndedHandler ev) {
        this.queueds.add(new LateRemoval(this.MultiplayerSessionEndedHandlerList, ev));
    }
    
    public void addRaceLapHandler(final RaceLapHandler ev) {
        if (this.RaceLapHandlerList == null) {
            this.RaceLapHandlerList = new ArrayList<RaceLapHandler>();
        }
        this.queueds.add(new LateAdd(this.RaceLapHandlerList, ev));
    }
    
    public void removeRaceLapHandler(final RaceLapHandler ev) {
        this.queueds.add(new LateRemoval(this.RaceLapHandlerList, ev));
    }
    
    public void addRaceEndHandler(final RaceEndHandler ev) {
        if (this.RaceEndHandlerList == null) {
            this.RaceEndHandlerList = new ArrayList<RaceEndHandler>();
        }
        this.queueds.add(new LateAdd(this.RaceEndHandlerList, ev));
    }
    
    public void removeRaceEndHandler(final RaceEndHandler ev) {
        this.queueds.add(new LateRemoval(this.RaceEndHandlerList, ev));
    }
    
    public Thread createThread() {
        final Thread t = new Thread(this);
        t.setName("SimConnect dispatcher thread");
        return t;
    }
    
    protected synchronized void processQueuedListeners() {
        while (!this.queueds.isEmpty()) {
            final LateProcessItem ri = this.queueds.poll();
            if (ri != null) {
                ri.doJob();
            }
        }
    }
    
    public void removeHandlers(final Object o) {
        if (o instanceof AssignedObjectHandler) {
            this.removeAssignedObjectHandler((AssignedObjectHandler)o);
        }
        if (o instanceof ClientDataHandler) {
            this.removeClientDataHandler((ClientDataHandler)o);
        }
        if (o instanceof CloudStateHandler) {
            this.removeCloudStateHandler((CloudStateHandler)o);
        }
        if (o instanceof CustomActionHandler) {
            this.removeCustomActionHandler((CustomActionHandler)o);
        }
        if (o instanceof EventFilenameHandler) {
            this.removeEventFilenameHandler((EventFilenameHandler)o);
        }
        if (o instanceof EventFrameHandler) {
            this.removeEventFrameHandler((EventFrameHandler)o);
        }
        if (o instanceof EventHandler) {
            this.removeEventHandler((EventHandler)o);
        }
        if (o instanceof EventObjectHandler) {
            this.removeEventObjectHandler((EventObjectHandler)o);
        }
        if (o instanceof EventWeatherModeHandler) {
            this.removeEventWeatherModeHandler((EventWeatherModeHandler)o);
        }
        if (o instanceof ExceptionHandler) {
            this.removeExceptionHandler((ExceptionHandler)o);
        }
        if (o instanceof OpenHandler) {
            this.removeOpenHandler((OpenHandler)o);
        }
        if (o instanceof QuitHandler) {
            this.removeQuitHandler((QuitHandler)o);
        }
        if (o instanceof ReservedKeyHandler) {
            this.removeReservedKeyHandler((ReservedKeyHandler)o);
        }
        if (o instanceof SimObjectDataHandler) {
            this.removeSimObjectDataHandler((SimObjectDataHandler)o);
        }
        if (o instanceof SimObjectDataTypeHandler) {
            this.removeSimObjectDataTypeHandler((SimObjectDataTypeHandler)o);
        }
        if (o instanceof SystemStateHandler) {
            this.removeSystemStateHandler((SystemStateHandler)o);
        }
        if (o instanceof WeatherObservationHandler) {
            this.removeWeatherObservationHandler((WeatherObservationHandler)o);
        }
        if (o instanceof FacilitiesListHandler) {
            this.removeFacilitiesListHandler((FacilitiesListHandler)o);
        }
        if (o instanceof MultiplayerClientStartedHandler) {
            this.removeMultiplayerClientStartedHandler((MultiplayerClientStartedHandler)o);
        }
        if (o instanceof MultiplayerServerStartedHandler) {
            this.removeMultiplayerServerStartedHandler((MultiplayerServerStartedHandler)o);
        }
        if (o instanceof MultiplayerSessionEndedHandler) {
            this.removeMultiplayerSessionEndedHandler((MultiplayerSessionEndedHandler)o);
        }
        if (o instanceof RaceLapHandler) {
            this.removeRaceLapHandler((RaceLapHandler)o);
        }
        if (o instanceof RaceEndHandler) {
            this.removeRaceEndHandler((RaceEndHandler)o);
        }
    }
    
    public void addHandlers(final Object o) {
        if (o instanceof AssignedObjectHandler) {
            this.addAssignedObjectHandler((AssignedObjectHandler)o);
        }
        if (o instanceof ClientDataHandler) {
            this.addClientDataHandler((ClientDataHandler)o);
        }
        if (o instanceof CloudStateHandler) {
            this.addCloudStateHandler((CloudStateHandler)o);
        }
        if (o instanceof CustomActionHandler) {
            this.addCustomActionHandler((CustomActionHandler)o);
        }
        if (o instanceof EventFilenameHandler) {
            this.addEventFilenameHandler((EventFilenameHandler)o);
        }
        if (o instanceof EventFrameHandler) {
            this.addEventFrameHandler((EventFrameHandler)o);
        }
        if (o instanceof EventHandler) {
            this.addEventHandler((EventHandler)o);
        }
        if (o instanceof EventObjectHandler) {
            this.addEventObjectHandler((EventObjectHandler)o);
        }
        if (o instanceof EventWeatherModeHandler) {
            this.addEventWeatherModeHandler((EventWeatherModeHandler)o);
        }
        if (o instanceof ExceptionHandler) {
            this.addExceptionHandler((ExceptionHandler)o);
        }
        if (o instanceof OpenHandler) {
            this.addOpenHandler((OpenHandler)o);
        }
        if (o instanceof QuitHandler) {
            this.addQuitHandler((QuitHandler)o);
        }
        if (o instanceof ReservedKeyHandler) {
            this.addReservedKeyHandler((ReservedKeyHandler)o);
        }
        if (o instanceof SimObjectDataHandler) {
            this.addSimObjectDataHandler((SimObjectDataHandler)o);
        }
        if (o instanceof SimObjectDataTypeHandler) {
            this.addSimObjectDataTypeHandler((SimObjectDataTypeHandler)o);
        }
        if (o instanceof SystemStateHandler) {
            this.addSystemStateHandler((SystemStateHandler)o);
        }
        if (o instanceof WeatherObservationHandler) {
            this.addWeatherObservationHandler((WeatherObservationHandler)o);
        }
        if (o instanceof FacilitiesListHandler) {
            this.addFacilitiesListHandler((FacilitiesListHandler)o);
        }
        if (o instanceof MultiplayerClientStartedHandler) {
            this.removeMultiplayerClientStartedHandler((MultiplayerClientStartedHandler)o);
        }
        if (o instanceof MultiplayerServerStartedHandler) {
            this.removeMultiplayerServerStartedHandler((MultiplayerServerStartedHandler)o);
        }
        if (o instanceof MultiplayerSessionEndedHandler) {
            this.removeMultiplayerSessionEndedHandler((MultiplayerSessionEndedHandler)o);
        }
        if (o instanceof RaceEndHandler) {
            this.removeRaceEndHandler((RaceEndHandler)o);
        }
        if (o instanceof RaceLapHandler) {
            this.removeRaceLapHandler((RaceLapHandler)o);
        }
    }
    
    static /* synthetic */ void access$1(final DispatcherTask dispatcherTask, final int nListeners) {
        dispatcherTask.nListeners = nListeners;
    }
    
    private abstract class LateProcessItem<T>
    {
        protected List<T> l;
        protected T item;
        
        protected LateProcessItem(final List<T> l, final T item) {
            this.l = l;
            this.item = item;
        }
        
        protected abstract void doJob();
    }
    
    private class LateRemoval<T> extends LateProcessItem<T>
    {
        protected LateRemoval(final List<T> l, final T item) {
            super(l, item);
        }
        
        @Override
        protected void doJob() {
            if (this.l != null) {
                synchronized (this.l) {
                    if (this.l.remove(this.item)) {
                        final DispatcherTask this$0 = DispatcherTask.this;
                        DispatcherTask.access$1(this$0, this$0.nListeners - 1);
                    }
                }
                // monitorexit(this.l)
            }
        }
    }
    
    private class LateAdd<T> extends LateProcessItem<T>
    {
        protected LateAdd(final List<T> l, final T item) {
            super(l, item);
        }
        
        @Override
        protected void doJob() {
            if (this.l != null) {
                synchronized (this.l) {
                    this.l.add(this.item);
                    final DispatcherTask this$0 = DispatcherTask.this;
                    DispatcherTask.access$1(this$0, this$0.nListeners + 1);
                }
                // monitorexit(this.l)
            }
        }
    }
}
