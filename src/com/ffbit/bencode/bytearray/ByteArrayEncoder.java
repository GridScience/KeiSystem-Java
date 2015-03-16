package com.ffbit.bencode.bytearray;

import com.ffbit.bencode.Encoder;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class ByteArrayEncoder implements Encoder<byte[]> {
    private OutputStream out;

    public ByteArrayEncoder() {
    }

    public ByteArrayEncoder(OutputStream out) {
        this.out = out;
    }

    @Override
    public boolean isApplicable(Object value) {
        return value instanceof byte[];
    }

    @Override
    public void encode(byte[] input) throws IOException {
        String prefix = String.valueOf(input.length) + STRING_SEPARATOR;

        out.write(prefix.getBytes());
        out.write(input);
    }

}
