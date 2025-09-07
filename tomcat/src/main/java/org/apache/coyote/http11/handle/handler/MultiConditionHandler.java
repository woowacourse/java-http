package org.apache.coyote.http11.handle.handler;

import java.util.Map;
import java.util.function.Function;
import org.apache.coyote.http11.handle.HttpHandlerCondition;
import org.apache.coyote.http11.reqeust.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class MultiConditionHandler implements HttpHandler {

    @Override
    public HttpResponse handle(final HttpRequest request) {
        final HttpHandlerCondition requestCondition = HttpHandlerCondition.from(request);
        final Function<HttpRequest, HttpResponse> handlerMethod = getHandlerMethodMapper().get(requestCondition);
        if (handlerMethod == null) {
            throw new IllegalStateException("해당 요청을 처리할 수 없는 핸들러입니다. " + requestCondition);
        }

        return handlerMethod.apply(request);    }

    @Override
    public boolean canHandle(final HttpRequest request) {
        final HttpHandlerCondition requestCondition = HttpHandlerCondition.from(request);

        return getHandlerMethodMapper().containsKey(requestCondition);    }

    protected abstract Map<HttpHandlerCondition, Function<HttpRequest, HttpResponse>> getHandlerMethodMapper();
}
