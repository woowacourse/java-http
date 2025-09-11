package org.apache.coyote.http.controller;

import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public class StaticFileController extends AbstractController {

    private final String filePath;
    private final String contentType;

    public StaticFileController(String filePath, String contentType) {
        this.filePath = filePath;
        this.contentType = contentType;
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) throws Exception {
        return handleStaticFile(filePath, contentType);
    }
}
