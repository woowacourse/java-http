package org.apache.coyote.http11.controller;

import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.Http11Response;

public class HomeController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, Http11Response response) {
        response.setBody("Hello world!".getBytes(StandardCharsets.UTF_8));
        response.ok("text/html");
    }
}
