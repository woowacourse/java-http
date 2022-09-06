package org.apache.coyote.http11.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";
    private static final String DEFAULT_RESOURCE_PACKAGE = "static";

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (request.getMethod().equalsIgnoreCase("GET")) {
            doGet(request, response);
        }

        if (request.getMethod().equalsIgnoreCase("POST")) {
            doPost(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
    }

    protected String readFile(final HttpRequest httpRequest, final String fileLocation) {
        try {
            Path filePath = new File(
                    getClass().getClassLoader().getResource(DEFAULT_RESOURCE_PACKAGE + fileLocation)
                            .getFile()
            ).toPath();
            return Files.readString(filePath);
        } catch (NullPointerException | IOException e) {
            return DEFAULT_RESPONSE_BODY;
        }
    }
}
