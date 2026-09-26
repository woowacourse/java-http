package org.apache.catalina.controller;

import org.apache.catalina.StaticResource;
import org.apache.catalina.StaticResources;
import org.apache.coyote.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.util.Optional;

public class StaticResourceController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        Optional<StaticResource> resource = StaticResources.find(request.getPath());
        if (resource.isEmpty()) {
            response.setError(HttpStatus.NOT_FOUND);
            return;
        }
        response.setBody(resource.get().mimeType(), resource.get().content());
    }
}
