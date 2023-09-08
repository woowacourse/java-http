package org.apache.coyote.controller;

import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

import java.io.IOException;

public interface RequestHandler {

    void handle(final HttpRequest request, final HttpResponse response) throws IOException;
}
