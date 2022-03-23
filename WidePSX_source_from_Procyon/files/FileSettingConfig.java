// 
// Decompiled by Procyon v0.5.36
// 

package files;

import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;

public class FileSettingConfig
{
    private static final String path;
    
    static {
        path = new String("config.ini");
    }
    
    public static String getPsxIp() throws IOException {
        return searchLine(FileSettingConfig.path, "PSX_HOST_IP");
    }
    
    public static String getPsxPort() throws IOException {
        return searchLine(FileSettingConfig.path, "PSX_HOST_PORT");
    }
    
    public static boolean getSimConnectEnabled() throws IOException {
        final String line = searchLine(FileSettingConfig.path, "SIMCONNECT_BRIDGE");
        try {
            return line.contains("1");
        }
        catch (NullPointerException e) {
            return false;
        }
    }
    
    public static boolean getSimSetPsxTraffic() throws IOException {
        final String line = searchLine(FileSettingConfig.path, "TCAS");
        try {
            return line.contains("1");
        }
        catch (NullPointerException e) {
            return false;
        }
    }
    
    public static boolean getPsxSetSimComXpndrAlt() throws IOException {
        final String line = searchLine(FileSettingConfig.path, "PSX_SET_COM_XPNDR");
        try {
            return line.contains("1");
        }
        catch (NullPointerException e) {
            return false;
        }
    }
    
    public static boolean getSendTrueTasToVATSIM() throws IOException {
        final String line = searchLine(FileSettingConfig.path, "SEND_TRUE_TAS");
        try {
            return line.contains("1");
        }
        catch (NullPointerException e) {
            return false;
        }
    }
    
    public static boolean getPrintOutputEnabled() throws IOException {
        final String line = searchLine(FileSettingConfig.path, "PRINTER");
        try {
            return line.contains("1");
        }
        catch (NullPointerException e) {
            return false;
        }
    }
    
