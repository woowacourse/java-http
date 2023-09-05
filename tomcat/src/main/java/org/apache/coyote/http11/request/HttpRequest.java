package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.UUID;
import org.apache.catalina.session.Manager;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.HttpHeaders;

public class HttpRequest {

    private static final String EMPTY_BODY = "";
    private final HttpRequestStartLine httpRequestStartLine;
    private final HttpRequestHeaders httpRequestHeaders;
    private final HttpRequestCookies httpRequestCookies;
    private final String requestBody;
    private final Session session;

    private HttpRequest(
            final HttpRequestStartLine requestLine,
            final HttpRequestHeaders httpRequestHeaders,
            final String requestBody
    ) throws IOException {
        this.httpRequestStartLine = requestLine;
        this.httpRequestHeaders = httpRequestHeaders;
        this.httpRequestCookies = generateHttpCookies(httpRequestHeaders);
        this.requestBody = requestBody;
        this.session = generateSession();
    }

    private Session generateSession() throws IOException {
        Manager manager = new SessionManager();
        String jsessionId = httpRequestCookies.get("JSESSIONID");
        if (jsessionId != null && manager.findSession(jsessionId) != null) {
            return manager.findSession(jsessionId);
        }
        Session newSession = new Session(String.valueOf(UUID.randomUUID()));
        manager.add(newSession);
        return newSession;
    }

    private HttpRequestCookies generateHttpCookies(final HttpRequestHeaders httpRequestHeaders) {
        String cookies = httpRequestHeaders.getValue(HttpHeaders.COOKIE);
        if (cookies != null) {
            return HttpRequestCookies.of(cookies);
        }
        return HttpRequestCookies.empty();
    }

    public static HttpRequest of(InputStream inputStream) throws IOException {
        Reader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        HttpRequestStartLine requestLine = HttpRequestStartLine.of(bufferedReader.readLine());
        HttpRequestHeaders httpRequestHeaders = HttpRequestHeaders.of(bufferedReader);

        String findContentLength = httpRequestHeaders.getValue(HttpHeaders.CONTENT_LENGTH);
        String requestBody = readRequestBody(findContentLength, bufferedReader);

        return new HttpRequest(requestLine, httpRequestHeaders, requestBody);
    }

    private static String readRequestBody(final String findContentLength, final BufferedReader bufferedReader)
            throws IOException {
        if (findContentLength != null) {
            int contentLength = Integer.parseInt(findContentLength);
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            return new String(buffer);
        }
        return EMPTY_BODY;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public String getPath() {
        return httpRequestStartLine.getUri().getPath();
    }

    public Session getSession() {
        return session;
    }

    public boolean isSameMethod(HttpMethod method) {
        return httpRequestStartLine.getHttpMethod()==method;
    }
}
