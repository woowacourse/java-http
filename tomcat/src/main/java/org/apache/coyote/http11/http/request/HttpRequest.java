package org.apache.coyote.http11.http.request;

import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.http.common.HttpCookie;
import org.apache.coyote.http11.http.common.header.HttpHeader;
import org.apache.coyote.http11.http.common.startline.HttpMethod;

public class HttpRequest {

    private final HttpStartLine httpStartLine;
    private final HttpHeader httpHeader;
    private final HttpCookie httpCookie;
    private final HttpRequestBody httpRequestBody;
    private final HttpSession httpSession;

    private HttpRequest(final HttpStartLine httpStartLine,
                        final HttpHeader httpHeader,
                        final HttpCookie httpCookie,
                        final HttpRequestBody httpRequestBody,
                        final HttpSession httpSession) {
        this.httpStartLine = httpStartLine;
        this.httpHeader = httpHeader;
        this.httpCookie = httpCookie;
        this.httpRequestBody = httpRequestBody;
        this.httpSession = httpSession;
    }


    public static HttpRequest from(final InputStream inputStream, final SessionManager sessionManager)
            throws IOException {
        validateNotNull(inputStream);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        final String startLine = bufferedReader.readLine();
        final HttpStartLine httpStartLine = HttpStartLine.from(startLine);

        List<String> headerLines = readHeaderLines(bufferedReader);
        final HttpHeader httpHeader = HttpHeader.from(headerLines);
        final HttpCookie httpCookie = HttpCookie.from(httpHeader);
        final HttpRequestBody httpRequestBody = HttpRequestBody.of(bufferedReader, httpHeader);
        final HttpSession session = sessionManager.findSession(httpCookie.getByName("JSESSIONID"));

        return new HttpRequest(httpStartLine, httpHeader, httpCookie, httpRequestBody, session);
    }

    private static List<String> readHeaderLines(final BufferedReader bufferedReader) throws IOException {
        List<String> headerLines = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            headerLines.add(line);
        }
        return headerLines;
    }

    private static void validateNotNull(final InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("inputStream은 null일 수 없습니다.");
        }
    }

    public HttpMethod getMethod() {
        return httpStartLine.getMethod();
    }

    public String getPath() {
        return httpStartLine.getPath();
    }

    public boolean containsQueryParameter(final String target) {
        if (target == null) {
            throw new IllegalArgumentException("찾으려는 query parameter key는 null일 수 없습니다.");
        }
        String cleanTarget = target.trim();

        return httpStartLine.containsTargetQueryParameter(cleanTarget);
    }

    public String getTargetQueryParameter(final String target) {
        if (target == null) {
            throw new IllegalArgumentException("찾으려는 query parameter key는 null일 수 없습니다.");
        }
        String cleanTarget = target.trim();

        return httpStartLine.getTargetQueryParameter(cleanTarget);
    }

    public HttpRequestBody getBody() {
        return httpRequestBody;
    }

    public HttpCookie getCookie() {
        return httpCookie;
    }

    public HttpSession getSession() {
        return httpSession;
    }
}
