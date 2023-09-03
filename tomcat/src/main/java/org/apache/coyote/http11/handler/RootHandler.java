package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RootHandler implements RestHandler {

    @Override
    public boolean supports(HttpRequest request) {
        return request.getUrl().equals("/");
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        return new HttpResponse.Builder().setContentType("text/html").setBody("Hello world!").build();
    }
}
