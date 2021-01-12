package link.thingscloud.vertx.remoting.impl;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import link.thingscloud.vertx.remoting.api.RemotingChannelContext;
import link.thingscloud.vertx.remoting.api.RemotingChannelListener;
import link.thingscloud.vertx.remoting.api.RemotingServer;
import link.thingscloud.vertx.remoting.common.Addr;
import link.thingscloud.vertx.remoting.config.RemotingServerConfig;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author zhouhailin
 * @version 1.0.0
 */
public class VertxRemotingServer implements RemotingServer {

    private final RemotingServerConfig serverConfig;

    private final Map<String, List<RemotingChannelListener>> uriListeners = new ConcurrentHashMap<>();

    private final Vertx vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(40));
    private final HttpServerOptions httpServerOptions = new HttpServerOptions().setMaxWebSocketFrameSize(1000000);
    private final HttpServer httpServer = vertx.createHttpServer(httpServerOptions);

    private static final Logger log = LoggerFactory.getLogger(VertxRemotingServer.class);

    public VertxRemotingServer(RemotingServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    @Override
    public VertxRemotingServer start() {
        log.info("vertx remoting server start ...");
        httpServer
                .webSocketHandler(serverWebSocketHandler)
                .listen(serverConfig.getServerListenPort());
        log.info("vertx remoting server listen on " + serverConfig.getServerListenPort());
        return this;
    }

    @Override
    public VertxRemotingServer shutdown() {
        log.info("vertx remoting server shutdown ...");
        httpServer.close(event -> {
            if (event.succeeded()) {
                log.info("vertx http server close succeed.");
            } else {
                log.info("vertx http server close failed", event.cause());
            }
        });
        vertx.close(event -> {
            if (event.succeeded()) {
                log.info("vertx close succeed.");
            } else {
                log.info("vertx close failed", event.cause());
            }
        });
        return this;
    }

    @Override
    public VertxRemotingServer addListener(String uri, RemotingChannelListener listener) {
        uriListeners.computeIfAbsent(uri, key -> new CopyOnWriteArrayList<>()).add(listener);
        return this;
    }

    private final Handler<ServerWebSocket> serverWebSocketHandler = serverWebSocket -> {
        String uri = serverWebSocket.uri();
        if (uri == null || uri.isEmpty()) {
            log.info("server websocket handler uri is empty.");
            serverWebSocket.close();
            return;
        }
        List<RemotingChannelListener> listeners = uriListeners.get(uri);
        if (listeners == null || listeners.isEmpty()) {
            log.info("server websocket handler uri : " + uri + ", but listeners is empty.");
            serverWebSocket.close();
            return;
        }
        serverWebSocket.accept();
        String id = serverWebSocket.textHandlerID().replace("__vertx.ws.", "");
        Addr localAddr = new Addr(serverWebSocket.localAddress().host(), serverWebSocket.localAddress().port(), uri);
        Addr remoteAddr = new Addr(serverWebSocket.remoteAddress().host(), serverWebSocket.remoteAddress().port());
        RemotingChannelContext context = new VertxRemotingChannelContext(id, uri, localAddr, remoteAddr, serverWebSocket);
        log.debug("[" + localAddr.toString() + " <- " + remoteAddr.toString() + "] " + "server websocket handler accept : " + id);
        listeners.forEach(listener -> listener.onOpened(context));
        serverWebSocket
                .frameHandler(frame -> {
                    if (frame.isText()) {
                        listeners.forEach(listener -> listener.onTextFrame(context, frame.textData()));
                    } else if (frame.isBinary()) {
                        listeners.forEach(listener -> listener.onBinaryFrame(context, frame.binaryData().toString()));
                    }
                })
                .exceptionHandler(cause -> listeners.forEach(listener -> listener.onException(context, cause)))
                .drainHandler(unused -> listeners.forEach(listener -> listener.onDrain(context)))
                .endHandler(unused -> listeners.forEach(listener -> listener.onEnd(context)))
                .closeHandler(unused -> listeners.forEach(listener -> listener.onClosed(context)));
    };
}
