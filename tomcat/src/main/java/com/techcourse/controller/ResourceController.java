package com.techcourse.controller;

import java.io.IOException;
import java.util.Set;
import org.apache.catalina.AbstractController;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class ResourceController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        response.sendStaticResource(request.getPath());
    }

    @Override
    protected Set<HttpMethod> allowedMethods() {
        return Set.of(HttpMethod.GET);
    }
}
