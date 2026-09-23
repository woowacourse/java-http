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

    public HttpResponse() {
    }

    public HttpResponse(String version, int statusCode, String reasonPhrase, String responseBody) {
        this.version = version;
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
        this.responseBody = responseBody;
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
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

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }
}
