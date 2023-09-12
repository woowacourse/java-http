package org.apache.coyote.http11.servlet;

import java.io.IOException;
import java.util.List;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.common.request.HttpRequest;
import org.apache.coyote.http11.common.response.HttpResponse;
import org.apache.coyote.http11.common.response.StatusCode;
import org.apache.coyote.http11.exception.BadRequestException;
import org.apache.coyote.http11.exception.MethodNotAllowedException;
import org.apache.coyote.http11.util.StaticFileLoader;

public abstract class Servlet {

    private final List<HttpMethod> allowedMethods;

    protected Servlet(final List<HttpMethod> allowedMethods) {
        this.allowedMethods = allowedMethods;
    }

    public void service(HttpRequest request, HttpResponse response) throws IOException {
        try {
            execute(request, response);
        } catch (BadRequestException e) {
            handleBadRequest(response);
        } catch (MethodNotAllowedException e) {
            handleMethodNotAllowed(response);
        }
    }

    private void execute(final HttpRequest request, final HttpResponse response) throws IOException {
        if (request.getMethod() == HttpMethod.GET) {
            doGet(request, response);
        } else if (request.getMethod() == HttpMethod.POST) {
            doPost(request, response);
        } else {
            throw new MethodNotAllowedException();
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        throw new MethodNotAllowedException();
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
        throw new MethodNotAllowedException();
    }

    private void handleBadRequest(HttpResponse response) throws IOException {
        String content = StaticFileLoader.load(Page.BAD_REQUEST.getUri());

        response.setStatusCode(StatusCode.BAD_REQUEST);
        response.setContentType(ContentType.TEXT_HTML);
        response.setContentLength(content.getBytes().length);
        response.setBody(content);
    }

    private void handleMethodNotAllowed(HttpResponse response) throws IOException {
        String content = StaticFileLoader.load(Page.BAD_REQUEST.getUri());

        response.setStatusCode(StatusCode.METHOD_NOT_ALLOWED);
        response.setContentType(ContentType.TEXT_HTML);
        response.setContentLength(content.getBytes().length);
        response.setAllow(allowedMethods);
        response.setBody(content);
    }
}
