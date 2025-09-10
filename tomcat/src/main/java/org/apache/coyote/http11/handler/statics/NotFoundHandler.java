package org.apache.coyote.http11.handler.statics;

import java.io.IOException;
import org.apache.coyote.http11.handler.Handler;
import org.apache.coyote.http11.handler.statics.util.StaticResourceUtils;
import org.apache.coyote.http11.http.HttpStatus;
import org.apache.coyote.http11.request.dto.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class NotFoundHandler implements Handler {

    @Override
    public boolean canHandle(HttpRequest request) {
        return true;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        StaticResourceUtils.serve(response, "404.html", HttpStatus.NOT_FOUND);
    }
}
