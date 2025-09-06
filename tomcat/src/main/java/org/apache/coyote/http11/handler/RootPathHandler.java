package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.MimeType;

import java.nio.charset.StandardCharsets;

public class RootPathHandler extends HttpRequestHandler {

    @Override
    String getSupportedUrl() {
        return "/";
    }

    @Override
    protected String handleGet(String request) {
        String response = "Hello world!";
        return String.join(
                "\r\n",
                "HTTP/1.1 200 OK",
                "Content-Type: " + MimeType.TEXT_HTML.getMimeType(),
                "Content-Length: " + response.getBytes(StandardCharsets.UTF_8).length,
                "",
                response
        );
    }
}
