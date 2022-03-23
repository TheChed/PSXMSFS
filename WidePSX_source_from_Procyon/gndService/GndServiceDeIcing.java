// 
// Decompiled by Procyon v0.5.36
// 

package gndService;

import network.SocketClientPSXMain;
import util.StringSplitter;

public class GndServiceDeIcing
{
    protected static boolean getIcing() {
        final StringSplitter ss = new StringSplitter(GndServiceBase.getQs429());
        final int wL = Integer.parseInt(ss.splitBetween('=', ';', 1, 1, false, false));
        final int wR = Integer.parseInt(ss.splitBetween(';', ';', 1, 2, false, false));
        final int eng1 = Integer.parseInt(ss.splitBetween(';', ';', 2, 3, false, false));
        final int eng2 = Integer.parseInt(ss.splitBetween(';', ';', 3, 4, false, false));
        final int eng3 = Integer.parseInt(ss.splitBetween(';', ';', 4, 5, false, false));
        final int eng4 = Integer.parseInt(ss.splitBetween(';', ';', 5, 6, false, false));
        return wL != 0 || wR != 0 || eng1 != 0 || eng2 != 0 || eng3 != 0 || eng4 != 0;
    }
    
    protected static int getIcingIntensity() {
        final StringSplitter ss = new StringSplitter(GndServiceBase.getQs429());
        final float PSXWINGSMULTIPLIER = 0.0495f;
        final float ICINGTRIGGERAMOUNT = 40.0f;
        final int wL = Integer.parseInt(ss.splitBetween('=', ';', 1, 1, false, false));
        final int wR = Integer.parseInt(ss.splitBetween(';', ';', 1, 2, false, false));
        if (wL > 808.0808f || wR > 808.0808f) {
            return 1;
        }
        return 0;
    }
    
    protected static void deIce() {
        SocketClientPSXMain.send("Qi178=45");
        SocketClientPSXMain.send("Qs429=0;0;0;0;0;0;0");
    }
}
