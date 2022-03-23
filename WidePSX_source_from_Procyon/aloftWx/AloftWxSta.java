// 
// Decompiled by Procyon v0.5.36
// 

package aloftWx;

import util.StatusMonitor;
import java.io.IOException;
import network.SocketClientPSXMain;
import util.StringSplitter;
import files.FileAsnWx;
import util.MutableString;
import util.MutableBoolean;

public class AloftWxSta implements Runnable
{
    private static boolean climbWxActive;
    
    static {
        AloftWxSta.climbWxActive = true;
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(5000L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (AloftWxBase.getPsxOnGround()) {
                AloftWxSta.climbWxActive = true;
            }
            final MutableBoolean tocFound = new MutableBoolean(false);
            final MutableBoolean todFound = new MutableBoolean(false);
            final MutableString wptWxInfos = new MutableString("");
            final MutableString tocInfos = new MutableString("");
            final MutableString todInfos = new MutableString("");
            try {
                if (FileAsnWx.searchWptInfoSta(getLastProgWpt(), wptWxInfos, tocInfos, todInfos, tocFound, todFound)) {
                    if (todFound.get()) {
                        AloftWxSta.climbWxActive = false;
                    }
                    if (tocFound.get() && !todFound.get()) {
                        AloftWxSta.climbWxActive = false;
                        final String infos = new String(getLayerInfo(wptWxInfos.get(), getLayer(AloftWxBase.getPsxAltitude())));
                        if (AloftWxBase.getPsxAltitude() > 20000) {
                            StringSplitter ss = new StringSplitter(infos);
                            String temp = new String(ss.splitBetween('(', ')', 1, 1, false, false));
                            final int oat = Math.abs(AloftWxBase.smoothOat(Integer.parseInt(temp)));
                            final String oatS = new String(AloftWxBase.addOatLeadZero(oat));
                            temp = infos.substring(0, infos.indexOf(64));
                            final String windDirS = new String(AloftWxBase.addWindLeadZero(Integer.parseInt(temp)));
                            ss = new StringSplitter(infos);
                            final String windVel = new String(ss.splitBetween('@', '(', 1, 1, false, false));
                            SocketClientPSXMain.send("Qi243=500");
                            SocketClientPSXMain.send("Qs327=A" + AloftWxBase.getPsxAltitude() + 'T' + oatS + "W000" + windDirS + windVel + ';' + windVel + AloftWxBase.getQs327End());
                        }
                    }
                }
            }
            catch (NumberFormatException e2) {
                e2.printStackTrace();
            }
            catch (IOException e3) {
                e3.printStackTrace();
            }
            if (AloftWxSta.climbWxActive) {
                if (!tocInfos.get().isEmpty()) {
                    StatusMonitor.setAloftWxTocNotFound(false);
                    int fmcAlt = getFmcAltitude();
                    if (fmcAlt < 18000) {
                        fmcAlt = 18000;
                    }
                    final String infos2 = new String(getLayerInfo(tocInfos.get(), getLayer(fmcAlt)));
                    StringSplitter ss2 = new StringSplitter(infos2);
                    String temp2 = new String(ss2.splitBetween('(', ')', 1, 1, false, false));
                    final int oat2 = Math.abs(Integer.parseInt(temp2));
                    final String oatS2 = new String(AloftWxBase.addOatLeadZero(oat2));
                    temp2 = infos2.substring(0, infos2.indexOf(64));
                    final String windDirS2 = new String(AloftWxBase.addWindLeadZero(Integer.parseInt(temp2)));
                    ss2 = new StringSplitter(infos2);
                    final String windVel2 = new String(ss2.splitBetween('@', '(', 1, 1, false, false));
                    SocketClientPSXMain.send("Qi243=500");
                    SocketClientPSXMain.send("Qs327=A" + fmcAlt + 'T' + oatS2 + "W000" + windDirS2 + windVel2 + ';' + windVel2 + AloftWxBase.getQs327End());
                    AloftWxSta.climbWxActive = false;
                }
                else {
                    StatusMonitor.setAloftWxTocNotFound(true);
                }
            }
        }
    }
    
    private static String getLastProgWpt() {
        final String temp = new String(AloftWxBase.getQs397());
        try {
            return temp.substring(temp.indexOf(59) + 1, temp.indexOf(95));
        }
        catch (IndexOutOfBoundsException e) {
            return "";
        }
    }
    
    private static int getFmcAltitude() {
        final String temp = new String(AloftWxBase.getQs395());
        return Integer.parseInt(temp.substring(temp.indexOf(100) + 1, temp.indexOf(101)));
    }
    
    private static int getLayer(final int argAltitude) {
        final int layer1 = 18000;
        final int layer2 = 24000;
        final int layer3 = 30000;
        final int layer4 = 34000;
        final int layer5 = 39000;
        final int layer6 = 44000;
        final int layer7 = 49000;
        int returnValue = 0;
        if (argAltitude >= 18000 && argAltitude < 24000) {
            if (argAltitude - 18000 < 24000 - argAltitude) {
                returnValue = 1;
            }
            else {
                returnValue = 2;
            }
        }
        else if (argAltitude >= 24000 && argAltitude < 30000) {
            if (argAltitude - 24000 < 30000 - argAltitude) {
                returnValue = 2;
            }
            else {
                returnValue = 3;
            }
        }
        else if (argAltitude >= 30000 && argAltitude < 34000) {
            if (argAltitude - 30000 < 34000 - argAltitude) {
                returnValue = 3;
            }
            else {
                returnValue = 4;
            }
        }
        else if (argAltitude >= 34000 && argAltitude < 39000) {
            if (argAltitude - 34000 < 39000 - argAltitude) {
                returnValue = 4;
            }
            else {
                returnValue = 5;
            }
        }
        else if (argAltitude >= 39000 && argAltitude < 44000) {
            if (argAltitude - 39000 < 44000 - argAltitude) {
                returnValue = 5;
            }
            else {
                returnValue = 6;
            }
        }
        else if (argAltitude >= 44000 && argAltitude < 49000) {
            if (argAltitude - 44000 < 49000 - argAltitude) {
                returnValue = 6;
            }
            else {
                returnValue = 7;
            }
        }
        else if (argAltitude >= 49000) {
            returnValue = 7;
        }
        return returnValue;
    }
    
    private static String getLayerInfo(final String argWptWxInfos, final int argLayer) {
        String temp = new String(argWptWxInfos);
        String returnValue = new String("");
        int loops = 0;
        if (argLayer == 1) {
            loops = 6;
        }
        if (argLayer == 2) {
            loops = 7;
        }
        if (argLayer == 3) {
            loops = 9;
        }
        if (argLayer == 4) {
            loops = 10;
        }
        if (argLayer == 5) {
            loops = 11;
        }
        if (argLayer == 6) {
            loops = 12;
        }
        if (argLayer == 7) {
            loops = 12;
        }
        try {
            for (int i = 0; i < loops; ++i) {
                returnValue = temp.substring(temp.indexOf(9) + 1);
                temp = temp.substring(temp.indexOf(9) + 1);
            }
        }
        catch (StringIndexOutOfBoundsException e) {
            returnValue = "";
            e.printStackTrace();
        }
        if (argLayer == 7) {
            returnValue = temp;
        }
        return returnValue;
    }
}
