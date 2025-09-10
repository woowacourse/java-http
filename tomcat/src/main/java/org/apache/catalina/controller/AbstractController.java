package org.apache.catalina.controller;

import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(AbstractController.class);

    @Override
    public HttpResponse service(HttpRequest httpRequest) {
        HttpMethod method = httpRequest.getMethod();
        return switch (method) {
            case GET -> doGet(httpRequest);
            case POST -> doPost(httpRequest);
            default -> ResponseEntity.notFound("");
        };
    }

    protected HttpResponse doGet(HttpRequest httpRequest) {
        log.debug("get method needs to be overridden");
        return ResponseEntity.notFound("");
    }

    protected HttpResponse doPost(HttpRequest httpRequest) {
        log.debug("post method needs to be overridden");
        return ResponseEntity.notFound("");
    }
}

