package org.apache.coyote.core.controller;

import java.io.IOException;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.http.reqeust.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.io.ClassPathResource;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response)
            throws IOException, UncheckedServletException {
        String method = request.getMethod();
        if (method.equals("POST")) {
            doPost(request, response);
        }
        if (method.equals("GET")) {
            doGet(request, response);
        }
    }

    protected void doGet(final HttpRequest request, final HttpResponse response) throws UncheckedServletException {
        String path = resourcePath(request.getPath());
        String responseBody = new ClassPathResource().getStaticContent(path);

        response.setContentLength(responseBody.getBytes().length);
        response.setResponseBody(responseBody);
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) throws UncheckedServletException {
        String path = resourcePath(request.getPath());
        String responseBody = new ClassPathResource().getStaticContent(path);

        response.setContentLength(responseBody.getBytes().length);
        response.setResponseBody(responseBody);
        response.sendRedirect("./index.html");
    }

    private String resourcePath(final String path) {
        if (!path.contains(".")) {
            return path + ".html";
        }
        return path;
    }
}
