package com.mic.keisystem.tracker;

import com.ffbit.bencode.BEncoder;
import com.mic.keisystem.*;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;
import sun.net.util.IPAddressUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * Created by Wallace on 2015/3/13.
 */
public final class TrackerHttpServer {

    public static final String ANNOUNCE_DEFAULT = "/announce";

    private HttpServer httpServer;

    private boolean serverStarted;

    private Loggable logger;

    public TrackerHttpServer(String announce) {
        this.announce = announce;
        this.seeds = new HashMap<InfoHash, List<Peer>>(32);
        // 实际上的 listener 只有一个
        this.trackerCommListeners = new ArrayList<TrackerCommListener>(2);
        serverStarted = false;
    }

    public boolean isServerStarted() {
        return serverStarted;
    }

    /**
     * Information
     *
     * @param address Address to use.
     * @param backlog Maximum clients connected to this server.
     * @throws java.io.IOException 继承自 {@code HttpServer} 的异常抛出。
     */
    public void startServer(InetSocketAddress address, int backlog) throws IOException {
        if (!serverStarted) {
            HttpServerProvider provider = HttpServerProvider.provider();
            httpServer = provider.createHttpServer(address, backlog);
            httpServer.createContext(getAnnounce(), new TrackerHttpHandler());
            httpServer.setExecutor(null);
            httpServer.start();
            serverStarted = true;
        }
    }

    public void stopServer() {
        if (serverStarted) {
            httpServer.stop(0);
            serverStarted = false;
        }
    }

    public void setLogger(Loggable logger) {
        this.logger = logger;
    }

    private Loggable getLogger() {
        return logger;
    }

    // region Fields

    private String announce;

    public String getAnnounce() {
        return this.announce;
    }

    private boolean freeToGo = false;

    public boolean isFreeToGo() {
        return freeToGo;
    }

    public void setFreeToGo(boolean freeToGo) {
        this.freeToGo = freeToGo;
    }

    private final HashMap<InfoHash, List<Peer>> seeds;

    public HashMap<InfoHash, List<Peer>> getSeeds() {
        return seeds;
    }

    private Peer myself;

    public Peer getMyself() {
        return myself;
    }

    public void setMyself(Peer myself) {
        this.myself = myself;
    }

    private List<TrackerCommListener> trackerCommListeners;

    // endregion

    public void addTrackerCommListener(TrackerCommListener listener) {
        if (!trackerCommListeners.contains(listener)) {
            trackerCommListeners.add(listener);
        }
    }

    public void removeTrackerCommListener(TrackerCommListener listener) {
        if (trackerCommListeners.contains(listener)) {
            trackerCommListeners.remove(listener);
        }
    }

