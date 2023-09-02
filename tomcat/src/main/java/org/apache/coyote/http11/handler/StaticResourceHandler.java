package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import org.apache.coyote.Handler;
import org.apache.coyote.common.HttpHeaders;
import org.apache.coyote.common.HttpProtocol;
import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.common.HttpResponse;
import org.apache.coyote.common.HttpStatus;
import org.apache.coyote.util.ResourceResolver;

public class StaticResourceHandler implements Handler {

    public static final StaticResourceHandler INSTANCE = new StaticResourceHandler();

    private StaticResourceHandler() {
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        String path = request.getPath();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(getContentType(path));
        HttpResponse response = new HttpResponse(HttpProtocol.HTTP11, HttpStatus.OK, headers);
        try {
            String contentBody = ResourceResolver.resolve(path);
            response.setContentBody(contentBody);
            return response;
        } catch (NoSuchFileException e) {
            return NotFoundHandler.INSTANCE.handle(request);
        }
    }

    private String getContentType(String path) {
        if (path.endsWith(".html")) {
            return "text/html;charset=utf-8";
        }
        if (path.endsWith(".css")) {
            return "text/css";
        }
        if (path.endsWith(".js")) {
            return "text/javascript";
        }
        if (path.endsWith(".svg")) {
            return "image/svg+xml";
        }
        return "*/*";
    }
}
