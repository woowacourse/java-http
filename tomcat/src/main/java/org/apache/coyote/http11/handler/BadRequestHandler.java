package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;

public class BadRequestHandler implements Handler {

    @Override
    public boolean supports(HttpRequest request) {
        return true;
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        return new HttpResponse.Builder()
                .setHttpStatusCode(HttpStatusCode.BAD_REQUEST).build();
    }
}
