package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.UUID;

public class HttpRequest {

    private static final String QUESTION_MARK = "?";

    private final HttpMethod method;
    private final String uri;
    private final String query;
    private final String version;
    private final HttpRequestHeader header;
    private final HttpRequestBody body;
    private HttpRequest(HttpMethod method, String uri, String query, String version,
                        HttpRequestHeader header, HttpRequestBody body) {
        this.method = method;
        this.uri = uri;
        this.query = query;
        this.version = version;
        this.header = header;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader reader) throws IOException {
        String startLine = reader.readLine();
        String[] tokens = startLine.split(" ");
        HttpMethod method = HttpMethod.valueOf(tokens[0]);
        String version = tokens[2];

        HttpRequestHeader header = HttpRequestHeader.from(reader);
        HttpRequestBody body = HttpRequestBody.from(reader, header);

        String rawPath = tokens[1];
        String uri = parseUri(rawPath);
        String query = parseQuery(rawPath);

        return new HttpRequest(method, uri, query, version, header, body);
    }

    public String getCookie(String key) {
        return header.getCookieValue(key);
    }

    public Session getSession(boolean create) {
        String jsessionid = getCookie("JSESSIONID");
        if(create && !hasSession()) {
            UUID uuid = UUID.randomUUID();
            Session session = new Session(uuid.toString());
            SessionManager.add(session);
            return session;
        }
        return SessionManager.findSession(jsessionid);
    }

    public boolean hasSession() {
        String jsessionid = getCookie("JSESSIONID");
        if (jsessionid == null || jsessionid.isEmpty()) {
           return false;
        }
        Session session = SessionManager.findSession(jsessionid);

        return session != null;
    }

    private static String parseUri(String rawPath) {
        return extractPathOnly(rawPath);
    }

    private static String extractPathOnly(String rawPath) {
        int index = rawPath.indexOf(QUESTION_MARK);
        if (index == -1) {
            return rawPath;
        }
        return rawPath.substring(0, index);
    }

    private static String parseQuery(String rawPath) {
        int index = rawPath.indexOf(QUESTION_MARK);
        if (index == -1 || index == rawPath.length() - 1) {
            return "";
        }
        return rawPath.substring(index + 1);
    }

    public HttpRequestHeader getHeader() {
        return header;
    }

    public HttpRequestBody getBody() {
        return body;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getQuery() {
        return query;
    }

    public String getVersion() {
        return version;
    }
}
