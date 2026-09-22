package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {
    private final String version;
    private final int statusCode;
    private final String reasonPhrase;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private final String responseBody;

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

}
