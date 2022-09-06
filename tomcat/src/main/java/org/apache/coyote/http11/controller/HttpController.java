package org.apache.coyote.http11.controller;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusLine;

public class HttpController implements Controller {

    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";
    private static final String DEFAULT_RESOURCE_PACKAGE = "static";

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        parseQuery(httpRequest);

        String responseBody = generateResponseBody(httpRequest);
        httpResponse.setStatusLine(new StatusLine(httpRequest.getProtocolVersion(), "200", "OK"));
        httpResponse.setResponseBody(responseBody);
    }

    private String generateResponseBody(final HttpRequest httpRequest) {
        try {
            Path filePath = new File(
                    getClass().getClassLoader().getResource(DEFAULT_RESOURCE_PACKAGE + httpRequest.getResource())
                            .getFile()
            ).toPath();
            return Files.readString(filePath);
        } catch (NullPointerException | IOException e) {
            return DEFAULT_RESPONSE_BODY;
        }
    }

    private void parseQuery(final HttpRequest httpRequest) {
        if (httpRequest.hasQueryString()) {
            Map<String, String> queryMap = httpRequest.getQueries();
            InMemoryUserRepository.findByAccountAndPassword(queryMap.get("account"), queryMap.get("password"));
        }
    }
}
