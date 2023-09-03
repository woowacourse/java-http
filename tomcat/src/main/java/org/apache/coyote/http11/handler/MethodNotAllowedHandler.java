package org.apache.coyote.http11.handler;

import java.io.IOException;
import org.apache.coyote.Handler;
import org.apache.coyote.common.HttpContentType;
import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.common.HttpResponse;
import org.apache.coyote.common.HttpStatus;
import org.apache.coyote.util.ResourceResolver;

public class MethodNotAllowedHandler implements Handler {

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        HttpResponse response = new HttpResponse();
        response.setContentType(HttpContentType.TEXT_HTML);
        response.setContentBody(ResourceResolver.resolve("/405.html"));
        response.setHttpStatus(HttpStatus.METHOD_NOT_ALLOWED);
        return response;
    }
}
