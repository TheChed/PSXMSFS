// 
// Decompiled by Procyon v0.5.36
// 

package network;

import gndService.GndServiceBase;

public class SituReloadSyncThread implements Runnable
{
    private static long time;
    
    @Override
    public void run() {
        try {
            SituReloadSyncThread.time = System.currentTimeMillis();
            while (System.currentTimeMillis() < SituReloadSyncThread.time + 20000L) {
                GndServiceBase.setReleaseScenGen(false);
                Thread.sleep(150L);
            }
            GndServiceBase.dispatchLtTaxiCutoff1();
        }
        catch (Exception ex) {}
    }
}
