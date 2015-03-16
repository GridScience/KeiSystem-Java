package com.mic.keisystem.tracker;

import com.mic.EventArgs;
import com.mic.keisystem.Peer;

import java.util.List;

/**
 * Created by MIC/Headcrabbed on 2015/3/15.
 */
public final class TrackerCommEventArgs extends EventArgs {

    public TrackerCommEventArgs(TrackerParameters trackerParameters, List<Peer> peerList) {
        this.trackerParameters = trackerParameters;
        this.peerList = peerList;
    }

    public TrackerParameters getTrackerParameters() {
        return trackerParameters;
    }

    private final TrackerParameters trackerParameters;

    public List<Peer> getPeerList() {
        return peerList;
    }

    private final List<Peer> peerList;

}
