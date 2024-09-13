package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;
import org.apache.catalina.Manager;
import org.apache.catalina.session.Session;

/**
 * HTTP Request object. see <a href=https://datatracker.ietf.org/doc/html/rfc2616#section-5>RFC 2616, section 5</a>
 */
public class HttpRequest {

    private static final int BUFFER_SIZE = 64;

    private final HttpHeaders headers;
    private final RequestLine requestLine;
    private final HttpBody body;

    private Manager manager;

    public HttpRequest(InputStream inputStream) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        try {
            this.requestLine = new RequestLine(bufferedReader.readLine());
            List<String> headerLines = bufferedReader.lines()
                    .takeWhile(line -> !line.isEmpty())
                    .toList();
            this.headers = new HttpHeaders(headerLines);
            this.body = parseRequestBody(bufferedReader);
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid HTTP request", e);
        }
    }

    private HttpBody parseRequestBody(BufferedReader bufferedReader) throws IOException {
        StringBuilder bodyBuilder = new StringBuilder();
        char[] buffer = new char[BUFFER_SIZE];
        int readBytes = 0;
        while (readBytes < headers.getContentLength()) {
            readBytes += bufferedReader.read(buffer);
            bodyBuilder.append(buffer, 0, readBytes);
        }
        String content = bodyBuilder.toString();
        return new HttpBody(content);
    }

    public boolean hasPath(String path) {
        return requestLine.hasPath(path);
    }

    public boolean hasMethod(HttpMethod httpMethod) {
        return requestLine.hasMethod(httpMethod);
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public URI getUri() {
        return requestLine.getUri();
    }

    public String getPath() {
        return getUri().getPath();
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getParameter(String name) {
        return requestLine.getQueryParameter(name);
    }

    public String getContent() {
        return body.getContent();
    }

    public Session getSession(boolean create) {
        if (manager == null) {
            throw new IllegalStateException("Manager has not been set.");
        }
        HttpCookie cookie = headers.getCookie(Session.JSESSIONID);
        Session session = null;
        if (cookie != null) {
            session = manager.findSession(cookie.getValue());
        }
        if (create && session == null) {
            session = manager.createSession();
        }
        return session;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public String toString() {
        return "HttpRequest{" +
               "requestLine=" + requestLine +
               ", header=" + headers +
               ", body='" + body + '\'' +
               '}';
    }
}
