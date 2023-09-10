package org.apache.coyote.http11.controller;

import java.util.Map;
import java.util.function.BiConsumer;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    private final Map<HttpMethod, BiConsumer<HttpRequest, HttpResponse>> supportedMethods = Map.of(
            HttpMethod.GET, this::doGet,
            HttpMethod.POST, this::doPost
    );


    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        final HttpMethod httpMethod = request.getHttpStartLine().getHttpRequestMethod();
        supportedMethods.get(httpMethod).accept(request, response);
    }

    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.notAllowedMethod();
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) {
        response.notAllowedMethod();
    }
}
