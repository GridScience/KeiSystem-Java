package com.ffbit.bencode.list;

import com.ffbit.bencode.BDecoderException;

public class ListDecoderException extends BDecoderException {
    private static final long serialVersionUID = 1L;

    public ListDecoderException(String message) {
        super(message);
    }

}
