package org.apache.coyote.http11.controller;

import java.net.http.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Controller {
    void service(HttpRequest request, HttpResponse response) throws Exception;
}
