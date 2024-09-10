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
        response.setStatus(HttpStatus.OK);
        response.setContentType(ContentTypeResolver.getContentType(staticFilePath));
        setMessageBody(request, response);
    }

    private void setMessageBody(HttpRequest request, HttpResponse response) {
        String staticFilePath = STATIC_PATH + request.getPath();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(staticFilePath)) {
            validateFileExist(inputStream);
            response.setMessageBody(new String(inputStream.readAllBytes()));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void validateFileExist(InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found");
        }
    }
}

