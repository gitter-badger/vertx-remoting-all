package link.thingscloud.vertx.remoting.config;

/**
 * @author zhouhailin
 * @version 1.0.0
 */
public class RemotingServerConfig extends RemotingConfig {

    private int serverListenPort = 8888;

    public int getServerListenPort() {
        return serverListenPort;
    }

    public RemotingServerConfig setServerListenPort(int serverListenPort) {
        this.serverListenPort = serverListenPort;
        return this;
    }
}
