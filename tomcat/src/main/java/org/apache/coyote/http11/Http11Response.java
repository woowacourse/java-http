package org.apache.coyote.http11;

import java.util.Map;
import java.util.Map.Entry;

public class Http11Response {

    private final Map<String, String> headers;
    private final String body;

    public Http11Response(Map<String, String> headers, String body) {
        this.headers = headers;
        this.body = body;
    }

    public String getResponse() {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                parseHeaders(),
                body);
    }

    private String parseHeaders() {
        // TODO: refactoring - join 으로 진행하고 싶음
        StringBuilder sb = new StringBuilder();
        for (Entry<String, String> header : headers.entrySet()) {
            String key = header.getKey();
            String value = header.getValue();
            sb.append(String.format("%s: %s ", key, value));
            sb.append("\r\n");
        }
        return sb.toString();
    }
}
