package org.apache.coyote.http11;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.coyote.http11.util.HttpParser;

public class HttpRequest {

    private final String protocol;
    private final HttpHeaders headers;
    private final Map<String, HttpCookie> cookies;
    private final HttpMethod httpMethod;
    private final String uri;
    private final String path;
    private final Map<String, String> parameters;
    private final Session session;
    private final String body;

    private HttpRequest(final String protocol,
            final HttpHeaders headers,
            final HttpMethod httpMethod,
            final String uri, final String path, final Map<String, String> parameters,
            final String body) {
        this.protocol = protocol;
        this.headers = headers;
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.path = path;
        this.parameters = parameters;
        this.body = body;
        this.cookies = getCookiesFromHeader();
        this.session = getSessionFromHeader();
    }

    private Map<String, HttpCookie> getCookiesFromHeader() {
        if (!this.headers.containsHeader(HttpHeaders.COOKIE)) {
            return Collections.emptyMap();
        }
        List<HttpCookie> parsedCookies = HttpParser.parseCookies(
                this.headers.get(HttpHeaders.COOKIE));
        return parsedCookies.stream()
                .collect(Collectors.toMap(HttpCookie::getName, Function.identity()));
    }

    private Session getSessionFromHeader() {
        if (!this.cookies.containsKey(HttpCookie.JSESSIONID)) {
            return null;
        }
        final var sessionCookie = this.cookies.get(HttpCookie.JSESSIONID);
        return new Session(sessionCookie.getValue());
    }

    public String getPath() {
        return path;
    }

    public HttpCookie getCookie(String name) {
        return cookies.get(name);
    }

    public Session getSession() {
        return this.session;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getHeader(String headerName) {
        return this.headers.get(headerName);
    }

    public boolean containsHeader(String headerName) {
        return this.headers.containsHeader(headerName);
    }

    public void addParameters(Map<String, String> parameters) {
        this.parameters.putAll(parameters);
    }

    public String getUri() {
        return uri;
    }

    public String getBody() {
        return body;
    }

    public static class HttpRequestBuilder {

        private String protocol;
        private HttpHeaders headers;
        private HttpMethod httpMethod;
        private String uri;
        private String path;
        private Map<String, String> parameters;
        private String body;

        public static HttpRequestBuilder builder() {
            return new HttpRequestBuilder();
        }

        public HttpRequestBuilder protocol(final String protocol) {
            this.protocol = protocol;
            return this;
        }

        public HttpRequestBuilder httpMethod(final HttpMethod httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public HttpRequestBuilder headers(final HttpHeaders headers) {
            this.headers = headers;
            return this;
        }

        public HttpRequestBuilder uri(final String uri) {
            this.uri = uri;
            return this;
        }

        public HttpRequestBuilder path(final String path) {
            this.path = path;
            return this;
        }

        public HttpRequestBuilder parameters(final Map<String, String> parameters) {
            this.parameters = parameters;
            return this;
        }

        public HttpRequestBuilder body(final String body) {
            this.body = body;
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(
                    protocol,
                    headers,
                    httpMethod,
                    uri,
                    path,
                    parameters,
                    body
            );
        }
    }
}
