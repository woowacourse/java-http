package com.techcourse.handler;

import com.techcourse.http.request.HttpRequest;
import com.techcourse.http.response.HttpResponse;

public interface RequestHandler {
    HttpResponse service(HttpRequest httpRequest) throws Exception;
}
