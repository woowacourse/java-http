package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.Response;

public interface Controller {

    Response handle(RequestLine requestLine);
}
