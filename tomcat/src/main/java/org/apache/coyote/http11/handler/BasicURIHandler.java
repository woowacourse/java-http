package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestHandler;
import org.apache.coyote.http11.HttpResponse;

public class BasicURIHandler implements HttpRequestHandler {
    @Override
    public boolean support(final HttpRequest httpRequest) {
        return httpRequest.isMethodEqualTo("GET") && httpRequest.isUriEqualTo("/");
    }

    @Override
    public void handle(final HttpRequest httpRequest, final OutputStream outputStream) throws IOException {
        final var responseBody = "Hello world!";

        final HttpResponse httpResponse = new HttpResponse.Builder()
                .responseBody(responseBody)
                .build(outputStream);
        httpResponse.flush();
    }
}
