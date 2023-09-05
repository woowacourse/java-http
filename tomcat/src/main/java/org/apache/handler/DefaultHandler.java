package org.apache.handler;

import org.apache.common.ContentType;
import org.apache.common.HttpStatus;
import org.apache.request.HttpRequest;
import org.apache.response.HttpResponse;

public class DefaultHandler implements RequestHandler {

    private static final String DEFAULT_RESPONSE = "Hello world!";

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        HttpResponse httpResponse = new HttpResponse(HttpStatus.OK, DEFAULT_RESPONSE);
        httpResponse.setContentType(ContentType.TEXT_HTML);
        return httpResponse;
    }
}
