package org.apache.coyote.controller;

import org.apache.coyote.http11.DispatchResult;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

@FunctionalInterface
public interface ControllerHandler {

    DispatchResult handle(HttpRequest request, HttpResponse response) throws Exception;
}
