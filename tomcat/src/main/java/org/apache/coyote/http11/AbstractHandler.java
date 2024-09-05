package org.apache.coyote.http11;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

abstract class AbstractHandler {

    protected abstract boolean canHandle(HttpRequest httpRequest);

    protected abstract String forward(HttpRequest httpRequest);

    public HttpResponse handle(HttpRequest httpRequest) {
        String acceptHeader = httpRequest.header()
                .get("Accept")
                .orElseThrow(IllegalArgumentException::new);

        String result = forward(httpRequest);

        String resourcePath = getClass().getClassLoader().getResource(result).getPath();
        String contentType = determineContentType(resourcePath, acceptHeader);

        return new HttpResponse(readStaticResource(resourcePath), contentType);
    }

    private String determineContentType(String resourcePath, String acceptHeader) {
        String encodedContentType = "%s;charset=utf-8";
        if (acceptHeader.startsWith("text/html") && resourcePath.endsWith(".html")) {
            return String.format(encodedContentType, "text/html");
        }

        if (acceptHeader.startsWith("text/css") && resourcePath.endsWith(".css")) {
            return String.format(encodedContentType, "text/css");
        }

        if (acceptHeader.startsWith("text/javascript") && resourcePath.endsWith(".js")) {
            return String.format(encodedContentType, "text/javascript");
        }

        throw new IllegalStateException();
    }

    private byte[] readStaticResource(String resourcePath) {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(resourcePath))) {
            return bufferedInputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
