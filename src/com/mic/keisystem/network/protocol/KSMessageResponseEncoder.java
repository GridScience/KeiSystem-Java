package com.mic.keisystem.network.protocol;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * Created by MIC/Headcrabbed on 2015/3/17.
 */
public final class KSMessageResponseEncoder implements ProtocolEncoder {

    // We don't expect response.

    @Override
    public void encode(IoSession ioSession, Object o, ProtocolEncoderOutput protocolEncoderOutput) throws Exception {
    }

    @Override
    public void dispose(IoSession ioSession) throws Exception {
    }

}
