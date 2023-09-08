package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Controller {
    boolean isSupported(HttpRequest request);

    HttpResponse service(HttpRequest request) throws Exception;
}

