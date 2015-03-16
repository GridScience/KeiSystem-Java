package com.ffbit.bencode;

import com.ffbit.bencode.bytearray.ByteArrayDecoder;
import com.ffbit.bencode.dictionary.DictionaryDecoder;
import com.ffbit.bencode.integer.IntegerDecoder;
import com.ffbit.bencode.list.ListDecoder;
import com.ffbit.bencode.string.StringDecoder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static com.ffbit.bencode.Decoder.EOF;

@SuppressWarnings("rawtypes")
public class BDecoder implements Iterable<Object>, Iterator<Object> {
    private InputStream in;

    //private Decoder stringDecoder;
    private Decoder integerDecoder;
    private Decoder listDecoder;
    private Decoder dictionaryDecoder;
    private Decoder byteArrayDecoder;
    private int current = EOF;

    public BDecoder(InputStream in) {
        this(in, Encoder.DEFAULT_CHARSET);
    }

    public BDecoder(InputStream in, Charset charset) {
        this.in = in;
        listDecoder = new ListDecoder(in, this);
        dictionaryDecoder = new DictionaryDecoder(in, this, charset);
        integerDecoder = new IntegerDecoder(in);
        //stringDecoder = new StringDecoder(in, charset);
        byteArrayDecoder = new ByteArrayDecoder(in);
    }

    public Object decode() throws IOException {
        if (!hasNext()) {
            throw new NoSuchElementException("There are no elements left");
        }

        Decoder decoder;

        if (listDecoder.isApplicable(current)) {
            decoder = listDecoder;
        } else if (dictionaryDecoder.isApplicable(current)) {
            decoder = dictionaryDecoder;
        } else if (integerDecoder.isApplicable(current)) {
            decoder = integerDecoder;
        } /* else if (stringDecoder.isApplicable(current)) {
            decoder = stringDecoder;
        } */ else if (byteArrayDecoder.isApplicable(current)) {
            decoder = byteArrayDecoder;
        } else {
            throw new BDecoderException("An unsupported symbol <" + (char) current + "> occurred");
        }

        return decoder.decode();
    }

    @Override
    public Iterator<Object> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        try {
            in.mark(1);
            current = in.read();
            in.reset();

            return current != EOF;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public Object next() {
        try {
            return decode();
        } catch (IOException e) {
            throw new BDecoderException(e.getMessage(), e);
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException(getClass() + " is read only.");
    }

}
