package org.apache.coyote.controller;

import org.apache.coyote.Controller;
import org.apache.coyote.http11.http.message.HttpRequest;
import org.apache.coyote.http11.http.message.HttpResponse;
import org.apache.coyote.http11.http.util.HttpMethod;

public abstract class AbstractController implements Controller {

    protected static final String FILE_PATH_PREFIX = "/";
    protected static final String EMPTY_MESSAGE_BODY = "";

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        final String method = request.getMethod();
        if (HttpMethod.GET.isSameMethod(method)) {
            doGet(request, response);
            return;
        }
        if (HttpMethod.POST.isSameMethod(method)) {
            doPost(request, response);
            return;
        }
        throw new IllegalArgumentException("잘못된 메소드 형식입니다. " + method);
    }

    protected abstract void doPost(HttpRequest request, HttpResponse response) throws Exception;

    protected abstract void doGet(HttpRequest request, HttpResponse response) throws Exception;
}
