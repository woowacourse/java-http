package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpResponseHeader {

    private final String startLine;
    private final Map<String, String> headers;

    public HttpResponseHeader(String startLine, Map<String, String> headers) {
        this.startLine = startLine;
        this.headers = headers;
    }

    public void setHeaders(String key, String value) {
        headers.put(key, value);
    }

    public static HttpResponseHeader createDefault(String requestUrl, int contentLength) {
        Map<String, String> headers = new HashMap<>();

        String contentType = "text/html;charset=utf-8";
        if (requestUrl.endsWith(".css")) {
            contentType = "text/css";
        } else if (requestUrl.endsWith(".js")) {
            contentType = "application/javascript;charset=utf-8";
        }

        headers.put("Content-Type", contentType);
        headers.put("Content-Length", String.valueOf(contentLength));

        return new HttpResponseHeader("HTTP/1.1 200 OK", headers);
    }

    public String getResponseHeaderString() {
        StringBuilder sb = new StringBuilder();
        sb.append(startLine).append("\r\n");

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }
        sb.append("\r\n");

        return sb.toString();
    }
}
