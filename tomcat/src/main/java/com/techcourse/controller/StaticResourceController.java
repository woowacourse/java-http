package com.techcourse.controller;

import com.techcourse.exception.ResourceNotFoundException;
import com.techcourse.http.HttpRequest;
import com.techcourse.http.HttpResponse;
import com.techcourse.resource.StaticResource;
import com.techcourse.resource.StaticResourceLoader;
import java.util.List;

public class StaticResourceController extends AbstractController {

    private final StaticResourceLoader staticResourceLoader;

    public StaticResourceController(StaticResourceLoader staticResourceLoader) {
        this.staticResourceLoader = staticResourceLoader;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        try {
            StaticResource staticResource = staticResourceLoader.load(request.getRequestLine().getPath());

            response.setHeader("Content-Type", List.of(staticResource.contentType()));
            response.setBody(staticResource.body());
        } catch (ResourceNotFoundException e) {
            StaticResource notFoundPage = staticResourceLoader.load("/404.html");

            response.setStatus("404", "Not Found");
            response.setHeader("Content-Type", List.of(notFoundPage.contentType()));
            response.setBody(notFoundPage.body());
        }
    }
}
