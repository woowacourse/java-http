package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.HttpMethod;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpRequestHeader httpRequestHeader;
    private final RequestBody requestBody;


    public HttpRequest(RequestLine requestLine, HttpRequestHeader httpRequestHeader, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.httpRequestHeader = httpRequestHeader;
        this.requestBody = requestBody;
    }

    public HttpRequest(RequestLine requestLine, HttpRequestHeader httpRequestHeader) {
        this(requestLine, httpRequestHeader, null);
    }

    public static HttpRequest from(BufferedReader bufferedReader) {
        try {
            RequestLine requestLine = new RequestLine(bufferedReader.readLine());
            HttpRequestHeader httpHeader = HttpRequestHeader.readRequestHeader(bufferedReader);
            RequestBody body = initializeBody(bufferedReader, requestLine, httpHeader).orElseGet(() -> null);
            return new HttpRequest(requestLine, httpHeader, body);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Optional<RequestBody> initializeBody(
            BufferedReader reader,
            RequestLine requestLine,
            HttpRequestHeader httpHeader
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
        return httpRequestHeader.hasCookie();
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
        return httpRequestHeader.getCookie();
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
