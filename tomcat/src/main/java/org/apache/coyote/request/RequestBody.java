package org.apache.coyote.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private final Map<String, String> body;

    public RequestBody() {
        this.body = new HashMap<>();
    }

    public RequestBody(BufferedReader bufferedReader, String bodyLength) throws IOException {
        this.body = mapBody(bufferedReader, bodyLength);
    }

    private Map<String, String> mapBody(BufferedReader bufferedReader, String bodyLength) throws IOException {
        Map<String, String> rawBody = new HashMap<>();

        int contentLength = Integer.parseInt(bodyLength);
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String bodyLine = new String(buffer);

        String[] bodyElements = bodyLine.split("&");
        for (int i = 0; i < bodyElements.length; i++) {
            String[] info = bodyElements[i].split("=");
            rawBody.put(info[0], info[1]);
        }
        return rawBody;
    }

    public String get(String key) {
        return body.get(key);
    }
}
