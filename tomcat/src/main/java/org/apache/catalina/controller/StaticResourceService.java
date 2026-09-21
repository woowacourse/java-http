package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.StaticResourceLoader;

import java.io.FileNotFoundException;
import java.io.IOException;

final class StaticResourceService {

    private final StaticResourceLoader resourceLoader = new StaticResourceLoader();

    void serve(String path, HttpResponse response) throws IOException {
        try {
            response.setBody(resourceLoader.load(path));
            response.setStatus(HttpStatus.OK);
            response.setContentType(path.endsWith(".css") ? "text/css" : "text/html");
        } catch (FileNotFoundException e) {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setContentType("text/plain");
            response.setBody("Not Found");
        }
    }
}
