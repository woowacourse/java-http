package nextstep.jwp.web.network.request;

import nextstep.jwp.web.network.HttpSession;
import nextstep.jwp.web.network.HttpSessions;
import nextstep.jwp.web.network.URI;
import nextstep.jwp.web.network.response.HttpHeaders;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.UUID;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final HttpBody body;

    public HttpRequest(InputStream inputStream) {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        this.requestLine = RequestLine.of(bufferedReader);
        this.headers = HttpHeaders.of(bufferedReader);
        this.body = HttpBody.of(bufferedReader, contentLength());
    }

    private int contentLength() {
        final String contentLengthAsString = this.headers.get("Content-Length");
        if (contentLengthAsString == null) {
            return 0;
        }
        return Integer.parseInt(this.headers.get("Content-Length"));
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
        if (sessionId == null) {
            final UUID id = UUID.randomUUID();
            final HttpSession session = new HttpSession(id);
            HttpSessions.setSession(id);
            return session;
        }
        return HttpSessions.getSession(sessionId);
    }

    public Map<String, String> getBodyAsMap() {
        return body.asMap();
    }
}
