package org.apache.coyote.http11.controller;

import jakarta.servlet.http.HttpSession;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Controller {
    void service(HttpRequest request, HttpResponse response, HttpSession session) throws Exception;
}
