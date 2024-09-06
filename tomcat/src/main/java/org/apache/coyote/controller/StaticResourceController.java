package org.apache.coyote.controller;

import org.apache.coyote.http11.message.common.FileExtension;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.util.ResourceReader;

public class StaticResourceController extends FrontController {

    private static final String STATIC_PREFIX = "static";
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String path = STATIC_PREFIX + request.getPath();
        String resource = ResourceReader.readResource(path);

        String extension = extractFileExtension(path);

        response.setContentType(FileExtension.getFileExtension(extension).getContentType());
        response.setHeader(CONTENT_LENGTH_HEADER, String.valueOf(resource.getBytes().length));
        response.setBody(resource);
    }

    private String extractFileExtension(String path) {
        int lastDotIndex = path.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return path.substring(lastDotIndex + 1);
    }
}
