package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Controller {
    HttpResponse service(HttpRequest request) throws Exception;

    boolean canHandle(HttpRequest request);
}
