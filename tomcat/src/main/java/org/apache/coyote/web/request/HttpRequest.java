package org.apache.coyote.web.request;

import static org.apache.coyote.web.session.SessionManager.JSESSIONID;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.support.HttpHeaders;
import org.apache.coyote.support.HttpMethod;

public class HttpRequest {

    private static final String DEFAULT_REQUEST_EXTENSION = "strings";
    private static final String ASSIGN_DELIMITER = "=";
    private static final String SET_COOKIE_DELIMITER = "; ";

    private final HttpRequestLine httpRequestLine;
    private final HttpHeaders httpHeaders;
    private final Map<String, String> parameters;

    public HttpRequest(final HttpRequestLine httpRequestLine,
                       final HttpHeaders httpHeaders,
                       final Map<String, String> parameters) {
        this.httpRequestLine = httpRequestLine;
        this.httpHeaders = httpHeaders;
        this.parameters = parameters;
    }

    public boolean isSameHttpMethod(final HttpMethod method) {
        return httpRequestLine.isSameMethod(method);
    }

    public String getParameter(final String name) {
        return parameters.get(name);
    }

    public Optional<String> getSession() {
        String cookie = httpHeaders.getCookie();
        return Arrays.stream(cookie.split(SET_COOKIE_DELIMITER))
                .filter(value -> value.contains(JSESSIONID))
                .map(session -> session.split(ASSIGN_DELIMITER)[1])
                .findFirst();
    }

    public String getRequestUrl() {
        return httpRequestLine.getRequestUrl();
    }

    public HttpRequestLine getHttpRequestLine() {
        return httpRequestLine;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }
}
