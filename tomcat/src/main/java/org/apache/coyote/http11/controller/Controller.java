package org.apache.coyote.http11.controller;

import java.io.IOException;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;

public interface Controller {

    boolean canHandle(HttpRequest httpRequest);

    void service(HttpRequest request, HttpResponse response) throws IOException;
}
