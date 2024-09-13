package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;

public class RootController extends AbstractController {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        response.statusCode(HttpStatusCode.OK)
                .responseBody("Hello world!");
    }
}
