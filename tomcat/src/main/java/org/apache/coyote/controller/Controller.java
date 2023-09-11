package org.apache.coyote.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.Http11Response;


public interface Controller {
    void service(HttpRequest request, Http11Response response) throws Exception;
}
