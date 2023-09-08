package nextstep.jwp;

import nextstep.jwp.handler.Handler;
import nextstep.jwp.interceptor.HandlerInterceptor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import java.io.IOException;
import java.util.List;

public class HandlerExecutionChain {

    private final Handler handler;
    private final List<HandlerInterceptor> handlerInterceptor;

    public HandlerExecutionChain(final Handler handler, final List<HandlerInterceptor> handlerInterceptor) {
        this.handler = handler;
        this.handlerInterceptor = handlerInterceptor;
    }

    public boolean applyPreHandle(final HttpRequest request, final HttpResponse response) throws IOException {
        for (final HandlerInterceptor interceptor : handlerInterceptor) {
            if (!interceptor.preHandle(request, response, handler)) {
                return false;
            }
        }
        return true;
    }

    public boolean isHandlerNull() {
        return handler == null;
    }

    public Handler getHandler() {
        return handler;
    }
}
