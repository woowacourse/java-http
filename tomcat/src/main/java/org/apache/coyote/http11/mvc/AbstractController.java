package org.apache.coyote.http11.mvc;

import java.util.Map;
import java.util.function.BiConsumer;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    private final Map<HttpMethod, BiConsumer<HttpRequest, HttpResponse>> supportedMethods = Map.of(
            HttpMethod.GET, this::doGetRequest,
            HttpMethod.POST, this::doPostRequest
    );


    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        final HttpMethod httpMethod = request.getHttpStartLine().getHttpRequestMethod();
        supportedMethods.get(httpMethod).accept(request, response);
    }

    protected void doGetRequest(final HttpRequest request, final HttpResponse response) {
        response.notAllowedMethod();
    }

    protected void doPostRequest(final HttpRequest request, final HttpResponse response) {
        response.notAllowedMethod();
    }
}
