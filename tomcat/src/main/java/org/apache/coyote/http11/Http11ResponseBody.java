package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Http11ResponseBody {

    private final String body;

    public Http11ResponseBody(String body) {
        this.body = body;
    }

    public static Http11ResponseBody from(String requestUri) throws IOException {
        return new Http11ResponseBody(getResponseBody(requestUri));
    }

    private static String getResponseBody(String resourcePath) throws IOException {
        if (resourcePath.equals("/")) {
            return "Hello world!";
        }

        try (InputStream inputStream = Http11Response.class.getClassLoader().getResourceAsStream("static" + resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public int getContentLength() {
        return body.getBytes(StandardCharsets.UTF_8).length;
    }

    public String getBody() {
        return body;
    }
}
