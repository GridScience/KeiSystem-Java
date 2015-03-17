package com.mic.keisystem;

import com.mic.BitConverter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.util.Arrays;

/**
 * Created by MIC/Headcrabbed on 2015/3/17.
 */
public final class KSMessage {

    public static final int MAGIC_NUMBER = 0x75686b72; // uhkr = UiHaru KazaRi

    //int32 totalSize (added for Apache MINA)
    //int32 MagicNumber
    //KMessageHeader Header
    //KMessageContent Content

    private final int magicNumber;
    private final KSMessageHeader header;
    private final KSMessageContent content;

    public static final int MINIMUM_SIZE_IN_BYTES = 4 + KSMessageHeader.SIZE_IN_BYTES + 4;

    public KSMessage(int magicNumber, KSMessageHeader header, KSMessageContent content) {
        this.magicNumber = magicNumber;
        this.header = header;
        this.content = content;
    }

    public int getMagicNumber() {
        return magicNumber;
    }

    public KSMessageHeader getHeader() {
        return header;
    }

    public KSMessageContent getContent() {
        return content;
    }

    public int getTotalSize() {
        return 4 + header.getHeaderSize() + content.getTotalSize();
    }

    public static boolean distinctEquals(KSMessage message1, KSMessage message2) {
        if (message1 == null || message2 == null) {
            return false;
        }
        return KSMessageHeader.distinctEquals(message1.header, message2.header);
    }

    public static boolean equals(KSMessage message1, KSMessage message2) {
        if (message1 == null || message2 == null) {
            return false;
        }
        return KSMessageHeader.equals(message1.header, message2.header);
    }

