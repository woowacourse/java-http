package org.apache.coyote.http11.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.exception.HttpFormatException;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final String body;

    public HttpRequest(RequestLine requestLine, HttpHeaders headers, String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest of(List<String> requestHead) {
        validateRequestHead(requestHead);
        RequestLine requestLine = createRequestLine(requestHead);
        HttpHeaders headers = createHeaders(requestHead);

        return new HttpRequest(requestLine, headers, "");
    }

    private static void validateRequestHead(List<String> requestLines) {
        if (requestLines.isEmpty()) {
            throw new HttpFormatException("올바르지 않은 HTTP 요청 형식입니다.");
        }
    }

    private static RequestLine createRequestLine(List<String> requestHead) {
        String firstLine = requestHead.getFirst();
        return RequestLine.of(firstLine);
    }

    private static HttpHeaders createHeaders(List<String> requestHead) {
        List<String> headers = new ArrayList<>(requestHead.subList(1, requestHead.size()));
        return HttpHeaders.of(headers);
    }

    public HttpRequest withBody(String body) {
        return new HttpRequest(requestLine, headers, body);
    }

    public int getBodyLength() {
        return headers.getAsInt(HttpHeader.CONTENT_LENGTH.getName())
                .orElse(0);
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public Queries getQueries() {
        return requestLine.getQueries();
    }

    public boolean isQueriesEmpty() {
        return getQueries().isEmpty();
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getBody() {
        return body;
    }

    public String getBodyParameter(String parameterName) {
        Queries queries = Queries.of(body);
        return queries.get(parameterName);
    }

    public String getQuery(String key) {
        return requestLine.getQuery(key);
    }

    public Set<String> getQueryKeys() {
        return requestLine.getQueryKeys();
    }

    public String getSessionId() {
        return headers.getSessionId();
    }

    public void addSession(String sessionId) {
        headers.addSession(sessionId);
    }
}
