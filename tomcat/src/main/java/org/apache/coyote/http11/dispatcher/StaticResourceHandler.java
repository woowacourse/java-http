package org.apache.coyote.http11.dispatcher;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.MimeTypeResolver;
import org.apache.coyote.http11.response.ResponseEntity;
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
    public void handle(HttpRequest request, HttpResponse httpResponse) {
        String body = ResourceUtil.readStaticResource(request.getPath(), getClass());

        httpResponse.setHttpResponse(
                ResponseEntity.ok(
                        body,
                        MimeTypeResolver.getContentTypeByExtension(request.getPath())
                )
        );
    }
}
