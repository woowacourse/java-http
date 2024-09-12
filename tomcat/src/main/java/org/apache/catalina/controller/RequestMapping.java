package org.apache.catalina.controller;

import java.util.Map;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.RequestLine;
import org.apache.coyote.http11.RequestUri;

public class RequestMapping {

    private final Map<RequestUri, Controller> controllers;

    public RequestMapping(Map<RequestUri, Controller> controllers) {
        this.controllers = controllers;
    }

    public static RequestMapping from(Map<RequestUri, Controller> controllers) {
        return new RequestMapping(controllers);
    }

    public Controller getController(Http11Request request) {
        RequestLine requestLine = request.getRequestLine();
        RequestUri requestUri = requestLine.getRequestUri();
        return controllers.getOrDefault(requestUri, new StaticController());
    }
}

