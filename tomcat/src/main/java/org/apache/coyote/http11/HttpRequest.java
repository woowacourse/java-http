package org.apache.coyote.http11;

import org.apache.catalina.session.Session;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;
import org.apache.catalina.Manager;

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

            // Parse Body, it should consider all character including escape characters
            StringBuilder bodyBuilder = new StringBuilder();
            char[] buffer = new char[BUFFER_SIZE];
            int readBytes = 0;
            while (readBytes < headers.getContentLength()) {
                readBytes += bufferedReader.read(buffer);
                bodyBuilder.append(buffer, 0, readBytes);
            }
            this.body = new HttpBody(bodyBuilder.toString());
        } catch (IOException e) {
            // IOException on reading lines using inputstream, parsing uri
            // IndexOutOfBoundsException on missing tokens in request-line and header
            throw new IllegalArgumentException("Invalid HTTP request", e);
        }
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public URI getUri() {
        return requestLine.getUri();
    }

    public boolean hasPath(String path) {
        return requestLine.hasPath(path);
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public boolean hasMethod(HttpMethod httpMethod) {
        return requestLine.hasMethod(httpMethod);
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

        HttpCookie cookie = headers.getCookie("JSESSIONID");
        Session session = null;
        if (cookie != null) {
            session = manager.findSession(cookie.getValue());
        }
        if (create && session == null) {
            session = new Session();
            manager.add(session);
            return session;
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
