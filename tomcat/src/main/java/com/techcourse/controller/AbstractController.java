package com.techcourse.controller;

import org.apache.coyote.http11.StaticResourceReader;
import org.apache.coyote.request.HttpMethod;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public abstract class AbstractController implements Controller{
    protected final StaticResourceReader resourceReader = new StaticResourceReader();

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if(request.getHttpMethod() == HttpMethod.POST){
            doPost(request, response);
        }

        if(request.getHttpMethod() == HttpMethod.GET){
            doGet(request,response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
}
