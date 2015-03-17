package com.mic.keisystem.network.protocol;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * Created by MIC/Headcrabbed on 2015/3/17.
 */
public final class KSMessageResponseDecoder extends CumulativeProtocolDecoder {

    // We don't expect response.

    @Override
    protected boolean doDecode(IoSession ioSession, IoBuffer ioBuffer, ProtocolDecoderOutput protocolDecoderOutput) throws Exception {
        return true;
    }

}
