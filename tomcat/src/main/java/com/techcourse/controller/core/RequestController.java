package com.techcourse.controller.core;

import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public interface RequestController {
    HttpResponse service(HttpRequest httpRequest) throws Exception;
}
