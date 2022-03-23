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

public class FileRunwaysEq
{
    private static final String path;
    
    static {
        path = new String("rwyEq.txt");
    }
    
    public static String getEquivalency(final String argArptId, final String argRwyId) {
        String result = "";
        final String arptId = argArptId;
        String rwyId = argRwyId;
        boolean equivalencyFound = false;
        try {
            final BufferedReader reader = new BufferedReader(new FileReader(FileRunwaysEq.path));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(arptId)) {
                    final StringSplitter ss = new StringSplitter(line);
                    String temp = ss.splitBetween(';', ';', 1, 2, false, false);
                    if (temp.startsWith("0") && !rwyId.startsWith("0")) {
                        temp = rwyId;
                        rwyId = "0" + temp;
                    }
                }
                if (line.startsWith(String.valueOf(arptId) + ";" + rwyId)) {
                    equivalencyFound = true;
                    final StringSplitter ss = new StringSplitter(line);
                    result = ss.splitFrom(';', 2, false);
                    break;
                }
            }
            reader.close();
            if (!equivalencyFound) {
                result = "";
            }
        }
        catch (FileNotFoundException e) {
            result = "";
        }
        catch (IOException e2) {
            result = "";
        }
        return result;
    }
}
