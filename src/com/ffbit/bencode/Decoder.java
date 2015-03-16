package com.ffbit.bencode;

import java.io.IOException;

public interface Decoder<T> {
    int EOF = -1;

    boolean isApplicable(int i);

    T decode() throws IOException;

}
