package org.apache.coyote.http11.request;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.cookie.HttpCookie;

public final class HttpRequest {
    private final String method;
    private final String url;
    private final Map<String, String> headers;
    private final List<HttpCookie> cookies;
    private final String rawBody;
    private final Map<String, String> parameters;

    public HttpRequest(
            String method,
            String url,
            Map<String, String> headers,
            List<HttpCookie> cookies,
            Map<String, String> parameters,
            String rawBody
    ) {
        this.method = method;
        this.url = url;
        this.headers = Collections.unmodifiableMap(headers);
        this.cookies = Collections.unmodifiableList(cookies);
        this.parameters = Collections.unmodifiableMap(parameters);
        this.rawBody = rawBody;
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Optional<HttpCookie> getCookie(String name) {
        return cookies.stream()
                .filter(cookie -> cookie.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public Optional<String> getCookieValue(String name) {
        return getCookie(name).map(HttpCookie::getValue);
    }

    public Optional<String> getRawBody() {
        return Optional.ofNullable(rawBody);
    }

    public Optional<String> getParameter(String name) {
        return Optional.ofNullable(parameters.get(name));
    }
}