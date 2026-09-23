package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {
    private final Map<String, String> headers = new LinkedHashMap<>();
    private String version;
    private int statusCode;
    private String reasonPhrase;
    private String responseBody;
    
    public HttpResponse(String version, int statusCode, String reasonPhrase, String responseBody) {
        this.version = version;
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
        this.responseBody = responseBody;
    }

    public byte[] toBytes() {
        StringBuilder response = new StringBuilder();
        response.append(version)
                .append(" ")
                .append(statusCode)
                .append(" ")
                .append(reasonPhrase)
                .append(" \r\n");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            response.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append("\r\n");
        }
        response.append("\r\n");
        response.append(responseBody);
        return response.toString().getBytes(StandardCharsets.UTF_8);
    }

    public void ok(String responseBody, String contentType) {
        this.statusCode = 200;
        this.reasonPhrase = "OK";
        this.responseBody = responseBody;
        headers.put("Content-Type", contentType);
        headers.put("Content-Length",
                String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
    }

    public void redirect(String location, String sessionCookie) {
        this.statusCode = 302;
        this.reasonPhrase = "Found";
        this.responseBody = "";
        headers.put("Location", location);
        headers.put("Content-Length", "0");
        if (!sessionCookie.isEmpty()) {
            headers.put("Set-Cookie", sessionCookie);
        }
    }
}
