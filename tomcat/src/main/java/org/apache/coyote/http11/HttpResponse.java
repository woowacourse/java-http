package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private final String httpMethod;
    private final String statusCode;
    private final Map<String, String> headers;
    private String responseBody;

    private HttpResponse(String httpMethod, String statusCode, Map<String, String> headers, String responseBody) {
        this.httpMethod = httpMethod;
        this.statusCode = statusCode;
        this.headers = headers;
        this.responseBody = responseBody;
    }
    public static HttpResponse from(String httpMethod, String statusCode) {
        return new HttpResponse(httpMethod, statusCode, new HashMap<>(), "");
    }

    public HttpResponse addHeader(String key, String value){
        headers.put(key, value);
        return this;
    }

    public HttpResponse addResponseBody(String responseBody){
        this.responseBody = responseBody;
        return this;
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
