package org.apache.coyote.http11.controller;

import java.util.Map;

import org.apache.coyote.http11.request_response.HttpStatus;
import org.apache.coyote.http11.request_response.request.HttpRequest;
import org.apache.coyote.http11.request_response.response.HttpResponse;
import org.apache.coyote.http11.util.StaticFileReader;

public class StaticResourceController extends ServletController {

    private final Map<String, String> contentTypes = Map.of(
        "css", "text/css;charset=utf-8",
        "html", "text/html;charset=utf-8",
        "js", "application/javascript;charset=utf-8"
    );

    @Override
    public boolean supports(HttpRequest request) {
        String requestUriPath = request.getUriPath();
        return requestUriPath.endsWith(".css") || requestUriPath.endsWith(".html") || requestUriPath.endsWith(".js");
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        String requestUriPath = request.getUriPath();
        int index = requestUriPath.lastIndexOf('.');
        String extension = requestUriPath.substring(index + 1);
        String responseBody = new StaticFileReader().readStaticFile(requestUriPath);
        return HttpResponse.builder()
            .status(HttpStatus.OK)
            .body(responseBody)
            .contentType(contentTypes.get(extension))
            .build();
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        throw new IllegalArgumentException("지원하지 않는 http method 입니다");
    }
}
