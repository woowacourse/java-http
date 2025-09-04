package org.apache.coyote.http11.reqeust.handler;

import java.util.Map;
import java.util.function.Function;
import org.apache.coyote.http11.reqeust.HttpMethod;
import org.apache.coyote.http11.reqeust.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class DefaultHttpRequestHandler implements HttpRequestHandler {

    private static final DefaultHttpRequestHandler instance = new DefaultHttpRequestHandler();

    private final Map<HttpRequestHandlerCondition, Function<HttpRequest, HttpResponse>> handlerMethodMapper = Map.of(
            new HttpRequestHandlerCondition(HttpMethod.GET, "/"), this::handleGetRoot,
            new HttpRequestHandlerCondition(HttpMethod.GET, "/index.html"), this::handleGetIndexHtml
    );

    private DefaultHttpRequestHandler() {
    }

    @Override
    public HttpResponse handle(final HttpRequest request) {
        final HttpRequestHandlerCondition requestCondition = HttpRequestHandlerCondition.from(request);
        final Function<HttpRequest, HttpResponse> handlerMethod = handlerMethodMapper.get(requestCondition);
        if (handlerMethod == null) {
            throw new IllegalStateException("해당 요청을 처리할 수 없는 핸들러입니다. " + requestCondition);
        }

        return handlerMethod.apply(request);
    }

    @Override
    public boolean canHandle(final HttpRequest request) {
        final HttpRequestHandlerCondition requestCondition = HttpRequestHandlerCondition.from(request);

        return handlerMethodMapper.containsKey(requestCondition);
    }

    private HttpResponse handleGetRoot(final HttpRequest request) {
        return new HttpResponse();
    }

    private HttpResponse handleGetIndexHtml(final HttpRequest request) {
        return new HttpResponse();
    }

    public static DefaultHttpRequestHandler getInstance() {
        return instance;
    }
}
