package com.ffbit.bencode;

import com.ffbit.bencode.bytearray.ByteArrayEncoder;
import com.ffbit.bencode.dictionary.DictionaryEncoder;
import com.ffbit.bencode.integer.IntegerEncoder;
import com.ffbit.bencode.list.ListEncoder;
import com.ffbit.bencode.string.StringEncoder;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

@SuppressWarnings("rawtypes")
public class BEncoder implements Encoder<Object> {
    private Encoder stringEncoder;
    private Encoder integerEncoder;
    private Encoder dictionaryEncoder;
    private Encoder listEncoder;
    private Encoder byteArrayEncoder;

    public BEncoder(OutputStream out) {
        this(out, DEFAULT_CHARSET);
    }

    public BEncoder(OutputStream out, Charset charset) {
        stringEncoder = new StringEncoder(out, charset);
        integerEncoder = new IntegerEncoder(out);
        byteArrayEncoder = new ByteArrayEncoder(out);
        dictionaryEncoder = new DictionaryEncoder(this, out);
        listEncoder = new ListEncoder(this, out);
    }

    @Override
    public boolean isApplicable(Object value) {
        return stringEncoder.isApplicable(value)
                || integerEncoder.isApplicable(value)
                || byteArrayEncoder.isApplicable(value)
                || listEncoder.isApplicable(value)
                || dictionaryEncoder.isApplicable(value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void encode(Object input) throws IOException {
        if (!isApplicable(input)) {
            throw new BEncoderException("An unsupported input occurred <" + input + ">");
        }

        if (stringEncoder.isApplicable(input)) {
            stringEncoder.encode(input);
        } else if (integerEncoder.isApplicable(input)) {
            integerEncoder.encode(input);
        } else if (byteArrayEncoder.isApplicable(input)) {
            byteArrayEncoder.encode(input);
        } else if (listEncoder.isApplicable(input)) {
            listEncoder.encode(input);
        } else if (dictionaryEncoder.isApplicable(input)) {
            dictionaryEncoder.encode(input);
        }
    }

}
