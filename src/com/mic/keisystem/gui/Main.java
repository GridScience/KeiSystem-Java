package com.mic.keisystem.gui;

import com.ffbit.bencode.BDecoder;
import com.ffbit.bencode.BEncoder;
import com.mic.keisystem.KEndpoint;
import com.mic.keisystem.KEndpoint4;
import com.mic.keisystem.Loggable;
import com.mic.keisystem.Peer;
import com.mic.keisystem.tracker.TrackerHttpServer;
import sun.awt.CharsetString;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.lang.System.out;
import static java.lang.System.in;
import static java.lang.System.setOut;

public class Main {

    public static void main(String[] args) {
        /*
        String parsing = "d4:nick9:你们好3:agei23e4:blog33:http://www.cnblogs.com/technology7:hobbiesl6:Coding10:Basketballee";
        byte[] parsingBytes = parsing.getBytes();
        ByteArrayInputStream sourceStream = new ByteArrayInputStream(parsingBytes);
        BDecoder decoder = new BDecoder(sourceStream, Charset.forName("UTF-8"));
        // 制作种子的时候应该先用 ASCII decode 一遍，获得编码，然后再用指定的编码 decode
        for (Object o : decoder) {
            System.out.println(o);
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BEncoder encoder = new BEncoder(out);
        try {
            encoder.encode(new byte[]{100, 32, 100, 97});
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        decoder = new BDecoder(in);
        for (Object o : decoder) {
            System.out.println(o + ", " + o.getClass().toGenericString());
        }
        // 从 ByteArrayStream 构建的，不需要调用任何 close()

        System.out.println(new byte[] { 97, 98, 99, 100});

        ByteArrayOutputStream stream = new ByteArrayOutputStream(50);
        stream.write(233000);
        stream.write(233303);
        System.out.println(stream.size());
        */

        out.println("Tracker server test");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        TrackerHttpServer server = new TrackerHttpServer(TrackerHttpServer.ANNOUNCE_DEFAULT);
        server.setLogger(new Loggable() {
            @Override
            public void log(String text) {
                out.println(text);
            }
        });

        try {
            out.print("Port: ");
            String portString = reader.readLine();
            int portNumber = Integer.parseInt(portString);

            InetSocketAddress address = new InetSocketAddress(portNumber);
            out.println("Socket address: " + address.toString());

            server.setFreeToGo(true);
            server.setMyself(Peer.create(KEndpoint4.fromAddressAndPort((Inet4Address) InetAddress.getByAddress(
                    new byte[]{(byte) 192, (byte) 168, 46, 43}), 9029)));
            server.startServer(address, 1);

            out.println("Server started.");

            reader.readLine();
            server.stopServer();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
}
