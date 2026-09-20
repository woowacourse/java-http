package org.apache.coyote.http11;

import org.apache.coyote.HttpMethod;

import java.util.Map;
import java.util.Optional;

public class HttpRequest {
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String COOKIE_HEADER = "Cookie";

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final byte[] body;
    private final FormContents formContents;
    private final Cookies cookies;

    public HttpRequest(RequestLine requestLine, Map<String, String> headers) {
        this(requestLine, headers, null);
    }

    public HttpRequest(RequestLine requestLine, Map<String, String> headers, byte[] body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
        this.formContents = FormContents.of(headers.get(CONTENT_TYPE_HEADER), body);
        this.cookies = Cookies.from(headers.get(COOKIE_HEADER));
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public Optional<Cookie> getCookie(String name) {
        return cookies.find(name);
    }

    public Optional<String> getParameter(String key) {
        Optional<String> queryParameter = requestLine.getUri().findParameter(key);
        if (queryParameter.isPresent()) {
            return queryParameter;
        }
        return formContents.find(key);
    }

    public boolean isGet() {
        return requestLine.getMethod() == HttpMethod.GET;
    }

    public boolean isPost() {
        return requestLine.getMethod() == HttpMethod.POST;
    }
}
