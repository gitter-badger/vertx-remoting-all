package link.thingscloud.vertx.remoting.common;

/**
 * @author zhouhailin
 * @version 1.0.0
 */
public final class Addr {
    private final String host;
    private final int port;
    private final String uri;

    public Addr(String host, int port) {
        this(host, port, null);
    }

    public Addr(String host, int port, String uri) {
        this.host = host;
        this.port = port;
        this.uri = uri;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUri() {
        return uri;
    }

    @Override
    public String toString() {
        if (uri == null) {
            return host + ":" + port;
        } else {
            return host + ":" + port + ":" + uri;
        }
    }
}
