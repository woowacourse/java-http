package org.apache.coyote.http11.controller;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiConsumer;
import org.apache.coyote.common.HttpMethod;
import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.common.HttpResponse;
import org.apache.coyote.exception.MethodNotAllowedException;

public abstract class HttpMethodController extends AbstractController {

    protected final Map<HttpMethod, BiConsumer<HttpRequest, HttpResponse>> handlers = new EnumMap<>(HttpMethod.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        HttpMethod httpMethod = request.getHttpMethod();
        handlers.getOrDefault(httpMethod, (ignore1, ignore2) -> {
            throw new MethodNotAllowedException(handlers.keySet());
        }).accept(request, response);
    }
}
