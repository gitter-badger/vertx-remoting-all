package link.thingscloud.vertx.remoting.api;

/**
 * @author zhouhailin
 * @version 1.0.0
 */
public interface RemotingService {

    RemotingService start();

    RemotingService shutdown();

    default RemotingService addListener(String uri, RemotingChannelListener listener) {
        return this;
    }

}
