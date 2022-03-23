// 
// Decompiled by Procyon v0.5.36
// 

package simBridge;

import java.io.IOException;
import network.BridgeSimConnect;
import util.StatusMonitor;

public class SimBridgeAntiIce
{
    public static void dispatchWdoHeat(final String argQh239, final String argQh240) throws IOException {
        if (StatusMonitor.getSimBridgeIsRunning()) {
            try {
                String Qh239String = argQh239;
                Qh239String = Qh239String.substring(Qh239String.indexOf(61) + 1);
                String Qh240String = argQh240;
                Qh240String = Qh240String.substring(Qh240String.indexOf(61) + 1);
                final int Qh239 = Integer.parseInt(Qh239String);
                final int Qh240 = Integer.parseInt(Qh240String);
                if (StatusMonitor.getSimBridgeIsRunning()) {
                    if (Qh239 == 1 || Qh240 == 1) {
                        BridgeSimConnect.updateWdoHeat(true);
                    }
                    else if (Qh239 != 1 && Qh240 != 1) {
                        BridgeSimConnect.updateWdoHeat(false);
                    }
                }
            }
            catch (NumberFormatException ex) {}
        }
    }
    
    public static void dispatchAntiIceEng1(final String argQh225, final String argQh229, final String argQi21) throws IOException {
        if (StatusMonitor.getSimBridgeIsRunning()) {
            try {
                String Qh225String = argQh225;
                Qh225String = Qh225String.substring(Qh225String.indexOf(61) + 1);
                String Qh229String = argQh229;
                Qh229String = Qh229String.substring(Qh229String.indexOf(61) + 1);
                String Qi21String = argQi21;
                Qi21String = Qi21String.substring(Qi21String.indexOf(61) + 1);
                final int Qh225 = Integer.parseInt(Qh225String);
                final int Qh226 = Integer.parseInt(Qh229String);
                final int Qi21 = Integer.parseInt(Qi21String);
                if (Qi21 == 0) {
                    if (Qh225 == 1) {
                        BridgeSimConnect.updateAntiIceEng1(true);
                    }
                    else {
                        BridgeSimConnect.updateAntiIceEng1(false);
                    }
                }
                else if (Qh226 != -1) {
                    BridgeSimConnect.updateAntiIceEng1(true);
                }
                else {
                    BridgeSimConnect.updateAntiIceEng1(false);
                }
            }
            catch (NumberFormatException ex) {}
        }
    }
    
    public static void dispatchAntiIceEng2(final String argQh226, final String argQh230, final String argQi21) throws IOException {
        if (StatusMonitor.getSimBridgeIsRunning()) {
            try {
                String Qh226String = argQh226;
                Qh226String = Qh226String.substring(Qh226String.indexOf(61) + 1);
                String Qh230String = argQh230;
                Qh230String = Qh230String.substring(Qh230String.indexOf(61) + 1);
                String Qi21String = argQi21;
                Qi21String = Qi21String.substring(Qi21String.indexOf(61) + 1);
                final int Qh226 = Integer.parseInt(Qh226String);
                final int Qh227 = Integer.parseInt(Qh230String);
                final int Qi21 = Integer.parseInt(Qi21String);
                if (Qi21 == 0) {
                    if (Qh226 == 1) {
                        BridgeSimConnect.updateAntiIceEng2(true);
                    }
                    else {
                        BridgeSimConnect.updateAntiIceEng2(false);
                    }
                }
                else if (Qh227 != -1) {
                    BridgeSimConnect.updateAntiIceEng2(true);
                }
                else {
                    BridgeSimConnect.updateAntiIceEng2(false);
                }
            }
            catch (NumberFormatException ex) {}
        }
    }
    
    public static void dispatchAntiIceEng3(final String argQh227, final String argQh231, final String argQi21) throws IOException {
        if (StatusMonitor.getSimBridgeIsRunning()) {
            try {
                String Qh227String = argQh227;
                Qh227String = Qh227String.substring(Qh227String.indexOf(61) + 1);
                String Qh231String = argQh231;
                Qh231String = Qh231String.substring(Qh231String.indexOf(61) + 1);
                String Qi21String = argQi21;
                Qi21String = Qi21String.substring(Qi21String.indexOf(61) + 1);
                final int Qh227 = Integer.parseInt(Qh227String);
                final int Qh228 = Integer.parseInt(Qh231String);
                final int Qi21 = Integer.parseInt(Qi21String);
                if (Qi21 == 0) {
                    if (Qh227 == 1) {
                        BridgeSimConnect.updateAntiIceEng3(true);
                    }
                    else {
                        BridgeSimConnect.updateAntiIceEng3(false);
                    }
                }
                else if (Qh228 != -1) {
                    BridgeSimConnect.updateAntiIceEng3(true);
                }
                else {
                    BridgeSimConnect.updateAntiIceEng3(false);
                }
            }
            catch (NumberFormatException ex) {}
        }
    }
    
    public static void dispatchAntiIceEng4(final String argQh228, final String argQh232, final String argQi21) throws IOException {
        if (StatusMonitor.getSimBridgeIsRunning()) {
            try {
                String Qh228String = argQh228;
                Qh228String = Qh228String.substring(Qh228String.indexOf(61) + 1);
                String Qh232String = argQh232;
                Qh232String = Qh232String.substring(Qh232String.indexOf(61) + 1);
                String Qi21String = argQi21;
                Qi21String = Qi21String.substring(Qi21String.indexOf(61) + 1);
                final int Qh228 = Integer.parseInt(Qh228String);
                final int Qh229 = Integer.parseInt(Qh232String);
                final int Qi21 = Integer.parseInt(Qi21String);
                if (Qi21 == 0) {
                    if (Qh228 == 1) {
                        BridgeSimConnect.updateAntiIceEng4(true);
                    }
                    else {
                        BridgeSimConnect.updateAntiIceEng4(false);
                    }
                }
                else if (Qh229 != -1) {
                    BridgeSimConnect.updateAntiIceEng4(true);
                }
                else {
                    BridgeSimConnect.updateAntiIceEng4(false);
                }
            }
            catch (NumberFormatException ex) {}
        }
    }
    
    public static void dispatchAntiIceWing(final String argQh233, final String argQh234, final String argQi22) throws IOException {
        if (StatusMonitor.getSimBridgeIsRunning()) {
            try {
                String Qh233String = argQh233;
                Qh233String = Qh233String.substring(Qh233String.indexOf(61) + 1);
                String Qh234String = argQh234;
                Qh234String = Qh234String.substring(Qh234String.indexOf(61) + 1);
                String Qi22String = argQi22;
                Qi22String = Qi22String.substring(Qi22String.indexOf(61) + 1);
                final int Qh233 = Integer.parseInt(Qh233String);
                final int Qh234 = Integer.parseInt(Qh234String);
                final int Qi22 = Integer.parseInt(Qi22String);
                if (Qi22 == 0) {
                    if (Qh233 == 1) {
                        BridgeSimConnect.updateAntiIceWing(true);
                    }
                    else {
                        BridgeSimConnect.updateAntiIceWing(false);
                    }
                }
                else if (Qh234 != -1) {
                    BridgeSimConnect.updateAntiIceWing(true);
                }
                else {
                    BridgeSimConnect.updateAntiIceWing(false);
                }
            }
            catch (NumberFormatException ex) {}
        }
    }
    
    public static void dispatchWipers(final String argQh235, final String argQh236) throws IOException {
        if (StatusMonitor.getSimBridgeIsRunning()) {}
    }
}
