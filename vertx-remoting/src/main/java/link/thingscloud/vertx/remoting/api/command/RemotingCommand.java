package link.thingscloud.vertx.remoting.api.command;

/**
 * @author zhouhailin
 * @version 1.0.0
 */
public class RemotingCommand {

    private final int code;
    private final String body;

    public RemotingCommand(int code, String body) {
        this.code = code;
        this.body = body;
    }

    public int getCode() {
        return code;
    }
}
