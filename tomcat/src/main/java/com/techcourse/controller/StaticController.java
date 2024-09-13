package com.techcourse.controller;

import static org.apache.coyote.http11.response.HttpResponseHeaderNames.CONTENT_LENGTH;
import static org.apache.coyote.http11.response.HttpResponseHeaderNames.CONTENT_TYPE;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.MimeTypes;

public class StaticController extends AbstractController {

    private static final String STATIC = "static";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        try {
            String path = STATIC + request.getPath();
            URL resource = getClass().getClassLoader().getResource(path);
            File file = new File(resource.getPath());
            var responseBody = Files.readString(file.toPath());
            String contentType = Files.probeContentType(file.toPath());
            response.addHeader(CONTENT_LENGTH.getHeaderName(), Long.toString(file.length()));
            response.addHeader(CONTENT_TYPE.getHeaderName(), MimeTypes.getMimeTypes(contentType));
            response.setResponseBody(responseBody);
        } catch (IOException e) {
            throw new IllegalArgumentException("");
        }
    }
}
