package com.mic.keisystem;

/**
 * Created by MIC/Headcrabbed on 2015/3/14.
 */
public enum ByteOrder {

    bigEndian(0), littleEndian(1);

    public static final int BIG_ENDIAN = 0;

    public static final int LITTLE_ENDIAN = 1;

    ByteOrder(int order) {
        this.order = order;
    }

    private final int order;

    public int getOrder() {
        return this.order;
    }

}
