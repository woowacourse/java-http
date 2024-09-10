package org.apache.coyote.http11.domain.controller;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiConsumer;
import org.apache.coyote.http11.domain.HttpMethod;
import org.apache.coyote.http11.domain.request.HttpRequest;
import org.apache.coyote.http11.domain.response.HttpResponse;
import org.apache.coyote.http11.domain.response.HttpStatus;

public abstract class AbstractController implements Controller {

    private final Map<HttpMethod, BiConsumer<HttpRequest, HttpResponse>> methods = new EnumMap<>(HttpMethod.class);

    public AbstractController() {
        methods.put(HttpMethod.GET, this::doGet);
        methods.put(HttpMethod.POST, this::doPost);
        methods.put(HttpMethod.HEAD, this::doHead);
        methods.put(HttpMethod.OPTIONS, this::doOptions);
        methods.put(HttpMethod.PUT, this::doPut);
        methods.put(HttpMethod.DELETE, this::doDelete);
        methods.put(HttpMethod.TRACE, this::doTrace);
        methods.put(HttpMethod.CONNECT, this::doConnect);
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        HttpMethod method = request.getMethod();
        BiConsumer<HttpRequest, HttpResponse> handler = methods.get(method);
        handler.accept(request, response);
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setStatus(HttpStatus.NOT_IMPLEMENTED);
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
        response.setStatus(HttpStatus.NOT_IMPLEMENTED);
    }

    protected void doHead(HttpRequest request, HttpResponse response) {
        response.setStatus(HttpStatus.NOT_IMPLEMENTED);
    }

    protected void doOptions(HttpRequest request, HttpResponse response) {
        response.setStatus(HttpStatus.NOT_IMPLEMENTED);
    }

    protected void doPut(HttpRequest request, HttpResponse response) {
        response.setStatus(HttpStatus.NOT_IMPLEMENTED);
    }

    protected void doDelete(HttpRequest request, HttpResponse response) {
        response.setStatus(HttpStatus.NOT_IMPLEMENTED);
    }

    protected void doTrace(HttpRequest request, HttpResponse response) {
        response.setStatus(HttpStatus.NOT_IMPLEMENTED);
    }

    protected void doConnect(HttpRequest request, HttpResponse response) {
        response.setStatus(HttpStatus.NOT_IMPLEMENTED);
    }
}
