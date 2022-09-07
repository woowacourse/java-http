package org.apache.controller;

import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public interface Controller {

    HttpResponse service(final HttpRequest request) throws Exception;
}
