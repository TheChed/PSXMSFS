// 
// Decompiled by Procyon v0.5.36
// 

package gndService;

import network.SocketClientPSXMain;
import util.Randomizer;
import util.StatusMonitor;

public class GndServicePreFltTimer implements Runnable
{
    @Override
    public void run() {
        boolean refuelOk = false;
        boolean boardingOk = false;
        boolean groundDisconnectedOk = false;
        if (StatusMonitor.getGndServiceRefuelCompletePhase()) {
            refuelOk = true;
            boardingOk = true;
        }
        else if (StatusMonitor.getGndServicePushReadyPhase()) {
            if (GndServiceDeIcing.getIcing()) {
                GndServiceDeIcing.deIce();
                GndServiceFlags.setDeIcingFlag(11);
            }
            return;
        }
        final long INITIALTIME = System.currentTimeMillis();
        final long PREFLIGHTDURATION = GndServiceBase.getMinutesToBlkOut() * 60000L;
        final long BLOCKOUTTIME = INITIALTIME + PREFLIGHTDURATION;
        long removeTime = INITIALTIME + PREFLIGHTDURATION * 80L / 100L;
        if (removeTime < BLOCKOUTTIME - 600000L) {
            removeTime = BLOCKOUTTIME - 600000L;
        }
        else if (removeTime > BLOCKOUTTIME - 120000L) {
            removeTime = BLOCKOUTTIME - 120000L;
        }
        long theoricRefuelTime = INITIALTIME + PREFLIGHTDURATION * 10L / 100L;
        long realRefuelTime = 0L;
        if (theoricRefuelTime < BLOCKOUTTIME - 2400000L) {
            theoricRefuelTime = BLOCKOUTTIME - 2400000L;
        }
        else if (theoricRefuelTime > BLOCKOUTTIME - 1200000L) {
            theoricRefuelTime = BLOCKOUTTIME - 1200000L;
        }
        boolean refuelDelay = false;
        final int randRefuelDelay = Randomizer.getRand(20);
        long refuelDelayMilliseconds = 0L;
        if (randRefuelDelay == 1) {
            refuelDelay = true;
            refuelDelayMilliseconds = 60000L * Randomizer.getBoundRand(5, 10);
            realRefuelTime = theoricRefuelTime + refuelDelayMilliseconds;
        }
        else {
            realRefuelTime = theoricRefuelTime;
        }
        long boardingStartTime = INITIALTIME + PREFLIGHTDURATION * 20L / 100L;
        if (boardingStartTime < BLOCKOUTTIME - 2100000L) {
            boardingStartTime = BLOCKOUTTIME - 2100000L;
        }
        else if (boardingStartTime > BLOCKOUTTIME - 1500000L) {
            boardingStartTime = BLOCKOUTTIME - 1500000L;
        }
        long theoricBoardingEndTime = boardingStartTime + 1800000L;
        long realBoardingEndTime = 0L;
        if (theoricBoardingEndTime > BLOCKOUTTIME - 60000L) {
            theoricBoardingEndTime = BLOCKOUTTIME - 60000L;
        }
        boolean boardingDelay = false;
        final int randBoardingDelay = Randomizer.getRand(20);
        long boardingDelayMilliseconds = 0L;
        if (randBoardingDelay == 1) {
            boardingDelay = true;
            boardingDelayMilliseconds = 60000L * Randomizer.getBoundRand(5, 10);
            realBoardingEndTime = theoricBoardingEndTime + boardingDelayMilliseconds;
        }
        else {
            realBoardingEndTime = theoricBoardingEndTime;
        }
        while (true) {
            final long currentTime = System.currentTimeMillis();
            if (!GndServiceBase.getOnGround()) {
                GndServiceBase.setDynText("Info : Waiting for landing -> Set PRK BRK, Engines OFF, Beacon OFF");
                break;
            }
            if (currentTime >= theoricRefuelTime && GndServiceFlags.getRefuelFlag() == 0) {
                if (refuelDelay) {
                    while (GndServiceBase.getConversation()) {
                        try {
                            Thread.sleep(1000L);
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    GndServiceBase.setConversation(true);
                    SocketClientPSXMain.send("Qi101=71");
                    GndServiceFlags.setRefuelFlag(1);
                    GndServiceBase.setDynText("FLT : Answer the Ground Engineer call \"Go ahead Ground\"");
                }
                else {
                    while (GndServiceBase.getConversation()) {
                        try {
                            Thread.sleep(1000L);
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    GndServiceBase.setConversation(true);
                    SocketClientPSXMain.send("Qi101=71");
                    GndServiceFlags.setRefuelFlag(5);
                    GndServiceBase.setDynText("FLT : Answer the Ground Engineer call \"Go ahead Ground\"");
                    GndServiceBase.setRefuelStatus(true);
                    GndServiceBase.setRefuelCompleted(false);
                }
            }
            if (refuelDelay && currentTime >= realRefuelTime && GndServiceFlags.getRefuelFlag() == 4) {
                while (GndServiceBase.getConversation()) {
                    try {
                        Thread.sleep(1000L);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                GndServiceBase.setConversation(true);
                SocketClientPSXMain.send("Qi101=71");
                GndServiceFlags.setRefuelFlag(5);
                GndServiceBase.setDynText("FLT : Answer the Ground Engineer call \"Go ahead Ground\"");
                GndServiceBase.setRefuelStatus(true);
                GndServiceBase.setRefuelCompleted(false);
            }
            if (GndServiceBase.getRefuelStatus() && GndServiceBase.getRefuelCompleted()) {
                while (GndServiceBase.getConversation()) {
                    try {
                        Thread.sleep(1000L);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                GndServiceBase.setConversation(true);
                GndServiceBase.setRefuelStatus(false);
                SocketClientPSXMain.send("Qi101=71");
                GndServiceFlags.setRefuelFlag(9);
                GndServiceBase.setDynText("FLT : Answer the Ground Engineer call \"Go ahead Ground\"");
            }
            if (GndServiceBase.getAcftVersion() == 0) {
                if (currentTime >= boardingStartTime && GndServiceFlags.getBoardingFlag() == 0) {
                    while (GndServiceBase.getConversation()) {
                        try {
                            Thread.sleep(1000L);
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    GndServiceBase.setConversation(true);
                    SocketClientPSXMain.send("Qi102=11");
                    GndServiceFlags.setBoardingFlag(1);
                    GndServiceBase.setDynText("CAB : Answer the Cabin Crew call \"Go ahead Cabin\"");
                }
            }
            else if (GndServiceFlags.getBoardingFlag() == 0) {
                GndServiceFlags.setBoardingFlag(1);
            }
            if (GndServiceBase.getAcftVersion() == 0) {
                if (currentTime >= theoricBoardingEndTime && GndServiceFlags.getBoardingFlag() == 5) {
                    if (boardingDelay) {
                        while (GndServiceBase.getConversation()) {
                            try {
                                Thread.sleep(1000L);
                            }
                            catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        GndServiceBase.setConversation(true);
                        SocketClientPSXMain.send("Qi102=11");
                        GndServiceFlags.setBoardingFlag(6);
                        GndServiceBase.setDynText("CAB : Answer the Cabin Crew call \"Go ahead Cabin\"");
                    }
                    else {
                        while (GndServiceBase.getConversation()) {
                            try {
                                Thread.sleep(1000L);
                            }
                            catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        GndServiceBase.setConversation(true);
                        SocketClientPSXMain.send("Qi102=11");
                        GndServiceFlags.setBoardingFlag(10);
                        GndServiceBase.setDynText("CAB : Answer the Cabin Crew call \"Go ahead Cabin\"");
                    }
                }
                if (boardingDelay && currentTime >= realBoardingEndTime && GndServiceFlags.getBoardingFlag() == 9) {
                    while (GndServiceBase.getConversation()) {
                        try {
                            Thread.sleep(1000L);
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    GndServiceBase.setConversation(true);
                    SocketClientPSXMain.send("Qi102=11");
                    GndServiceFlags.setBoardingFlag(10);
                    GndServiceBase.setDynText("CAB : Answer the Cabin Crew call \"Go ahead Cabin\"");
                }
            }
            else {
                if (currentTime >= theoricBoardingEndTime && GndServiceFlags.getBoardingFlag() == 1) {
                    if (boardingDelay) {
                        while (GndServiceBase.getConversation()) {
                            try {
                                Thread.sleep(1000L);
                            }
                            catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        GndServiceBase.setConversation(true);
                        SocketClientPSXMain.send("Qi114=4");
                        GndServiceFlags.setBoardingFlag(14);
                        GndServiceBase.setDynText("FLT : Answer the Ground Engineer call \"Go ahead Ground\"");
                    }
                    else {
                        while (GndServiceBase.getConversation()) {
                            try {
                                Thread.sleep(1000L);
                            }
                            catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        GndServiceBase.setConversation(true);
                        SocketClientPSXMain.send("Qi114=4");
                        GndServiceFlags.setBoardingFlag(18);
                        GndServiceBase.setDynText("FLT : Answer the Ground Engineer call \"Go ahead Ground\"");
                    }
                }
                if (boardingDelay && currentTime >= realBoardingEndTime && GndServiceFlags.getBoardingFlag() == 17) {
                    while (GndServiceBase.getConversation()) {
                        try {
                            Thread.sleep(1000L);
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    GndServiceBase.setConversation(true);
                    SocketClientPSXMain.send("Qi114=4");
                    GndServiceFlags.setBoardingFlag(18);
                    GndServiceBase.setDynText("FLT : Answer the Ground Engineer call \"Go ahead Ground\"");
                }
            }
            if (currentTime >= removeTime && GndServiceFlags.getPowerFlag() == 1) {
                while (GndServiceBase.getConversation()) {
                    try {
                        Thread.sleep(1000L);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                GndServiceBase.setConversation(true);
                SocketClientPSXMain.send("Qi101=71");
                GndServiceFlags.setPowerFlag(2);
                GndServiceBase.setDynText("FLT : Answer the Ground Engineer call \"Go ahead Ground\"");
            }
            else if (currentTime >= removeTime && refuelOk && boardingOk && GndServiceFlags.getPowerFlag() == 0) {
                GndServiceBase.setDynText("Set APU PWR ON, APU BLEED ON and wait...");
            }
            if (GndServiceFlags.getPowerFlag() == 6) {
                groundDisconnectedOk = true;
            }
            if (GndServiceFlags.getBoardingFlag() == 13 || GndServiceFlags.getBoardingFlag() == 21) {
                boardingOk = true;
            }
            if (GndServiceFlags.getRefuelFlag() == 12) {
                refuelOk = true;
            }
            if (refuelOk && boardingOk && groundDisconnectedOk && GndServiceFlags.getMiscFlag() == 0) {
                GndServiceFlags.setMiscFlag(1);
                GndServiceBase.setDynText("Hydraulics pressurization clearance : call the Ground Engineer : \"Cockpit to Ground\"");
            }
            if (refuelOk && boardingOk && groundDisconnectedOk && GndServiceFlags.getMiscFlag() == 6 && GndServiceFlags.getDeIcingFlag() == 0) {
                if (GndServiceDeIcing.getIcing()) {
                    GndServiceFlags.setDeIcingFlag(1);
                }
                else {
                    GndServiceFlags.setDeIcingFlag(11);
                }
            }
            if (GndServiceFlags.getDeIcingFlag() == 1) {
                while (GndServiceBase.getConversation()) {
                    try {
                        Thread.sleep(1000L);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(15000L);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                GndServiceBase.setConversation(true);
                SocketClientPSXMain.send("Qi101=71");
                GndServiceFlags.setDeIcingFlag(2);
                GndServiceBase.setDynText("FLT : Answer the Ground Engineer call \"Go ahead Ground\"");
            }
            if (GndServiceFlags.getDeIcingFlag() == 7) {
                while (GndServiceBase.getConversation()) {
                    try {
                        Thread.sleep(1000L);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                GndServiceBase.setConversation(true);
                SocketClientPSXMain.send("Qi101=71");
                GndServiceFlags.setDeIcingFlag(8);
                GndServiceBase.setDynText("FLT : Answer the Ground Engineer call \"Go ahead Ground\"");
            }
            if (refuelOk && boardingOk && groundDisconnectedOk && GndServiceFlags.getDeIcingFlag() == 11) {
                break;
            }
            try {
                Thread.sleep(1000L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
