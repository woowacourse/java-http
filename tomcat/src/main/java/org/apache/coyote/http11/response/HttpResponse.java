package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {
    private final String protocolVersion;
    private final StatusCode statusCode;
    private final Map<String, String> responseHeader;
    private final String responseBody;

    private HttpResponse(String protocolVersion, StatusCode statusCode, String responseBody) {
        this.protocolVersion = protocolVersion;
        this.statusCode = statusCode;
        this.responseHeader = new LinkedHashMap<>();
        this.responseBody = responseBody;
    }

    public static HttpResponse of(String protocolVersion, StatusCode statusCode) {
        return new HttpResponse(protocolVersion, statusCode, "");
    }

    public static HttpResponse of(String protocolVersion, StatusCode statusCode, String responseBody) {
        return new HttpResponse(protocolVersion, statusCode, responseBody);
    }

    public HttpResponse addHeader(String key, String value) {
        responseHeader.put(key, value);
        return this;
    }

    public byte[] getBytes() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(protocolVersion + " " + statusCode.getMessage() + " ")
            .append(System.lineSeparator());

        for (Map.Entry<String, String> header : responseHeader.entrySet()) {
            stringBuilder.append(header.getKey() + ": " + header.getValue() + " ")
                .append(System.lineSeparator());
        }

        stringBuilder.append(System.lineSeparator()).append(responseBody);

        return stringBuilder.toString().getBytes();
    }
}
