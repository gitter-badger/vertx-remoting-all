package link.thingscloud.vertx.remoting.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketBase;
import link.thingscloud.vertx.remoting.api.RemotingChannelContext;
import link.thingscloud.vertx.remoting.common.Addr;

/**
 * @author zhouhailin
 * @version 1.0.0
 */
public class VertxRemotingChannelContext implements RemotingChannelContext {

    private final String id;
    private final String uri;
    private final Addr localAddr;
    private final Addr remoteAddr;
    private final WebSocketBase webSocketBase;

    public VertxRemotingChannelContext(String id, String uri, Addr localAddr, Addr remoteAddr, WebSocketBase webSocketBase) {
        this.id = id;
        this.uri = uri;
        this.localAddr = localAddr;
        this.remoteAddr = remoteAddr;
        this.webSocketBase = webSocketBase;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public Addr getLocalAddr() {
        return localAddr;
    }

    @Override
    public Addr getRemoteAddr() {
        return remoteAddr;
    }

    @Override
    public String addr() {
        return "[" + localAddr.toString() + (webSocketBase instanceof ServerWebSocket ? " <- " : " -> ") + remoteAddr.toString() + "]";
    }

    @Override
    public VertxRemotingChannelContext writeTextMessage(String text, Handler<AsyncResult<Void>> handler) {
        webSocketBase.writeTextMessage(text, handler);
        return this;
    }

    @Override
    public String toString() {
        return "[" + localAddr.toString() + (webSocketBase instanceof ServerWebSocket ? " <- " : " -> ") + remoteAddr.toString() + "] [" + id + "] [" + uri + "]";
    }
}
