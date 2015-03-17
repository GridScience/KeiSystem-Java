package com.mic.keisystem.network.protocol;

import com.mic.BitConverter;
import com.mic.keisystem.InvalidKSMessageException;
import com.mic.keisystem.KSMessage;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.io.IOException;

/**
 * Created by MIC/Headcrabbed on 2015/3/17.
 */
public final class KSMessageRequestDecoder extends CumulativeProtocolDecoder {

    // See: http://mina.apache.org/mina-project/userguide/ch9-codec-filter/ch9-codec-filter.html

    @Override
    protected boolean doDecode(IoSession ioSession, IoBuffer ioBuffer, ProtocolDecoderOutput protocolDecoderOutput) throws Exception {
        // 前4个字节是长度说明符（int），参数 i 为说明符长度
        if (ioBuffer.prefixedDataAvailable(4)) {
            KSMessage message = readMessage(ioBuffer);
            KSMessageRequest request = new KSMessageRequest(message);
            protocolDecoderOutput.write(request);
            return true;
        } else {
            return false;
        }
    }

    private KSMessage readMessage(IoBuffer ioBuffer) throws IOException, InvalidKSMessageException {
        return KSMessage.fromByteArray(ioBuffer.asInputStream());
    }

}
