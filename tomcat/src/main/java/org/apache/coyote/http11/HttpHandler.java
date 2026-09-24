package org.apache.coyote.http11;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface HttpHandler {

    void handle(HttpRequest request, HttpResponse response) throws Exception;
}
