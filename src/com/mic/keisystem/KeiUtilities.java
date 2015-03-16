package com.mic.keisystem;

import java.util.Dictionary;
import java.util.HashMap;

/**
 * Created by MIC/Headcrabbed on 2015/3/15.
 */
public final class KeiUtilities {

    public static HashMap<String, String> decomposeParameters(String parameters) {
        // Warning: DO simplify this method, please. Also, write a unit test to test the "contentType is always 1"
        // warning reported by the compiler.
        HashMap<String, String> hashMap = new HashMap<String, String>(16);
        int inside = -1; // -1: starting; '&'; '='
        int contentType = 0; // 0: key; 1: value
        char[] chars = parameters.toCharArray();
        StringBuilder sb1 = new StringBuilder(64);
        StringBuilder sb2 = new StringBuilder(64);
        for (int i = 0; i < chars.length; i++) {
            switch (chars[i]) {
                case '&': {
                    if (inside != -1 && inside != '&') {
                        String key = sb1.toString();
                        String value = sb2.toString();
                        if (key.length() > 0) {
                            hashMap.put(key, value);
                        }
                        inside = '&';
                    }
                    sb1.delete(0, sb1.length());
                    sb2.delete(0, sb2.length());
                    contentType = 0;
                    break;
                }
                case '=': {
                    if (inside != -1 && inside != '&' && inside != '=') {
                        contentType = 1;
                        inside = '=';
                    }
                    break;
                }
                default: {
                    inside = chars[i];
                    if (contentType == 0) {
                        // key
                        sb1.append(chars[i]);
                    } else if (contentType == 1) {
                        // value
                        sb2.append(chars[i]);
                    }
                    break;
                }
            }
        }
        if (sb1.length() > 0) {
            hashMap.put(sb1.toString(), sb2.toString());
        }
        return hashMap;
    }

    public static String unescapePartiallyEncodedString(String text) throws IllegalArgumentException {
        // 40 hex characters for 20-byte SHA-1
        StringBuilder stringBuilder = new StringBuilder(40);
        int i = 0;
        int textLength = text.length();
        while (i < textLength) {
            if (text.charAt(i) == '%') {
                // escaped
                if (i + 2 > textLength) {
                    throw new IllegalArgumentException("Illegal encoded string.");
                }
                i++;
                stringBuilder.append(ensureUpper(text.charAt(i++)));
                stringBuilder.append(ensureUpper(text.charAt(i++)));
            } else {
                // Handle it manually.
                int v = text.charAt(i);
                char c1, c2;
                c1 = getHex((v & 0xf0) >> 4);
                c2 = getHex(v & 0x0f);
                stringBuilder.append(c1);
                stringBuilder.append(c2);
                i++;
            }
        }
        return stringBuilder.toString();
    }

    private static char ensureUpper(char ch) {
        if ('a' <= ch && ch <= 'z') {
            return (char) (ch - 0x20);
        } else {
            return ch;
        }
    }

    private static char getHex(int code) {
        if (code > 0xf) {
            code %= 0x10;
        }
        if (code < 10) {
            return (char)('0' + code);
        }else {
            return (char)('A' + code - 10);
        }
    }

    public static boolean isHexDigit(char ch) {
        return ('0' <= ch && ch <= '9') || ('A' <= ch && ch <= 'F') || ('a' <= ch && ch <= 'f');
    }

    public static int fromHex(char ch) {
        if ('0' <= ch && ch <= '9') {
            return ch - '0';
        } else if ('A' <= ch && ch <= 'F') {
            return ch - 'A' + 10;
        } else if ('a' <= ch && ch <= 'f') {
            return ch - 'a' + 10;
        } else {
            return 0;
        }
    }

}
