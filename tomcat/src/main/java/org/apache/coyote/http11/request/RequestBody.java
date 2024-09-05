package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;

public class RequestBody {

    private final String requestBody;

    public RequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public static RequestBody read(BufferedReader reader, int contentLength) {
        try {
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            return new RequestBody(new String(buffer));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
