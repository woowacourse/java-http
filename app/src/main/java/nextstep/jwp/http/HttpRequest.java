package nextstep.jwp.http;

import java.util.Map;

public class HttpRequest {

    private final HttpMethod method;
    private final String uri;
    private final String protocol;
    private final Map<String, String> headers;
    private final String body;
    private final HttpCookie httpCookie;

    public HttpRequest(HttpMethod method, String uri, String protocol,
            Map<String, String> headers, String body, HttpCookie httpCookie) {
        this.method = method;
        this.uri = uri;
        this.protocol = protocol;
        this.headers = headers;
        this.body = body;
        this.httpCookie = httpCookie;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getBody() {
        return body;
    }

    public HttpCookie getCookie() {
        return httpCookie;
    }

    public HttpSession getSession() {
        return HttpSessions.getSession(getCookie().getCookies("JSESSIONID"));
    }
}
