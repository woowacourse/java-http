package org.apache.coyote.controller;

import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

import java.io.IOException;

public interface Controller {

    boolean supports(final HttpRequest httpRequest);

    void service(final HttpRequest request, final HttpResponse response) throws IOException;
}
