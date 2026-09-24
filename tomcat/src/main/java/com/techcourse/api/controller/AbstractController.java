package com.techcourse.api.controller;

import com.techcourse.api.Controller;
import java.io.IOException;
import org.apache.catalina.StaticResourceLoader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        if (request.isPost()) {
            doPost(request, response);
            return;
        }
        if (request.isGet()) {
            doGet(request, response);
            return;
        }
        throw new UnsupportedOperationException("지원하지 않는 HTTP 메서드입니다.");
    }

    protected abstract void doPost(final HttpRequest request, final HttpResponse response) throws Exception;

    protected abstract void doGet(final HttpRequest request, final HttpResponse response) throws Exception;

    protected final String readResource(final String resourcePath) throws IOException {
        return StaticResourceLoader.read(resourcePath);
    }
}
