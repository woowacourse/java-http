package org.apache.coyote.http11.handler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.Handler;
import org.apache.coyote.common.HttpHeaders;
import org.apache.coyote.common.HttpProtocol;
import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.common.HttpResponse;
import org.apache.coyote.common.HttpStatus;

public class StaticResourceHandler implements Handler {

    public static final StaticResourceHandler INSTANCE = new StaticResourceHandler();
    private static final String STATIC_PATH = "static";

    private StaticResourceHandler() {
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        String path = request.getPath();
        URL resource = resolveResource(path);
        if (resource == null) {
            return NotFoundHandler.INSTANCE.handle(request);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(getContentType(path));
        HttpResponse response = new HttpResponse(HttpProtocol.HTTP11, HttpStatus.OK, headers);
        String contentBody = getContentBody(resource);
        response.setContentBody(contentBody);
        return response;
    }

    private URL resolveResource(String path) {
        ClassLoader classLoader = getClass().getClassLoader();
        return classLoader.getResource(STATIC_PATH + path);
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

    private String getContentBody(URL resource) throws IOException {
        File file = new File(resource.getFile());
        return new String(Files.readAllBytes(file.toPath()));
    }
}
