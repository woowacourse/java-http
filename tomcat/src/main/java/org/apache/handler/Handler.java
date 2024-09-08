package org.apache.handler;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public interface Handler {

    void handle(HttpRequest request, HttpResponse response);
}
