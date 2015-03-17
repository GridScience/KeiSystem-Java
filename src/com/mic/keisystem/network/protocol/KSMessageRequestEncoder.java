package com.mic.keisystem.network.protocol;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import java.nio.ByteBuffer;

/**
 * Created by MIC/Headcrabbed on 2015/3/17.
 */
public final class KSMessageRequestEncoder implements ProtocolEncoder {

    @Override
    public void encode(IoSession ioSession, Object o, ProtocolEncoderOutput protocolEncoderOutput) throws Exception {
        KSMessageRequest request = (KSMessageRequest) o;
        IoBuffer buffer = IoBuffer.allocate(request.getMessage().getTotalSize(), false);
        byte[] messageData = request.getMessage().toByteArray();
        buffer.put(messageData);
        buffer.flip();
        protocolEncoderOutput.write(buffer);
    }

    @Override
    public void dispose(IoSession ioSession) throws Exception {
        // Do nothing since there are nothing to be disposed.
    }

}
