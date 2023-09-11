package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;
import java.net.URISyntaxException;

public interface Controller {
    boolean isSupported(HttpRequest request);

    void service(HttpRequest request, HttpResponse response) throws URISyntaxException, IOException;
}

