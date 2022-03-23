// 
// Decompiled by Procyon v0.5.36
// 

package network;

import java.io.IOException;
import java.net.SocketException;
import java.io.Reader;
import java.io.InputStreamReader;
import util.StatusMonitor;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClientPSXMain implements Runnable
{
    private static DataFromPsxMain dataFromPsxMain;
    private static String ip;
    private static int port;
    private static Socket socket;
    private static PrintWriter ou;
    private static BufferedReader in;
    private static Thread recvThread;
    
    static {
        SocketClientPSXMain.dataFromPsxMain = new DataFromPsxMain();
        SocketClientPSXMain.ip = new String("");
        SocketClientPSXMain.port = 0;
    }
    
    public SocketClientPSXMain() {
        StatusMonitor.setPsxMainIsConnected(false);
        StatusMonitor.setPsxMainIsDisconnected(false);
        StatusMonitor.setPsxMainUnableToConnect(false);
        SocketClientPSXMain.recvThread = new Thread(this);
    }
    
    public static void setIp(final String argIp) {
        SocketClientPSXMain.ip = argIp;
    }
    
    public static boolean setPort(final String argPort) {
        try {
            SocketClientPSXMain.port = Integer.valueOf(argPort);
            StatusMonitor.setPsxMainInvalidIp(false);
            return true;
        }
        catch (NumberFormatException e) {
            StatusMonitor.setPsxMainInvalidIp(true);
            return false;
        }
    }
    
    public static void connect() {
        try {
            (SocketClientPSXMain.socket = new Socket(SocketClientPSXMain.ip, SocketClientPSXMain.port)).setTcpNoDelay(true);
            SocketClientPSXMain.ou = new PrintWriter(SocketClientPSXMain.socket.getOutputStream(), true);
            SocketClientPSXMain.in = new BufferedReader(new InputStreamReader(SocketClientPSXMain.socket.getInputStream()));
            StatusMonitor.setPsxMainIsConnected(true);
            StatusMonitor.setPsxMainIsDisconnected(false);
            StatusMonitor.setPsxMainUnableToConnect(false);
            SocketClientPSXMain.recvThread.start();
        }
        catch (SocketException e) {
            StatusMonitor.setPsxMainIsConnected(false);
            StatusMonitor.setPsxMainIsDisconnected(false);
            StatusMonitor.setPsxMainUnableToConnect(true);
        }
        catch (IOException e2) {
            StatusMonitor.setPsxMainIsConnected(false);
            StatusMonitor.setPsxMainIsDisconnected(false);
            StatusMonitor.setPsxMainUnableToConnect(true);
        }
    }
    
    public static void send(final String argMessage) {
        if (SocketClientPSXMain.ou != null) {
            SocketClientPSXMain.ou.println(argMessage);
        }
    }
    
    @Override
    public void run() {
        try {
            while (true) {
                final String message;
                if ((message = SocketClientPSXMain.in.readLine()) != null) {
                    if (message.equals("exit")) {
                        break;
                    }
                    SocketClientPSXMain.dataFromPsxMain.dispatchPsxMainData(message);
                }
            }
            StatusMonitor.setPsxMainIsConnected(false);
            StatusMonitor.setPsxMainIsDisconnected(true);
            StatusMonitor.setPsxMainUnableToConnect(false);
            StatusMonitor.setSimBridgeIsRunning(false);
        }
        catch (IOException e) {
            e.printStackTrace();
            StatusMonitor.setPsxMainIsConnected(false);
            StatusMonitor.setPsxMainIsDisconnected(true);
            StatusMonitor.setPsxMainUnableToConnect(false);
            StatusMonitor.setSimBridgeIsRunning(false);
        }
        try {
            if (SocketClientPSXMain.in != null) {
                SocketClientPSXMain.in.close();
            }
            if (SocketClientPSXMain.ou != null) {
                SocketClientPSXMain.ou.close();
            }
            if (SocketClientPSXMain.socket != null) {
                SocketClientPSXMain.socket.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
