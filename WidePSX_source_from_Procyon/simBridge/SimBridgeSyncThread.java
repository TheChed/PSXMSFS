// 
// Decompiled by Procyon v0.5.36
// 

package simBridge;

import util.StatusMonitor;

public class SimBridgeSyncThread implements Runnable
{
    @Override
    public void run() {
        while (true) {
            try {
                while (true) {
                    if (StatusMonitor.getSimBridgeIsRunning()) {
                        SimBridgeLtsGear.dispatchQs424(SimBridgeBase.getQs424());
                        SimBridgeLtsGear.dispatchQs443(SimBridgeBase.getQs443());
                        SimBridgeFltCtrls.dispatchQs480();
                        SimBridgeFltCtrls.dispatchQh388();
                        SimBridgeFltCtrls.dispatchQh389();
                        SimBridgeFltCtrls.dispatchQh397(SimBridgeBase.getQh397());
                        SimBridgeEngines.dispatchQs436(SimBridgeBase.getQs436());
                        SimBridgeEngines.dispatchQh392(SimBridgeBase.getQh392());
                        SimBridgeEngines.dispatchQh393(SimBridgeBase.getQh393());
                        SimBridgeEngines.dispatchQh394(SimBridgeBase.getQh394());
                        SimBridgeEngines.dispatchQh395(SimBridgeBase.getQh395());
                        SimBridgeAntiIce.dispatchWdoHeat(SimBridgeBase.getQh239(), SimBridgeBase.getQh240());
                        SimBridgeAntiIce.dispatchAntiIceEng1(SimBridgeBase.getQh225(), SimBridgeBase.getQh229(), SimBridgeBase.getQi21());
                        SimBridgeAntiIce.dispatchAntiIceEng2(SimBridgeBase.getQh226(), SimBridgeBase.getQh230(), SimBridgeBase.getQi21());
                        SimBridgeAntiIce.dispatchAntiIceEng3(SimBridgeBase.getQh227(), SimBridgeBase.getQh231(), SimBridgeBase.getQi21());
                        SimBridgeAntiIce.dispatchAntiIceEng4(SimBridgeBase.getQh228(), SimBridgeBase.getQh232(), SimBridgeBase.getQi21());
                        SimBridgeAntiIce.dispatchAntiIceWing(SimBridgeBase.getQh233(), SimBridgeBase.getQh234(), SimBridgeBase.getQi22());
                        SimBridgeAntiIce.dispatchWipers(SimBridgeBase.getQh235(), SimBridgeBase.getQh236());
                    }
                    Thread.sleep(1000L);
                }
            }
            catch (Exception ex) {
                continue;
            }
            break;
        }
    }
}
