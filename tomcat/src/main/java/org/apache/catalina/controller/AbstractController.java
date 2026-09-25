package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public final void service(final HttpRequest request, final HttpResponse response) throws Exception {
        switch (request.getMethod()) {
            case "GET" -> doGet(request, response);
            case "POST" -> doPost(request, response);
            default -> { }
        }
    }

    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        // 필요한 컨트롤러에서 재정의
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        // 필요한 컨트롤러에서 재정의
    }
}
