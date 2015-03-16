package com.ffbit.bencode.bytearray;

import com.ffbit.bencode.BDecoderException;

public class ByteArrayDecoderException extends BDecoderException {
    private static final long serialVersionUID = 1L;

    public ByteArrayDecoderException(String message) {
        super(message);
    }

}
