package org.apache.coyote.http11.request_response.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.coyote.http11.request_response.HttpMethod;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final HttpRequestBody body;

    public HttpRequest(BufferedReader bufferedReader) throws IOException {
        requestLine = new RequestLine(bufferedReader.readLine());
        headers = parseHeaders(bufferedReader);
        body = parseBody(bufferedReader);
    }

    private static HttpHeaders parseHeaders(BufferedReader bufferedReader) throws IOException {
        String header;
        List<String> headers = new ArrayList<>();
        while ((header = bufferedReader.readLine()) != null) {
            if (header.isEmpty()) {
                break;
            }
            headers.add(header);
        }
        return new HttpHeaders(headers);
    }

    private HttpRequestBody parseBody(BufferedReader bufferedReader) throws IOException {
        String body = "";
        int contentLength = headers.getContentLength();
        if (contentLength > 0) {
            char[] bodyChars = new char[contentLength];
            bufferedReader.read(bodyChars, 0, contentLength);
            body = new String(bodyChars);
        }
        return new HttpRequestBody(body);
    }

    public Session getSession(boolean createNew) {
        HttpCookie httpCookie = getHttpCookie();
        String jSessionId = httpCookie.getCookie("JSESSIONID");
        if (jSessionId == null || SessionManager.getInstance().findSession(jSessionId) == null) {
            if (createNew) {
                Session newSession = new Session();
                SessionManager.getInstance().add(newSession);
                return newSession;
            }
            return null;
        } else {
            SessionManager sessionManager = SessionManager.getInstance();
            return sessionManager.findSession(jSessionId);
        }
    }

    public HttpCookie getHttpCookie() {
        return headers.getCookie();
    }

    public String getBody() {
        return body.getValue();
    }

    public Map<String, String > getFormData() {
        return body.parseFormData();
    }

    public String getUriPath() {
        return requestLine.getUriPath();
    }

    public HttpMethod getRequestMethod() {
        return requestLine.getMethod();
    }
}
