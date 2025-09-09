package org.apache.coyote.http11.handler;

import org.apache.coyote.HttpStatus;
import org.apache.coyote.http11.MimeType;
import org.apache.coyote.http11.response.HttpResponse;

import java.util.Map;

public class RootPathHandler extends HttpRequestHandler {

    @Override
    String getSupportedUrl() {
        return "/";
    }

    @Override
    protected HttpResponse handleGet(String request) {
        String response = "Hello world!";
        return new HttpResponse(
                HttpStatus.OK,
                response,
                MimeType.TEXT_HTML,
                Map.of()
        );
    }
}
