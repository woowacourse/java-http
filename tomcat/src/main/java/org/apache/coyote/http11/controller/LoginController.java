package org.apache.coyote.http11.controller;


import static org.apache.coyote.http11.http.StatusCode.FOUND;
import static org.apache.coyote.http11.http.StatusCode.OK;
import static org.apache.coyote.http11.http.StatusCode.UNAUTHORIZED;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusLine;

public class LoginController extends AbstractController {

    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";
    private static final String DEFAULT_RESOURCE_PACKAGE = "static";
    private static final String REQUEST_URI = "/login";

    @Override
    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        processRequest(httpRequest, httpResponse);
    }

    @Override
    public boolean canHandle(HttpRequest request) {
        REQUEST_URI.equals(request.getPath());
        return REQUEST_URI.equals(request.getResource());
    }

    private void processRequest(final HttpRequest httpRequest,
                                HttpResponse httpResponse) {
        try {
            if (!httpRequest.hasQueryString()) {
                String responseBody = readFile(httpRequest, "/login.html");
                httpResponse
                        .setStatusLine(new StatusLine(httpRequest.getProtocolVersion(), OK.getNumber(), "OK"));
                httpResponse.setResponseBody(responseBody);
                return;
            }

            Map<String, String> queries = httpRequest.getQueries();
            if (InMemoryUserRepository.login(queries.get("account"), queries.get("password"))) {
                String responseBody = readFile(httpRequest, "/index.html");
                httpResponse
                        .setStatusLine(new StatusLine(httpRequest.getProtocolVersion(), FOUND.getNumber(), "FOUND"));
                httpResponse.setResponseBody(responseBody);
            }
        } catch (Exception e) {
            String responseBody = readFile(httpRequest, "/401.html");
            httpResponse.setStatusLine(
                    new StatusLine(httpRequest.getProtocolVersion(), UNAUTHORIZED.getNumber(), "UNAUTHORIZED"));
            httpResponse.setResponseBody(responseBody);
        }
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
