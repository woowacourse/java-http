package nextstep.jwp;

import nextstep.jwp.handler.Handler;
import nextstep.jwp.interceptor.HandlerInterceptor;
import org.apache.coyote.http11.HttpMethod;
import java.util.List;
import java.util.Map;

public class HandlerMapping {

    private final Map<String, Handler> httpGetHandlers;
    private final Map<String, Handler> httpPostHandlers;
    private final List<HandlerInterceptor> handlerInterceptors;

    public HandlerMapping(final Map<String, Handler> httpGetHandlers, final Map<String, Handler> httpPostHandlers, final List<HandlerInterceptor> handlerInterceptors) {
        this.httpGetHandlers = httpGetHandlers;
        this.httpPostHandlers = httpPostHandlers;
        this.handlerInterceptors = handlerInterceptors;
    }

    public HandlerExecutionChain findHandler(final HttpMethod httpMethod, final String path) {
        if (httpMethod.equals(HttpMethod.GET)) {
            return new HandlerExecutionChain(httpGetHandlers.get(path), handlerInterceptors);
        }
        if (httpMethod.equals(HttpMethod.POST)) {
            return new HandlerExecutionChain(httpPostHandlers.get(path), handlerInterceptors);
        }
        return null;
    }
}
