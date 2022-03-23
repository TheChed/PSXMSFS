// 
// Decompiled by Procyon v0.5.36
// 

package network;

import simBridge.SimBridgeBase;
import simBridge.SimBridgeOffsetPos;
import gndService.GndServiceBase;
import misc.PrinterService;
import traffic.TrafficRadioXpndr;
import aloftWx.FmcUplinkRte;
import aloftWx.FmcUplinkDes;
import aloftWx.FmcUplinkBase;
import aloftWx.TurbWptData;
import aloftWx.TurbBase;
import aloftWx.AloftWxBase;
import util.ObserverData;
import java.util.ArrayList;
import util.ObservableData;

public class DataFromPsxMain implements ObservableData
{
    private static ArrayList<ObserverData> observersList;
    private static AloftWxBase aloftWxBase;
    private static TurbBase turbBase;
    private static TurbWptData turbWptData;
    private static FmcUplinkBase fmcUplinkBase;
    private static FmcUplinkDes fmcUplinkDes;
    private static FmcUplinkRte fmcUplinkRte;
    private static TrafficRadioXpndr trafficRadioXpndr;
    private static PrinterService printerService;
    private static GndServiceBase GndServiceBase;
    private static SimBridgeOffsetPos simBridgeOffsetPos;
    private static SimBridgeBase simBridgeBase;
    
    static {
        DataFromPsxMain.observersList = new ArrayList<ObserverData>();
    }
    
    public DataFromPsxMain() {
        DataFromPsxMain.aloftWxBase = new AloftWxBase();
        DataFromPsxMain.turbBase = new TurbBase();
        DataFromPsxMain.turbWptData = new TurbWptData();
        DataFromPsxMain.fmcUplinkBase = new FmcUplinkBase();
        DataFromPsxMain.fmcUplinkDes = new FmcUplinkDes();
        DataFromPsxMain.fmcUplinkRte = new FmcUplinkRte();
        DataFromPsxMain.trafficRadioXpndr = new TrafficRadioXpndr();
        DataFromPsxMain.printerService = new PrinterService();
        DataFromPsxMain.GndServiceBase = new GndServiceBase();
        DataFromPsxMain.simBridgeOffsetPos = new SimBridgeOffsetPos();
        DataFromPsxMain.simBridgeBase = new SimBridgeBase();
        this.addObserver(DataFromPsxMain.aloftWxBase);
        this.addObserver(DataFromPsxMain.turbBase);
        this.addObserver(DataFromPsxMain.turbWptData);
        this.addObserver(DataFromPsxMain.fmcUplinkBase);
        this.addObserver(DataFromPsxMain.fmcUplinkDes);
        this.addObserver(DataFromPsxMain.fmcUplinkRte);
        this.addObserver(DataFromPsxMain.trafficRadioXpndr);
        this.addObserver(DataFromPsxMain.printerService);
        this.addObserver(DataFromPsxMain.GndServiceBase);
        this.addObserver(DataFromPsxMain.simBridgeOffsetPos);
        this.addObserver(DataFromPsxMain.simBridgeBase);
    }
    
    @Override
    public void addObserver(final ObserverData argObserver) {
        DataFromPsxMain.observersList.add(argObserver);
    }
    
    @Override
    public void removeObserver(final ObserverData argObserver) {
        DataFromPsxMain.observersList.remove(argObserver);
    }
    
    @Override
    public void notifyObservers(final ObservableData argObs, final Object argId, final Object argData) {
        for (int i = 0; i < DataFromPsxMain.observersList.size(); ++i) {
            final ObserverData observer = DataFromPsxMain.observersList.get(i);
            observer.updateObservers(this, argId, argData);
        }
    }
    
