package org.apache.coyote.http.handler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.coyote.http.controller.Controller;
import org.apache.coyote.http.controller.Controllers;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.RequestLine;
import org.apache.coyote.http.response.HttpResponse;

public class RequestHandler {

    private static final String TEXT_HTML_CHARSET_UTF_8 = "text/html;charset=utf-8";

    private final Controllers controllers;

    public RequestHandler() {
        this.controllers = new Controllers();
    }

    public HttpResponse handle(final HttpRequest httpRequest) throws URISyntaxException, IOException {
        try {
            final RequestLine requestLine = httpRequest.getRequestLine();
            final String url = requestLine.getUrl();
            final Controller controller = controllers.get(url);

            if (controller == null) {
                final URL resource = getClass().getClassLoader().getResource("static" + url);
                validateNullResource(resource);
                final String responseBody = Files.readString(Paths.get(resource.toURI()));
                return HttpResponse.ok(responseBody, TEXT_HTML_CHARSET_UTF_8);
            }

            return controller.service(httpRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void validateNullResource(final URL resource) {
        if (resource == null) {
            throw new IllegalArgumentException("존재하지 않는 resource 입니다.");
        }
    }
}
