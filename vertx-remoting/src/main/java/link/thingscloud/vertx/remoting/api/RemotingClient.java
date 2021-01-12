package link.thingscloud.vertx.remoting.api;

import link.thingscloud.vertx.remoting.common.Addr;

/**
 * @author zhouhailin
 * @version 1.0.0
 */
public interface RemotingClient extends RemotingService {

    RemotingClient connect(Addr addr);

    RemotingClient disconnect(Addr addr);

}
