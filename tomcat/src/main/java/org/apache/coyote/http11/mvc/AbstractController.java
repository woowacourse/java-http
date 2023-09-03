package org.apache.coyote.http11.mvc;

import java.util.Map;
import java.util.function.BiFunction;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.mvc.view.ResponseEntity;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    private final Map<HttpMethod, BiFunction<HttpRequest, HttpResponse, ResponseEntity>> supportedMethods = Map.of(
            HttpMethod.GET, this::handleGetRequest,
            HttpMethod.POST, this::handlePostRequest
    );


    @Override
    public ResponseEntity handleRequest(final HttpRequest request, final HttpResponse response) {
        final HttpMethod httpMethod = request.getHttpStartLine().getHttpRequestMethod();
        return supportedMethods.get(httpMethod).apply(request, response);
    }

    protected ResponseEntity handleGetRequest(final HttpRequest request, final HttpResponse response) {
        return ResponseEntity.forwardTo(HttpStatus.METHOD_NOT_ALLOWED, "/405.html");
    }

    protected ResponseEntity handlePostRequest(final HttpRequest request, final HttpResponse response) {
        return ResponseEntity.forwardTo(HttpStatus.METHOD_NOT_ALLOWED, "/405.html");
    }
}
