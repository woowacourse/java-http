package nextstep.jwp.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import nextstep.jwp.http.common.Body;
import nextstep.jwp.http.request.requestline.HttpMethod;
import nextstep.jwp.http.request.requestline.RequestLine;
import nextstep.jwp.http.session.HttpCookie;
import nextstep.jwp.http.session.HttpSession;
import nextstep.jwp.http.session.HttpSessions;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders header;
    private final HttpCookie cookie;
    private final HttpSession session;
    private final Body body;

    private HttpRequest(RequestLine requestLine, HttpHeaders headers,
        Body body) {
        this.requestLine = requestLine;
        this.header = headers;
        this.cookie = headers.getCookie();
        this.session = loadSession();
        this.body = body;
    }

    public static HttpRequest parse(BufferedReader bufferedReader) throws IOException {
        RequestLine requestLine = RequestLine.parse(bufferedReader.readLine());
        HttpHeaders headers = HttpHeaders.parse(bufferedReader);
        Body body = Body.parse(bufferedReader, headers);

        return new HttpRequest(requestLine, headers, body);
    }

    private HttpSession loadSession() {
        if (this.cookie.containSession()) {
            return HttpSessions.getSession(this.cookie.getSessionId());
        }
        return HttpSessions.createSession();
    }

    public boolean isGet() {
        return requestLine.getMethod().equals(HttpMethod.GET);
    }

    public boolean isPost() {
        return requestLine.getMethod().equals(HttpMethod.POST);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public HttpHeaders getHeader() {
        return header;
    }

    public Body getBody() {
        return body;
    }

    public HttpSession getSession() {
        return session;
    }
}
