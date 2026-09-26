package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public abstract class AbstractController implements Controller {

    private static final String STATIC_TARGET_PATH = "static";
    private static final String NOT_FOUND_FILE_PATH = "/404.html";

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.isMatchedMethod("GET")) {
            doGet(request, response);
            return;
        }
        if (request.isMatchedMethod("POST")) {
            doPost(request, response);
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        doNotFound(request, response);
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        doNotFound(request, response);
    }

    protected void doNotFound(HttpRequest request, HttpResponse response) throws Exception {
        URL resource = getClass().getClassLoader().getResource(STATIC_TARGET_PATH + NOT_FOUND_FILE_PATH);
        if (resource == null) {
            response.writeNotFound(request.getVersion(), "", "text/plain", "404 NOT FOUND".getBytes(StandardCharsets.UTF_8));
            return;
        }
        byte[] body = Files.readAllBytes(new File(resource.getFile()).toPath());
        response.writeNotFound(request.getVersion(), "", getContentType(resource.getPath()), body);
    }

    protected void writeStaticResource(
            HttpRequest request, HttpResponse response, String resourcePath) throws Exception {
        String fullResourcePath = STATIC_TARGET_PATH + resourcePath;
        URL resource = getClass().getClassLoader().getResource(fullResourcePath);
        if (resource == null) {
            doNotFound(request, response);
            return;
        }
        byte[] body = Files.readAllBytes(new File(resource.getFile()).toPath());
        response.writeOk(request.getVersion(), "", getContentType(resource.getPath()), body);
    }

    protected String getContentType(String resource) {
        if (resource.endsWith(".html")) {
            return "text/html";
        }
        if (resource.endsWith(".css")) {
            return "text/css";
        }
        return "";
    }
}
