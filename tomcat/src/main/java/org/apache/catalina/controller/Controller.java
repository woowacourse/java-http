package org.apache.catalina.controller;

import java.io.IOException;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

public interface Controller {
    void service(HttpRequest request, HttpResponse response) throws IOException;
}
