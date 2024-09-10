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
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);

        String encodedBody = new String(buffer);
        String decodedBody = URLDecoder.decode(encodedBody, StandardCharsets.UTF_8);

        return new Http11RequestBody(decodedBody);
    }
    
    public boolean hasBody() {
        return !body.isBlank();
    }

    public String getBody() {
        return body;
    }
}
