package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import org.apache.coyote.Handler;
import org.apache.coyote.common.HttpContentType;
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

    private HttpContentType getContentType(String path) {
        if (path.endsWith(".html")) {
            return HttpContentType.TEXT_HTML;
        }
        if (path.endsWith(".css")) {
            return HttpContentType.TEXT_CSS;
        }
        if (path.endsWith(".js")) {
            return HttpContentType.APPLICATION_JAVASCRIPT;
        }
        if (path.endsWith(".svg")) {
            return HttpContentType.IMAGE_SVG;
        }
        return HttpContentType.TEXT_PLAIN;
    }
}
