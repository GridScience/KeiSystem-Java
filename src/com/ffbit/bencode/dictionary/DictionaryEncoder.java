package com.ffbit.bencode.dictionary;

import com.ffbit.bencode.BEncoder;
import com.ffbit.bencode.Encoder;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DictionaryEncoder implements Encoder<Map<String, ?>> {
    private final BEncoder parent;
    private final OutputStream out;

    public DictionaryEncoder(BEncoder parent, OutputStream out) {
        this.parent = parent;
        this.out = out;
    }

    @Override
    public boolean isApplicable(Object value) {
        return value instanceof Map;
    }

    @Override
    public void encode(Map<String, ?> input) throws IOException {
        out.write(DICTIONARY_PREFIX);

        for (String key : getSortedKeys(input)) {
            parent.encode(key);
            parent.encode(input.get(key));
        }

        out.write(END_SUFFIX);
    }

    private List<String> getSortedKeys(Map<String, ?> input) {
        List<String> keys = new ArrayList<String>(input.keySet());
        Collections.sort(keys);

        return keys;
    }

}
