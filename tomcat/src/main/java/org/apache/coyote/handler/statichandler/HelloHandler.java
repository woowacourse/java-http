package org.apache.coyote.handler.statichandler;

import org.apache.coyote.handler.Handler;
import org.apache.coyote.http11.*;

public class HelloHandler implements Handler {

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        String body = "Hello world!";
        HttpResponse response = new HttpResponse();
        response.setHttpVersion(HttpVersion.HTTP11.value());
        response.setHttpStatus(HttpStatus.OK);
        response.setHeader(HttpHeader.CONTENT_TYPE, ContentType.TEXT_HTML.value())
                .setHeader(HttpHeader.CONTENT_LENGTH, body.length() + " ");
        response.setBody(body);
        return response;
    }
}
