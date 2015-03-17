package com.mic.keisystem.network;

import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.core.filterchain.IoFilterChainBuilder;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.example.gettingstarted.timeserver.MinaTimeServer;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.DefaultSocketSessionConfig;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

/**
 * Created by MIC/Headcrabbed on 2015/3/17.
 */
public abstract class KSNodeBase {

    private NioSocketAcceptor serverIoAcceptor = null;

    public void createServer(int portNumber) {
        serverIoAcceptor = new NioSocketAcceptor();
        serverIoAcceptor.getSessionConfig().setReuseAddress(true);
        serverIoAcceptor.getFilterChain().addLast("logger", new LoggingFilter("mina-logger"));

    }

}
