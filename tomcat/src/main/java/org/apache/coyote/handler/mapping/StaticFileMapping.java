package org.apache.coyote.handler.mapping;

import org.apache.coyote.http.HttpRequest;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class StaticFileMapping implements HandlerMapping {

    @Override
    public boolean supports(final HttpRequest httpRequest) {
        return httpRequest.isGetRequest() &&
                ("/".equals(httpRequest.getRequestUri().getRequestUri()) ||
                        "/index.html".equals(httpRequest.getRequestUri().getRequestUri()) ||
                        httpRequest.getRequestUri().getRequestUri().endsWith(".js") ||
                        httpRequest.getRequestUri().getRequestUri().endsWith(".css") ||
                        httpRequest.getRequestUri().getRequestUri().endsWith(".ico")
                );
    }

    @Override
    public String handle(final HttpRequest httpRequest) throws IOException {
        final String requestUri = httpRequest.getRequestUri().getRequestUri();
        if ("/".equals(requestUri)) {
            final var responseBody = "Hello world!";

            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
        }

        if ("/index.html".equals(requestUri)) {
            final String filePath = "static" + requestUri;
            final URL fileUrl = getClass().getClassLoader().getResource(filePath);
            final Path path = new File(fileUrl.getPath()).toPath();
            final String responseBody = new String(Files.readAllBytes(path));

            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
        }

        if (requestUri.endsWith(".js")) {
            final String filePath = "static" + requestUri;
            final URL fileUrl = getClass().getClassLoader().getResource(filePath);
            final Path path = new File(fileUrl.getPath()).toPath();
            final String responseBody = new String(Files.readAllBytes(path));

            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/javascript ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
        }

        if (requestUri.endsWith(".css")) {
            final String filePath = "static" + requestUri;
            final URL fileUrl = getClass().getClassLoader().getResource(filePath);
            final Path path = new File(fileUrl.getPath()).toPath();
            final String responseBody = new String(Files.readAllBytes(path));

            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/css;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
        }

        if (requestUri.endsWith(".ico")) {
            final String filePath = "static" + requestUri;
            final URL fileUrl = getClass().getClassLoader().getResource(filePath);
            final Path path = new File(fileUrl.getPath()).toPath();
            final String responseBody = new String(Files.readAllBytes(path));

            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: image/x-icon ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
        }

        return null;
    }
}
