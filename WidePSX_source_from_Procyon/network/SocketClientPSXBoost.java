// 
// Decompiled by Procyon v0.5.36
// 

package network;

import simBridge.SimBridgeBase;
import java.awt.Component;
import javax.swing.JOptionPane;
import java.io.IOException;
import java.net.UnknownHostException;
import java.io.Reader;
import java.io.InputStreamReader;
import java.net.SocketException;
import util.StatusMonitor;
import java.io.BufferedReader;
import java.net.Socket;

public class SocketClientPSXBoost implements Runnable
{
    private static String ip;
    private static int port;
    private static Socket socket;
    private static BufferedReader in;
    private static Thread recvThread;
    
    static {
        SocketClientPSXBoost.ip = new String("");
        SocketClientPSXBoost.port = 0;
    }
    
    public SocketClientPSXBoost() {
        StatusMonitor.setPsxBoostIsConnected(false);
        StatusMonitor.setPsxBoostIsDisconnected(false);
        StatusMonitor.setPsxBoostUnableToConnect(false);
        SocketClientPSXBoost.recvThread = new Thread(this);
    }
    
    public static boolean setIp(final String argIp) {
        SocketClientPSXBoost.ip = argIp;
        return true;
    }
    
    public static boolean setPort(final String argPort) {
        try {
            SocketClientPSXBoost.port = Integer.valueOf(argPort);
            StatusMonitor.setPsxBoostInvalidIp(false);
            return true;
        }
        catch (NumberFormatException e) {
            StatusMonitor.setPsxBoostInvalidIp(true);
            return false;
        }
    }
    
    public static void connect() {
        try {
            SocketClientPSXBoost.socket = new Socket(SocketClientPSXBoost.ip, SocketClientPSXBoost.port);
            try {
                SocketClientPSXBoost.socket.setTcpNoDelay(true);
            }
            catch (SocketException e) {
                e.printStackTrace();
            }
            SocketClientPSXBoost.in = new BufferedReader(new InputStreamReader(SocketClientPSXBoost.socket.getInputStream()));
            StatusMonitor.setPsxBoostIsConnected(true);
            StatusMonitor.setPsxBoostIsDisconnected(false);
            StatusMonitor.setPsxBoostUnableToConnect(false);
            SocketClientPSXBoost.recvThread.start();
        }
        catch (UnknownHostException e3) {
            StatusMonitor.setPsxBoostIsConnected(false);
            StatusMonitor.setPsxBoostIsDisconnected(false);
            StatusMonitor.setPsxBoostUnableToConnect(true);
        }
        catch (IOException e4) {
            StatusMonitor.setPsxBoostIsConnected(false);
            StatusMonitor.setPsxBoostIsDisconnected(false);
            StatusMonitor.setPsxBoostUnableToConnect(true);
        }
        catch (IllegalThreadStateException e2) {
            e2.printStackTrace();
        }
    }
    
    @Override
    public void run() {
        try {
            String received = new String("");
            while (true) {
                if ((received = SocketClientPSXBoost.in.readLine()) != null) {
                    try {
                        if (received.equals("exit")) {
                            StatusMonitor.setPsxBoostIsConnected(false);
                            StatusMonitor.setPsxBoostIsDisconnected(true);
                            StatusMonitor.setPsxBoostUnableToConnect(false);
                            StatusMonitor.setSimBridgeIsRunning(false);
                            JOptionPane.showMessageDialog(null, "PSX Boost Server has disconnected. WidePSX will close.\n\"exit\" message received.", "Warning", 2);
                            System.exit(0);
                            break;
                        }
                        if (!StatusMonitor.getSimBridgeIsRunning()) {
                            continue;
                        }
                        SimBridgeBase.dispatchBoostData(received);
                    }
                    catch (StringIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        catch (Exception e2) {
            e2.printStackTrace();
            StatusMonitor.setPsxBoostIsConnected(false);
            StatusMonitor.setPsxBoostIsDisconnected(true);
            StatusMonitor.setPsxBoostUnableToConnect(false);
            StatusMonitor.setSimBridgeIsRunning(false);
            JOptionPane.showMessageDialog(null, "PSX Boost Server Exception received. WidePSX will close.\nException : " + e2.getMessage(), "Warning", 2);
            System.exit(0);
        }
    }
}
