package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.FileHandler;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestHandler;
import org.apache.coyote.http11.HttpResponse;

import java.io.IOException;
import java.io.OutputStream;

public class IndexPageHandler implements HttpRequestHandler {
    @Override
    public boolean support(final HttpRequest httpRequest) {
        return httpRequest.isMethodEqualTo("GET") && httpRequest.isUriEqualTo("/index.html");
    }

    @Override
    public void handle(final HttpRequest httpRequest, final OutputStream outputStream) throws IOException {
        final HttpResponse httpResponse = new HttpResponse.Builder()
                .responseBody(new FileHandler().readFromResourcePath("static/index.html"))
                .build(outputStream);

        httpResponse.flush();
    }
}
