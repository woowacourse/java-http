package com.techcourse.controller;

import org.apache.catalina.connector.HttpRequest;
import org.apache.catalina.connector.HttpResponse;
import org.apache.catalina.servlet.Controller;
import org.apache.tomcat.util.http.ResourceURI;

public class RootController implements Controller {
    private static final ResourceURI ROOT_RESOURCE_URI = new ResourceURI("/index.html");

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        switch (request.getHttpMethod()) {
            case GET -> doGet(request, response);
        }
    }

    private void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.writeStaticResource(ROOT_RESOURCE_URI);
    }
}
