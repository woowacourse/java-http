package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class Http11RequestBody {

    private final String body;

    public Http11RequestBody(String body) {
        this.body = body;
    }

    public static Http11RequestBody of(BufferedReader bufferedReader, int contentLength) throws IOException {
        StringBuilder bodyBuffer = readRequestBody(bufferedReader, contentLength);

        String encodedBody = bodyBuffer.toString();
        String decodedBody = URLDecoder.decode(encodedBody, StandardCharsets.UTF_8);

        return new Http11RequestBody(decodedBody);
    }

    private static StringBuilder readRequestBody(BufferedReader bufferedReader, int contentLength) throws IOException {
        StringBuilder bodyBuffer = new StringBuilder();
        char[] buffer = new char[contentLength];
        int totalCharsRead = 0;

        while (totalCharsRead < contentLength) {
            int charsRead = bufferedReader.read(buffer, 0, Math.min(buffer.length, contentLength - totalCharsRead));
            if (charsRead == -1) {
                break;
            }
            bodyBuffer.append(buffer, 0, charsRead);
            totalCharsRead += charsRead;
        }
        return bodyBuffer;
    }

    public boolean hasBody() {
        return !body.isBlank();
    }

    public String getBody() {
        return body;
    }
}
