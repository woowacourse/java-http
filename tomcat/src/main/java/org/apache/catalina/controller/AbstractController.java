package org.apache.catalina.controller;

import org.apache.coyote.http11.exception.HttpException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public abstract class AbstractController implements Controller {
    private static final String GET = "GET";
    private static final String POST = "POST";

    @Override
    public HttpResponse service(HttpRequest request) throws Exception {
        String method = request.startLine().method();
        if (GET.equals(method)) {
            return doGet(request);
        }
        if (POST.equals(method)) {
            return doPost(request);
        }
        throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    protected HttpResponse doPost(HttpRequest request) throws Exception {
        throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    protected HttpResponse doGet(HttpRequest request) throws Exception {
        throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
