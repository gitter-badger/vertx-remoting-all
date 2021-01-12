package link.thingscloud.vertx.remoting.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.WebSocket;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import link.thingscloud.vertx.remoting.api.RemotingChannelContext;
import link.thingscloud.vertx.remoting.api.RemotingChannelListener;
import link.thingscloud.vertx.remoting.api.RemotingClient;
import link.thingscloud.vertx.remoting.common.Addr;
import link.thingscloud.vertx.remoting.config.RemotingClientConfig;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author zhouhailin
 * @version 1.0.0
 */
public class VertxRemotingClient implements RemotingClient {

    private final RemotingClientConfig clientConfig;

    private final Map<String, WebSocket> webSocketMap = new ConcurrentHashMap<>();

    private final Map<String, List<RemotingChannelListener>> uriListeners = new ConcurrentHashMap<>();

    private final Vertx vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(40));
    private final HttpClientOptions httpClientOptions = new HttpClientOptions().setMaxWebSocketFrameSize(1000000);
    private final HttpClient httpClient = vertx.createHttpClient(httpClientOptions);

    private static final Logger log = LoggerFactory.getLogger(VertxRemotingClient.class);

    public VertxRemotingClient(RemotingClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    @Override
    public VertxRemotingClient connect(Addr addr) {
        List<RemotingChannelListener> listeners = uriListeners.get(addr.getUri());
        if (listeners == null || listeners.isEmpty()) {
            log.info("client websocket handler uri : " + addr.getUri() + ", but listeners is empty.");
            return this;
        }
        String addrStr = addr.toString();
        log.info("vertx remoting client connect " + addrStr + " ...");

        httpClient.webSocket(addr.getPort(), addr.getHost(), addr.getUri(), ar -> {
            if (ar.succeeded()) {
                WebSocket webSocket = ar.result();
                String id = webSocket.textHandlerID().replace("__vertx.ws.", "");
                Addr localAddr = new Addr(webSocket.localAddress().host(), webSocket.localAddress().port());
                Addr remoteAddr = new Addr(webSocket.remoteAddress().host(), webSocket.remoteAddress().port(), addr.getUri());
                RemotingChannelContext context = new VertxRemotingChannelContext(id, addr.getUri(), localAddr, remoteAddr, webSocket);
                log.debug("[" + localAddr.toString() + " -> " + remoteAddr.toString() + "] " + "client websocket handler : " + id);
                listeners.forEach(listener -> listener.onOpened(context));
                webSocket
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
                        .closeHandler(unused -> {
                            if (clientConfig.isAutoConnection()) {
                                vertx.setTimer(clientConfig.getAutoReconnectionMillis(), event -> connect(addr));
                            }
                            listeners.forEach(listener -> listener.onClosed(context));
                        });

            } else {
                log.error("vertx remoting client connect " + addrStr + " failed, cause :", ar.cause());
                vertx.setTimer(clientConfig.getAutoReconnectionMillis(), event -> connect(addr));
            }
        });
        return this;
    }

    @Override
    public VertxRemotingClient disconnect(Addr addr) {
        String addrStr = addr.toString();
        log.info("vertx remoting client disconnect " + addrStr + " ...");
        WebSocket webSocket = webSocketMap.remove(addrStr);
        if (webSocket != null) {
            webSocket.close(event -> log.info("vertx remoting client disconnect " + addrStr));
        }
        return this;
    }

    @Override
    public VertxRemotingClient start() {
        log.info("vertx remoting client start ...");
        return this;
    }

    @Override
    public VertxRemotingClient shutdown() {
        log.info("vertx remoting client shutdown ...");
        return this;
    }

    @Override
    public VertxRemotingClient addListener(String uri, RemotingChannelListener listener) {
        uriListeners.computeIfAbsent(uri, key -> new CopyOnWriteArrayList<>()).add(listener);
        return this;
    }

    private final Handler<AsyncResult<WebSocket>> websocketHandler = asyncResult -> {
        if (asyncResult.succeeded()) {
            WebSocket webSocket = asyncResult.result();
        }
    };

}
