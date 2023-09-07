package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;
import static org.apache.coyote.http11.HttpMethod.POST;

public class HttpRequest {

    public static final String QUERY_PARAM_DELIMITER = "&";
    public static final String EMPTY = "";
    private final HttpMethod method;
    private final HttpPath path;
    private final HttpVersion version;
    private final Map<String, String> queryParameters;
    private final String messageBody;
    private final Headers headers;
    private final HttpCookie httpCookie;

    public static HttpRequestBuilder builder() {
        return new HttpRequestBuilder();
    }

    public HttpRequest(final HttpMethod method, final HttpPath path, final HttpVersion httpVersion,
                       final Map<String, String> queryParameters,
                       final Map<String, String> headers, final String messageBody, final Map<String, String> cookie) {
        this.method = method;
        this.path = path;
        this.version = httpVersion;
        this.queryParameters = ofNullable(queryParameters).orElse(Map.of());
        this.headers = ofNullable(headers).map(Headers::new).orElse(new Headers());
        this.messageBody = Optional.ofNullable(messageBody).orElse(EMPTY);
        this.httpCookie = new HttpCookie(cookie);
    }

    public boolean isPost() {
        return method == POST;
    }

    public HttpPath getPath() {
        return path;
    }

    public Map<String, String> getForm() {
        String[] keyValues = messageBody.split(QUERY_PARAM_DELIMITER);
        return Arrays.asList(keyValues).stream()
                .map(keyValue -> keyValue.split("="))
                .collect(toMap(keyValue -> keyValue[0], keyValue -> keyValue[1]));
    }

    public String getCookie(String key) {
        return httpCookie.getCookie(key);
    }

}

class HttpRequestBuilder {

    private HttpMethod method;
    private HttpPath path;
    private HttpVersion version;
    private Map<String, String> queryParameters;
    private String messageBody;
    private Map<String, String> headers;
    private Map<String, String> cookie;

    public HttpRequestBuilder method(HttpMethod method) {
        this.method = method;
        return this;
    }

    public HttpRequestBuilder path(HttpPath path) {
        this.path = path;
        return this;
    }

    public HttpRequestBuilder version(HttpVersion version) {
        this.version = version;
        return this;
    }

    public HttpRequestBuilder queryParameters(Map<String, String> queryParameters) {
        this.queryParameters = queryParameters;
        return this;
    }

    public HttpRequestBuilder messageBody(String messageBody) {
        this.messageBody = messageBody;
        return this;
    }

    public HttpRequestBuilder headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public HttpRequestBuilder cookie(Map<String, String> cookie) {
        this.cookie = cookie;
        return this;
    }

    public HttpRequest build() {
        return new HttpRequest(method, path, version, queryParameters, headers, messageBody, cookie);
    }

}
