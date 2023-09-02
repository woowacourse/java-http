package org.apache.coyote.http11.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestHeader {
    private final Map<String, String> headers;

    private RequestHeader(Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeader from(BufferedReader httpRequestHeader) throws IOException {
        Map<String, String> headerValues = new HashMap<>();
        String line = httpRequestHeader.readLine();
        while (!line.equals("")) {
            String[] header = line.split(":");
            headerValues.put(header[0], header[1]);
            line = httpRequestHeader.readLine();
        }
        return new RequestHeader(headerValues);
    }
}
