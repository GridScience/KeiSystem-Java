package com.mic.keisystem;

import com.mic.BitConverter;

import java.net.Inet4Address;

/**
 * Created by MIC/Headcrabbed on 2015/3/14.
 */
public final class KEndpoint4 extends KEndpoint {

    @Override
    public String getAddressString() {
        // 采用这么变态的写法是因为 Java 的 byte 是有符号的
        // 我去，二十年了就没有人要改进一下吗
        return Integer.toString(this.address[0] & 0xff) + "." +
                Integer.toString(this.address[1] & 0xff) + "." +
                Integer.toString(this.address[2] & 0xff) + "." +
                Integer.toString(this.address[3] & 0xff);
    }

    @Override
    public void setAddress(byte[] address) throws IllegalArgumentException {
        if (address == null || address.length != 4) {
            throw new IllegalArgumentException("The address must be a IPv4 address.");
        }
        this.address = address.clone();
    }

    public static KEndpoint4 fromAddressAndPort(Inet4Address address, int port) {
        KEndpoint4 kep = new KEndpoint4();
        kep.setAddress(address.getAddress());
        kep.setPort(port);
        return kep;
    }

    public static KEndpoint4 fromByteArray(byte[] bytes) {
        if (bytes == null || bytes.length != SIZE_IN_BYTES) {
            throw new IllegalArgumentException("The array must contain 6 bytes, the first 4 bytes for address, " +
                    "and the last 2 bytes for port (little endian).");
        }
        KEndpoint4 kep = new KEndpoint4();
        // 这么不优美的写法是 Enumerable 的缺失导致的
        kep.setAddress(new byte[]{bytes[0], bytes[1], bytes[2], bytes[3]});
        kep.setPort(BitConverter.toInt(new byte[]{0, 0, bytes[4], bytes[5]}, 0));
        return kep;
    }

    @Override
    public KEndpoint clone() {
        KEndpoint4 endpoint = new KEndpoint4();
        endpoint.setAddress(this.address);
        endpoint.setPort(this.port);
        return endpoint;
    }

    public static final int SIZE_IN_BYTES = 6;

}
