package nextstep.jwp.web.network.request;

import nextstep.jwp.web.network.HttpSession;
import nextstep.jwp.web.network.HttpSessions;
import nextstep.jwp.web.network.URI;
import nextstep.jwp.web.network.response.HttpHeaders;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final HttpBody body;

    public HttpRequest(InputStream inputStream) {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        this.requestLine = RequestLine.of(bufferedReader);
        this.headers = HttpHeaders.of(bufferedReader);
        this.body = HttpBody.of(bufferedReader, this.headers);
    }

    public boolean isGet() {
        return this.requestLine.isGet();
    }

    public boolean isPost() {
        return this.requestLine.isPost();
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public URI getURI() {
        return requestLine.getURI();
    }

    public String getPath() {
        return getURI().getPath();
    }

    public Cookies getCookies() {
        return headers.getCookies();
    }

    public HttpSession getSession() {
        final Cookies cookies = getCookies();
        final String sessionId = cookies.get("JSESSIONID");
        if (HttpSessions.doesNotContain(sessionId)) {
            final HttpSession session = createSession(sessionId);
            HttpSessions.setSession(session);
            return session;
        }
        return HttpSessions.getSession(sessionId);
    }

    private HttpSession createSession(String sessionId) {
        if (sessionId == null) {
            return new HttpSession(UUID.randomUUID());
        }
        return new HttpSession(sessionId);
    }

    public String getAttribute(String key) {
        return body.getAttribute(key);
    }
}
