package org.apache.catalina.controller;

import org.apache.catalina.resource.Resource;
import org.apache.catalina.resource.ResourceReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

import java.util.Optional;

public class StaticResourceController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final Optional<Resource> readResource = ResourceReader.read(request.getPath());
        if (readResource.isEmpty()) {
            final Resource notFound = ResourceReader.read("/404.html").orElseThrow();
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setBody(notFound.contentType(), notFound.content());
            return;
        }
        final Resource resource = readResource.get();
        response.setBody(resource.contentType(), resource.content());
    }
}
