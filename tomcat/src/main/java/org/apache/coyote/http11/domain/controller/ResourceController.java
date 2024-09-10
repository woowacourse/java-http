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
    protected void doGet(HttpRequest request, HttpResponse response) {
        String staticFilePath = STATIC_PATH + request.getPath();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(staticFilePath);

        if (inputStream == null) {
            response.setStatus(HttpStatus.NOT_FOUND);
            return;
        }

        try {
            String responseBody = new String(inputStream.readAllBytes());
            response.setStatus(HttpStatus.OK);
            response.setContentType(ContentTypeResolver.getContentType(staticFilePath));
            response.setMessageBody(responseBody);

        } catch (IOException e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

