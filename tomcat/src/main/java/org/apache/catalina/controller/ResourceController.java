package org.apache.catalina.controller;

import java.io.IOException;
import org.apache.coyote.http11.FileType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class ResourceController implements Controller {
    public static final String HELLO_WORLD = "Hello world!";

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        final var url = request.getRequestPath();

        if ("/".equals(url)) {
            buildIndexUrlResponse(response);
            return;
        }
        buildResourceResponse(response, url);
    }

    private void buildIndexUrlResponse(HttpResponse response) {
        response.ok();
        response.setContentType(FileType.HTML);
        response.setContentOfPlainText(HELLO_WORLD);
    }

    private void buildResourceResponse(HttpResponse response, String url) throws IOException {
        response.ok();
        response.setContentType(FileType.parseFilenameExtension(url));
        response.setContentOfResources(url);
    }
}
