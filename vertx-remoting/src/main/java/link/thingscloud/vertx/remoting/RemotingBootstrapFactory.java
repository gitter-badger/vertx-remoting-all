package link.thingscloud.vertx.remoting;

import link.thingscloud.vertx.remoting.api.RemotingClient;
import link.thingscloud.vertx.remoting.api.RemotingServer;
import link.thingscloud.vertx.remoting.config.RemotingClientConfig;
import link.thingscloud.vertx.remoting.config.RemotingServerConfig;
import link.thingscloud.vertx.remoting.impl.VertxRemotingClient;
import link.thingscloud.vertx.remoting.impl.VertxRemotingServer;

/**
 * @author zhouhailin
 * @version 1.0.0
 */
public class RemotingBootstrapFactory {

    public static RemotingClient createRemotingClient(final RemotingClientConfig config) {
        return new VertxRemotingClient(config);
    }

    public static RemotingServer createRemotingServer(final RemotingServerConfig config) {
        return new VertxRemotingServer(config);
    }

}
