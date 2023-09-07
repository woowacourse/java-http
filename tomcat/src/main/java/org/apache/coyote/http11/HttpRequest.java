package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;

public class HttpRequest {

    private static final String HEADER_BODY_DELIMITER = "";
    private static final String INDEX_HTML = "/index.html";
    private static final int URI_INDEX = 0;

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final String body;
    private final JsonProperties jsonProperties;

    private HttpRequest(RequestLine requestLine, HttpHeaders headers,String body, JsonProperties jsonProperties) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
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

        request.add(HEADER_BODY_DELIMITER);
        if (header.getContentLength() > 0) {
            var bodyChars = new char[header.getContentLength()];
            reader.read(bodyChars, 0, bodyChars.length);
            body = new String(bodyChars);
            properties = JsonProperties.from(body.trim(), header);
        }

        final String[] uri = request.get(URI_INDEX).split(" ");
        var requestLine = RequestLine.from(uri);

        return new HttpRequest(requestLine, header, body, properties);
    }

    public boolean hasQueryStrings() {
        return requestLine.hasQueryStrings();
    }

    public boolean hasCookie(String key) {
        return headers.hasCookie(key);
    }

    public Session getSession() {
        final Optional<String> sessionId = headers.getCookie("JSESSIONID");
        final var sessionManager = SessionManager.getInstance();
        final var localSession = sessionManager.findSession(sessionId.orElse(""));

        if (localSession == null) {
            return sessionManager.createSession(UUID.randomUUID().toString());
        }
        return localSession;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getQueryString(String key) {
        return requestLine.getQueryStrings().getValue(key);
    }

    public String getJsonProperty(String key) {
        return jsonProperties.getValue(key);
    }

    public String getPath() {
        return requestLine.getPath();
    }

}
