package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;

public class HttpRequest {

    private final HttpRequestStartLine startLine;
    private final HttpHeaders headers;
    private final String body;
    private HttpParameters parameters;
    private HttpCookie cookies;

    public HttpRequest(
            final HttpRequestStartLine startLine,
            final HttpHeaders headers,
            final String body,
            final HttpParameters parameters,
            final HttpCookie cookies
    ) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
        this.parameters = parameters;
        this.cookies = cookies;
    }

    public static HttpRequest from(final BufferedReader br) throws IOException {
        String line = br.readLine();
        if (line == null) {
            return null;
        }
        HttpRequestStartLine startLine = HttpRequestStartLine.from(line);
        HttpHeaders headers = HttpHeaders.from(br);
        String body = readBody(br, headers);
        HttpParameters params = HttpParameters.getAllParameters(startLine, headers, body);
        HttpCookie cookies = HttpCookie.parse(headers.getCookieHeader());

        return new HttpRequest(
                startLine,
                headers,
                body,
                params,
                cookies
        );
    }

    private static String readBody(final BufferedReader br, final HttpHeaders headers) throws IOException {
        String contentLengthHeader = headers.get("Content-Length");
        if (contentLengthHeader == null) {
            return "";
        }

        int contentLength = Integer.parseInt(contentLengthHeader);
        char[] body = new char[contentLength];
        br.read(body, 0, contentLength);

        return new String(body);
    }

    public HttpParameters getParameters() {
        return parameters;
    }

    public HttpRequestStartLine getStartLine() {
        return startLine;
    }

    public String getMethod() {
        return startLine.getHttpMethod();
    }

    public String getPath() {
        return startLine.getPath();
    }

    public String getCookie(final String name) {
        return cookies.get(name);
    }

    public Session getSession(boolean create) {
        if (create) {
            return SessionManager.getInstance().createSession();
        }
        String jsessionId = getCookie("JSESSIONID");
        return SessionManager.getInstance().findSession(jsessionId);
    }
}
