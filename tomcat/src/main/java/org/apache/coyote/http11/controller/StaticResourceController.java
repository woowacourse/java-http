package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponseBody;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class StaticResourceController implements Controller {

    @Override
    public ResponseEntity service(HttpRequest request) throws IOException {
        String requestURI = request.getHttpRequestStartLine().getPath();

        URL resource = getClass()
                .getClassLoader()
                .getResource("static" + requestURI);
        File file = new File(resource.getFile());
        String responseBody = new String(Files.readAllBytes(file.toPath()));
        return ResponseEntity
                .builder()
                .httpStatus(HttpStatus.OK)
                .contentType(generateContentType(requestURI))
                .responseBody(HttpResponseBody.from(responseBody))
                .build();
    }

    private ContentType generateContentType(String requestURI) {
        if (requestURI.endsWith(".css")) {
            return ContentType.CSS;
        }
        return ContentType.HTML;
    }
}
