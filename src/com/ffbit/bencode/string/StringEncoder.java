package com.ffbit.bencode.string;

import com.ffbit.bencode.Encoder;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class StringEncoder implements Encoder<String> {
    private OutputStream out;
    private Charset charset;

    public StringEncoder(OutputStream out, Charset charset) {
        this.out = out;
        this.charset = charset;
    }

    @Override
    public boolean isApplicable(Object value) {
        return value instanceof String;
    }

    @Override
    public void encode(String input) throws IOException {
        byte[] content = input.getBytes(charset);
        String prefix = String.valueOf(content.length) + STRING_SEPARATOR;

        out.write(prefix.getBytes(charset));
        out.write(content);
    }

    public void encode(byte[] input) throws IOException {
        String prefix = String.valueOf(input.length) + STRING_SEPARATOR;

        out.write(prefix.getBytes(charset));
        out.write(input);
    }

}
