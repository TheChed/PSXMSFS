// 
// Decompiled by Procyon v0.5.36
// 

package simBridge;

import java.io.IOException;
import network.BridgeSimConnect;
import util.StatusMonitor;
import network.SocketClientPSXMain;
import java.util.Calendar;
import java.util.TimeZone;

public class SimBridgeTimeSync implements Runnable
{
    public static int[] getPsxDate() {
        final int[] result = new int[4];
        long psxTimeSinceEpoch = 0L;
        String temp = SimBridgeBase.getQs123();
        try {
            temp = temp.substring(temp.indexOf(61) + 1);
            psxTimeSinceEpoch = Long.parseLong(temp);
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
        }
        catch (IndexOutOfBoundsException e2) {
            e2.printStackTrace();
        }
        final Calendar psxCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        psxCalendar.clear();
        psxCalendar.setTimeInMillis(psxTimeSinceEpoch);
        result[0] = psxCalendar.get(1);
        result[1] = psxCalendar.get(6);
        result[2] = psxCalendar.get(11);
        result[3] = psxCalendar.get(12);
        return result;
    }
    
    public static void setPsxTime(final int[] argTime) {
        final Calendar simCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        simCalendar.clear();
        final int hours = argTime[3] / 3600;
        final int minutes = argTime[3] % 3600 / 60;
        final int seconds = argTime[3] % 60;
        simCalendar.set(argTime[0], argTime[1], argTime[2], hours, minutes, seconds);
        final long timeSinceEpoch = simCalendar.getTimeInMillis();
        SocketClientPSXMain.send("Qs123=" + String.valueOf(timeSinceEpoch));
        SocketClientPSXMain.send("Qs124=" + String.valueOf(timeSinceEpoch));
        SocketClientPSXMain.send("Qs125=" + String.valueOf(timeSinceEpoch));
    }
    
    @Override
    public void run() {
        long psxTime = 0L;
        long lastPsxTime = 0L;
        while (true) {
            if (StatusMonitor.getSimBridgeIsRunning()) {
                final String Qs123 = SimBridgeBase.getQs123();
                psxTime = Long.parseLong(Qs123.substring(Qs123.indexOf(61) + 1));
                final long timeDiff = Math.abs(psxTime - lastPsxTime);
                if (timeDiff > 600000L) {
                    final int[] psxDate = getPsxDate();
                    try {
                        BridgeSimConnect.forceSimTimeSync(psxDate);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                Thread.sleep(10000L);
            }
            catch (InterruptedException e2) {
                e2.printStackTrace();
            }
            lastPsxTime = psxTime;
        }
    }
}
