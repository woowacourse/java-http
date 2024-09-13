package org.apache.catalina.controller;

import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.RequestLine;
import org.apache.coyote.http11.RequestUri;

public class StaticController extends AbstractController {

    @Override
    protected void doGet(Http11Request request, Http11Response response) {
        RequestLine requestLine = request.getRequestLine();
        RequestUri requestUri = requestLine.getRequestUri();
        response.setStaticResponse(request, requestUri.getRequestUri(), 200);
    }
}
