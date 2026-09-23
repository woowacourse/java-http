package org.apache.coyote.http11.request;

import org.apache.coyote.HttpMethod;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.Cookies;
import org.apache.coyote.http11.FormContents;
import org.apache.coyote.http11.HttpHeaders;

import java.util.Optional;

public class HttpRequest {
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String COOKIE_HEADER = "Cookie";

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final byte[] body;
    private final FormContents formContents;
    private final Cookies cookies;

    public HttpRequest(RequestLine requestLine, HttpHeaders headers) {
        this(requestLine, headers, new byte[0]);
    }

    public HttpRequest(RequestLine requestLine, HttpHeaders headers, byte[] body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
        this.formContents = retrieveFormContents(headers, body);
        this.cookies = retrieveCookies(headers);
    }

    private static FormContents retrieveFormContents(HttpHeaders headers, byte[] body) {
        String contentTypeValue = headers.get(CONTENT_TYPE_HEADER)
                .orElse("");
        return FormContents.of(contentTypeValue, body);
    }

    private static Cookies retrieveCookies(HttpHeaders headers) {
        String cookiePairs = headers.get(COOKIE_HEADER)
                .orElse("");
        return Cookies.from(cookiePairs);
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

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public boolean isGet() {
        return requestLine.getMethod() == HttpMethod.GET;
    }

    public boolean isPost() {
        return requestLine.getMethod() == HttpMethod.POST;
    }
}
