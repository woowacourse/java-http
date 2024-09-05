package org.apache.coyote.http11;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

class StaticResourceHandler implements ResourceHandler {

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        URL resource = getClass().getClassLoader().getResource("static/" + httpRequest.getUri());

        return resource != null;
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        URI uri = httpRequest.getUri();
        String path = uri.getPath();
        String acceptHeader = httpRequest.header()
                .get("Accept")
                .orElseThrow(IllegalArgumentException::new);
        String resourcePath = getClass().getClassLoader().getResource("static/" + path).getPath();
        String contentType = determineContentType(resourcePath, acceptHeader);

        return new HttpResponse(readStaticResource(resourcePath), contentType);
    }

    private String determineContentType(String resourcePath, String acceptHeader) {
        String encodedContentType = "%s;charset=utf-8";
        if (acceptHeader.startsWith("text/html") && resourcePath.endsWith(".html")) {
            return  String.format(encodedContentType, "text/html");
        }

        if (acceptHeader.startsWith("text/css") && resourcePath.endsWith(".css")) {
            return  String.format(encodedContentType, "text/css");
        }

        if (acceptHeader.startsWith("text/javascript") && resourcePath.endsWith(".js")) {
            return  String.format(encodedContentType, "text/javascript");
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
