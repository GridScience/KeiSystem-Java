package com.mic;

/**
 * Created by MIC/Headcrabbed on 2015/3/14.
 * 转换全为 little endian（低位在前: b[0] b[1] b[2] b[3]）。
 */
public final class BitConverter {

    public static byte[] getBytes(long value) {
        byte[] buffer = new byte[8];
        int offset = 0;
        buffer[offset++] = (byte) value;
        buffer[offset++] = (byte) (value >> 8);
        buffer[offset++] = (byte) (value >> 16);
        buffer[offset++] = (byte) (value >> 24);
        buffer[offset++] = (byte) (value >> 32);
        buffer[offset++] = (byte) (value >> 40);
        buffer[offset++] = (byte) (value >> 48);
        buffer[offset++] = (byte) (value >> 56);
        return buffer;
    }

    public static byte[] getBytes(int value) {
        byte[] buffer = new byte[4];
        int offset = 0;
        buffer[offset++] = (byte) value;
        buffer[offset++] = (byte) (value >> 8);
        buffer[offset++] = (byte) (value >> 16);
        buffer[offset++] = (byte) (value >> 24);
        return buffer;
    }

    public static byte[] getBytes(short value) {
        byte[] buffer = new byte[2];
        int offset = 0;
        buffer[offset++] = (byte) value;
        buffer[offset++] = (byte) (value >> 8);
        return buffer;
    }

    public static byte[] getBytes(byte value) {
        return new byte[]{value};
    }

    public static byte[] getBytes(double value) {
        return getBytes(Double.doubleToLongBits(value));
    }

    public static byte[] getBytes(float value) {
        return getBytes(Float.floatToIntBits(value));
    }

    public static long toLong(byte[] bytes, int start, int length) {
        long num = bytes[start];
        int shift = 8;
        for (int i = start + 1; i < start + 8 && i < start + length && i < bytes.length; i++) {
            num = ((bytes[i] & 0xff) << shift) | num;
            shift += 8;
        }
        return num;
    }

    public static int toInt(byte[] bytes, int start, int length) {
        int num = bytes[start];
        int shift = 8;
        for (int i = start + 1; i < start + 4 && i < start + length && i < bytes.length; i++) {
            num = ((bytes[i] & 0xff) << shift) | num;
            shift += 8;
        }
        return num;
    }

    public static short toShort(byte[] bytes, int start, int length) {
        short num = bytes[start];
        int shift = 8;
        for (int i = start + 1; i < start + 2 && i < start + length && i < bytes.length; i++) {
            num = (short) (((bytes[i] & 0xff) << shift) | num);
            shift += 8;
        }
        return num;
    }

    public static byte toByte(byte[] bytes, int start, int length) {
        return bytes[start];
    }

    public static double toDouble(byte[] bytes, int start, int length) {
        return Double.longBitsToDouble(toLong(bytes, start, length));
    }

    public static float toFloat(byte[] bytes, int start, int length) {
        return Float.intBitsToFloat(toInt(bytes, start, length));
    }

    public static long doubleToLongBits(double value) {
        return Double.doubleToLongBits(value);
    }

    public static double longBitsToDouble(long bits) {
        return Double.longBitsToDouble(bits);
    }

    public static int floatToIntBits(float value) {
        return Float.floatToIntBits(value);
    }

    public static float intBitsToFloat(int bits) {
        return Float.intBitsToFloat(bits);
    }

}
