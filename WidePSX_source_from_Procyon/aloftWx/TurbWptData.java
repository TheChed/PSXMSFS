// 
// Decompiled by Procyon v0.5.36
// 

package aloftWx;

import util.StringSplitter;
import util.StatusMonitor;
import network.DataFromPsxMain;
import util.ObservableData;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;
import java.util.Random;
import util.ObserverData;

public class TurbWptData implements ObserverData
{
    private static Random randomGenerator;
    private static String[][] dataFromGuiTable;
    private static String Qs373;
    private static String Qs376;
    private static String Qs377;
    private static String[][] turbulenceLegsTable;
    private static final Lock lockQs373;
    private static final Lock lockQs376;
    private static final Lock lockQs377;
    private static final Lock lockTurbulenceLegsTable;
    private static final Lock lockDataFromGuiTable;
    
    static {
        TurbWptData.randomGenerator = new Random();
        TurbWptData.dataFromGuiTable = new String[5][5];
        TurbWptData.Qs373 = new String("");
        TurbWptData.Qs376 = new String("");
        TurbWptData.Qs377 = new String("");
        lockQs373 = new ReentrantLock();
        lockQs376 = new ReentrantLock();
        lockQs377 = new ReentrantLock();
        lockTurbulenceLegsTable = new ReentrantLock();
        lockDataFromGuiTable = new ReentrantLock();
    }
    
    public TurbWptData() {
    }
    
    public TurbWptData(final String[][] argData) {
        TurbWptData.dataFromGuiTable = argData;
        TurbWptData.turbulenceLegsTable = new String[][] { { "", "", "", "", "", "", "", "", "", "", "", "", "" }, { "", "", "", "", "", "", "", "", "", "", "", "", "" }, { "", "", "", "", "", "", "", "", "", "", "", "", "" }, { "", "", "", "", "", "", "", "", "", "", "", "", "" }, { "", "", "", "", "", "", "", "", "", "", "", "", "" } };
    }
    
    @Override
    public void updateObservers(final ObservableData argObs, final Object argId, final Object argData) {
        if (argObs instanceof DataFromPsxMain) {
            final String s;
            switch (s = (String)argId) {
                case "Qs373": {
                    TurbWptData.lockQs373.lock();
                    TurbWptData.Qs373 = (String)argData;
                    TurbWptData.lockQs373.unlock();
                    break;
                }
                case "Qs376": {
                    TurbWptData.lockQs376.lock();
                    TurbWptData.Qs376 = (String)argData;
                    TurbWptData.lockQs376.unlock();
                    if (!StatusMonitor.getTurbConfigIsReady()) {
                        checkTableRteMatch();
                        break;
                    }
                    break;
                }
                case "Qs377": {
                    TurbWptData.lockQs377.lock();
                    TurbWptData.Qs377 = (String)argData;
                    TurbWptData.lockQs377.unlock();
                    if (!StatusMonitor.getTurbConfigIsReady()) {
                        checkTableRteMatch();
                        break;
                    }
                    break;
                }
                default:
                    break;
            }
        }
    }
    
    public static void updateTableData(final String[][] argData) {
        TurbWptData.lockDataFromGuiTable.lock();
        TurbWptData.dataFromGuiTable = argData;
        TurbWptData.lockDataFromGuiTable.unlock();
    }
    
