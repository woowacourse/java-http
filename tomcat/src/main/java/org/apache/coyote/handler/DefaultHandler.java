package org.apache.coyote.handler;

import org.apache.coyote.request.Request;
import org.apache.coyote.response.HttpStatus;
import org.apache.coyote.response.Response;

public class DefaultHandler {

    public void response(Request request, Response response) {
        String body = "Hello world!";

        response.setHttpStatus(HttpStatus.OK);
        response.addHeaders("Content-Length", String.valueOf(body.getBytes().length));
        response.addHeaders("Content-Type", request.getResourceTypes());
        response.setResponseBody(body);
    }
}
