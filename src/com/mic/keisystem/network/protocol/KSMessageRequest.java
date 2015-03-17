package com.mic.keisystem.network.protocol;

import com.mic.keisystem.KSMessage;

/**
 * Created by MIC/Headcrabbed on 2015/3/17.
 */
public final class KSMessageRequest {

    private KSMessage message;

    public KSMessageRequest(KSMessage message) {
        this.message = message;
    }

    public KSMessage getMessage() {
        return message;
    }
}
