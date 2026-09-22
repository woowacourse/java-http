package org.apache.coyote.http11.request.line;

import org.apache.coyote.http11.HttpVersion;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestLine {

    private static final Pattern REQUEST_LINE_PATTERN =
            Pattern.compile("^(?<method>[A-Z]+) (?<uri>\\S+) (?<version>HTTP/\\d\\.\\d)$");

    private HttpMethod httpMethod;
    private RequestUri requestUri;
    private HttpVersion httpVersion;

    public RequestLine(String requestLine) {
        Matcher matcher = REQUEST_LINE_PATTERN.matcher(requestLine);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("유효하지 않은 Request Line 입니다: " + requestLine);
        }
        this.httpMethod = HttpMethod.valueOf(matcher.group("method"));
        this.requestUri = new RequestUri(matcher.group("uri"));
        this.httpVersion = HttpVersion.from(matcher.group("version"));
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public RequestUri getRequestUri() {
        return requestUri;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

}
