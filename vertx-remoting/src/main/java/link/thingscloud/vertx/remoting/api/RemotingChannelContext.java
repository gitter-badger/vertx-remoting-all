package link.thingscloud.vertx.remoting.api;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import link.thingscloud.vertx.remoting.common.Addr;

/**
 * @author zhouhailin
 * @version 1.0.0
 */
public interface RemotingChannelContext {

    String getId();

    String getUri();

    Addr getLocalAddr();

    Addr getRemoteAddr();

    String addr();

    default RemotingChannelContext writeTextMessage(String text) {
        return writeTextMessage(text, null);
    }

    RemotingChannelContext writeTextMessage(String text, Handler<AsyncResult<Void>> handler);

}
