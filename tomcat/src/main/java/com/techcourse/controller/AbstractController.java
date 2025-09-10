package com.techcourse.controller;

import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public abstract class AbstractController implements Controller {
    @Override
    public HttpResponse service(HttpRequest request) throws Exception {
        if (HttpMethod.GET.equals(request.getMethod())) {
            return doGet(request);
        } else if (HttpMethod.POST.equals(request.getMethod())) {
            return doPost(request);
        }
        return new HttpResponse("HTTP/1.1", HttpStatus.METHOD_NOT_ALLOWED, HttpHeaders.empty(), "");
    }

    protected HttpResponse doPost(HttpRequest request) throws Exception { 
        return new HttpResponse("HTTP/1.1", HttpStatus.METHOD_NOT_ALLOWED, HttpHeaders.empty(), "");
    }
    protected HttpResponse doGet(HttpRequest request) throws Exception { 
        return new HttpResponse("HTTP/1.1", HttpStatus.NOT_FOUND, HttpHeaders.empty(), "");
    }
}
