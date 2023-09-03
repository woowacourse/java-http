package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class LoginHandler implements Handler {

    @Override
    public boolean supports(HttpRequest request) {
        return request.getUrl().equals("/login");
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        return null;
    }
}
