package org.apache.coyote.http11.request;

import jakarta.servlet.http.HttpSession;
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

    public String getPath() {
        return target.path();
    }

    public HttpVersion getVersion() {
        return version;
    }

    public HttpBody getBody() {
        return body;
    }

    public String getBodyParamValue(String name) {
        return getFirstValue(bodyParameters.all(name));
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
