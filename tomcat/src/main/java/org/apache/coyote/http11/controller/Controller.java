package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;

public interface Controller {

    String providableUrl();

    void service(HttpRequest request, HttpResponse response);
}