    protected void dispatchPsxMainData(final String argMessage) {
        final String var = new String(argMessage);
        if (var.startsWith("load3")) {
            SocketClientPSXMain.send("demand=Qs483");
            SocketClientPSXMain.send("demand=Qs480");
        }
        if (var.startsWith("Qs121=")) {
            this.notifyObservers(this, "Qs121", var);
        }
        if (var.startsWith("Qs483=")) {
            this.notifyObservers(this, "Qs483", var);
        }
        if (var.startsWith("Qs327=")) {
            this.notifyObservers(this, "Qs327", var);
        }
        if (var.startsWith("Qs395=")) {
            this.notifyObservers(this, "Qs395", var);
        }
        if (var.startsWith("Qs397=")) {
            this.notifyObservers(this, "Qs397", var);
        }
        if (var.startsWith("Qs373=")) {
            this.notifyObservers(this, "Qs373", var);
        }
        if (var.startsWith("Qs376=")) {
            this.notifyObservers(this, "Qs376", var);
        }
        if (var.startsWith("Qs377=")) {
            this.notifyObservers(this, "Qs377", var);
        }
        if (var.startsWith("Qi129=")) {
            this.notifyObservers(this, "Qi129", var);
        }
        if (var.startsWith("Qs393=")) {
            this.notifyObservers(this, "Qs393", var);
        }
        if (var.startsWith("Qi242=")) {
            this.notifyObservers(this, "Qi242", var);
        }
        if (var.startsWith("Qs112=")) {
            this.notifyObservers(this, "Qs112", var);
        }
        if (var.startsWith("Qs117=")) {
            this.notifyObservers(this, "Qs117", var);
        }
        if (var.startsWith("Qs118=")) {
            this.notifyObservers(this, "Qs118", var);
        }
        if (var.startsWith("Qs119=")) {
            this.notifyObservers(this, "Qs119", var);
        }
        if (var.startsWith("Qi0=")) {
            this.notifyObservers(this, "Qi0", var);
        }
        if (var.startsWith("Qs123=")) {
            this.notifyObservers(this, "Qs123", var);
        }
        if (var.startsWith("Qi257=")) {
            this.notifyObservers(this, "Qi257", var);
        }
        if (var.startsWith("Qh178=")) {
            this.notifyObservers(this, "Qh178", var);
        }
        if (var.startsWith("Qi132=")) {
            this.notifyObservers(this, "Qi132", var);
        }
        if (var.startsWith("Qi174=")) {
            this.notifyObservers(this, "Qi174", var);
        }
        if (var.startsWith("Qi109=")) {
            this.notifyObservers(this, "Qi109", var);
        }
        if (var.startsWith("Qi110=")) {
            this.notifyObservers(this, "Qi110", var);
        }
        if (var.startsWith("Qi111=")) {
            this.notifyObservers(this, "Qi111", var);
        }
        if (var.startsWith("Qh288=")) {
            this.notifyObservers(this, "Qh288", var);
        }
        if (var.startsWith("Qh82=")) {
            this.notifyObservers(this, "Qh82", var);
        }
        if (var.startsWith("Qh93=")) {
            this.notifyObservers(this, "Qh93", var);
        }
        if (var.startsWith("Qh410=")) {
            this.notifyObservers(this, "Qh410", var);
        }
        if (var.startsWith("Qh411=")) {
            this.notifyObservers(this, "Qh411", var);
        }
        if (var.startsWith("Qh412=")) {
            this.notifyObservers(this, "Qh412", var);
        }
        if (var.startsWith("Qi179=")) {
            this.notifyObservers(this, "Qi179", var);
        }
        if (var.startsWith("Qi180=")) {
            this.notifyObservers(this, "Qi180", var);
        }
        if (var.startsWith("Qi181=")) {
            this.notifyObservers(this, "Qi181", var);
        }
        if (var.startsWith("Qs438=")) {
            this.notifyObservers(this, "Qs438", var);
        }
        if (var.startsWith("Qi220=")) {
            this.notifyObservers(this, "Qi220", var);
        }
        if (var.startsWith("Qi191=")) {
            this.notifyObservers(this, "Qi191", var);
        }
        if (var.startsWith("Qi132=")) {
            this.notifyObservers(this, "Qi132", var);
        }
        if (var.startsWith("Qs444=")) {
            this.notifyObservers(this, "Qs444", var);
        }
        if (var.startsWith("Qs122=")) {
            this.notifyObservers(this, "Qs122", var);
        }
        if (var.startsWith("Qi198=")) {
            this.notifyObservers(this, "Qi198", var);
        }
        if (var.startsWith("Qs388=")) {
            this.notifyObservers(this, "Qs388", var);
        }
        if (var.startsWith("Qs389=")) {
            this.notifyObservers(this, "Qs389", var);
        }
        if (var.startsWith("Qs424=")) {
            this.notifyObservers(this, "Qs424", var);
        }
        if (var.startsWith("Qs443=")) {
            this.notifyObservers(this, "Qs443", var);
        }
        if (var.startsWith("Qs120=")) {
            this.notifyObservers(this, "Qs120", var);
        }
        if (var.startsWith("Qh388=")) {
            this.notifyObservers(this, "Qh388", var);
        }
        if (var.startsWith("Qh389=")) {
            this.notifyObservers(this, "Qh389", var);
        }
        if (var.startsWith("Qs382=")) {
            this.notifyObservers(this, "Qs382", var);
        }
        if (var.startsWith("Qs383=")) {
            this.notifyObservers(this, "Qs383", var);
        }
        if (var.startsWith("Qh414=")) {
            this.notifyObservers(this, "Qh414", var);
        }
        if (var.startsWith("Qh397=")) {
            this.notifyObservers(this, "Qh397", var);
        }
        if (var.startsWith("Qi123=")) {
            this.notifyObservers(this, "Qi123", var);
        }
        if (var.startsWith("Qs436=")) {
            this.notifyObservers(this, "Qs436", var);
        }
        if (var.startsWith("Qh392=")) {
            this.notifyObservers(this, "Qh392", var);
        }
        if (var.startsWith("Qh393=")) {
            this.notifyObservers(this, "Qh393", var);
        }
        if (var.startsWith("Qh394=")) {
            this.notifyObservers(this, "Qh394", var);
        }
        if (var.startsWith("Qh395=")) {
            this.notifyObservers(this, "Qh395", var);
        }
        if (var.startsWith("Qs113=")) {
            this.notifyObservers(this, "Qs113", var);
        }
        if (var.startsWith("Qs114=")) {
            this.notifyObservers(this, "Qs114", var);
        }
        if (var.startsWith("Qs115=")) {
            this.notifyObservers(this, "Qs115", var);
        }
        if (var.startsWith("Qh239=")) {
            this.notifyObservers(this, "Qh239", var);
        }
        if (var.startsWith("Qh240=")) {
            this.notifyObservers(this, "Qh240", var);
        }
        if (var.startsWith("Qh225=")) {
            this.notifyObservers(this, "Qh225", var);
        }
        if (var.startsWith("Qh226=")) {
            this.notifyObservers(this, "Qh226", var);
        }
        if (var.startsWith("Qh227=")) {
            this.notifyObservers(this, "Qh227", var);
        }
        if (var.startsWith("Qh228=")) {
            this.notifyObservers(this, "Qh228", var);
        }
        if (var.startsWith("Qh229=")) {
            this.notifyObservers(this, "Qh229", var);
        }
        if (var.startsWith("Qh230=")) {
            this.notifyObservers(this, "Qh230", var);
        }
        if (var.startsWith("Qh231=")) {
            this.notifyObservers(this, "Qh231", var);
        }
        if (var.startsWith("Qh232=")) {
            this.notifyObservers(this, "Qh232", var);
        }
        if (var.startsWith("Qh233=")) {
            this.notifyObservers(this, "Qh233", var);
        }
        if (var.startsWith("Qh234=")) {
            this.notifyObservers(this, "Qh234", var);
        }
        if (var.startsWith("Qh235=")) {
            this.notifyObservers(this, "Qh235", var);
        }
        if (var.startsWith("Qh236=")) {
            this.notifyObservers(this, "Qh236", var);
        }
        if (var.startsWith("Qi21=")) {
            this.notifyObservers(this, "Qi21", var);
        }
        if (var.startsWith("Qi22=")) {
            this.notifyObservers(this, "Qi22", var);
        }
        if (var.startsWith("Qs448=")) {
            this.notifyObservers(this, "Qs448", var);
        }
        if (var.startsWith("Qs429=")) {
            this.notifyObservers(this, "Qs429", var);
        }
        if (var.startsWith("Qi178=")) {
            this.notifyObservers(this, "Qi178", var);
        }
        if (var.startsWith("Qs480=")) {
            this.notifyObservers(this, "Qs480", var);
        }
        if (var.startsWith("Qh177=")) {
            this.notifyObservers(this, "Qh177", var);
        }
        if (var.startsWith("Qh392=")) {
            this.notifyObservers(this, "Qh392", var);
        }
        if (var.startsWith("Qh213=")) {
            this.notifyObservers(this, "Qh213", var);
        }
        if (var.startsWith("Qh184=")) {
            this.notifyObservers(this, "Qh184", var);
        }
        if (var.startsWith("Qh397=")) {
            this.notifyObservers(this, "Qh397", var);
        }
    }
}
