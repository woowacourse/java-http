package org.apache.coyote.http.response;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private static final String LINE_BREAK = "\r\n";
    private static final String SPACE = " ";
    private static final String HEADER_SEPARATOR = ": ";

    private final HttpVersion httpVersion;
    private StatusCode statusCode;
    private final Map<String, String> headers;
    private String responseBody;

    private HttpResponse(HttpVersion httpVersion, StatusCode statusCode, Map<String, String> headers, String responseBody) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
        this.headers = headers;
        this.responseBody = responseBody;
    }

    public HttpResponse() {
        this(HttpVersion.HTTP_1_1, null, new HashMap<>(), "");
    }

    public byte[] getBytes() {
        StringBuilder responseBuilder = new StringBuilder();

        appendStatusLine(responseBuilder, httpVersion, statusCode);
        appendHeaders(responseBuilder, headers);
        appendBody(responseBuilder, responseBody);

        return responseBuilder.toString().getBytes(StandardCharsets.UTF_8);
    }

    private void appendStatusLine(StringBuilder responseBuilder, HttpVersion httpVersion, StatusCode statusCode) {
        responseBuilder.append(httpVersion.getValue())
                .append(SPACE)
                .append(statusCode.getValue())
                .append(SPACE)
                .append(LINE_BREAK);
    }

    private void appendHeaders(StringBuilder responseBuilder, Map<String, String> headers) {
        for (Map.Entry<String, String> header : headers.entrySet()) {
            responseBuilder.append(header.getKey())
                    .append(HEADER_SEPARATOR)
                    .append(header.getValue())
                    .append(SPACE)
                    .append(LINE_BREAK);
        }
        responseBuilder.append(LINE_BREAK);
    }

    private void appendBody(StringBuilder responseBuilder, String responseBody) {
        if (responseBody != null && !responseBody.isEmpty()) {
            responseBuilder.append(responseBody);
        }
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public void setResponseBody(String responseBody){
        this.responseBody = responseBody;
    }

    public String getContentType(String resource) throws IOException {
        return Files.probeContentType(Path.of(resource));
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
