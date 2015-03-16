package com.ffbit.bencode.string;

import com.ffbit.bencode.Decoder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static com.ffbit.bencode.Encoder.STRING_SEPARATOR;

public class StringDecoder implements Decoder<String> {
    private final InputStream in;
    private final Charset charset;

    private char current;
    private int length;

    public StringDecoder(InputStream in, Charset charset) {
        this.in = in;
        this.charset = charset;
    }

    @Override
    public boolean isApplicable(int b) {
        return Character.isDigit(b);
    }

    @Override
    public String decode() throws IOException {
        readLength();
        checkSeparator();
        byte[] bytes = new byte[length];
        in.read(bytes);

        return new String(bytes, charset);
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
            throw new StringDecoderException(
                    "Unexpected separator of string <" + current + ">, " +
                            "expected <" + STRING_SEPARATOR + ">");
        }
    }

}
