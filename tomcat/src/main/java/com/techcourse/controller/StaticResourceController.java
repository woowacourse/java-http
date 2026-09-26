package com.techcourse.controller;

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
        StaticResource staticResource = staticResourceLoader.load(request.getRequestLine().getPath());

        response.setHeader("Content-Type", List.of(staticResource.contentType()));
        response.setBody(staticResource.body());
    }
}
