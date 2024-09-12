package com.techcourse.controller.model;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.requestLine.RequestLine;
import org.apache.coyote.request.requestLine.RequestMethod;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.util.HttpStatus;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        RequestLine requestLine = httpRequest.getRequestLine();

        if (requestLine.isSameMethod(RequestMethod.GET)) {
            doGet(httpRequest, httpResponse);
            return;
        }
        if (requestLine.isSameMethod(RequestMethod.POST)) {
            doPost(httpRequest, httpResponse);
            return;
        }
        httpResponse.sendError(HttpStatus.NOT_FOUND);
    }

    protected abstract void doPost(HttpRequest httpRequest, HttpResponse httpResponse);

    protected abstract void doGet(HttpRequest httpRequest, HttpResponse httpResponse);
}
