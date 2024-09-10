package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final Map<String, String> body;

    public HttpRequest(final RequestLine requestLine, final HttpHeaders headers, final Map<String, String> body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(final BufferedReader reader) throws IOException {
        final var requestLine = RequestLine.from(reader.readLine());
        final var httpHeaders = HttpHeaders.parse(reader);
        final var requestBody = parseRequestBody(httpHeaders, reader);
        return new HttpRequest(requestLine, httpHeaders, requestBody);
    }

    private static Map<String, String> parseRequestBody(final HttpHeaders headers, final BufferedReader bufferedReader)
            throws IOException {
        if (!headers.contains("Content-Length")) {
            return Map.of();
        }
        final int contentLength = Integer.parseInt(headers.get("Content-Length"));
        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        final var requestBody = new String(buffer);
        return generate(requestBody);
    }

    private static Map<String, String> generate(final String requestBody) {
        final var body = new HashMap<String, String>();
        final var params = requestBody.split("&");
        for (final var param : params) {
            body.put(param.split("=")[0].strip(), param.split("=")[1].strip());
        }
        return body;
    }

    public boolean hasMethod(final HttpMethod method) {
        return requestLine.isEqualHttpMethod(method);
    }

    public String getContentTypeToResponseText() {
        final var path = requestLine.getPath();
        final var contentType = ContentType.from(path);
        return contentType.toResponseText();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public Map<String, String> getBody() {
        return body;
    }
}
