package com.ffbit.bencode.string;

import com.ffbit.bencode.BDecoderException;

public class StringDecoderException extends BDecoderException {
    private static final long serialVersionUID = 1L;

    public StringDecoderException(String message) {
        super(message);
    }

}
