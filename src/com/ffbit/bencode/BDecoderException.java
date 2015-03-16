package com.ffbit.bencode;

public class BDecoderException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public BDecoderException(String message) {
        super(message);
    }

    public BDecoderException(String message, Throwable cause) {
        super(message, cause);
    }

}
