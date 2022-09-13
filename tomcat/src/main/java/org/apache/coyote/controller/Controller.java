package org.apache.coyote.controller;

import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;

public interface Controller {
    void service(HttpRequest request, HttpResponse response);
}
