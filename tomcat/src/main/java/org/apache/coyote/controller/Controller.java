package org.apache.coyote.controller;

import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;

public interface Controller {

    void service(HttpRequest request, HttpResponse response) throws Exception;

}
