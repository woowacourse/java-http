package org.apache.controller;

import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.http11.request.Http11RequestStartLine;
import org.apache.coyote.http11.request.HttpMethod;

public class StaticResourceController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        String endpoint = request.getEndpoint();
        response.addStaticBody(endpoint);
    }

    @Override
    public boolean isMatch(Http11RequestStartLine startLine) {
        return startLine.getMethod() == HttpMethod.GET && startLine.getEndPoint().contains(".");
    }
}
