package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.nio.charset.StandardCharsets;

public class RootController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.writeOk(request.getVersion(), "", "text/html", "Hello world!".getBytes(StandardCharsets.UTF_8));
    }
}
