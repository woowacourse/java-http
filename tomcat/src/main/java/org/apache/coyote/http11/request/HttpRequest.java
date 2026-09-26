package org.apache.coyote.http11.request;

import jakarta.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import org.apache.coyote.http11.HttpBody;
import org.apache.coyote.http11.HttpVersion;

public final class HttpRequest {

    private final HttpMethod method;
    private final RequestTarget target;
    private final HttpVersion version;
    private final RequestHeaders headers;
    private final HttpBody body;
    private final QueryParameters bodyParameters;
    private final HttpCookie cookie;
    private HttpSession session;

    public HttpRequest(
            HttpMethod method,
            RequestTarget target,
            HttpVersion version,
            RequestHeaders headers,
            HttpBody body,
            QueryParameters bodyParameters,
            HttpCookie cookie
    ) {
        this.method = method;
        this.target = target;
        this.version = version;
        this.headers = headers;
        this.body = body;
        this.bodyParameters = bodyParameters;
        this.cookie = cookie;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public RequestTarget getTarget() {
        return target;
    }

    public String getPath() {
        return target.path();
    }

    public HttpVersion getVersion() {
        return version;
    }

    public boolean isMatched(HttpMethod method, String path) {
        return this.method == method && target.path().equals(path);
    }

    public boolean isGet() {
        return method == HttpMethod.GET;
    }

    public RequestHeaders getHeaders() {
        return headers;
    }

    public HttpCookie getCookie() {
        return cookie;
    }

    public HttpBody getBody() {
        return body;
    }

    public String getBodyAsString() {
        return body.asString(StandardCharsets.UTF_8);
    }

    public String getQueryParamValue(String name) {
        return getFirstValue(target.queryParameters().all(name));
    }

    public List<String> getQueryParamValues(String name) {
        return target.queryParameters().all(name);
    }

    public String getBodyParamValue(String name) {
        return getFirstValue(bodyParameters.all(name));
    }

    public List<String> getBodyParamValues(String name) {
        return bodyParameters.all(name);
    }

    public Optional<String> getSessionId() {
        return cookie.getValue("JSESSIONID");
    }

    public HttpSession getSession() {
        return session;
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }

    private String getFirstValue(List<String> values) {
        if (values.isEmpty()) {
            return null;
        }
        return values.getFirst();
    }
}
