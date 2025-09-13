package com.techcourse.controller;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.nio.charset.StandardCharsets;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.io.StaticResource;
import org.apache.coyote.message.HttpRequest;
import org.apache.coyote.message.HttpResponse;

public class NotFoundController extends AbstractController {

    @Override
    public HttpResponse service(final HttpRequest request) throws Exception {
        final var resource = new StaticResource("/404.html");
        final var body = resource.readAsString();

        return HttpResponse.notFound()
                .contentType("text/html;charset=utf-8")
                .contentLength(body.getBytes(UTF_8).length)
                .body(body)
                .build();
    }
}
