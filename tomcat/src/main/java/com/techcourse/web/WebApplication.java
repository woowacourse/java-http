package com.techcourse.web;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface WebApplication {
    HttpResponse service(HttpRequest request);
}
