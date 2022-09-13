package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticFileController extends AbstractController {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (ContentType.isExistExtension(request.getPath())) {
            response.createStaticFileResponse(request.getPath());
        }
    }
}
