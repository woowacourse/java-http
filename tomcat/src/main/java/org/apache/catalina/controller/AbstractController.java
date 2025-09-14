package org.apache.catalina.controller;

import org.apache.catalina.ResponseUtil;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public final HttpResponse service(HttpRequest request) throws Exception {
        switch (request.method()) {
            case "GET" -> {
                return doGet(request);
            }
            case "POST" -> {
                return doPost(request);
            }
            default -> {
                return ResponseUtil.buildMethodNotAllowedResponse(request.version());
            }
        }
    }

    protected abstract HttpResponse doGet(HttpRequest request) throws Exception;
    
    protected abstract HttpResponse doPost(HttpRequest request) throws Exception;
}
