package org.apache.catalina.controller;

import org.apache.catalina.resource.StaticResourceLoader;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

import java.io.FileNotFoundException;
import java.io.IOException;

final class StaticResourceService {

    private final StaticResourceLoader resourceLoader = new StaticResourceLoader();

    void serve(String path, HttpResponse response) throws IOException {
        try {
            response.setBody(resourceLoader.load(path));
            response.setStatus(HttpStatus.OK);
            response.setContentType(contentType(path));
        } catch (FileNotFoundException e) {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setContentType("text/plain");
            response.setBody("Not Found");
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
