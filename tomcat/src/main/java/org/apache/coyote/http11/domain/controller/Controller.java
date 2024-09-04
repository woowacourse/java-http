package org.apache.coyote.http11.domain.controller;

import org.apache.coyote.http11.domain.request.HttpRequest;
import org.apache.coyote.http11.domain.response.HttpResponse;

public interface Controller {
    HttpResponse service(HttpRequest request);
}
