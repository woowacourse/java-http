package org.apache.coyote.http11.request;

import java.io.IOException;
import java.util.Map;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.component.HttpHeaders;
import org.apache.coyote.http11.response.HttpResponse;

public class HttpRequest {

    private final HttpRequestLine httpRequestLine;
    private final Map<String, String> headers;
    private final RequestBody body;

    public HttpRequest(
            HttpRequestLine httpRequestLine,
            Map<String, String> headers,
            RequestBody body
    ) {
        this.httpRequestLine = httpRequestLine;
        this.headers = headers;
        this.body = body;
    }

    public HttpResponse<String> getHttpResponse() throws IOException {
        return httpRequestLine.getHttpResponse(body, this);
    }

    public Session getSession(boolean needSession) {
        if (headers.containsKey(HttpHeaders.COOKIE)) {
            String value = headers.get(HttpHeaders.COOKIE);
            HttpCookie cookie = HttpCookie.from(value);
            SessionManager sessionManager = SessionManager.getInstance();
            Session session = sessionManager.findSession(cookie.getJSessionId());
            if (session != null) {
                return session;
            }
        }
        return needSession ? new Session() : null;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
