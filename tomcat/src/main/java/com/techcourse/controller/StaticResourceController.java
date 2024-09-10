package com.techcourse.controller;

import com.techcourse.http.HttpRequest;
import com.techcourse.http.HttpResponse;
import org.apache.catalina.StaticResourceProvider;
import org.apache.coyote.http11.AbstractController;

public class StaticResourceController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String path = request.getPath();
        response.setBody(StaticResourceProvider.getStaticResource(path))
                .setContentType(StaticResourceProvider.probeContentType(path));
    }
}
