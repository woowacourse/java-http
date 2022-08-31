package org.apache.coyote.http11;

import java.util.Map;

public class HttpResponse {
    private final String httpMethod;
    private final String statusCode;
    private final Map<String, String> headers;
    private final String responseBody;

    public HttpResponse(String httpMethod, String statusCode, Map<String, String> headers,
        String responseBody) {
        this.httpMethod = httpMethod;
        this.statusCode = statusCode;
        this.headers = headers;
        this.responseBody = responseBody;
    }

    public static HttpResponse from(String httpMethod, String statusCode, Map<String, String> headers,
        String responseBody) {
        return new HttpResponse(httpMethod, statusCode, headers, responseBody);
    }

    public static HttpResponse from(String httpMethod, String statusCode, Map<String, String> headers) {
        return new HttpResponse(httpMethod, statusCode, headers, "");
    }

    public byte[] getBytes() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(httpMethod + " " + statusCode + " ")
            .append(System.lineSeparator());

        for (Map.Entry<String, String> header : headers.entrySet()) {
            stringBuilder.append(header.getKey() + ": " + header.getValue() + " ")
                .append(System.lineSeparator());
        }

        stringBuilder.append(System.lineSeparator()).append(responseBody);

        return stringBuilder.toString().getBytes();
    }
}
