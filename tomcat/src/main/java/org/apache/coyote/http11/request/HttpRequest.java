package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import org.apache.coyote.http11.cookie.HttpCookies;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders headers;
    private final RequestBody body;

    private HttpRequest(RequestLine requestLine, RequestHeaders headers, RequestBody body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest of(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        RequestLine requestLine = new RequestLine(reader.readLine());
        RequestHeaders requestHeaders = getRequestHeaders(reader);

        if (requestLine.isPost()) {
            RequestBody body = parseRequestBody(reader, requestHeaders.getContentLength());
            return new HttpRequest(requestLine, requestHeaders, body);
        }

        return new HttpRequest(requestLine, requestHeaders, null);
    }

    private static RequestHeaders getRequestHeaders(BufferedReader reader) {
        List<String> headers = reader.lines()
                .takeWhile(s -> !s.isBlank())
                .toList();
        return new RequestHeaders(headers);
    }

    private static RequestBody parseRequestBody(BufferedReader reader, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new RequestBody(new String(buffer));
    }

    public boolean isGet() {
        return requestLine.isGet();
    }

    public boolean isPost() {
        return requestLine.isPost();
    }

    public String findBodyValueByKey(final String key) {
        return body.findByKey(key);
    }

    public boolean sessionNotExists() {
        String cookieString = headers.getCookieString();

        if (cookieString == null) {
            return true;
        }

        HttpCookies cookies = new HttpCookies(cookieString);
        return !cookies.hasJSESSIONID();
    }

    public Session getSession(boolean createIfNotExists) {
        if (sessionNotExists() && createIfNotExists) {
            Session session = new Session();
            SessionManager.getInstance().add(session);
            return session;
        }

        if (sessionNotExists()) {
            return null;
        }

        HttpCookies cookies = new HttpCookies(headers.getCookieString());
        return SessionManager.getInstance().findSession(cookies.getJSESSIONID());
    }

    public String getPath() {
        return requestLine.getPath();
    }
}
