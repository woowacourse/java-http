package org.apache.coyote.controller;

import java.util.Map;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller {

    private final Map<HttpMethod, ThrowableBiConsumer<HttpRequest, HttpResponse, Exception>> methodHandlerMap = Map.of(
        HttpMethod.GET, this::doGet,
        HttpMethod.POST, this::doPost
    );

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        final HttpMethod method = request.method();
        methodHandlerMap.get(method)
            .acceptNow(request, response);
    }

    protected abstract void doPost(HttpRequest request, HttpResponse response)
        throws Exception;

    protected abstract void doGet(HttpRequest request, HttpResponse response)
        throws Exception;
}
