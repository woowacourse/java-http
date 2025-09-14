package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request_response.request.HttpRequest;
import org.apache.coyote.http11.request_response.response.HttpResponse;

public interface Controller {

    boolean supports(HttpRequest request);

    HttpResponse service(HttpRequest request) throws Exception;
}
