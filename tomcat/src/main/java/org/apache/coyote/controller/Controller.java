package org.apache.coyote.controller;

import java.io.IOException;
import org.apache.coyote.httpRequest.HttpRequest;
import org.apache.coyote.httpResponse.HttpResponse;

public interface Controller {
    void service(HttpRequest request, HttpResponse response) throws IOException;
}
