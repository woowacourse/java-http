package org.apache.coyote.http11.controller;

import java.util.Map;

import org.apache.coyote.http11.request_response.HttpMethod;
import org.apache.coyote.http11.request_response.HttpStatus;
import org.apache.coyote.http11.util.StaticFileReader;
import org.apache.coyote.http11.request_response.request.HttpRequest;
import org.apache.coyote.http11.request_response.response.HttpResponse;

public class StaticResourceController implements Controller {

    private final Map<String, String> contentTypes = Map.of(
        "css", "text/css;charset=utf-8",
        "html", "text/html;charset=utf-8",
        "js", "application/javascript;charset=utf-8"
    );

    @Override
    public boolean supports(HttpRequest request) {
        if (!request.getRequestMethod().equals(HttpMethod.GET)) {
            return false;
        }
        String requestUriPath = request.getUriPath();
        return requestUriPath.endsWith(".css") || requestUriPath.endsWith(".html") || requestUriPath.endsWith(".js");
    }

    @Override
    public HttpResponse service(HttpRequest request) {
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
}
