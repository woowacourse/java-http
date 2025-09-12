package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.catalina.Manager;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final RequestBody requestBody;
    private final HttpCookie cookies;
    private Session session;

    public HttpRequest(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        this.requestLine = new RequestLine(reader.readLine());
        this.headers = new HttpHeaders(reader);
        this.requestBody = new RequestBody(requestLine, headers, reader);
        this.cookies = new HttpCookie(headers.get("Cookie"));
    }

    public Session getSession() throws IOException {
        if (this.session != null) {
            return this.session;
        }
        Manager sessionManager = SessionManager.getInstance();
        String sessionId = cookies.getSessionId();
        this.session = sessionManager.findSession(sessionId);
        return this.session;
    }

    public boolean isGetMethod() {
        return requestLine.isGet();
    }

    public boolean isPostMethod() {
        return requestLine.isPost();
    }

    public String getBodyParam(String key) {
        return requestBody.getParam(key);
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getQueryParam(String key) {
        return requestLine.getQueryParam(key);
    }

    public Map<String, String> getQueryParams() {
        return requestLine.getQueryParams();
    }

    public Map<String, String> getBodyParams() {
        return requestBody.getParams();
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public HttpCookie getCookies() {
        return cookies;
    }

    public String getBody() {
        return requestBody.getRawBody();
    }
}
