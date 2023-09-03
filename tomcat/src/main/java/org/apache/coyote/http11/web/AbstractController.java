package org.apache.coyote.http11.web;

import java.util.Map;
import java.util.function.BiFunction;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestMethod;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseStatus;
import org.apache.coyote.http11.response.ResponseEntity;

public abstract class AbstractController implements Controller {

    private final Map<HttpRequestMethod, BiFunction<HttpRequest, HttpResponse, ResponseEntity>> supportedMethods = Map.of(
            HttpRequestMethod.GET, this::handleGetRequest,
            HttpRequestMethod.POST, this::handlePostRequest
    );


    @Override
    public ResponseEntity handleRequest(final HttpRequest request, final HttpResponse response) {
        final HttpRequestMethod httpRequestMethod = request.getHttpStartLine().getHttpRequestMethod();
        return supportedMethods.get(httpRequestMethod).apply(request, response);
    }

    protected ResponseEntity handleGetRequest(final HttpRequest request, final HttpResponse response) {
        return ResponseEntity.forwardTo(HttpResponseStatus.METHOD_NOT_ALLOWED, "/404.html");
    }

    protected ResponseEntity handlePostRequest(final HttpRequest request, final HttpResponse response) {
        return ResponseEntity.forwardTo(HttpResponseStatus.METHOD_NOT_ALLOWED, "/404.html");
    }
}
