package com.techcourse.framework.handler;

import org.apache.coyote.http11.protocol.request.HttpRequest;
import org.apache.coyote.http11.protocol.response.HttpResponse;

public interface Controller {
    void service(HttpRequest request, HttpResponse response);
}
