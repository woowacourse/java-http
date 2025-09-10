package com.techcourse.controller;

import org.apache.coyote.http11.httpRequest.HttpRequest;
import org.apache.coyote.http11.httpResponse.HttpResponse;

public interface Controller {

    HttpResponse service(final HttpRequest httpRequest) throws Exception;
}
