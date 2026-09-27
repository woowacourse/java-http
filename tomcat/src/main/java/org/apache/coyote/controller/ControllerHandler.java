package org.apache.coyote.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

@FunctionalInterface
public interface ControllerHandler {

    void handle(HttpRequest request, HttpResponse response) throws Exception;
}