    private void raiseTrackerComm(Object sender, TrackerCommEventArgs e) {
        for (TrackerCommListener listener : trackerCommListeners) {
            if (listener != null) {
                listener.onTrackerComm(sender, e);
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (isServerStarted()) {
            stopServer();
        }
        super.finalize();
    }

    private final class TrackerHttpHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            if (method.equalsIgnoreCase("GET")) {
                try {
                    handleGetRequest(httpExchange);
                }
                catch (IOException ex) {
                    getLogger().log(ex.getLocalizedMessage());
                    throw ex;
                }
            } else {
                writeFailure(httpExchange);
            }
            httpExchange.close();
        }

        private void handleGetRequest(HttpExchange httpExchange) throws IOException {
            String requestString = httpExchange.getRequestURI().toString().toLowerCase();
            getLogger().log("#Request: " + requestString);
            if (requestString.startsWith(getAnnounce())) {
                handleTrackerRequest(httpExchange);
            }
        }

        private void writeFailure(HttpExchange httpExchange) throws IOException {
            Headers headers = httpExchange.getResponseHeaders();
            headers.set("Content-Type", "text/plain");
            headers.set("Connection", "Close");
            // code: 500; content-length: 0; connection: close
            httpExchange.sendResponseHeaders(HttpResponseCode.SERVER_ERRROR, -1);
        }

        private void writeSuccess(HttpExchange httpExchange, String contentType, long contentLength) throws IOException {
            Headers headers = httpExchange.getResponseHeaders();
            headers.set("Content-Type", contentType);
            // code: 200; content-length: X; connection: keep-alive
            httpExchange.sendResponseHeaders(HttpResponseCode.OK, contentLength);
        }

        private void handleTrackerRequest(HttpExchange httpExchange) throws IOException {
            // Decompose parameters
            URI uri = httpExchange.getRequestURI();
            // toString()? toASCIIString()?
            // +1: announce? vs. announce (method token '?')
            String paramString = uri.toString().substring(getAnnounce().length() + 1);
            HashMap<String, String> parameters = KeiUtilities.decomposeParameters(paramString);
            // May throw exceptions.
            TrackerParameters trackerParameters = TrackerParameters.resolve(parameters);

            // Reinspect yourself.
            // 警告：该方法存在诸多风险，请查找 InetAddress.getLocalHost 获取更多信息
            // 考虑改用如下代码（http://blog.csdn.net/thunder09/article/details/5360251）
            /*
            Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                System.out.println(netInterface.getName());
                Enumeration addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = (InetAddress) addresses.nextElement();
                    if (ip != null && ip instanceof Inet4Address) {
                        System.out.println("本机的IP = " + ip.getHostAddress());
                    }
                }
            }
            */
            setMyself(Peer.create(KEndpoint4.fromAddressAndPort((Inet4Address) InetAddress.getLocalHost(),
                    trackerParameters.getPortNumber())));
            getLogger().log("Reported myself: " + getMyself().toString());

            // Update peer list.
            List<Peer> peerList = null;
            if (isFreeToGo()) {
                if (getSeeds().containsKey(trackerParameters.getInfoHash())) {
                    // 如果之前有相关种子
                    peerList = getSeeds().get(trackerParameters.getInfoHash());
                    if (!peerList.contains(myself)) {
                        // 如果自己不在列表中，那么就要加入列表
                        peerList.add(myself);
                        // 而且确认已经加入网络，强制设置状态为 started
                        trackerParameters.setTaskStatus(TaskStatus.started);
                    }
                } else {
                    // 如果之前没有相关种子
                    peerList = new ArrayList<Peer>(16);
                    // 将自己加入用户列表
                    peerList.add(myself);
                    getSeeds().put(trackerParameters.getInfoHash(), peerList);
                    // 而且确认已经加入网络，强制设置状态为 started
                    trackerParameters.setTaskStatus(TaskStatus.started);
                }
                TrackerCommEventArgs eventArgs = new TrackerCommEventArgs(trackerParameters, peerList);
                // 父类型 HttpServer 已经提供了内置的异步实现，所以这里不需要异步触发事件
                raiseTrackerComm(this, eventArgs);

                // 反馈给 BT 客户端
                ByteArrayOutputStream bufferStream = new ByteArrayOutputStream(512);
                HashMap<String, Object> data = new HashMap<String, Object>();
                if (peerList != null && peerList.size() > 0) {
                    synchronized (peerList) {
                        if (trackerParameters.isCompact()) {
                            // 紧凑列表
                            byte[] peerArray = new byte[peerList.size() * Peer.SIZE_IN_BYTES];
                            byte[] temp;
                            int i = 0;
                            for (Peer peer : peerList) {
                                temp = peer.toByteArray(ByteOrder.bigEndian);
                                System.arraycopy(temp, 0, peerArray, i * Peer.SIZE_IN_BYTES, Peer.SIZE_IN_BYTES);
                                i++;
                            }
                            data.put("peers", peerArray);
                        } else {
                            // 完整列表
                            ArrayList<Map<String, Object>> peerListForEncoding = new ArrayList<Map<String, Object>>();
                            for (Peer peer : peerList) {
                                HashMap<String, Object> hashMap = new HashMap<String, Object>(4);
                                hashMap.put("id", peer.getPeerID());
                                hashMap.put("ip", peer.getEndpoint().getAddressString());
                                hashMap.put("port", peer.getEndpoint().getPort());
                                peerListForEncoding.add(hashMap);
                            }
                            data.put("peers", peerListForEncoding);
                        }
                        data.put("complete", peerList.size());
                    }
                } else {
                    // 没有种子，返回空列表
                    data.put("peers", "");
                }
                // 输出
                BEncoder encoder = new BEncoder(bufferStream, Charset.forName("UTF-8"));
                try {
                    encoder.encode(data);
                }
                catch (Throwable ex) {
                    ex.printStackTrace(System.out);
                }
                getLogger().log("Data encoded.");
                int dataLength = bufferStream.size();
                getLogger().log("Data length: " + Integer.toString(dataLength));
                getLogger().log("Data string: " + bufferStream.toString());
                try {
                    writeSuccess(httpExchange, "text/plain", dataLength);
                    bufferStream.writeTo(httpExchange.getResponseBody());
                    //httpExchange.getResponseBody().flush();
                    getLogger().log("Success: written.");
                }
                catch (Throwable ex) {
                    ex.printStackTrace(System.out);
                }
                // 用的是 ByteArrayOutputStream，不需要 close()
            }
        }
    }

}
