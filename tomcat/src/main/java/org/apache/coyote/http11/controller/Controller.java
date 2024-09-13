package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;

public interface Controller {
    void service(Http11Request request, Http11Response response) throws Exception;
}
