package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

import java.util.EnumSet;
import java.util.stream.Collectors;

public abstract class AbstractController implements Controller {

    private final EnumSet<HttpMethod> supportedMethods;

    protected AbstractController(HttpMethod firstMethod, HttpMethod... otherMethods) {
        this.supportedMethods = EnumSet.of(firstMethod, otherMethods);
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (!supportedMethods.contains(request.getMethod())) {
            response.sendError(HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed");
            response.setHeader("Allow", supportedMethods.stream()
                    .map(HttpMethod::name)
                    .collect(Collectors.joining(", ")));
            return;
        }

        switch (request.getMethod()) {
            case GET -> doGet(request, response);
            case POST -> doPost(request, response);
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        throw new IllegalStateException("GET 메서드가 구현되지 않았습니다.");
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        throw new IllegalStateException("POST 메서드가 구현되지 않았습니다.");
    }
}
