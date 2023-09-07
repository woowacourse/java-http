package nextstep.jwp;

import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.HttpMethod;
import java.util.Map;

public class HandlerResolver {

    private final Map<String, Controller> httpGetHandlers;
    private final Map<String, Controller> httpPostHandlers;

    public HandlerResolver(final Map<String, Controller> httpGetHandlers, final Map<String, Controller> httpPostHandlers) {
        this.httpGetHandlers = httpGetHandlers;
        this.httpPostHandlers = httpPostHandlers;
    }

    public Controller resolve(final HttpMethod httpMethod, final String path) {
        if (httpMethod.equals(HttpMethod.GET)) {
            return httpGetHandlers.get(path);
        }
        if (httpMethod.equals(HttpMethod.POST)) {
            return httpPostHandlers.get(path);
        }
        return null;
    }
}