    public static void checkTableRteMatch() {
        boolean atLeastOneMatch = false;
        boolean tableFromIsEmpty = true;
        boolean tableToIsEmpty = true;
        boolean wpt1Match = false;
        boolean wpt2Match = false;
        for (int i = 0; i < 5; ++i) {
            tableFromIsEmpty = getTableGuiDataValueAt(i, 1).isEmpty();
            tableToIsEmpty = getTableGuiDataValueAt(i, 2).isEmpty();
            wpt1Match = false;
            wpt2Match = false;
            String latitudeWpt1 = new String("");
            String longitudeWpt1 = new String("");
            String latitudeWpt2 = new String("");
            String longitudeWpt2 = new String("");
            String route = new String(getActiveRte());
            route = route.substring(route.indexOf(35) + 1);
            String leg = new String("");
            String legWpt = new String("");
            while (route.indexOf(59) != -1) {
                leg = route.substring(0, route.indexOf(59) + 1);
                legWpt = leg.substring(0, leg.indexOf(39));
                legWpt = legWpt.substring(10);
                if (legWpt.equals(getTableGuiDataValueAt(i, 1)) && !getTableGuiDataValueAt(i, 1).isEmpty()) {
                    wpt1Match = true;
                    final StringSplitter ss = new StringSplitter(leg);
                    final String coordinates = new String(ss.splitBetween('\'', '\'', 3, 4, false, false));
                    latitudeWpt1 = coordinates.substring(0, coordinates.indexOf(47));
                    longitudeWpt1 = coordinates.substring(coordinates.indexOf(47) + 1);
                }
                else if (legWpt.equals(getTableGuiDataValueAt(i, 2)) && !getTableGuiDataValueAt(i, 2).isEmpty()) {
                    wpt2Match = true;
                    final StringSplitter ss = new StringSplitter(leg);
                    final String coordinates = new String(ss.splitBetween('\'', '\'', 3, 4, false, false));
                    latitudeWpt2 = coordinates.substring(0, coordinates.indexOf(47));
                    longitudeWpt2 = coordinates.substring(coordinates.indexOf(47) + 1);
                }
                if (wpt1Match && wpt2Match) {
                    StatusMonitor.setTurbConfigIsReady(true);
                    StatusMonitor.setTurbWptsNotFound(false);
                    final double D = TurbBase.getDistance(Double.parseDouble(latitudeWpt1), Double.parseDouble(longitudeWpt1), Double.parseDouble(latitudeWpt2), Double.parseDouble(longitudeWpt2));
                    final String intensity = new String(getTableGuiDataValueAt(i, 0));
                    final String from = new String(getTableGuiDataValueAt(i, 1));
                    final String to = new String(getTableGuiDataValueAt(i, 2));
                    final String lowLim = new String(getTableGuiDataValueAt(i, 3));
                    final String upLim = new String(getTableGuiDataValueAt(i, 4));
                    String befAftBegin = new String("");
                    String befAftBeginOffset = new String("");
                    String befAftEnd = new String("");
                    String befAftEndOffset = new String("");
                    int random = TurbWptData.randomGenerator.nextInt(2);
                    if (random == 0) {
                        befAftBegin = "B";
                    }
                    else {
                        befAftBegin = "A";
                    }
                    final int zoneLengthOffset = (int)(D * TurbBase.getEdgeOffsetPercentage() / 100.0);
                    if (zoneLengthOffset != 0) {
                        befAftBeginOffset = String.valueOf(TurbWptData.randomGenerator.nextInt(zoneLengthOffset + 1));
                    }
                    else {
                        befAftBeginOffset = "0";
                    }
                    random = TurbWptData.randomGenerator.nextInt(2);
                    if (random == 0) {
                        befAftEnd = "B";
                    }
                    else {
                        befAftEnd = "A";
                    }
                    if (zoneLengthOffset != 0) {
                        befAftEndOffset = String.valueOf(TurbWptData.randomGenerator.nextInt(zoneLengthOffset + 1));
                    }
                    else {
                        befAftEndOffset = "0";
                    }
                    TurbWptData.lockTurbulenceLegsTable.lock();
                    TurbWptData.turbulenceLegsTable[i][0] = intensity.toUpperCase();
                    TurbWptData.turbulenceLegsTable[i][1] = from.toUpperCase();
                    TurbWptData.turbulenceLegsTable[i][2] = to.toUpperCase();
                    TurbWptData.turbulenceLegsTable[i][3] = lowLim;
                    TurbWptData.turbulenceLegsTable[i][4] = upLim;
                    TurbWptData.turbulenceLegsTable[i][5] = befAftBegin;
                    TurbWptData.turbulenceLegsTable[i][6] = befAftBeginOffset;
                    TurbWptData.turbulenceLegsTable[i][7] = befAftEnd;
                    TurbWptData.turbulenceLegsTable[i][8] = befAftEndOffset;
                    TurbWptData.turbulenceLegsTable[i][9] = latitudeWpt1;
                    TurbWptData.turbulenceLegsTable[i][10] = longitudeWpt1;
                    TurbWptData.turbulenceLegsTable[i][11] = latitudeWpt2;
                    TurbWptData.turbulenceLegsTable[i][12] = longitudeWpt2;
                    TurbWptData.lockTurbulenceLegsTable.unlock();
                    atLeastOneMatch = true;
                    break;
                }
                route = route.substring(route.indexOf(59) + 1);
            }
            if (!tableFromIsEmpty && !tableToIsEmpty && (!wpt1Match || !wpt2Match)) {
                StatusMonitor.setTurbConfigIsReady(false);
                StatusMonitor.setTurbWptsNotFound(true);
                break;
            }
        }
        if (!atLeastOneMatch || StatusMonitor.getTurbWptsNotFound()) {
            for (int i = 0; i < 5; ++i) {
                TurbWptData.lockTurbulenceLegsTable.lock();
                TurbWptData.turbulenceLegsTable[i][0] = "";
                TurbWptData.turbulenceLegsTable[i][1] = "";
                TurbWptData.turbulenceLegsTable[i][2] = "";
                TurbWptData.turbulenceLegsTable[i][3] = "";
                TurbWptData.turbulenceLegsTable[i][4] = "";
                TurbWptData.turbulenceLegsTable[i][5] = "";
                TurbWptData.turbulenceLegsTable[i][6] = "";
                TurbWptData.turbulenceLegsTable[i][7] = "";
                TurbWptData.turbulenceLegsTable[i][8] = "";
                TurbWptData.turbulenceLegsTable[i][9] = "";
                TurbWptData.turbulenceLegsTable[i][10] = "";
                TurbWptData.turbulenceLegsTable[i][11] = "";
                TurbWptData.turbulenceLegsTable[i][12] = "";
                TurbWptData.lockTurbulenceLegsTable.unlock();
            }
        }
        if (!atLeastOneMatch) {
            StatusMonitor.setTurbConfigIsReady(false);
        }
    }
    
