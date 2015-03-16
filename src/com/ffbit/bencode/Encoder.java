package com.ffbit.bencode;

import java.io.IOException;
import java.nio.charset.Charset;

public interface Encoder<T> {
    char INTEGER_PREFIX = 'i';
    char LIST_PREFIX = 'l';
    char DICTIONARY_PREFIX = 'd';
    char END_SUFFIX = 'e';
    char STRING_SEPARATOR = ':';

    Charset DEFAULT_CHARSET = Charset.forName("US-ASCII");

    boolean isApplicable(Object value);

    void encode(T input) throws IOException;

}
