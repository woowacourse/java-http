package org.apache.catalina.controller;

import org.apache.coyote.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.util.Map;
import java.util.function.BiConsumer;

public abstract class AbstractController implements Controller {
    private final Map<HttpMethod, BiConsumer<HttpRequest, HttpResponse>> handlers =
            Map.of(
                    HttpMethod.GET, this::doGet,
                    HttpMethod.POST, this::doPost);

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        handlers.get(request.getMethod()).accept(request, response);
    }

    protected void doGet(HttpRequest request, HttpResponse response) {

    }

    protected void doPost(HttpRequest request, HttpResponse response) {

    }
}
