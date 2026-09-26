package com.techcourse.controller;

import java.util.Optional;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.ResourceResolver;

public class RootController extends AbstractController {

    private static final String NOT_FOUND_PAGE = "/404.html";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String path = request.getPath();
        Optional<String> resource = ResourceResolver.resolve(path);
        if (resource.isPresent()) {
            response.ok(ResourceResolver.resolveContentType(path), resource.get());
            return;
        }
        response.notFound(ResourceResolver.resolveContentType(NOT_FOUND_PAGE),
                ResourceResolver.resolve(NOT_FOUND_PAGE).orElse("404 Not Found"));
    }
}
