package nextstep.jwp.web.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.web.http.Headers;
import nextstep.jwp.web.session.HttpSession;
import nextstep.jwp.web.session.HttpSessions;

public class HttpRequest {

    private StartLine startLine;

    private Headers headers;

    private RequestBody body;

    private HttpRequestCookie cookie;

    private HttpRequest() {
    }

    public HttpRequest(BufferedReader bufferedReader) throws IOException {
        this.startLine = new StartLine(parseHttpRequestStartLine(bufferedReader));
        this.headers = new Headers(parseHttpRequestHeaders(bufferedReader));
        this.body = new RequestBody(parseHttpRequestBody(bufferedReader));
        this.cookie = initializeCookie();
    }

    private String parseHttpRequestStartLine(BufferedReader bufferedReader) throws IOException {
        return bufferedReader.readLine();
    }

    private List<String> parseHttpRequestHeaders(BufferedReader bufferedReader) throws IOException {
        List<String> rawHeaders = new ArrayList<>();
        String content = "";

        while (!"".equals(content = bufferedReader.readLine())) {
            rawHeaders.add(content);
        }

        return rawHeaders;
    }

    private String parseHttpRequestBody(BufferedReader bufferedReader) throws IOException {
        int contentLength = headers.getContentLength();
        if (contentLength == 0) {
            return null;
        }

        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);

        return new String(buffer);
    }

    private HttpRequestCookie initializeCookie() {
        if (headers.hasCookie()) {
            return new HttpRequestCookie(
                headers.getValue("Cookie")
                    .get(0)
            );
        }

        return new HttpRequestCookie();
    }

    public HttpMethod getMethod() {
        return startLine.getMethod();
    }

    public String getUrl() {
        return startLine.getUrl();
    }

    public String getAttribute(String key) {
        return body.getAttribute(key);
    }

    public String getCookieValue(String key) {
        return cookie.get(key);
    }

    public HttpSession getSession() {
        String sessionId = getCookieValue("JSESSIONID");
        if (sessionId != null) {
            return HttpSessions.getSession(sessionId);
        }

        return HttpSessions.getSession();
    }

    public String getRequestParams(String key) {
        return startLine.getRequestParams(key);
    }
}
