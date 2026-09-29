package org.apache.coyote.http11.controller;

import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RootController extends AbstractController {

    @Override
    protected void doGet(
            HttpRequest request,
            HttpResponse response
    ) {
        response.addHeader(
                "Content-Type",
                "text/html;charset=utf-8"
        );

        response.addBody(
                "Hello world!".getBytes(StandardCharsets.UTF_8)
        );
    }
}
