package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HttpResponse {

    private final Map<String, String> headers = new LinkedHashMap<>();
    private int statusCode = 200;
    private String body;

    public HttpResponse() {
    }

    private String parseResponseLine() {
        return String.format("HTTP/1.1 %d OK ", statusCode);
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

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public String getResponse() {
        return String.join("\r\n",
                parseResponseLine(),
                parseHeaders(),
                body);
    }

    public String getBody() {
        return body;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
