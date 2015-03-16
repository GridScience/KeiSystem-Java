package com.ffbit.bencode.dictionary;

import com.ffbit.bencode.BDecoderException;

public class DictionaryDecoderException extends BDecoderException {
    private static final long serialVersionUID = 1L;

    public DictionaryDecoderException(String message) {
        super(message);
    }

}
