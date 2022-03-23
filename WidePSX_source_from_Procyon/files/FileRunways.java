// 
// Decompiled by Procyon v0.5.36
// 

package files;

import java.io.IOException;
import java.io.FileNotFoundException;
import util.StringSplitter;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;

public class FileRunways
{
    private static final String path;
    private static final double DEG2RAD = 0.017453292519943295;
    
    static {
        path = new String("runways.csv");
    }
    
    public static double[] getFileRwyLatLonAltLen(final String argArptId, final String argRwyId) throws IOException {
        boolean rwyFound = false;
        boolean arptFound = false;
        final double[] result = { 0.0, 0.0, 0.0, 0.0 };
        String line = new String("");
        String rwyId = new String(argRwyId);
        final String arptId = new String(argArptId);
        final String eqRwy = FileRunwaysEq.getEquivalency(argArptId, argRwyId);
        if (!eqRwy.isEmpty()) {
            rwyId = eqRwy;
        }
        if (rwyId.length() == 1) {
            rwyId = "00" + rwyId + "0";
        }
        else if (rwyId.length() == 2) {
            final String temp = new String(rwyId.substring(0, 1));
            if (rwyId.charAt(1) == 'L') {
                rwyId = "00" + temp + "1";
            }
            else if (rwyId.charAt(1) == 'R') {
                rwyId = "00" + temp + "2";
            }
            else {
                rwyId = "0" + rwyId + "0";
            }
        }
        else if (rwyId.length() == 3) {
            if (rwyId.charAt(0) == '0') {
                final String temp = new String(rwyId.substring(1, 2));
                if (rwyId.charAt(2) == 'L') {
                    rwyId = "00" + temp + "1";
                }
                else {
                    rwyId = "00" + temp + "2";
                }
            }
            else {
                final String temp = new String(rwyId.substring(0, 2));
                if (rwyId.charAt(2) == 'L') {
                    rwyId = "0" + temp + "1";
                }
                else {
                    rwyId = "0" + temp + "2";
                }
            }
        }
        try {
            final BufferedReader reader = new BufferedReader(new FileReader(FileRunways.path));
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(String.valueOf(arptId) + "," + rwyId)) {
                    rwyFound = true;
                    final StringSplitter ss = new StringSplitter(line);
                    result[0] = Double.parseDouble(ss.splitBetween(',', ',', 2, 3, false, false)) * 0.017453292519943295;
                    result[1] = Double.parseDouble(ss.splitBetween(',', ',', 3, 4, false, false)) * 0.017453292519943295;
                    result[2] = Double.parseDouble(ss.splitBetween(',', ',', 4, 5, false, false));
                    result[3] = Double.parseDouble(ss.splitBetween(',', ',', 6, 7, false, false));
                    break;
                }
                if (!line.startsWith(arptId)) {
                    continue;
                }
                arptFound = true;
                final StringSplitter ss = new StringSplitter(line);
                result[2] = Double.parseDouble(ss.splitBetween(',', ',', 4, 5, false, false));
            }
            reader.close();
            if (!rwyFound && !arptFound) {
                result[1] = (result[0] = 9999.0);
                result[3] = (result[2] = 9999.0);
            }
            else if (arptFound && !rwyFound) {
                result[0] = 9999.0;
                result[3] = (result[1] = 9999.0);
            }
        }
        catch (FileNotFoundException e) {
            result[1] = (result[0] = 9999.0);
            result[3] = (result[2] = 9999.0);
        }
        return result;
    }
}
