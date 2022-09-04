package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;

public class RegisterPageHandler extends ResourceHandler {

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        return generateResourceResponse("/register.html");
    }
}