    public static int getPrintLeftAlign() throws IOException {
        final String line = searchLine(FileSettingConfig.path, "LEFT_ALIGN");
        try {
            final int align = Integer.parseInt(line);
            return align;
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public static int getPrintHeightAlign() throws IOException {
        final String line = searchLine(FileSettingConfig.path, "HEIGHT_ALIGN");
        try {
            final int align = Integer.parseInt(line);
            return align;
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public static int getPrintEndLines() throws IOException {
        final String line = searchLine(FileSettingConfig.path, "END_LINES");
        try {
            final int align = Integer.parseInt(line);
            return align;
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public static String getGndServiceUnits() throws IOException {
        return searchLine(FileSettingConfig.path, "UNITS");
    }
    
    public static String getPsxBoostIp() throws IOException {
        return searchLine(FileSettingConfig.path, "BOOST_IP");
    }
    
    public static String getPsxBoostPort() throws IOException {
        return searchLine(FileSettingConfig.path, "BOOST_PORT");
    }
    
    public static boolean getScenGenEnabled() throws IOException {
        final String line = searchLine(FileSettingConfig.path, "USE_SCEN_GEN");
        try {
            return line.contains("1");
        }
        catch (NullPointerException e) {
            return false;
        }
    }
    
    public static String getSimIp() throws IOException {
        return searchLine(FileSettingConfig.path, "SIM_IP");
    }
    
    public static String getSimPort() throws IOException {
        return searchLine(FileSettingConfig.path, "SIM_PORT");
    }
    
    public static String getScenGenInUse() throws IOException {
        return searchLine(FileSettingConfig.path, "SCENGEN");
    }
    
    public static boolean getSimBridgeDestOffsetEnabled() throws IOException {
        final String line = searchLine(FileSettingConfig.path, "DEST_OFFSETS");
        try {
            return line.contains("1");
        }
        catch (NullPointerException e) {
            return false;
        }
    }
    
    public static boolean getSimBridgeAllOffsetEnabled() throws IOException {
        final String line = searchLine(FileSettingConfig.path, "ALL_OFFSETS");
        try {
            return line.contains("1");
        }
        catch (NullPointerException e) {
            return false;
        }
    }
    
    public static boolean getSimBridgeAutoAlignPsxPos() throws IOException {
        final String line = searchLine(FileSettingConfig.path, "AUTO_ALIGN");
        try {
            return line.contains("1");
        }
        catch (NullPointerException e) {
            return false;
        }
    }
    
    public static String getAsnFileDefaultLoc() throws IOException {
        return searchLine(FileSettingConfig.path, "WX_FILE_PATH");
    }
    
    public static String getSimBridgeOffsetInhibitAlt() throws IOException {
        return searchLine(FileSettingConfig.path, "INHIBIT");
    }
    
    public static String getSimBridgeHeightRef() throws IOException {
        return searchLine(FileSettingConfig.path, "HEIGHT");
    }
    
    public static int getGndServiceScenario() throws IOException {
        final String line = searchLine(FileSettingConfig.path, "SCENARIO");
        try {
            if (line.contains("0")) {
                return 0;
            }
            if (line.contains("1")) {
                return 1;
            }
            if (line.contains("2")) {
                return 2;
            }
            return 3;
        }
        catch (NullPointerException e) {
            return 0;
        }
    }
    
    public static boolean getGndServiceExtPush() throws IOException {
        final String line = searchLine(FileSettingConfig.path, "EXT_PUSH");
        try {
            return line.contains("1");
        }
        catch (NullPointerException e) {
            return false;
        }
    }
    
    public static String getWindowPosX() throws IOException {
        return searchLine(FileSettingConfig.path, "WIN_X");
    }
    
    public static String getWindowPosY() throws IOException {
        return searchLine(FileSettingConfig.path, "WIN_Y");
    }
    
    public static String getSimBridgeStartSlave() throws IOException {
        return searchLine(FileSettingConfig.path, "SLAVE");
    }
    
    public static boolean getSimBridgeDoorsSync() throws IOException {
        final String line = searchLine(FileSettingConfig.path, "DOORS");
        try {
            return line.contains("1");
        }
        catch (NullPointerException e) {
            return false;
        }
    }
    
    public static boolean getSimBridgeHeightRefEnabled() throws IOException {
        final String line = searchLine(FileSettingConfig.path, "HEIGHT_ENABLED");
        try {
            return line.contains("1");
        }
        catch (NullPointerException e) {
            return false;
        }
    }
    
    public static String getGndServiceVolumeAcp() throws IOException {
        return searchLine(FileSettingConfig.path, "ACP");
    }
    
    public static boolean getScenGenTabSimSlaveShowConfirmDialog() throws IOException {
        final String line = searchLine(FileSettingConfig.path, "SHOW_SIM_SLAVE_CONFIRM");
        try {
            return line.contains("1");
        }
        catch (NullPointerException e) {
            return true;
        }
    }
    
    public static boolean getScenGenTabPsxSlaveShowConfirmDialog() throws IOException {
        final String line = searchLine(FileSettingConfig.path, "SHOW_PSX_SLAVE_CONFIRM");
        try {
            return line.contains("1");
        }
        catch (NullPointerException e) {
            return true;
        }
    }
    
    public static boolean getScenGenTabExtPushShowConfirmDialog() throws IOException {
        final String line = searchLine(FileSettingConfig.path, "SHOW_EXT_PUSH_CONFIRM");
        try {
            return line.contains("1");
        }
        catch (NullPointerException e) {
            return true;
        }
    }
    
    public static boolean getScenGenUseBetaAlgos() throws IOException {
        final String line = searchLine(FileSettingConfig.path, "BETA_ALGOS");
        try {
            return line.contains("1");
        }
        catch (NullPointerException e) {
            return false;
        }
    }
    
    public static boolean getAloftWxDynMode() throws IOException {
        final String line = searchLine(FileSettingConfig.path, "ALOFTWX_DYN_MODE");
        try {
            return line.contains("1");
        }
        catch (NullPointerException e) {
            return false;
        }
    }
    
    public static boolean getAloftWxTurbMode() throws IOException {
        final String line = searchLine(FileSettingConfig.path, "ALOFTWX_TURB_MODE");
        try {
            return line.contains("1");
        }
        catch (NullPointerException e) {
            return false;
        }
    }
    
    public static void savePsxIp(final String argValue) throws IOException {
        writeLine(FileSettingConfig.path, "PSX_HOST_IP", argValue);
    }
    
    public static void savePsxPort(final String argValue) throws IOException {
        writeLine(FileSettingConfig.path, "PSX_HOST_PORT", argValue);
    }
    
    public static void saveSimConnectEnabled(final boolean argEnabled) throws IOException {
        if (argEnabled) {
            writeLine(FileSettingConfig.path, "SIMCONNECT_BRIDGE", "1");
        }
        else {
            writeLine(FileSettingConfig.path, "SIMCONNECT_BRIDGE", "0");
        }
    }
    
    public static void saveSimSetPsxTraffic(final boolean argEnabled) throws IOException {
        if (argEnabled) {
            writeLine(FileSettingConfig.path, "TCAS", "1");
        }
        else {
            writeLine(FileSettingConfig.path, "TCAS", "0");
        }
    }
    
    public static void savePsxSetSimComXpndrAlt(final boolean argSet) throws IOException {
        if (argSet) {
            writeLine(FileSettingConfig.path, "PSX_SET_COM_XPNDR", "1");
        }
        else {
            writeLine(FileSettingConfig.path, "PSX_SET_COM_XPNDR", "0");
        }
    }
    
    public static void saveSendTrueTasToVATSIM(final boolean argSet) throws IOException {
        if (argSet) {
            writeLine(FileSettingConfig.path, "SEND_TRUE_TAS", "1");
        }
        else {
            writeLine(FileSettingConfig.path, "SEND_TRUE_TAS", "0");
        }
    }
    
    public static void savePrintOutputEnabled(final boolean argEnabled) throws IOException {
        if (argEnabled) {
            writeLine(FileSettingConfig.path, "PRINTER", "1");
        }
        else {
            writeLine(FileSettingConfig.path, "PRINTER", "0");
        }
    }
    
    public static void savePrintLeftAlign(final String argAlign) throws IOException {
        if (!argAlign.isEmpty()) {
            writeLine(FileSettingConfig.path, "LEFT_ALIGN", argAlign);
        }
        else {
            writeLine(FileSettingConfig.path, "LEFT_ALIGN", "0");
        }
    }
    
    public static void savePrintHeightAlign(final String argAlign) throws IOException {
        if (!argAlign.isEmpty()) {
            writeLine(FileSettingConfig.path, "HEIGHT_ALIGN", argAlign);
        }
        else {
            writeLine(FileSettingConfig.path, "HEIGHT_ALIGN", "0");
        }
    }
    
    public static void savePrintEndLines(final String argLines) throws IOException {
        if (!argLines.isEmpty()) {
            writeLine(FileSettingConfig.path, "END_LINES", argLines);
        }
        else {
            writeLine(FileSettingConfig.path, "END_LINES", "0");
        }
    }
    
    public static void saveGndServiceUnits(final String argUnits) throws IOException {
        writeLine(FileSettingConfig.path, "UNITS", argUnits);
    }
    
    public static void savePsxBoostIp(final String argIp) throws IOException {
        writeLine(FileSettingConfig.path, "BOOST_IP", argIp);
    }
    
    public static void savePsxBoostPort(final String argPort) throws IOException {
        writeLine(FileSettingConfig.path, "BOOST_PORT", argPort);
    }
    
    public static void saveScenGenEnabled(final boolean argEnabled) throws IOException {
        if (argEnabled) {
            writeLine(FileSettingConfig.path, "USE_SCEN_GEN", "1");
        }
        else {
            writeLine(FileSettingConfig.path, "USE_SCEN_GEN", "0");
        }
    }
    
    public static void saveSimIp(final String argIp) throws IOException {
        writeLine(FileSettingConfig.path, "SIM_IP", argIp);
    }
    
    public static void saveSimPort(final String argIp) throws IOException {
        writeLine(FileSettingConfig.path, "SIM_PORT", argIp);
    }
    
    public static void saveScenGenInUse(final String argScenGen) throws IOException {
        writeLine(FileSettingConfig.path, "SCENGEN", argScenGen);
    }
    
    public static void saveSimBridgeEnableDestOffsets(final boolean argEnabled) throws IOException {
        if (argEnabled) {
            writeLine(FileSettingConfig.path, "DEST_OFFSETS", "1");
        }
        else {
            writeLine(FileSettingConfig.path, "DEST_OFFSETS", "0");
        }
    }
    
    public static void saveSimBridgeEnableAllOffsets(final boolean argEnabled) throws IOException {
        if (argEnabled) {
            writeLine(FileSettingConfig.path, "ALL_OFFSETS", "1");
        }
        else {
            writeLine(FileSettingConfig.path, "ALL_OFFSETS", "0");
        }
    }
    
    public static void saveSimBridgeAutoAlignPsxPos(final boolean argEnabled) throws IOException {
        if (argEnabled) {
            writeLine(FileSettingConfig.path, "AUTO_ALIGN", "1");
        }
        else {
            writeLine(FileSettingConfig.path, "AUTO_ALIGN", "0");
        }
    }
    
    public static void saveAsnFileDefaultLoc(final String argLocation) throws IOException {
        writeLine(FileSettingConfig.path, "WX_FILE_PATH", argLocation);
    }
    
    public static void saveSimBridgeOffsetInhibitAlt(final String argLocation) throws IOException {
        writeLine(FileSettingConfig.path, "INHIBIT", argLocation);
    }
    
    public static void saveSimBridgeHeightRef(final String argLocation) throws IOException {
        writeLine(FileSettingConfig.path, "HEIGHT", argLocation);
    }
    
    public static void saveGndServiceScenario(final int argScenario) throws IOException {
        writeLine(FileSettingConfig.path, "SCENARIO", Integer.toString(argScenario));
    }
    
    public static void saveGndServiceExtPush(final boolean argExtPush) throws IOException {
        if (argExtPush) {
            writeLine(FileSettingConfig.path, "EXT_PUSH", "1");
        }
        else {
            writeLine(FileSettingConfig.path, "EXT_PUSH", "0");
        }
    }
    
    public static void saveWindowPosX(final String argPosX) throws IOException {
        writeLine(FileSettingConfig.path, "WIN_X", argPosX);
    }
    
    public static void saveWindowPosY(final String argPosY) throws IOException {
        writeLine(FileSettingConfig.path, "WIN_Y", argPosY);
    }
    
    public static void saveSimBridgeStartSlave(final String argValue) throws IOException {
        writeLine(FileSettingConfig.path, "SLAVE", argValue);
    }
    
    public static void saveSimBridgeDoorsSync(final boolean argEnabled) throws IOException {
        if (argEnabled) {
            writeLine(FileSettingConfig.path, "DOORS", "1");
        }
        else {
            writeLine(FileSettingConfig.path, "DOORS", "0");
        }
    }
    
    public static void saveSimBridgeHeightRefEnabled(final boolean argEnabled) throws IOException {
        if (argEnabled) {
            writeLine(FileSettingConfig.path, "HEIGHT_ENABLED", "1");
        }
        else {
            writeLine(FileSettingConfig.path, "HEIGHT_ENABLED", "0");
        }
    }
    
    public static void saveGndServiceVolumeAcp(final String argAcp) throws IOException {
        writeLine(FileSettingConfig.path, "ACP", argAcp);
    }
    
    public static void saveScenGenTabSimSlaveShowConfirmDialog(final boolean argEnabled) throws IOException {
        if (argEnabled) {
            writeLine(FileSettingConfig.path, "SHOW_SIM_SLAVE_CONFIRM", "1");
        }
        else {
            writeLine(FileSettingConfig.path, "SHOW_SIM_SLAVE_CONFIRM", "0");
        }
    }
    
    public static void saveScenGenTabPsxSlaveShowConfirmDialog(final boolean argEnabled) throws IOException {
        if (argEnabled) {
            writeLine(FileSettingConfig.path, "SHOW_PSX_SLAVE_CONFIRM", "1");
        }
        else {
            writeLine(FileSettingConfig.path, "SHOW_PSX_SLAVE_CONFIRM", "0");
        }
    }
    
    public static void saveScenGenTabExtPushShowConfirmDialog(final boolean argEnabled) throws IOException {
        if (argEnabled) {
            writeLine(FileSettingConfig.path, "SHOW_EXT_PUSH_CONFIRM", "1");
        }
        else {
            writeLine(FileSettingConfig.path, "SHOW_EXT_PUSH_CONFIRM", "0");
        }
    }
    
    public static void saveScenGenUseBetaAlgos(final boolean argEnabled) throws IOException {
        if (argEnabled) {
            writeLine(FileSettingConfig.path, "BETA_ALGOS", "1");
        }
        else {
            writeLine(FileSettingConfig.path, "BETA_ALGOS", "0");
        }
    }
    
    public static void saveAloftWxDynMode(final boolean argEnabled) throws IOException {
        if (argEnabled) {
            writeLine(FileSettingConfig.path, "ALOFTWX_DYN_MODE", "1");
        }
        else {
            writeLine(FileSettingConfig.path, "ALOFTWX_DYN_MODE", "0");
        }
    }
    
    public static void saveAloftWxTurbMode(final boolean argEnabled) throws IOException {
        if (argEnabled) {
            writeLine(FileSettingConfig.path, "ALOFTWX_TURB_MODE", "1");
        }
        else {
            writeLine(FileSettingConfig.path, "ALOFTWX_TURB_MODE", "0");
        }
    }
    
    private static String searchLine(final String argPath, final String argLineBegin) throws IOException {
        final File configFile = new File(argPath);
        configFile.createNewFile();
        final BufferedReader reader = new BufferedReader(new FileReader(argPath));
        String line = new String("");
        while ((line = reader.readLine()) != null) {
            if (line.startsWith(argLineBegin)) {
                line = line.substring(line.indexOf(61) + 1);
                break;
            }
            line = "";
        }
        reader.close();
        return line;
    }
    
    private static void writeLine(final String argPath, final String argLineId, final String argValue) throws IOException {
        final File configFile = new File(argPath);
        configFile.createNewFile();
        final File tempFile = new File("temp.txt");
        tempFile.createNewFile();
        final BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
        final BufferedReader reader = new BufferedReader(new FileReader(argPath));
        String line = new String("");
        while ((line = reader.readLine()) != null) {
            if (!line.startsWith(argLineId)) {
                writer.write(String.valueOf(line) + System.getProperty("line.separator"));
            }
        }
        writer.write(String.valueOf(argLineId) + "=" + argValue + System.getProperty("line.separator"));
        writer.close();
        reader.close();
        configFile.delete();
        tempFile.renameTo(configFile);
    }
}
