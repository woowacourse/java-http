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
import org.apache.coyote.http11.HttpCookies;

public class HttpRequest {

    private static final String EMPTY_BODY = "";
    private final HttpRequestStartLine httpRequestStartLine;
    private final HttpRequestHeaders httpRequestHeaders;
    private final HttpCookies httpCookies;
    private final String requestBody;
    private Manager manager = new SessionManager();
    private Session session = null;

    private HttpRequest(final HttpRequestStartLine requestLine, final HttpRequestHeaders httpRequestHeaders,
                        final HttpCookies httpCookies, final String requestBody) throws IOException {
        this.httpRequestStartLine = requestLine;
        this.httpRequestHeaders = httpRequestHeaders;
        this.httpCookies = httpCookies;
        this.requestBody = requestBody;

        if(httpCookies.get("JSESSIONID")!=null){
            this.session = manager.findSession(httpCookies.get("JSESSIONID"));
        }
    }

    public static HttpRequest of(InputStream inputStream) throws IOException {
        Reader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        HttpRequestStartLine requestLine = HttpRequestStartLine.of(bufferedReader.readLine());
        HttpRequestHeaders httpRequestHeaders = HttpRequestHeaders.of(bufferedReader);
        HttpCookies httpCookies = HttpCookies.empty();

        String findContentLength = httpRequestHeaders.getValue("content-length");
        if (findContentLength != null) {
            int contentLength = Integer.parseInt(findContentLength);
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            String requestBody = new String(buffer);
            return new HttpRequest(requestLine, httpRequestHeaders, httpCookies, requestBody);
        }

        String cookies = httpRequestHeaders.getValue("cookie");
        if (cookies != null) {
            return new HttpRequest(requestLine, httpRequestHeaders, HttpCookies.of(cookies), EMPTY_BODY);
        }

        return new HttpRequest(requestLine, httpRequestHeaders, httpCookies, EMPTY_BODY);
    }

    public String getRequestBody() {
        return requestBody;
    }

    public String getPath() {
        return httpRequestStartLine.getUri().getPath();
    }

    public String getQuery() {
        return httpRequestStartLine.getUri().getQuery();
    }

    public Session getSession(boolean create) {
        if(session == null && create){
            session =  new Session(String.valueOf(UUID.randomUUID()));
            manager.add(session);
        }
        return session;
    }

    public Session getSession() {
        return getSession(true);
    }

    public boolean isSameMethod(String method) {
        return httpRequestStartLine.getHttpMethod()
                .equals(HttpMethod.valueOf(method));
    }
}
