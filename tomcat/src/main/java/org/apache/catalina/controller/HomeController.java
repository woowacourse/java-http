package org.apache.catalina.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.message.HttpHeaders;
import org.apache.coyote.http11.message.StatusLine;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

public class HomeController extends AbstractController {

    private static final String DEFAULT_BODY = "Hello world!";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        final String path = request.getPath();

        if (path.equals("/") || path.equals("")) {
            renderDefaultPage(request, response);
        }
    }

    private void renderDefaultPage(final HttpRequest request, final HttpResponse response) {
        final HttpHeaders headers = new HttpHeaders();
        headers.addHeader("Content-Type", ContentType.HTML.getMimeType());
        headers.addHeader("Content-Length", String.valueOf(DEFAULT_BODY.getBytes(StandardCharsets.UTF_8).length));

        response.setHeaders(headers);
        response.setStatusLine(new StatusLine(request.getVersion(), StatusCode.OK));
        response.setBody(DEFAULT_BODY.getBytes(StandardCharsets.UTF_8));
    }
}
