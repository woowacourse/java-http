package org.apache.controller;

import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.http11.request.Http11RequestStartLine;
import org.apache.coyote.http11.request.HttpMethod;

public class HomeController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        response.addBody("Hello world!");
        response.addContentType("text/html");
    }

    @Override
    public boolean isMatch(Http11RequestStartLine startLine) {
        return startLine.getMethod() == HttpMethod.GET && startLine.getEndPoint().equals("/");
    }
}
