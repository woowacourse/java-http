package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.util.MimeTypeResolver;
import org.apache.coyote.util.ResourceUtil;

public class StaticResourceHandler implements RequestHandler {

    @Override
    public boolean canHandle(HttpRequest request) {
        if (!ResourceUtil.isStaticResourceExist(request.getPath(), this.getClass())) {
            return false;
        }

        return true;
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        String body = ResourceUtil.readStaticResource(request.getPath(), getClass());

        return ResponseEntity.ok(
                body,
                MimeTypeResolver.getContentTypeByExtension(request.getPath())
        );
    }
}
