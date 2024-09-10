package org.apache.coyote.http11;

import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;

public sealed interface Controller permits AbstractController {
    void service(Http11Request request, Http11Response response);
}
