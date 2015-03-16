package com.ffbit.bencode.integer;

import com.ffbit.bencode.Decoder;

import java.io.IOException;
import java.io.InputStream;

import static com.ffbit.bencode.Encoder.INTEGER_PREFIX;
import static com.ffbit.bencode.Encoder.END_SUFFIX;

public class IntegerDecoder implements Decoder<Number> {
    private final char MINUS = '-';
    private final char PLUS = '+';

    private InputStream in;
    private StringBuilder sb;
    private char current;

    public IntegerDecoder(InputStream in) {
        this.in = in;
        sb = new StringBuilder();
    }

    @Override
    public boolean isApplicable(int b) {
        return b == INTEGER_PREFIX;
    }

    @Override
    public Number decode() throws IOException {
        checkPrefix();
        readSign();
        readDigits();
        checkSuffix();
        return toInteger();
    }

    private void checkPrefix() throws IOException {
        if (read() != INTEGER_PREFIX) {
            throw new IntegerDecoderException(
                    "Unexpected beginning of integer <" + current + ">, " +
                            "expected <" + INTEGER_PREFIX + ">");
        }
    }

    private char read() throws IOException {
        current = (char) in.read();
        return current;
    }

    private void readSign() throws IOException {
        if (read() == MINUS) {
            sb.append(current);
            read();
        } else {
            sb.append(PLUS);
        }
    }

    private void readDigits() throws IOException {
        while (Character.isDigit(current)) {
            sb.append(current);
            read();
        }
    }

    private void checkSuffix() throws IOException {
        if (current != END_SUFFIX) {
            throw new IntegerDecoderException(
                    "Unexpected end of integer <" + current + ">, " +
                            "expected <" + END_SUFFIX + ">");
        }
    }

    private Number toInteger() {
        if (sb.length() == 1) {
            throw new IntegerDecoderException("An empty integer occurred");
        }

        String numString = sb.toString();
        clear();

        return Long.valueOf(numString);
    }

    private void clear() {
        sb.setLength(0);
    }

}
