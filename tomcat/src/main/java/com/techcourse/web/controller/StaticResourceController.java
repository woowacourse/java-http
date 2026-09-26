package com.techcourse.web.controller;

import java.util.Optional;
import org.apache.coyote.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.ResourceLoader;

public class StaticResourceController extends AbstractController {

    private static final String DEFAULT_REQUEST = "/";
    private static final String INDEX_PAGE = "/index.html";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String path = request.getPath();
        if (path.equals(DEFAULT_REQUEST)) {
            path = INDEX_PAGE;
        }

        Optional<String> body = ResourceLoader.read(path);
        if (body.isEmpty()) {
            renderNotFound(response);
            return;
        }
        response.ok(ResourceLoader.contentTypeOf(path), body.get());
    }
}
