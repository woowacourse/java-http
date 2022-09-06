package org.apache.coyote.http11.controller;

import static org.apache.coyote.http11.http.StatusCode.OK;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusLine;

public class DefaultController extends AbstractController {

    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";
    private static final String DEFAULT_RESOURCE_PACKAGE = "static";

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        String responseBody = readFile(request, request.getResource());
        response.setResponseBody(responseBody);
        response.setStatusLine(new StatusLine(request.getProtocolVersion(), OK.getNumber(), "OK"));
    }

    @Override
    public boolean canHandle(HttpRequest request) {
        return false;
    }

    private String readFile(final HttpRequest httpRequest, final String fileLocation) {
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
