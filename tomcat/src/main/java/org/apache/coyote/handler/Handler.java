package org.apache.coyote.handler;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Handler {

    void service(HttpRequest httpRequest, HttpResponse httpResponse);
}
