package org.mvc.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.mvc.view.ViewResolver;

public abstract class AbstractController implements Controller {

    private final ViewResolver viewResolver = new ViewResolver();

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        final String view = switch (request.method()) {
            case "POST" -> doPost(request, response);
            case "GET" -> doGet(request, response);
            default -> throw new IllegalStateException("지원하지 않는 메소드");
        };

        viewResolver.resolve(view, response);
    }

    protected String doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        throw new IllegalStateException("지원하지 않는 메소드");
    }

    protected String doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        throw new IllegalStateException("지원하지 않는 메소드");
    }
}
