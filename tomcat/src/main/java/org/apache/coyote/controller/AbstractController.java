package org.apache.coyote.controller;

import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;


public abstract class AbstractController implements Controller {

    private static final String ALLOWED_METHODS = "GET, POST";

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {

        if (HttpMethod.GET == request.getMethod()) {
            doGet(request, response);
            return;
        }

        if (HttpMethod.POST == request.getMethod()) {
            doPost(request, response);
            return;
        }

        response.methodNotAllowed(ALLOWED_METHODS);
    }

    //하위 클래스가 필요한 메서드만 재정의한다.
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
    }
}