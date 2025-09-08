package org.apache.coyote.util;

import java.io.IOException;
import java.io.InputStream;

public class HttpResponse {

    private final String statusLine;
    private final String contentType;
    private final byte[] body;

    private HttpResponse(String statusLine, String contentType, byte[] body) {
        this.statusLine = statusLine;
        this.contentType = contentType;
        this.body = body;
    }

    public static HttpResponse of(String statusLine, String resourcePath) {
        String contentType = HttpContentTypeResolver.resolve(resourcePath);
        byte[] body = readStaticFile(resourcePath);
        return new HttpResponse(statusLine, contentType, body);
    }

    public static HttpResponse of(String statusLine, String contentType, byte[] body) {
        return new HttpResponse(statusLine, contentType, body);
    }

    private static byte[] readStaticFile(String path) {
        try (InputStream is = HttpResponse.class.getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                return "404 Not Found".getBytes();
            }
            return is.readAllBytes();
        } catch (IOException e) {
            return "500 Internal Server Error".getBytes();
        }
    }

    public String createHeader() {
        return String.join("\r\n",
                statusLine,
                "Content-Type: " + contentType,
                "Content-Length: " + body.length,
                "", ""
        );
    }

    public byte[] getBody() {
        return body;
    }
}
