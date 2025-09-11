package org.apache.coyote.http11.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.coyote.http11.request_response.HttpRequest;
import org.apache.coyote.http11.request_response.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class RegisterPageController implements Controller {

    private static final String STATIC_RESOURCE_PATH = "static";

    @Override
    public boolean supports(HttpRequest request) {
        return request.getRequestMethod().equals("GET") && request.getUriPath().equals("/register");
    }

    @Override
    public HttpResponse service(HttpRequest request) throws Exception {
        String responseBody = readStaticFile("/register.html");
        return HttpResponse.builder()
            .status(HttpStatus.OK)
            .contentType("text/html;charset=utf-8")
            .body(responseBody)
            .build();
    }

    private String readStaticFile(String filePath) throws IOException {
        String staticFilePath = STATIC_RESOURCE_PATH + filePath;
        URL resource = getClass().getClassLoader().getResource(staticFilePath);
        if (resource == null) {
            throw new IllegalArgumentException("리소스가 존재하지 않습니다. " + staticFilePath);
        }
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }
}
