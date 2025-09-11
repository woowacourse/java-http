package com.techcourse.controller;

import java.io.IOException;
import java.io.InputStream;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticResourceController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(StaticResourceController.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String path = request.getPath();
        loadStaticResource(response, path);
    }

    private void loadStaticResource(HttpResponse response, String path) throws IOException {
        String resourcePath = "static" + path;
        try (InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (fileInputStream == null) {
                response.send(HttpStatus.NOT_FOUND);
                return;
            }
            String contentType = determineContentType(path);
            byte[] bodyBytes = fileInputStream.readAllBytes();
            response.send(HttpStatus.OK, contentType, bodyBytes);
        }
    }

    private String determineContentType(String path) {
        return ContentType.fromPath(path);
    }
}
