package org.apache.coyote.http11.controller;

import org.apache.coyote.common.HttpContentType;
import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.common.HttpResponse;
import org.apache.coyote.common.HttpStatus;
import org.apache.coyote.util.ResourceResolver;

public class StaticResourceController extends AbstractController {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        String path = request.getPath();
        response.setContentType(getContentType(path));
        response.setContentBody(ResourceResolver.resolve(path));
        response.setHttpStatus(HttpStatus.OK);
    }

    private HttpContentType getContentType(String path) {
        int index = path.lastIndexOf(".");
        if (index <= 0) {
            return HttpContentType.TEXT_PLAIN;
        }
        String extension = path.substring(index);
        return HttpContentType.from(extension);
    }
}
