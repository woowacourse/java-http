package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private final String protocolVersion;
    private String status;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private String body = "";

    public HttpResponse(String protocolVersion) {
        this.protocolVersion = protocolVersion;
        this.status = "200 OK";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public String response() {
        headers.put("Content-Length", body.getBytes().length + "");

        String response = String.join("\r\n",
                protocolVersion.trim() + " " + status.trim() + " ",
                responseHeaders()
        );

        response = String.join("\r\n", response, body);

        return response;
    }

    private String responseHeaders() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> header : headers.entrySet()) {
            builder.append(header.getKey()).append(": ");
            builder.append(header.getValue()).append(" ");
            builder.append("\r\n");
        }
        return builder.toString();
    }
}
