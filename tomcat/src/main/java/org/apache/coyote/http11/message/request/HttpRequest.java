package org.apache.coyote.http11.message.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.message.HttpHeaders;
import org.apache.coyote.http11.message.HttpMethod;

public class HttpRequest {

    private static final int URI_INDEX = 0;

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final JsonProperties jsonProperties;

    private HttpRequest(RequestLine requestLine, HttpHeaders headers, JsonProperties jsonProperties) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.jsonProperties = jsonProperties;
    }

    public static HttpRequest from(final BufferedReader reader) throws IOException {
        final var request = new ArrayList<String>();
        String line;
        var body = "";
        JsonProperties properties = null;

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            request.add(line);
        }
        final var header = HttpHeaders.createBasicRequestHeadersFrom(request);

        if (header.getContentLength() > 0) {
            var bodyChars = new char[header.getContentLength()];
            reader.read(bodyChars, 0, bodyChars.length);
            body = new String(bodyChars);
            properties = JsonProperties.from(body.trim(), header);
        }

        final String[] uri = request.get(URI_INDEX).split(" ");
        var requestLine = RequestLine.from(uri);

        return new HttpRequest(requestLine, header, properties);
    }

    public boolean hasCookie(String key) {
        return headers.hasCookie(key);
    }

    public boolean isPathMatch(String path) {
        return requestLine.isPathMatch(path);
    }

    public Session getSession() {
        final Optional<String> sessionId = headers.getCookie("JSESSIONID");
        final var sessionManager = SessionManager.getInstance();
        final var localSession = sessionManager.findSession(sessionId.orElse(""));

        if (localSession == null) {
            return sessionManager.createSession();
        }
        return localSession;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public String getHeaderValue(String key) {
        return headers.get(key);
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getJsonProperty(String key) {
        return jsonProperties.getValue(key);
    }
}
