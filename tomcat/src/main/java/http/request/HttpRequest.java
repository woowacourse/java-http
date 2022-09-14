package http.request;

import http.header.ContentType;
import http.header.HttpCookie;
import http.header.HttpHeaders;
import http.session.Session;
import http.session.SessionManager;
import http.support.ResourcesUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpRequest {

    private static final String SESSION_COOKIE_NAME = "JSESSIONID";

    private final HttpRequestStartLine httpRequestStartLine;
    private final HttpHeaders headers;
    private final String body;

    private HttpRequest(final HttpRequestStartLine httpRequestStartLine, final HttpHeaders headers, final String body) {
        this.httpRequestStartLine = httpRequestStartLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest parse(final BufferedReader bufferedReader) throws IOException {
        HttpRequestStartLine startLine = HttpRequestStartLine.parse(bufferedReader.readLine());
        HttpHeaders httpHeaders = HttpHeaders.parse(readHeaders(bufferedReader));
        int contentLength = Integer.parseInt(httpHeaders.getOrDefault("Content-Length", "0"));
        String body = readBody(bufferedReader, contentLength);

        return new HttpRequest(startLine, httpHeaders, body);
    }

    private static List<String> readHeaders(final BufferedReader bufferedReader) throws IOException {
        List<String> headerLines = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isBlank()) {
            headerLines.add(line);
        }
        return headerLines;
    }

    private static String readBody(final BufferedReader bufferedReader, final int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public ContentType getAcceptContentType() {
        return ContentType.fromExtension(ResourcesUtil.parseExtension(httpRequestStartLine.getUrl()));
    }

    public HttpCookie getCookie() {
        return HttpCookie.parseCookie(headers.getOrDefault("Cookie", ""));
    }

    public Session getSession() {
        HttpCookie cookie = getCookie();
        String sessionId = cookie.get(SESSION_COOKIE_NAME);
        SessionManager sessionManager = SessionManager.instance();
        if (sessionId == null) {
            return null;
        }
        return sessionManager.findSession(sessionId);
    }

    public HttpMethod getHttpMethod() {
        return httpRequestStartLine.getHttpMethod();
    }

    public String getUrl() {
        return httpRequestStartLine.getUrl();
    }

    public QueryParams getQueryParams() {
        return httpRequestStartLine.getQueryParams();
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