    protected static String getTableGuiDataValueAt(final int argLine, final int argColomn) {
        TurbWptData.lockDataFromGuiTable.lock();
        final String temp = TurbWptData.dataFromGuiTable[argLine][argColomn].toUpperCase();
        TurbWptData.lockDataFromGuiTable.unlock();
        return temp;
    }
    
    protected static void setTableTurbLegsValueAt(final int argLine, final int argColomn, final String argValue) {
        TurbWptData.lockTurbulenceLegsTable.lock();
        TurbWptData.turbulenceLegsTable[argLine][argColomn] = argValue;
        TurbWptData.lockTurbulenceLegsTable.unlock();
    }
    
    protected static String getTableTurbLegsValueAt(final int argLine, final int argColomn) {
        TurbWptData.lockTurbulenceLegsTable.lock();
        final String temp = TurbWptData.turbulenceLegsTable[argLine][argColomn];
        TurbWptData.lockTurbulenceLegsTable.unlock();
        return temp;
    }
    
    private static String getActiveRte() {
        String temp = new String(getQs373());
        int activeRte = 0;
        try {
            temp = temp.substring(temp.indexOf(61) + 1);
            activeRte = Integer.parseInt(temp.substring(2, 3));
            if (activeRte == 1) {
                final String rte1 = new String(getQs376());
                final String dep = new String(rte1.substring(0, rte1.indexOf(59)));
                final StringSplitter ss = new StringSplitter(rte1);
                final String arr = new String(ss.splitBetween(';', ';', 1, 2, false, false));
                if (dep.equals("bbbb") || arr.equals("bbbb") || rte1.isEmpty()) {
                    return "";
                }
                return rte1.substring(rte1.indexOf(61) + 1);
            }
            else {
                if (activeRte != 2) {
                    return "";
                }
                final String rte2 = new String(getQs377());
                final String dep = new String(rte2.substring(0, rte2.indexOf(59)));
                final StringSplitter ss = new StringSplitter(rte2);
                final String arr = new String(ss.splitBetween(';', ';', 1, 2, false, false));
                if (dep.equals("bbbb") || arr.equals("bbbb") || rte2.isEmpty()) {
                    return "";
                }
                return rte2.substring(rte2.indexOf(61) + 1);
            }
        }
        catch (IndexOutOfBoundsException e) {
            return "";
        }
        catch (NumberFormatException e2) {
            return "";
        }
    }
    
    private static String getQs373() {
        TurbWptData.lockQs373.lock();
        final String temp = new String(TurbWptData.Qs373);
        TurbWptData.lockQs373.unlock();
        return temp;
    }
    
    private static String getQs376() {
        TurbWptData.lockQs376.lock();
        final String temp = new String(TurbWptData.Qs376);
        TurbWptData.lockQs376.unlock();
        return temp;
    }
    
    private static String getQs377() {
        TurbWptData.lockQs377.lock();
        final String temp = new String(TurbWptData.Qs377);
        TurbWptData.lockQs377.unlock();
        return temp;
    }
}
