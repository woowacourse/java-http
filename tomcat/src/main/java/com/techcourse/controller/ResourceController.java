package com.techcourse.controller;

import org.apache.coyote.file.ResourcesReader;
import org.apache.coyote.http11.path.Path;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class ResourceController extends AbstractController {
    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        try {
            response.setResource(ResourcesReader.read(Path.from(request.getPath())));
        } catch (final Exception e) {
            response.setResource(ResourcesReader.read(Path.from("404.html")));
        }
    }
}
