package org.apache.coyote.http11.domain.controller;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import org.apache.coyote.http11.domain.HttpMethod;
import org.apache.coyote.http11.domain.request.HttpRequest;
import org.apache.coyote.http11.domain.response.HttpResponse;
import org.apache.coyote.http11.domain.response.HttpStatus;

public abstract class AbstractController implements Controller {

    protected final StaticResourceHandler staticResourceHandler;
    private final Map<HttpMethod, Function<HttpRequest, HttpResponse>> methods = new EnumMap<>(HttpMethod.class);

    public AbstractController() {
        this.staticResourceHandler = new StaticResourceHandler();

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
    public HttpResponse service(HttpRequest request) {
        HttpMethod method = request.getMethod();
        Function<HttpRequest, HttpResponse> handler = methods.get(method);
        return handler.apply(request);
    }

    protected HttpResponse doGet(HttpRequest request) {
        return HttpResponse.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    protected HttpResponse doPost(HttpRequest request) {
        return HttpResponse.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    protected HttpResponse doHead(HttpRequest request) {
        return HttpResponse.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    protected HttpResponse doOptions(HttpRequest request) {
        return HttpResponse.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    protected HttpResponse doPut(HttpRequest request) {
        return HttpResponse.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    protected HttpResponse doDelete(HttpRequest request) {
        return HttpResponse.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    protected HttpResponse doTrace(HttpRequest request) {
        return HttpResponse.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    protected HttpResponse doConnect(HttpRequest request) {
        return HttpResponse.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
