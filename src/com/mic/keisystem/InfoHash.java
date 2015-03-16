package com.mic.keisystem;

/**
 * Created by Wallace on 2015/3/13.
 */
public final class InfoHash {

    public static final InfoHash EMPTY = new InfoHash();

    private byte[] dataBytes = null;

    private String hexStringCache = null;

    private InfoHash() {
        this.dataBytes = new byte[20];
    }

    public static InfoHash fromByteArray(byte[] dataBytes) throws IllegalArgumentException {
        if (dataBytes == null || dataBytes.length != 20) {
            throw new IllegalArgumentException("dataBytes must be an 20-item byte array.");
        }
        InfoHash infoHash = new InfoHash();
        /*
        for (int i = 0; i < 20; i++) {
            infoHash.dataBytes[i] = dataBytes[i];
        }
        */
        System.arraycopy(dataBytes, 0, infoHash.dataBytes, 0, 20);
        return infoHash;
    }

    public static InfoHash fromHexString(String hexString) throws IllegalArgumentException {
        if (hexString == null || hexString.length() != 40) {
            throw new IllegalArgumentException("hexString must be a 40-character string.");
        }
        byte[] buffer = new byte[20];
        for (int i = 0; i < 20; i++) {
            if (!(KeiUtilities.isHexDigit(hexString.charAt(i * 2)) && KeiUtilities.isHexDigit(hexString.charAt(i * 2 + 1)))) {
                throw new IllegalArgumentException("hexString has a wrong format.");
            }
            buffer[i] = (byte) ((KeiUtilities.fromHex(hexString.charAt(i * 2)) << 4) + KeiUtilities.fromHex(hexString.charAt(i * 2 + 1)));
        }
        InfoHash infoHash = new InfoHash();
        infoHash.dataBytes = buffer;
        return infoHash;
    }

    public String toHexString() {
        if (this.hexStringCache == null) {
            StringBuilder sb = new StringBuilder(40);
            byte b1, b2;
            char c1, c2;
            for (int i = 0; i < 20; i++) {
                b1 = (byte) ((dataBytes[i] >> 4) & 0xf);
                b2 = (byte) (dataBytes[i] & 0xf);
                c1 = b1 > 9 ? (char) (b1 + (byte) 'A' - 10) : (char) (b1 + (byte) '0');
                c2 = b2 > 9 ? (char) (b2 + (byte) 'A' - 10) : (char) (b2 + (byte) '0');
                sb.append(c1);
                sb.append(c2);
            }
            this.hexStringCache = sb.toString();
        }
        return this.hexStringCache;
    }

    public byte[] toByteArray() {
        return this.dataBytes.clone();
    }

    @Override
    public String toString() {
        return toHexString();
    }

    @Override
    public int hashCode() {
        return toHexString().hashCode();
    }

    public static boolean equals(InfoHash infoHash1, InfoHash infoHash2) {
        if (infoHash1 == null || infoHash2 == null) {
            return false;
        }
        return infoHash1.hashCode() == infoHash2.hashCode();
    }

}
