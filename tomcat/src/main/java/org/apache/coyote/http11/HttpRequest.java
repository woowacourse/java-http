package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.coyote.http11.util.HttpParser;

public class HttpRequest {

    private final HttpHeaders headers;
    private final Map<String, HttpCookie> cookies = new HashMap<>();
    private HttpMethod httpMethod;
    private String uri;
    private String path;
    private Map<String, String> parameters = new HashMap<>();
    private String protocol;
    private Session session;
    private String body;

    public HttpRequest() {
        this.headers = new HttpHeaders();
    }

    public void addCookie(HttpCookie cookie) {
        this.cookies.put(cookie.getName(), cookie);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public HttpCookie getCookie(String name) {
        return cookies.get(name);
    }

    public Session getSession() {
        return this.session;
    }

    public void addHeaders(final Map<String, String> headers) {
        this.headers.putAll(headers);
        addCookies();
        addSession();
    }

    private void addCookies() {
        if (!this.headers.containsHeader(HttpHeaders.COOKIE)) {
            return;
        }
        List<HttpCookie> parsedCookies = HttpParser.parseCookies(
                this.headers.get(HttpHeaders.COOKIE));
        final Map<String, HttpCookie> cookieMap = parsedCookies.stream()
                .collect(Collectors.toMap(HttpCookie::getName, Function.identity()));
        this.cookies.putAll(cookieMap);
    }

    private void addSession() {
        if (!this.cookies.containsKey(HttpCookie.JSESSIONID)) {
            return;
        }
        final var sessionCookie = this.cookies.get(HttpCookie.JSESSIONID);
        this.session = new Session(sessionCookie.getValue());
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
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

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
