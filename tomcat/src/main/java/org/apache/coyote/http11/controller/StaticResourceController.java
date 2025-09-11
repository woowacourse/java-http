package org.apache.coyote.http11.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;

import org.apache.coyote.http11.request_response.HttpRequest;
import org.apache.coyote.http11.request_response.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class StaticResourceController implements Controller {

    private static final String STATIC_RESOURCE_PATH = "static";

    private final Map<String, String> contentTypes = Map.of(
        "css", "text/css;charset=utf-8",
        "html", "text/html;charset=utf-8",
        "js", "application/javascript;charset=utf-8"
    );

    @Override
    public boolean supports(HttpRequest request) {
        if (!request.getRequestMethod().equals("GET")) {
            return false;
        }
        String requestUriPath = request.getUriPath();
        return requestUriPath.endsWith(".css") || requestUriPath.endsWith(".html") || requestUriPath.endsWith(".js");
    }

    @Override
    public HttpResponse service(HttpRequest request) throws Exception {
        String requestUriPath = request.getUriPath();
        int index = requestUriPath.lastIndexOf('.');
        String extension = requestUriPath.substring(index + 1);
        String responseBody = readStaticFile(requestUriPath);
            return HttpResponse.builder()
                .status(HttpStatus.OK)
                .body(responseBody)
                .contentType(contentTypes.get(extension))
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
