package nextstep.jwp;

import nextstep.jwp.infrastructure.http.HandlerMapping;
import nextstep.jwp.infrastructure.http.interceptor.HandlerInterceptor;

public class WebApplicationContext {

    private final HandlerMapping handlerMapping;
    private final HandlerInterceptor interceptor;

    public WebApplicationContext(final HandlerMapping handlerMapping, final HandlerInterceptor interceptor) {
        this.handlerMapping = handlerMapping;
        this.interceptor = interceptor;
    }

    public HandlerMapping getHandlerMapping() {
        return handlerMapping;
    }

    public HandlerInterceptor getInterceptor() {
        return interceptor;
    }
}
