package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;

public interface Controller {

    void service(RequestLine requestLine, HttpRequest request, HttpResponse response) throws Exception;
}
