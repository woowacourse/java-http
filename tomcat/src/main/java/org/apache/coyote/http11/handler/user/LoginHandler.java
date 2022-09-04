package org.apache.coyote.http11.handler.user;

import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;

public class LoginHandler extends UserHandler{

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        final String body = httpRequest.getBody();
        return generateLoginResponse(body);
    }
}
