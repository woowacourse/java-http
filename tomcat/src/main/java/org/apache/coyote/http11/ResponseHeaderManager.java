package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseHeaderManager {

    private static final String delimiter = " ";
    private final String version = "HTTP/1.1";
    private String responseLine = String.join(delimiter,
            version,
            String.valueOf(HttpStatus.OK.getStatus()),
            HttpStatus.OK.name());
    private Map<String, String> headers = new LinkedHashMap<>();

    public ResponseHeaderManager() {
        headers.put("Content-Type", "text/html;charset=utf-8");
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getVersion() {
        return "HTTP/1.1";
    }

    public String buildHeader(int contentLength) {
        headers.put("Content-Length", String.valueOf(contentLength));

        StringBuilder sb = new StringBuilder();
        sb.append(responseLine).append("\r\n");
        for (Map.Entry<String, String> e : headers.entrySet()) {
            sb.append(e.getKey()).append(":").append(e.getValue()).append("\r\n");
        }
        sb.append("\r\n");
        return sb.toString();
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }
}
