package com.techcourse.controller;

import org.apache.catalina.connector.HttpRequest;
import org.apache.catalina.connector.HttpResponse;
import org.apache.catalina.StaticResourceProvider;
import jakarta.servlet.http.AbstractController;

public class StaticResourceController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String path = request.getPath();
        response.setBody(StaticResourceProvider.getStaticResource(path))
                .setContentType(StaticResourceProvider.probeContentType(path));
    }
}
