package org.apache.catalina.controller;

import static org.apache.coyote.http11.request.HttpMethod.CONNECT;
import static org.apache.coyote.http11.request.HttpMethod.DELETE;
import static org.apache.coyote.http11.request.HttpMethod.GET;
import static org.apache.coyote.http11.request.HttpMethod.HEAD;
import static org.apache.coyote.http11.request.HttpMethod.OPTIONS;
import static org.apache.coyote.http11.request.HttpMethod.PATCH;
import static org.apache.coyote.http11.request.HttpMethod.POST;
import static org.apache.coyote.http11.request.HttpMethod.PUT;
import static org.apache.coyote.http11.request.HttpMethod.TRACE;
import static org.apache.coyote.http11.response.HttpStatusCode.NOT_IMPLEMENTED;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    private final Map<HttpMethod, BiConsumer<HttpRequest, HttpResponse>> methodHandlers = new HashMap<>();

    public AbstractController() {
        methodHandlers.put(GET, this::doGet);
        methodHandlers.put(POST, this::doPost);
        methodHandlers.put(PUT, this::doPut);
        methodHandlers.put(DELETE, this::doDelete);
        methodHandlers.put(PATCH, this::doPatch);
        methodHandlers.put(HEAD, this::doHead);
        methodHandlers.put(OPTIONS, this::doOptions);
        methodHandlers.put(TRACE, this::doTrace);
        methodHandlers.put(CONNECT, this::doConnect);
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        BiConsumer<HttpRequest, HttpResponse> handler = methodHandlers.get(request.getMethod());
        if (handler == null) {
            response.setStatusLine(request.getVersion(), NOT_IMPLEMENTED);
            return;
        }
        handler.accept(request, response);
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setStatusLine(request.getVersion(), NOT_IMPLEMENTED);
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
        response.setStatusLine(request.getVersion(), NOT_IMPLEMENTED);
    }

    protected void doPut(HttpRequest request, HttpResponse response) {
        response.setStatusLine(request.getVersion(), NOT_IMPLEMENTED);
    }

    protected void doDelete(HttpRequest request, HttpResponse response) {
        response.setStatusLine(request.getVersion(), NOT_IMPLEMENTED);
    }

    protected void doPatch(HttpRequest request, HttpResponse response) {
        response.setStatusLine(request.getVersion(), NOT_IMPLEMENTED);
    }

    protected void doHead(HttpRequest request, HttpResponse response) {
        response.setStatusLine(request.getVersion(), NOT_IMPLEMENTED);
    }

    protected void doOptions(HttpRequest request, HttpResponse response) {
        response.setStatusLine(request.getVersion(), NOT_IMPLEMENTED);
    }

    protected void doTrace(HttpRequest request, HttpResponse response) {
        response.setStatusLine(request.getVersion(), NOT_IMPLEMENTED);
    }

    protected void doConnect(HttpRequest request, HttpResponse response) {
        response.setStatusLine(request.getVersion(), NOT_IMPLEMENTED);
    }
}
