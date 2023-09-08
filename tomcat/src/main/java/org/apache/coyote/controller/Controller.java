package org.apache.coyote.controller;

import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public interface Controller {

    boolean supports(final HttpRequest httpRequest);

    void service(final HttpRequest request, final HttpResponse response) throws Exception;
}
