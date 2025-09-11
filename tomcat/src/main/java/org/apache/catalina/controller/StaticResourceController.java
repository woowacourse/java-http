package org.apache.catalina.controller;

import java.io.IOException;
import java.io.InputStream;
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
        byte[] bodyBytes = readStaticResource(path);
        sendStaticResource(response, path, bodyBytes);
    }

    private byte[] readStaticResource(String path) throws IOException {
        String resourcePath = "static" + path;
        try (InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (fileInputStream == null) {
                return null;
            }
            return fileInputStream.readAllBytes();
        }
    }

    private void sendStaticResource(HttpResponse response, String path, byte[] bodyBytes) throws IOException {
        if (bodyBytes == null) {
            response.send(HttpStatus.NOT_FOUND);
            return;
        }
        String contentType = determineContentType(path);
        response.send(HttpStatus.OK, contentType, bodyBytes);
    }

    private String determineContentType(String path) {
        return ContentType.fromPath(path);
    }
}
