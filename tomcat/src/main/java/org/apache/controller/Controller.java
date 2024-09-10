package org.apache.controller;

import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.http11.request.Http11RequestStartLine;

public interface Controller {

    void service(HttpRequest request, HttpResponse response) throws Exception;

    boolean isMatch(Http11RequestStartLine startLine);
}
