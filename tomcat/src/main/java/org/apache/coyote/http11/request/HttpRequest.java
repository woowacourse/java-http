package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.HttpCookies;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private RequestBody requestBody;

    public HttpRequest(String requestLine, List<String> headers) {
        this.requestLine = RequestLine.from(requestLine);
        this.headers = parseHeaders(headers);
        this.requestBody = RequestBody.empty();
    }

    private HttpHeaders parseHeaders(List<String> headers) {
        Map<String, String> headersMap = headers.stream()
                .map(header -> header.split(": "))
                .collect(Collectors.toMap(header -> header[0], header -> header[1]));

        return new HttpHeaders(headersMap);
    }

    public int getContentLength() {
        if (!headers.containsKey(HttpHeaders.CONTENT_LENGTH)) {
            return 0;
        }
        return Integer.parseInt(headers.get(HttpHeaders.CONTENT_LENGTH));
    }

    public boolean sessionNotExists() {
        Map<String, String> headersMap = headers.getHeaders();
        String cookieString = headersMap.get(HttpHeaders.COOKIE);
        if (cookieString == null) {
            return true;
        }

        HttpCookies cookie = HttpCookies.from(cookieString);
        if (!cookie.contains(Cookie.JSESSIONID)) {
            return true;
        }

        return SessionManager.findSession(cookie.get(Cookie.JSESSIONID)) == null;
    }

    public Session getSession(boolean createIfNotExists) {
        boolean sessionNotExists = sessionNotExists();

        if (sessionNotExists && createIfNotExists) {
            Session session = new Session();
            SessionManager.add(session);
            return session;
        }

        if (sessionNotExists) {
            return null;
        }

        HttpCookies cookie = HttpCookies.from(headers.get(HttpHeaders.COOKIE));
        String sessionId = cookie.get(Cookie.JSESSIONID);
        return SessionManager.findSession(sessionId);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public boolean isSameMethod(HttpMethod method) {
        return requestLine.getMethod().equals(method);
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getExtension() {
        return requestLine.getExtension();
    }

    public Map<String, String> getQueryString() {
        return requestLine.getQueryString();
    }

    public Map<String, String> getParams() {
        return requestBody.getParams();
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = RequestBody.from(requestBody);
    }
}
