package com.techcourse.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.MimeTypes;

public class StaticController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        try {
            String path = "static" + request.getPath();
            URL resource = getClass().getClassLoader().getResource(path);
            File file = new File(resource.getPath());
            var responseBody = Files.readString(file.toPath());
            String contentType = Files.probeContentType(file.toPath());
            response.addHeader("Content-Length", Long.toString(file.length()));
            response.addHeader("Content-Type", MimeTypes.getMimeTypes(contentType));
            response.setResponseBody(responseBody);
        } catch (IOException e) {
            throw new IllegalArgumentException("");
        }
    }
}
