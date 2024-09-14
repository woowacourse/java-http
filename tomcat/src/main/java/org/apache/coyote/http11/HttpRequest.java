package org.apache.coyote.http11;

import static org.apache.catalina.Globals.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HttpRequest {

    private static final int MAX_CONTENT_LENGTH = 1 * 1024 * 1024; // body 최대 사이즈 1MB

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final HttpRequestBody requestBody;

    public HttpRequest(RequestLine requestLine, HttpHeaders headers, HttpRequestBody requestBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        RequestLine requestLine = getRequestLine(reader);
        HttpHeaders headers = getHttpHeaders(reader);
        HttpRequestBody body = getHttpRequestBody(requestLine, headers, reader);

        return new HttpRequest(
            requestLine,
            headers,
            body
        );
    }

    private static RequestLine getRequestLine(BufferedReader reader) throws IOException {
        String firstLine = reader.readLine();
        return RequestLine.from(firstLine);
    }

    private static HttpHeaders getHttpHeaders(BufferedReader reader) throws IOException {
        String line;
        List<String> headerLines = new ArrayList<>();
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            headerLines.add(line);
        }
        return HttpHeaders.from(headerLines);
    }

    private static HttpRequestBody getHttpRequestBody(RequestLine requestLine, HttpHeaders headers,
        BufferedReader reader) throws IOException {
        if (requestLine.getMethod() == HttpMethod.POST) {
            int contentLength = headers.getContentLength();
            char[] buffer = new char[MAX_CONTENT_LENGTH];
            int bytesRead = reader.read(buffer, 0, contentLength);
            if (bytesRead > 0) {
                String requestBody = new String(buffer, 0, bytesRead);
                String decodedBodyLine = URLDecoder.decode(requestBody, StandardCharsets.UTF_8);
                return HttpRequestBody.from(decodedBodyLine);
            }
        }
        return HttpRequestBody.empty();
    }

    public Optional<String> getSessionIdFromCookie() {
        return headers.getCookie(SESSION_COOKIE_NAME);
    }

    public boolean isHttp11VersionRequest() {
        return requestLine.isHttp11Version();
    }

    public HttpRequestBody getRequestBody() {
        if (requestLine.getMethod() == HttpMethod.GET) {
            throw new IllegalArgumentException("GET method don't have payload");
        }
        return requestBody;
    }

    public boolean hasMethod(HttpMethod method) {
        return this.requestLine.getMethod() == method;
    }

    public String getUri() {
        return requestLine.getPath();
    }
}
