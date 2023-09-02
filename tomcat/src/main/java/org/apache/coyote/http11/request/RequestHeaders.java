package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestHeaders {

    private final Map<String, String> headers;

    public RequestHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeaders from(final BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();

        String line;
        while ((line = bufferedReader.readLine()) == null) {
            if (line.isEmpty()) {
                break;
            }
            final String[] splitedLine = line.replace(": ", " ").split(" ");
            headers.put(splitedLine[0], splitedLine[1]);
        }

        return new RequestHeaders(headers);
    }

    public String getValue(String key) {
        return headers.get(key);
    }
}
