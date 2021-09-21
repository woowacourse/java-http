package nextstep.jwp.http.request;

import nextstep.jwp.http.HttpCookie;
import nextstep.jwp.http.HttpSession;
import nextstep.jwp.http.HttpSessions;

public class HttpRequest {

    private final HttpMethod method;
    private final String uri;
    private final String protocol;
    private final HttpHeaders headers;
    private final String body;

    public HttpRequest(HttpMethod method, String uri, String protocol,
            HttpHeaders headers) {
        this(method, uri, protocol, headers, "");
    }

    public HttpRequest(HttpMethod method, String uri, String protocol,
            HttpHeaders headers, String body) {
        this.method = method;
        this.uri = uri;
        this.protocol = protocol;
        this.headers = headers;
        this.body = body;
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
        return headers.getCookie();
    }

    public HttpSession getSession() {
        return HttpSessions.getSession(getCookie().getCookies("JSESSIONID"));
    }

    public boolean hasSession() {
        return headers.getCookie().hasSession();
    }
}
