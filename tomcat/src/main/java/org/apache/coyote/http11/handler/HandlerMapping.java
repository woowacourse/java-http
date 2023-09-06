package org.apache.coyote.http11.handler;

import java.io.File;
import org.apache.coyote.http11.ViewResolver;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;

public class HandlerMapping {

    private static final HttpHandlers handlers = new HttpHandlers();

    public HttpResponse extractHttpResponse(HttpRequest request) {
        ResponseEntity responseEntity = handlers.handle(request);
        File viewFile = ViewResolver.findViewFile(responseEntity.getPath());

        return new HttpResponse(responseEntity.getHttpStatus(), viewFile);
    }

}
