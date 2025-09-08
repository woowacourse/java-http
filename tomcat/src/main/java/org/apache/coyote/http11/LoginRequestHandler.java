package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class LoginRequestHandler implements HttpRequestHandler {

    @Override
    public boolean support(final HttpRequest httpRequest) {
        return httpRequest.getRequestMethod() == RequestMethod.GET &&
                httpRequest.getRequestUrl().startsWith("/login");
    }

    @Override
    public String response(final HttpRequest httpRequest) {
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        Path resourcePath = Path.of(resource.getPath());
        byte[] bytes = readAllBytes(resourcePath);

        return createSuccessResponse(bytes);
    }

    private byte[] readAllBytes(final Path resourcePath) {
        try {
            return Files.readAllBytes(resourcePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String createSuccessResponse(final byte[] bytes) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + bytes.length + " ",
                "",
                new String(bytes));
    }
}
