package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Http11Response {

    private final String statusCode;
    private final String contentType;
    private final String path;

    public Http11Response(String statusCode, String contentType, String path) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.path = path;
    }

    public String serializeResponse() {
        final String responseBody = serializeResponseBody(path);
        return String.join("\r\n",
                "HTTP/1.1 " + statusCode + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String serializeResponseBody(String resourcePath) {
        try {
            final URL resource = getClass().getClassLoader().getResource(resourcePath);
            if (resource != null) {
                final Path path = Paths.get(resource.toURI());
                return Files.readString(path);
            }
            return "Hello world!";
        } catch (IOException | URISyntaxException e) {
            throw new IllegalArgumentException("응답 파일이 존재하지 않습니다.");
        }
    }
}
