package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.request.body.RequestBody;
import org.apache.coyote.http11.request.header.RequestHeaders;
import org.apache.coyote.http11.request.startLine.HttpMethod;
import org.apache.coyote.http11.request.startLine.RequestLine;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final RequestBody requestBody;

    private HttpRequest(RequestLine requestLine, RequestHeaders requestHeaders, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest parse(BufferedReader bufferedReader) throws IOException {
        RequestLine requestLine = new RequestLine(bufferedReader.readLine());
        RequestHeaders requestHeaders = createRequestHeaders(bufferedReader);
        RequestBody requestBody = createRequestBody(bufferedReader, requestHeaders);

        return new HttpRequest(requestLine, requestHeaders, requestBody);
    }

    private static RequestHeaders createRequestHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();

        String headerLine = bufferedReader.readLine();
        while (!headerLine.isBlank()) {
            String[] header = headerLine.split(": ");
            headers.put(header[0], header[1]);
            headerLine = bufferedReader.readLine();
        }

        return new RequestHeaders(headers);
    }

    private static RequestBody createRequestBody(BufferedReader bufferedReader, RequestHeaders requestHeaders)
            throws IOException {
        StringBuilder body = new StringBuilder();
        int contentLength = requestHeaders.get("Content-Length")
                .map(Integer::parseInt)
                .orElse(0);

        if (contentLength > 0) {
            char[] bodyChars = new char[contentLength];
            bufferedReader.read(bodyChars, 0, contentLength);
            body.append(bodyChars);
        }

        Map<String, String> params = new HashMap<>();

        String[] paramPairs = body.toString().split("&");
        for (String pair : paramPairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(
                        URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8)
                );
            }
        }

        return new RequestBody(params);
    }

    public boolean matchesMethod(HttpMethod method) {
        return requestLine.matchesMethod(method);
    }

    public Optional<String> getHeader(String header) {
        return requestHeaders.get(header);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public String getUri() {
        return requestLine.getUri();
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }
}
