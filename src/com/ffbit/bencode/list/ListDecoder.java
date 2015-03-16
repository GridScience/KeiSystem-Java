package com.ffbit.bencode.list;

import com.ffbit.bencode.BDecoder;
import com.ffbit.bencode.Decoder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.ffbit.bencode.Encoder.LIST_PREFIX;
import static com.ffbit.bencode.Encoder.END_SUFFIX;

public class ListDecoder implements Decoder<List> {
    private BDecoder parent;
    private InputStream in;
    private char current;

    public ListDecoder(BDecoder parent) {
        this.parent = parent;
    }

    public ListDecoder(InputStream in, BDecoder parent) {
        this(parent);
        this.in = in;
    }

    @Override
    public boolean isApplicable(int b) {
        return b == LIST_PREFIX;
    }

    @Override
    public List<Object> decode() throws IOException {
        checkPrefix();
        List<Object> content = readContent();
        checkSuffix();
        return content;
    }

    private void checkPrefix() throws IOException {
        if (read() != LIST_PREFIX) {
            throw new ListDecoderException(
                    "Unexpected beginning of list <" + current + ">, " +
                            "expected <" + LIST_PREFIX + ">");
        }

    }

    private char read() throws IOException {
        current = (char) in.read();
        return current;
    }

    private List<Object> readContent() throws IOException {
        List<Object> content = new ArrayList<Object>();

        in.mark(1);
        while (read() != END_SUFFIX && current != EOF) {
            in.reset();
            Object element = parent.decode();
            content.add(element);
            in.mark(1);
        }
        in.reset();

        return content;
    }

    private void checkSuffix() throws IOException {
        if (read() != END_SUFFIX) {
            throw new ListDecoderException(
                    "Unexpected end of list <" + current + ">, " +
                            "expected <" + END_SUFFIX + ">");
        }
    }

}
