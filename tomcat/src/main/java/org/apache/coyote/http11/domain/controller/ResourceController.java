package org.apache.coyote.http11.domain.controller;

import java.io.IOException;
import java.io.InputStream;
import org.apache.coyote.http11.domain.request.HttpRequest;
import org.apache.coyote.http11.domain.response.ContentTypeResolver;
import org.apache.coyote.http11.domain.response.HttpResponse;
import org.apache.coyote.http11.domain.response.HttpStatus;

public class ResourceController extends AbstractController {

    private static final String STATIC_PATH = "static";

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        String staticFilePath = STATIC_PATH + request.getPath();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(staticFilePath);

        if (inputStream == null) {
            return HttpResponse.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            String responseBody = new String(inputStream.readAllBytes());
            return HttpResponse.status(HttpStatus.OK)
                    .contentType(ContentTypeResolver.getContentType(staticFilePath))
                    .body(responseBody)
                    .build();

        } catch (IOException e) {
            return HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}

