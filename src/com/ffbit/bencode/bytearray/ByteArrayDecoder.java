package com.ffbit.bencode.bytearray;

import com.ffbit.bencode.Decoder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static com.ffbit.bencode.Encoder.STRING_SEPARATOR;

public class ByteArrayDecoder implements Decoder<byte[]> {
    private final InputStream in;

    private char current;
    private int length;

    public ByteArrayDecoder(InputStream in) {
        this.in = in;
    }

    @Override
    public boolean isApplicable(int b) {
        return Character.isDigit(b);
    }

    @Override
    public byte[] decode() throws IOException {
        readLength();
        checkSeparator();
        byte[] bytes = new byte[length];
        in.read(bytes);

        return bytes;
    }

    private void readLength() throws IOException {
        StringBuilder sb = new StringBuilder();

        while (Character.isDigit(read())) {
            sb.append(current);
        }

        length = Integer.valueOf(sb.toString());
    }

    private char read() throws IOException {
        current = (char) in.read();
        return current;
    }

    private void checkSeparator() {
        if (current != STRING_SEPARATOR) {
            throw new ByteArrayDecoderException(
                    "Unexpected separator of string <" + current + ">, " +
                            "expected <" + STRING_SEPARATOR + ">");
        }
    }

}
