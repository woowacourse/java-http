package com.techcourse.controller;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpMethodType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) {
        HttpMethodType method = request.getMethodType();
        
        try {
            return switch (method) {
                case GET -> doGet(request);
                case POST -> doPost(request);
                case PUT -> doPut(request);
                case DELETE -> doDelete(request);
                case PATCH -> doPatch(request);
            };
        } catch (IOException e) {
            return HttpResponse.notFound();
        }
    }

    protected HttpResponse doGet(HttpRequest request) throws IOException {
        return HttpResponse.notFound();
    }

    protected HttpResponse doPost(HttpRequest request) {
        return HttpResponse.notFound();
    }

    protected HttpResponse doPut(HttpRequest request) {
        return HttpResponse.notFound();
    }

    protected HttpResponse doDelete(HttpRequest request) {
        return HttpResponse.notFound();
    }

    protected HttpResponse doPatch(HttpRequest request) {
        return HttpResponse.notFound();
    }
}
