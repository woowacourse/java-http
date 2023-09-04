package org.apache.handler;

import java.io.IOException;
import org.apache.common.ContentType;
import org.apache.common.HttpStatus;
import org.apache.request.HttpRequest;
import org.apache.response.HttpResponse;

public class DefaultHandler implements RequestHandler {

    private static final String DEFAULT_RESPONSE = "Hello world!";

    @Override
    public HttpResponse handle(HttpRequest httpRequest) throws IOException {
        return new HttpResponse(HttpStatus.OK, ContentType.TEXT_HTML, DEFAULT_RESPONSE);
    }
}
