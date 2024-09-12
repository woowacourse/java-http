package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequestHeaders;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpRequestHeaders httpRequestHeaders;
    private final RequestBody requestBody;


    public HttpRequest(RequestLine requestLine, HttpRequestHeaders httpRequestHeaders, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.httpRequestHeaders = httpRequestHeaders;
        this.requestBody = requestBody;
    }

    public HttpRequest(RequestLine requestLine, HttpRequestHeaders httpRequestHeaders) {
        this(requestLine, httpRequestHeaders, null);
    }

    public static HttpRequest from(BufferedReader bufferedReader) {
        try {
            RequestLine requestLine = new RequestLine(bufferedReader.readLine());
            HttpRequestHeaders httpHeader = HttpRequestHeaders.readRequestHeader(bufferedReader);
            RequestBody body = initializeBody(bufferedReader, requestLine, httpHeader).orElseGet(() -> null);
            return new HttpRequest(requestLine, httpHeader, body);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Optional<RequestBody> initializeBody(
            BufferedReader reader,
            RequestLine requestLine,
            HttpRequestHeaders httpHeader
    ) {
        if (!(requestLine.getMethod() == HttpMethod.POST)) {
            return Optional.empty();
        }
        return Optional.of(RequestBody.read(reader, httpHeader.contentLength()));
    }

    public boolean isMethod(HttpMethod method) {
        return method == requestLine.getMethod();
    }

    public boolean isQueryStringRequest() {
        return requestLine.isQueryStringRequest();
    }

    public boolean hasCookie() {
        return httpRequestHeaders.hasCookie();
    }

    public boolean hasRequestBody() {
        return requestBody != null;
    }

    public Map<String, String> getQueryParameters() {
        return requestLine.getParameters();
    }

    public Cookie getCookie() {
        if (!hasCookie()) {
            throw new IllegalStateException("쿠키가 존재하지 않습니다.");
        }
        return httpRequestHeaders.getCookie();
    }

    public String getRequestUri() {
        return requestLine.getRequestURI();
    }

    public RequestBody getRequestBody() {
        if (!hasRequestBody()) {
            throw new IllegalStateException("requestBody is null");
        }
        return requestBody;
    }
}
