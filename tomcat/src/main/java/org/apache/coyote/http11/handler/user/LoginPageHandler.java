package org.apache.coyote.http11.handler.user;

import org.apache.coyote.http11.handler.ResourceHandler;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;

public class LoginPageHandler extends ResourceHandler {
    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        return generateResourceResponse("/login.html");
    }
}
