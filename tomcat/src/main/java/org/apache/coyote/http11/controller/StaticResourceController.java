package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class StaticResourceController implements Controller {

    private ResponseEntity generateResponseEntity(HttpRequest request) throws IOException {
        String requestURI = request.getHttpRequestStartLine().getPath();

        URL resource = getClass()
                .getClassLoader()
                .getResource("static" + requestURI);
        File file = new File(resource.getFile());
        String responseBody = new String(Files.readAllBytes(file.toPath()));
        return ResponseEntity
                .builder()
                .httpStatus(HttpStatus.OK)
                .contentType(ContentType.from(requestURI))
                .responseBody(HttpResponseBody.from(responseBody))
                .build();
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        response.modifyResponse(generateResponseEntity(request));
    }
}
