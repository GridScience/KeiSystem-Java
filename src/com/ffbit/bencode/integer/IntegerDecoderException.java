package com.ffbit.bencode.integer;

import com.ffbit.bencode.BDecoderException;

public class IntegerDecoderException extends BDecoderException {
    private static final long serialVersionUID = 1L;

    public IntegerDecoderException(String message) {
        super(message);
    }

}
