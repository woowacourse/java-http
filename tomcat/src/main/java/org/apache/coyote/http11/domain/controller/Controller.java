package org.apache.coyote.http11.domain.controller;

import org.apache.coyote.http11.domain.request.HttpRequest;
import org.apache.coyote.http11.domain.response.HttpResponse;

public interface Controller {
    void service(HttpRequest request, HttpResponse response);
}
