package link.thingscloud.vertx.remoting.api;

/**
 * @author zhouhailin
 * @version 1.0.0
 */
public interface RemotingChannelListener {

    void onOpened(RemotingChannelContext context);

    void onClosed(RemotingChannelContext context);

    void onTextFrame(RemotingChannelContext context, String text);

    default void onBinaryFrame(RemotingChannelContext context, String text) {
        onTextFrame(context, text);
    }

    default void onDrain(RemotingChannelContext context) {
    }

    default void onEnd(RemotingChannelContext context) {
    }

    void onException(RemotingChannelContext context, Throwable cause);

}
