package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public interface Controller {

    boolean supports(HttpRequest httpRequest);

    void handle(HttpRequest httpRequest, HttpResponse httpResponse);
}
