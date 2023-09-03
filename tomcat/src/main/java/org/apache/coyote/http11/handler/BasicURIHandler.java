package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestHandler;

public class BasicURIHandler implements HttpRequestHandler {
    @Override
    public boolean support(final HttpRequest httpRequest) {
        return httpRequest.isMethodEqualTo("GET") && httpRequest.isUriEqualTo("/");
    }

    @Override
    public void handle(final HttpRequest httpRequest, final OutputStream outputStream) throws IOException {
        final var responseBody = "Hello world!";

        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
