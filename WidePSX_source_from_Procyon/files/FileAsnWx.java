// 
// Decompiled by Procyon v0.5.36
// 

package files;

import util.MutableBoolean;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import util.MutableString;

public class FileAsnWx
{
    private static String path;
    
    static {
        FileAsnWx.path = new String("");
    }
    
    public static void setPath(final String argPath) {
        FileAsnWx.path = argPath;
    }
    
    public static short checkFileName(final String argFilePath) {
        final String filePath = new String(argFilePath);
        if (!filePath.isEmpty()) {
            final int pos = filePath.length() - 22;
            try {
                final String fileName = filePath.substring(pos);
                if (fileName.equals("activeflightplanwx.txt")) {
                    return 1;
                }
                return 2;
            }
            catch (IndexOutOfBoundsException e) {
                return 2;
            }
        }
        return 3;
    }
    
    public static boolean searchWptInfoDyn(final String argWpt, final MutableString argWptWxInfos) throws IOException {
        boolean matchFound = false;
        String line1 = new String("");
        String line2 = new String("");
        try {
            final BufferedReader reader = new BufferedReader(new FileReader(FileAsnWx.path));
            while ((line1 = reader.readLine()) != null) {
                line2 = reader.readLine();
                String temp = new String(line1);
                try {
                    temp = line1.substring(0, line1.indexOf(9));
                }
                catch (IndexOutOfBoundsException e) {
                    matchFound = false;
                }
                if (temp.equals(argWpt)) {
                    argWptWxInfos.set(String.valueOf(line1) + line2);
                    matchFound = true;
                    break;
                }
            }
            reader.close();
        }
        catch (FileNotFoundException e2) {
            matchFound = false;
        }
        return matchFound;
    }
    
    public static boolean searchWptInfoSta(final String argWpt, final MutableString argWptWxInfo, final MutableString argTocInfo, final MutableString argTodInfo, final MutableBoolean argTocFound, final MutableBoolean argTodFound) throws IOException {
        boolean matchFound = false;
        String line1 = new String("");
        String line2 = new String("");
        try {
            final BufferedReader reader = new BufferedReader(new FileReader(FileAsnWx.path));
            while ((line1 = reader.readLine()) != null) {
                line2 = reader.readLine();
                String temp = new String(line1);
                try {
                    temp = line1.substring(0, line1.indexOf(9));
                }
                catch (IndexOutOfBoundsException e) {
                    matchFound = false;
                }
                if (temp.equals("TOC")) {
                    argTocFound.set(true);
                    argTocInfo.set(String.valueOf(line1) + line2);
                }
                if (temp.equals("TOD")) {
                    argTodFound.set(true);
                    argTodInfo.set(String.valueOf(line1) + line2);
                }
                if (temp.equals(argWpt) && (argTocFound.get() || argTodFound.get())) {
                    matchFound = true;
                    argWptWxInfo.set(String.valueOf(line1) + line2);
                    break;
                }
            }
            reader.close();
        }
        catch (FileNotFoundException e2) {
            matchFound = false;
        }
        return matchFound;
    }
}
