package com.techcourse.controller;

import com.techcourse.StaticResourceReader;
import java.io.IOException;
import java.net.URLConnection;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(ResourceController.class);

    private final StaticResourceReader staticResourceReader;

    public ResourceController() {
        this.staticResourceReader = new StaticResourceReader();
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse.Builder responseBuilder) {
        String contentType = URLConnection.guessContentTypeFromName(request.path());
        final byte[] responseBody;
        try {
            responseBody = staticResourceReader.read(request.path());
            if (responseBody == null) {
                responseBuilder.status(Status.NOT_FOUND)
                        .build();
            }
            responseBuilder.status(Status.OK)
                    .contentType(contentType)
                    .body(responseBody)
                    .build();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            responseBuilder.status(Status.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}
