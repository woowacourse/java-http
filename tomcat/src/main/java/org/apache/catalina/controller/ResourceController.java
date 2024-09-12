package org.apache.catalina.controller;

import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(ResourceController.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (request.getMethod() != HttpMethod.GET) {
            log.warn("처리할 수 없는 요청입니다: {} {}", request.getMethod(), request.getPath());
            throw new IllegalArgumentException("처리할 수 없는 요청입니다.");
        }

        File responseFile = File.of(request.getPath());
        responseFile.addToResponse(response);
    }
}
