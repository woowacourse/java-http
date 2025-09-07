package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class IndexHtmlRequestHandler implements HttpRequestHandler {
    @Override
    public boolean support(final RequestStartLine requestStartLine) {
        return requestStartLine.requestMethod() == RequestMethod.GET &&
                requestStartLine.requestUrl().equals("/index.html");
    }

    @Override
    public String response(final RequestStartLine requestStartLine) {
        URL resource = getClass().getClassLoader().getResource("static/index.html");
        Path resourcePath = Path.of(resource.getPath());

        byte[] bytes = readAllBytes(resourcePath);

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + bytes.length + " ",
                "",
                new String(bytes));
    }

    private byte[] readAllBytes(final Path resourcePath) {
        try {
            return Files.readAllBytes(resourcePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
