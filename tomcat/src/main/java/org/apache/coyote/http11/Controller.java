package org.apache.coyote.http11;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public sealed interface Controller permits AbstractController {
    void service(HttpRequest request, HttpResponse response);
}