    public void writeToStream(OutputStream outputStream) throws IOException {
        // added totalSize entry
        outputStream.write(BitConverter.getBytes(getTotalSize()));
        outputStream.write(BitConverter.getBytes(magicNumber));
        header.writeToStream(outputStream);
        content.writeToStream(outputStream);
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream(getTotalSize());
        try {
            writeToStream(stream);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        byte[] buffer = stream.toByteArray();
        try {
            stream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return buffer;
    }

    public static KSMessage fromByteArray(InputStream inputStream) throws IOException, InvalidKSMessageException {
        byte[] buffer = new byte[4];
        // added totalSize entry
        inputStream.read(buffer);
        // Read magic number.
        inputStream.read(buffer);
        int receivedMagicNumber = BitConverter.toInt(buffer, 0);
        if (receivedMagicNumber != MAGIC_NUMBER) {
            throw new InvalidKSMessageException();
        }
        KSMessageHeader header = KSMessageHeader.fromByteArray(inputStream);
        KSMessageContent content = KSMessageContent.fromByteArray(inputStream);
        return new KSMessage(receivedMagicNumber, header, content);
    }

    public static final class KSMessageHeader {

        //int32 HeaderSize
        //int16 HeaderVersion
        //int64 MessageID
        //KMessageCode(int32) Code
        //KEndPoint4 SourceEndPoint

        private final int headerSize;
        private final short headerVersion;
        private final long messageID;
        private final KSMessageCode code;
        private final KEndpoint4 sourceEndPoint;

        public static final int SIZE_IN_BYTES = 24;

        private KSMessageHeader(int headerSize, short headerVersion, long messageID, KSMessageCode code, KEndpoint4 sourceEndPoint) {
            this.headerSize = headerSize;
            this.headerVersion = headerVersion;
            this.messageID = messageID;
            this.code = code;
            this.sourceEndPoint = sourceEndPoint;
        }

        public KSMessageHeader create(long messageID, KSMessageCode code, KEndpoint4 sourceEndPoint) {
            return new KSMessageHeader(SIZE_IN_BYTES, (short) 0x0002, messageID, code, sourceEndPoint);
        }

        public int getHeaderSize() {
            return headerSize;
        }

        public short getHeaderVersion() {
            return headerVersion;
        }

        public long getMessageID() {
            return messageID;
        }

        public KSMessageCode getCode() {
            return code;
        }

        public KEndpoint4 getSourceEndPoint() {
            return sourceEndPoint;
        }

        /**
         * 注意该方法可能会有冲突，因为只精确到毫秒级。
         * @return 一个 {@code long}，表示消息 ID。
         */
        public static long getHashMessageID() {
            Instant instant = Instant.now();
            return instant.toEpochMilli();
        }

        /**
         * 该方法比较消息 ID 和消息源，多一次数组比较调用。
         * @param header1 第一个消息头。
         * @param header2 第二个消息头。
         * @return 两个消息的 ID 是否相等。
         */
        public static boolean distinctEquals(KSMessageHeader header1, KSMessageHeader header2) {
            if (header1 == null || header2 == null) {
                return false;
            }
            return header1.messageID == header2.messageID && KEndpoint.equals(header1.sourceEndPoint, header2.sourceEndPoint);
        }

        /**
         * 该方法只比较两个消息的 ID，速度提高，但是这样消息头有较大概率重复。
         * @param header1 第一个消息头。
         * @param header2 第二个消息头。
         * @return 两个消息的消息 ID 是否相等。
         */
        public static boolean equals(KSMessageHeader header1, KSMessageHeader header2) {
            if (header1 == null || header2 == null) {
                return false;
            }
            return header1.messageID == header2.messageID;
        }

        public void writeToStream(OutputStream outputStream) throws IOException {
            outputStream.write(BitConverter.getBytes(headerSize));
            outputStream.write(BitConverter.getBytes(headerVersion));
            outputStream.write(BitConverter.getBytes(messageID));
            outputStream.write(BitConverter.getBytes(code.numericValue));
            outputStream.write(sourceEndPoint.toByteArray(ByteOrder.littleEndian));
        }

        public static KSMessageHeader fromByteArray(InputStream inputStream) throws IOException {
            // TODO: 注意！这个方法此时还没有考虑到版本的向前兼容性，所以采用了这种写起来方便扩展起来麻烦的方法
            byte[] buffer = new byte[SIZE_IN_BYTES];
            inputStream.read(buffer);
            // +4
            int headerSize = BitConverter.toInt(buffer, 0);
            // +2
            short headerVersion = BitConverter.toShort(buffer, 4);
            // +8
            long messageID = BitConverter.toLong(buffer, 6);
            // +4
            int messageCodeInt = BitConverter.toInt(buffer, 14);
            KSMessageCode messageCode = KSMessageCode.valueOf(messageCodeInt);
            KEndpoint4 endpoint = KEndpoint4.fromByteArray(Arrays.copyOfRange(buffer, 18, 18 + 6 + 1));
            return new KSMessageHeader(headerSize, headerVersion, messageID, messageCode,  endpoint);
        }

    }

    public static final class KSMessageContent {
        //int32 DataLength
        //int8[] Data

        private final int dataLength;
        private final byte[] data;

        public KSMessageContent(byte[] data) {
            this.data = data;
            this.dataLength = data.length;
        }

        public int getDataLength() {
            return dataLength;
        }

        public byte[] getData() {
            return data;
        }

        public int getTotalSize() {
            return dataLength + 4;
        }

        private void writeToStream(OutputStream outputStream) throws IOException {
            outputStream.write(BitConverter.getBytes(dataLength));
            if (dataLength > 0) {
                outputStream.write(data, 0, data.length);
            }
        }

        public static KSMessageContent fromByteArray(InputStream inputStream) throws IOException {
            byte[] buffer = new byte[4];
            inputStream.read(buffer);
            int dataLength = BitConverter.toInt(buffer, 0);
            byte[] data = new byte[dataLength];
            inputStream.read(data);
            return new KSMessageContent(data);
        }

    }

    public static enum KSMessageCode {

        emptyMessage(0), reportAlive(1), clientEnterNetwork(2), clientExitNetwork(3),
        peerEnterNetwork(4), peerExitNetwork(5), gotPeer(6);

        KSMessageCode(int numericValue) {
            this.numericValue = numericValue;
        }

        private final int numericValue;

        public int getNumericValue() {
            return numericValue;
        }

        public static int EMPTY_MESSAGE = 0;
        public static int REPORT_ALIVE = 1;
        public static int CLIENT_ENTER_NETWORK = 2;
        public static int CLIENT_EXIT_NETWORK = 3;
        public static int PEER_ENTER_NETWORK = 4;
        public static int PEER_EXIT_NETWORK = 5;
        public static int GOT_PEER;

        public static KSMessageCode valueOf(int i) {
            return values()[i];
        }

    }

}
