package com.techcourse.controller;

import java.util.Set;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;

public class StaticResourceController extends AbstractController {

    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of(
            ".html", ".css", ".js", ".ico"
    );

    @Override
    public boolean isProvidableUrl(final String path) {
        int lastDotIndex = path.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return false;
        }
        String extension = path.substring(lastDotIndex);
        return SUPPORTED_EXTENSIONS.contains(extension);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        final String path = request.getPath();

        if (path.contains("..")) {
            response.setNotFound();
            return;
        }

        final String resourcePath = path.substring(1);
        response.setOk(resourcePath);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        response.setInternalServerError();
    }
}
