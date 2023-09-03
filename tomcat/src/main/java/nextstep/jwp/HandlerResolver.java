package nextstep.jwp;

import org.apache.coyote.http11.Handler;
import org.apache.coyote.http11.request.HttpMethod;
import java.util.Map;

public class HandlerResolver {

    private final Map<String, Handler> httpGetHandlers;
    private final Map<String, Handler> httpPostHandlers;

    public HandlerResolver(final Map<String, Handler> httpGetHandlers, final Map<String, Handler> httpPostHandlers) {
        this.httpGetHandlers = httpGetHandlers;
        this.httpPostHandlers = httpPostHandlers;
    }

    public Handler resolve(final HttpMethod httpMethod, final String path) {
        if (httpMethod.equals(HttpMethod.GET)) {
            return httpGetHandlers.get(path);
        }
        if (httpMethod.equals(HttpMethod.POST)) {
            return httpPostHandlers.get(path);
        }
        return null;
    }
}
