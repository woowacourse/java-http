package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.RequestLine;

public interface Controller {

    String handle(RequestLine requestLine);
}
