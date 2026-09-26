package org.apache.coyote.http11.controller;

import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RootController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.setBody("Hello world!".getBytes(StandardCharsets.UTF_8), "text/html");
    }
}
