package org.apache.catalina.resource;

import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

import java.io.FileNotFoundException;
import java.io.IOException;

public final class StaticResourceService {

    private final StaticResourceLoader resourceLoader = new StaticResourceLoader();

    public void serve(String path, HttpResponse response) throws IOException {
        try {
            response.setBody(resourceLoader.load(path));
            response.setStatus(HttpStatus.OK);
            response.setContentType(contentType(path));
        } catch (FileNotFoundException e) {
            response.sendError(HttpStatus.NOT_FOUND, "Not Found");
        }
    }

    private String contentType(String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }
        if (path.endsWith(".js")) {
            return "text/javascript";
        }
        if (path.endsWith(".svg")) {
            return "image/svg+xml";
        }
        return "text/html";
    }
}
