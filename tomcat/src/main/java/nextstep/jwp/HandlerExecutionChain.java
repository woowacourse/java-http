package nextstep.jwp;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.interceptor.HandlerInterceptor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import java.io.IOException;
import java.util.List;

public class HandlerExecutionChain {

    private final Controller controller;
    private final List<HandlerInterceptor> handlerInterceptor;

    public HandlerExecutionChain(final Controller controller, final List<HandlerInterceptor> handlerInterceptor) {
        this.controller = controller;
        this.handlerInterceptor = handlerInterceptor;
    }

    public boolean applyPreHandle(final HttpRequest request, final HttpResponse response) throws IOException {
        for (final HandlerInterceptor interceptor : handlerInterceptor) {
            if (!interceptor.preHandle(request, response, controller)) {
                return false;
            }
        }
        return true;
    }

    public boolean isHandlerNull() {
        return controller == null;
    }

    public Controller getHandler() {
        return controller;
    }
}
