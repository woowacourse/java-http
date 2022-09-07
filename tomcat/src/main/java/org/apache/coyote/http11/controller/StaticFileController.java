package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class StaticFileController extends AbstractController {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (ContentType.isExistExtension(request.getPath())) {
            response.addStatusLine(HttpStatus.getStatusCodeAndMessage(200));
            response.addContentTypeHeader(ContentType.findContentType(request.getPath()));
            response.addBodyFromFile(request.getPath());
        }
    }
}
