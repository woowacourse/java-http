package org.apache.coyote.http11.controller;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticResourceController
        extends AbstractController {

    @Override
    protected void doGet(
            HttpRequest request,
            HttpResponse response
    ) throws IOException {
        String resourceName =
                removeLeadingSlash(request.path());

        response.fromResource(resourceName);
    }

    private String removeLeadingSlash(String path) {
        if (path.startsWith("/")) {
            return path.substring(1);
        }

        return path;
    }
}
