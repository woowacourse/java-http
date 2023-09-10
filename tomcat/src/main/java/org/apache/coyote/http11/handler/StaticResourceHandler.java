package org.apache.coyote.http11.handler;

import org.apache.coyote.AbstractHandler;
import org.apache.coyote.common.HttpContentType;
import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.common.HttpResponse;
import org.apache.coyote.common.HttpStatus;
import org.apache.coyote.util.ResourceResolver;

public class StaticResourceHandler extends AbstractHandler {

    @Override
    public void handle(HttpRequest request, HttpResponse response) {
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
