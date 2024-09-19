package org.apache.coyote.controller;

import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public abstract class AbstractController implements Controller {

    private static final String METHOD_NOT_ALLOWED_PAGE = "/405.html";
    private static final String NOT_IMPLEMENTED_PAGE = "/501.html";

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        HttpMethod httpMethod = request.getHttpMethod();

        validateMethod(httpMethod, response);

        if (httpMethod.isPost()) {
            doPost(request, response);
        }

        if (httpMethod.isGet()) {
            doGet(request, response);
        }
    }

    private static void validateMethod(HttpMethod httpMethod, HttpResponse response) {
        if (!httpMethod.isValidMethod()) {
            response.setStatus(HttpStatus.NOT_IMPLEMENTED);
            response.redirectTo(NOT_IMPLEMENTED_PAGE);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
        response.redirectTo(METHOD_NOT_ALLOWED_PAGE);
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
        response.redirectTo(METHOD_NOT_ALLOWED_PAGE);
    }
}
