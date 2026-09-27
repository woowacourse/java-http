package org.apache.coyote.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class StaticResourceController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        response.setStatus(405, "Method Not Allowed");
        response.setBody("");
        response.addHeader("Content-Length", "0");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String path = request.getPath();

        int statusCode = 200;
        String statusMessage = "OK";
        String responseBody = "Hello world!";
        String contentType = getContentType(path);

        if (!path.equals("/")) {
            String resourcePath = "static" + path;
            byte[] fileBytes = readResource(resourcePath);

            if (fileBytes == null) {
                fileBytes = readResource("static/404.html");

                if (fileBytes == null) {
                    throw new IllegalArgumentException("404.html 리소스를 찾을 수 없습니다.");
                }

                statusCode = 404;
                statusMessage = "Not Found";
                contentType = "text/html;charset=utf-8";
            }

            responseBody = new String(fileBytes, StandardCharsets.UTF_8);
        }

        int contentLength = responseBody.getBytes(StandardCharsets.UTF_8).length;

        response.setStatus(statusCode, statusMessage);
        response.setBody(responseBody);
        response.addHeader("Content-Type", contentType);
        response.addHeader("Content-Length", String.valueOf(contentLength));
    }

    private byte[] readResource(String resourcePath) throws IOException, URISyntaxException {
        URL resourceUrl = getClass().getClassLoader().getResource(resourcePath);
        if (resourceUrl == null) {
            return null;
        }
        URI resourceUri = resourceUrl.toURI();
        Path path = Paths.get(resourceUri);

        return Files.readAllBytes(path);
    }

    private String getContentType(String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }
        return "text/html;charset=utf-8";
    }
}
