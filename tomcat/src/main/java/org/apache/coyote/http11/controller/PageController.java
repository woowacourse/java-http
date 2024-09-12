package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;

public class PageController extends AbstractController {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        String method = request.getMethod();
        if (method.equalsIgnoreCase("GET")) {
            doGet(request, response);
            return;
        }
        throw new IllegalArgumentException("Method not supported: " + method);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        final URL resource = getClass().getClassLoader().getResource("static" + request.getPath());
        if (resource == null) {
            throw new IllegalArgumentException("Invalid static" + request.getPath());
        }
        final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        response.setPath(request.getPath());
        response.setFileType(request.getPath().split("\\.")[1]);
        response.setHttpStatusCode(HttpStatusCode.OK);
        response.setResponseBody(responseBody);
    }
}
