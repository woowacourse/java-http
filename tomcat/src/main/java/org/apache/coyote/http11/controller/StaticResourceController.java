package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticResourceController implements Controller {

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        RequestLine requestLine = httpRequest.getRequestLine();

        httpResponse.setHttpStatus(HttpStatus.OK)
                .setPath(requestLine.getPath());
    }
}
