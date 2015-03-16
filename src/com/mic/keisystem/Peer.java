package com.mic.keisystem;

import com.sun.istack.internal.Nullable;

/**
 * Created by MIC/Headcrabbed on 2015/3/14.
 */
public final class Peer {

    private KEndpoint endpoint;

    private String peerID;

    private Peer() {
    }

    public static Peer create(KEndpoint endpoint) {
        return create(endpoint, null);
    }

    public static Peer create(KEndpoint endpoint, @Nullable String peerID) {
        Peer peer = new Peer();
        peer.endpoint = endpoint.clone();
        peer.peerID = peerID;
        return peer;
    }

    public String getPeerID() {
        return this.peerID;
    }

    public KEndpoint getEndpoint() {
        return this.endpoint;
    }

    public static boolean equals(Peer peer1, Peer peer2) {
        if (peer1 == null || peer2 == null) {
            return false;
        }
        return KEndpoint.equals(peer1.endpoint, peer2.endpoint);
    }

    public byte[] toByteArray() {
        return this.toByteArray(ByteOrder.bigEndian);
    }

    public byte[] toByteArray(ByteOrder byteOrder) {
        return this.endpoint.toByteArray(byteOrder);
    }

    public static final int SIZE_IN_BYTES = KEndpoint4.SIZE_IN_BYTES;

    @Override
    public String toString() {
        if (peerID == null) {
            return endpoint.toString();
        } else {
            return endpoint.toString() + ", ID: " + peerID;
        }
    }

}
