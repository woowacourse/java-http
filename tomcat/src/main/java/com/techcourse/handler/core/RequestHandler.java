package com.techcourse.handler.core;

import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public interface RequestHandler {
    HttpResponse service(HttpRequest httpRequest) throws Exception;
}
