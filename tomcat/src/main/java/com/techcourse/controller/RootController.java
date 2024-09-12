package com.techcourse.controller;

import org.apache.catalina.connector.HttpRequest;
import org.apache.catalina.connector.HttpResponse;
import org.apache.catalina.servlet.AbstractController;
import org.apache.tomcat.util.http.ResourceURI;

public class RootController extends AbstractController {

    private static final ResourceURI ROOT_RESOURCE_URI = new ResourceURI("/index.html");

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.writeStaticResource(ROOT_RESOURCE_URI);
    }
}
