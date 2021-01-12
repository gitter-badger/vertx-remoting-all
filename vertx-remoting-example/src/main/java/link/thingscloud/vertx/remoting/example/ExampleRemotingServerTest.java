package link.thingscloud.vertx.remoting.example;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import link.thingscloud.vertx.remoting.RemotingBootstrapFactory;
import link.thingscloud.vertx.remoting.api.RemotingChannelContext;
import link.thingscloud.vertx.remoting.api.RemotingChannelListener;
import link.thingscloud.vertx.remoting.api.RemotingServer;
import link.thingscloud.vertx.remoting.config.RemotingServerConfig;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhouhailin
 * @version 1.0.0
 */
public class ExampleRemotingServerTest {

    private static final Logger log = LoggerFactory.getLogger(ExampleRemotingServerTest.class);

    public static void main(String[] args) {
        AtomicInteger cnt = new AtomicInteger(1);
        RemotingServer remotingServer = RemotingBootstrapFactory.createRemotingServer(new RemotingServerConfig());
        remotingServer.start();
        remotingServer.addListener("/myapp", new RemotingChannelListener() {
            @Override
            public void onOpened(RemotingChannelContext context) {
                log.info(context + " onOpened.");
            }

            @Override
            public void onClosed(RemotingChannelContext context) {
                log.info(context + " onClosed.");
            }

            @Override
            public void onTextFrame(RemotingChannelContext context, String text) {
                log.info(context + " onTextFrame text : " + text);
                context.writeTextMessage(cnt.getAndIncrement() + " onTextFrame text : " + new Date());
            }

            @Override
            public void onException(RemotingChannelContext context, Throwable cause) {
                log.error(context + " onException : ", cause);
            }
        });
    }
}
