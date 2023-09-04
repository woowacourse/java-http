package org.apache.coyote.handler.mapping;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class StaticFileMapping implements HandlerMapping {

    @Override
    public boolean supports(final String httpMethod, final String requestUri) {
        return "GET".equals(httpMethod) &&
                ("/".equals(requestUri) ||
                        "/index.html".equals(requestUri) ||
                        requestUri.endsWith(".js") ||
                        requestUri.endsWith(".css") ||
                        requestUri.endsWith(".ico")
                );
    }

    @Override
    public String handle(final String requestUri, final Map<String, String> headers, final String requestBody) throws IOException {
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
