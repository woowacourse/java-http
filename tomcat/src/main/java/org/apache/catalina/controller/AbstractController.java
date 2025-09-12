package org.apache.catalina.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.catalina.Controller;
import org.apache.catalina.loader.ResourceLoader;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.MimeType;

public abstract class AbstractController implements Controller {

    protected final ResourceLoader resourceLoader;

    protected AbstractController(final ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        switch (request.getRequestLine().getMethod()) {
            case GET:
                doGet(request, response);
                break;
            case POST:
                doPost(request, response);
                break;
            default:
                throw new IllegalArgumentException("현재 지원하지 않는 HTTP 메서드입니다.");
        }
    }

    protected abstract void doPost(final HttpRequest request, final HttpResponse response) throws Exception;

    protected abstract void doGet(final HttpRequest request, final HttpResponse response) throws Exception;

    protected void handleError(final HttpResponse response, final HttpStatus status) throws Exception {
        byte[] body;
        try {
            body = resourceLoader.getResourceAsBytes("/" + status.getCode() + ".html");
        } catch (IOException e) {
            body = resourceLoader.getDefaultErrorPage(status).getBytes(StandardCharsets.UTF_8);
        }
        response.error(status, body);
        response.setHeader("Content-Type", MimeType.HTML.getType());
    }
}
