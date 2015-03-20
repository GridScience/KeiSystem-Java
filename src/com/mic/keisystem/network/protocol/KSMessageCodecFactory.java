package com.mic.keisystem.network.protocol;

import com.mic.keisystem.KSMessage;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * Created by MIC/Headcrabbed on 2015/3/17.
 */
public final class KSMessageCodecFactory implements ProtocolCodecFactory {

    private ProtocolEncoder encoder;
    private ProtocolDecoder decoder;

    public KSMessageCodecFactory() {
        this.encoder = new KSMessageRequestEncoder();
        this.decoder = new KSMessageRequestDecoder();
    }

    @Override
    public ProtocolEncoder getEncoder(IoSession ioSession) throws Exception {
        return encoder;
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession ioSession) throws Exception {
        return decoder;
    }

}
