package org.apache.coyote.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpHeader {

    private final Map<String, String> values;

    private HttpHeader(final Map<String, String> values) {
        this.values = values;
    }

    public static HttpHeader from(final BufferedReader bufferedReader) throws IOException {
        final Map<String, String> values = new HashMap<>();

        String header;
        while (!"".equals((header = bufferedReader.readLine()))) {
            final String[] splitHeader = header.split(": ");
            values.put(splitHeader[0], splitHeader[1]);
        }

        return new HttpHeader(values);
    }

    public boolean hasSessionId() {
        final String cookie = values.get("Cookie");
        if (cookie == null) {
            return false;
        }
        return cookie.contains("JSESSION");
    }

    public String getSessionId() {
        final String cookie = values.get("Cookie");
        return cookie.split("JSESSIONID=")[1];
    }

    public int getContentLength() {
        if (values.containsKey("Content-Length")) {
            return Integer.parseInt(values.get("Content-Length"));
        }
        return 0;
    }
}
