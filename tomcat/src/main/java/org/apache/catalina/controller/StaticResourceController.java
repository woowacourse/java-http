package org.apache.catalina.controller;

import org.apache.catalina.resource.StaticResource;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticResourceController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        StaticResource.from(request.getPath()).writeTo(response);
    }
}
