package org.apache.controller;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

public class BasicController implements Controller {

    @Override
    public boolean isProcessable(HttpRequest httpRequest) {
        return httpRequest.pathEquals("/");
    }

    @Override
    public HttpResponse process(HttpRequest httpRequest) {
        HttpResponse httpResponse = HttpResponse.createEmptyResponse(httpRequest);
        httpResponse.setResponseBody("Hello world!");
        httpResponse.setHttpStatus(HttpStatus.OK);

        return httpResponse;
    }
}
