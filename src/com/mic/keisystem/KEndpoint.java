package com.mic.keisystem;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * Created by MIC/Headcrabbed on 2015/3/17.
 */
public abstract class KEndpoint implements Cloneable {

    protected byte[] address;

    protected int port;

    public abstract String getAddressString();

    public byte[] getAddress() {
        return this.address.clone();
    }

    abstract void setAddress(byte[] addr);

    public int getPort() {
        return this.port;
    }

    void setPort(int port) {
        if (port > 65535) {
            throw new IllegalArgumentException("Port must be a positive integer less than 65536.");
        }
        this.port = port;
    }

    public byte[] getPortBytes() {
        // little endian
        byte b0, b1, b2, b3;
        b0 = (byte) (this.port & 0xff);
        b1 = (byte) ((this.port >> 8) & 0xff);
        return new byte[]{b0, b1};
    }

    /**
     * @param byteOrder 采用的字节序。C# 的 BitConverter 使用 little endian（所以 {@code BitConverter} 也采用了此设计），
     * 而 BitTorrent 客户端需要 big endian。
     */
    public byte[] toByteArray(ByteOrder byteOrder) {
        byte[] addressBytes = this.address.clone();
        byte[] portBytes = getPortBytes();
        byte[] result = new byte[addressBytes.length + portBytes.length];
        /*
        for (int i = 0; i < addressBytes.length; i++) {
            result[i] = addressBytes[i];
        }
        */
        System.arraycopy(addressBytes, 0, result, 0, addressBytes.length);
        switch (byteOrder) {
            case littleEndian:
                /*
                for (int i = 0; i < portBytes.length; i++) {
                    result[addressBytes.length + i] = portBytes[i];
                }
                */
                System.arraycopy(portBytes, 0, result, addressBytes.length, portBytes.length);
                break;
            case bigEndian:
                for (int i = 0; i < portBytes.length; i++) {
                    result[addressBytes.length + i] = portBytes[portBytes.length - 1 - i];
                }
                break;
        }
        return result;
    }

    public static boolean equals(KEndpoint endPoint1, KEndpoint endPoint2) {
        if (endPoint1 == null || endPoint2 == null) {
            return false;
        }
        if (endPoint1.address.length != endPoint2.address.length) {
            return false;
        }
        if (endPoint1.port != endPoint2.port) {
            return false;
        }
        for (int i = 0; i < endPoint1.address.length; i++) {
            if (endPoint1.address[i] != endPoint2.address[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return getAddressString() + ":" + Integer.toString(this.port);
    }

    public static boolean addressEquals(KEndpoint endPoint1, KEndpoint endPoint2) {
        if (endPoint1 == null || endPoint2 == null) {
            return false;
        }
        if (endPoint1.address.length != endPoint2.address.length) {
            return false;
        }
        for (int i = 0; i < endPoint1.address.length; i++) {
            if (endPoint1.address[i] != endPoint2.address[i]) {
                return false;
            }
        }
        return true;
    }

    public static InetSocketAddress getLoopbackEndpoint(int port) {
        InetAddress loopbackAddress = null;
        try {
            loopbackAddress = InetAddress.getByAddress(new byte[]{127, 2, 23, (byte) 233});
        }
        catch (UnknownHostException e) {
            // Unknown handle method
        }
        return new InetSocketAddress(loopbackAddress, port);
    }

    public InetSocketAddress getEndpoint() {
        InetAddress address = null;
        try {
            address = InetAddress.getByAddress(this.address);
        }
        catch (UnknownHostException e) {
            // Unknown handle method
        }
        return new InetSocketAddress(address, this.port);
    }

    @Override
    public abstract KEndpoint clone();

}
