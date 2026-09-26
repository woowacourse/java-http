package com.techcourse.controller;

import com.techcourse.view.ResourceRenderer;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    private static final String REDIRECT_PREFIX = "redirect:";
    private final ResourceRenderer resourceRenderer = new ResourceRenderer();

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        final String view = switch (request.method()) {
            case "POST" -> doPost(request, response);
            case "GET" -> doGet(request, response);
            default -> throw new IllegalStateException("지원하지 않는 메소드");
        };

        if (view == null) {
            return;
        }
        if (view.startsWith(REDIRECT_PREFIX)) {
            response.sendRedirect(view.substring(REDIRECT_PREFIX.length()));
            return;
        }
        resourceRenderer.render(view, response);
    }

    protected String doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        throw new IllegalStateException("지원하지 않는 메소드");
    }

    protected String doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        throw new IllegalStateException("지원하지 않는 메소드");
    }
}
