package org.apache.coyote.http11.controller;


import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Controller {
    boolean canHandle(String url);

    HttpResponse handle(HttpRequest httpRequest);
}
