package link.thingscloud.vertx.remoting.config;

/**
 * @author zhouhailin
 * @version 1.0.0
 */
public class RemotingClientConfig extends RemotingConfig {

    private boolean autoConnection = true;
    private long autoReconnectionMillis = 5000;

    public boolean isAutoConnection() {
        return autoConnection;
    }

    public RemotingClientConfig setAutoConnection(boolean autoConnection) {
        this.autoConnection = autoConnection;
        return this;
    }

    public long getAutoReconnectionMillis() {
        return autoReconnectionMillis;
    }

    public RemotingClientConfig setAutoReconnectionMillis(long autoReconnectionMillis) {
        this.autoReconnectionMillis = autoReconnectionMillis;
        return this;
    }
}
