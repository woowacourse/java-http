package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;

public class HelloWorldController extends Controller {

    @Override
    public HttpResponse doGet(HttpRequest request) {
        return HttpResponse.builder()
                .statusCode(HttpStatusCode.OK)
                .responseBody("Hello world!");
    }
}
