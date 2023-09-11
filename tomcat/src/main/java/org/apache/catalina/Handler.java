package org.apache.catalina;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Handler {

    void handle(HttpRequest request, HttpResponse response) throws Exception;
}
