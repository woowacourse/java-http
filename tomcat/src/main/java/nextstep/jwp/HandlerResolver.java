package nextstep.jwp;

import org.apache.coyote.http11.Handler;
import java.util.Map;

public class HandlerResolver {

    private final Map<String, Handler> httpGetHandlers;

    public HandlerResolver(final Map<String, Handler> httpGetHandlers) {
        this.httpGetHandlers = httpGetHandlers;
    }

    public Handler resolve(final String path) {
        return httpGetHandlers.get(path);
    }
}
