package org.apache.coyote.http11;

import org.apache.coyote.http11.header.Cookies;
import org.apache.coyote.http11.header.Headers;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Headers headers;
    private final String body;

    public HttpRequest(final RequestLine requestLine, final Headers headers, final String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader reader) {
        try {
            RequestLine requestLine = new RequestLine(reader.readLine());
            Headers headers = new Headers();
            String next;
            while (reader.ready() && !(next = reader.readLine()).isEmpty()) {
                headers.add(next);
            }

            int contentLength = headers.getContentLength();
            char[] bodyBuffer = new char[contentLength];
            while (contentLength > 0) {
                int readCount = reader.read(bodyBuffer, 0, contentLength);
                contentLength -= readCount;
            }

            return new HttpRequest(
                    requestLine,
                    headers,
                    new String(bodyBuffer)
            );
        } catch (IOException e) {
            throw new IllegalArgumentException("HTTP 요청을 잘 읽지 못했습니다");
        }
    }


    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public HttpTarget getTarget() {
        return requestLine.getTarget();
    }

    public String getVersion() {
        return requestLine.getHttpVersion();
    }

    public List<HttpHeader> getHeaders() {
        return headers.getHeaders();
    }

    public Cookies getCookies() {
        HttpHeader cookieHeader = headers.get("Cookie");
        return Cookies.from(cookieHeader);
    }

    public Optional<Session> getSession(String sessionKey) {
        return getCookies().get(sessionKey)
                .flatMap(SessionManager::findSession);
    }

    public HttpHeader getHeader(String name) {
        return headers.get(name);
    }

    public String getBody() {
        return body;
    }
}
