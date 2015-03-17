package com.mic.keisystem.tracker;

import com.mic.keisystem.InfoHash;
import com.mic.keisystem.KSUtilities;

import java.util.HashMap;

/**
 * Created by MIC/Headcrabbed on 2015/3/15.
 */
public final class TrackerParameters {

    private final InfoHash infoHash;

    private int portNumber;

    private TaskStatus taskStatus;

    private boolean compact;

    private boolean noPeerID;

    private String peerIDString;

    public InfoHash getInfoHash() {
        return infoHash;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public boolean isCompact() {
        return compact;
    }

    public boolean isNoPeerID() {
        return noPeerID;
    }

    public String getPeerIDString() {
        return peerIDString;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public void setCompact(boolean compact) {
        this.compact = compact;
    }

    public void setNoPeerID(boolean noPeerID) {
        this.noPeerID = noPeerID;
    }

    public void setPeerIDString(String peerIDString) {
        this.peerIDString = peerIDString;
    }

    private TrackerParameters(InfoHash infoHash, int portNumber, TaskStatus taskStatus, boolean compact,
                              boolean noPeerID, String peerIDString) {
        this.infoHash = infoHash;
        this.portNumber = portNumber;
        this.taskStatus = taskStatus;
        this.compact = compact;
        this.noPeerID = noPeerID;
        this.peerIDString = peerIDString;
    }

    public static TrackerParameters resolve(HashMap<String, String> parameters) {
        // Resolve request parameters.
        // Remember that these statements may throw exceptions.
        String infoHashString = KSUtilities.unescapePartiallyEncodedString(parameters.get("info_hash"));
        System.out.println(infoHashString);
        InfoHash infoHash = InfoHash.fromHexString(infoHashString);
        int portNumber = Integer.parseInt(parameters.get("port"));
        TaskStatus taskStatus;
        if (parameters.containsKey("event")) {
            String s = parameters.get("event").toLowerCase();
            if (s.equals("started")) {
                taskStatus = TaskStatus.started;
            } else if (s.equals("stopped")) {
                taskStatus = TaskStatus.stopped;
            } else if (s.equals("paused")) {
                taskStatus = TaskStatus.paused;
            } else {
                taskStatus = TaskStatus.none;
            }
        } else {
            taskStatus = TaskStatus.none;
        }
        boolean compact = false;
        if (parameters.containsKey("compact")) {
            int n = Integer.parseInt(parameters.get("compact"));
            compact = n != 0;
        }
        boolean noPeerID = false;
        if (parameters.containsKey("no_peer_id")) {
            int n = Integer.parseInt(parameters.get("no_peer_id"));
            noPeerID = n != 0;
        }
        String peerIDString = KSUtilities.unescapePartiallyEncodedString(parameters.get("peer_id"));

        return new TrackerParameters(infoHash, portNumber, taskStatus, compact, noPeerID, peerIDString);
    }

}
