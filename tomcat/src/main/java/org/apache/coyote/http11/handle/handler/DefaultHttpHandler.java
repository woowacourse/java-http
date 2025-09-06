package org.apache.coyote.http11.handle.handler;

import java.util.Map;
import java.util.function.Function;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.handle.HttpHandlerCondition;
import org.apache.coyote.http11.reqeust.HttpMethod;
import org.apache.coyote.http11.reqeust.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class DefaultHttpHandler implements HttpHandler {

    private static final DefaultHttpHandler instance = new DefaultHttpHandler();

    private final Map<HttpHandlerCondition, Function<HttpRequest, HttpResponse>> handlerMethodMapper = Map.of(
            new HttpHandlerCondition(HttpMethod.GET, "/"), this::handleGetRoot
    );

    private DefaultHttpHandler() {
    }

    @Override
    public HttpResponse handle(final HttpRequest request) {
        final HttpHandlerCondition requestCondition = HttpHandlerCondition.from(request);
        final Function<HttpRequest, HttpResponse> handlerMethod = handlerMethodMapper.get(requestCondition);
        if (handlerMethod == null) {
            throw new IllegalStateException("해당 요청을 처리할 수 없는 핸들러입니다. " + requestCondition);
        }

        return handlerMethod.apply(request);
    }

    @Override
    public boolean canHandle(final HttpRequest request) {
        final HttpHandlerCondition requestCondition = HttpHandlerCondition.from(request);

        return handlerMethodMapper.containsKey(requestCondition);
    }

    private HttpResponse handleGetRoot(final HttpRequest request) {
        final String body = "Hello world!";
        final HttpHeaders headers = new HttpHeaders();
        headers.addHeader("Content-Type", "text/html;charset=utf-8 ");
        headers.addHeader("Content-Length", body.getBytes().length + " ");

        return new HttpResponse(
                request.protocolVersion(),
                HttpStatus.OK,
                headers,
                body
        );
    }

    public static DefaultHttpHandler getInstance() {
        return instance;
    }
}
