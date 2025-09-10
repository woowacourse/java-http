package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.util.StaticResourceResolver;

public class StaticFileController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        var requestPath = request.getPath();
        if (requestPath.isBlank() || "/".equals(requestPath)) {
            requestPath = "/index.html";
        }

        final var responseBody = StaticResourceResolver.read(requestPath);
        if (responseBody == null) {
            response.sendNotFound();
            return;
        }

        final var mimeType = ContentType.from(requestPath).getMimeType();
        response.sendOk(mimeType, responseBody);
    }
}
