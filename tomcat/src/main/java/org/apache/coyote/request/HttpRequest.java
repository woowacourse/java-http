package org.apache.coyote.request;

import static org.apache.coyote.request.startline.HttpMethod.GET;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;
import org.apache.coyote.cookie.Cookie;
import org.apache.coyote.cookie.Cookies;
import org.apache.coyote.request.query.QueryParams;
import org.apache.coyote.request.startline.HttpMethod;
import org.apache.coyote.request.startline.StartLine;

public class HttpRequest {

    private static final String HTML_EXTENSION = ".html";
    private static final String QUERY_START_CHARACTER = "?";
    private static final String ROOT = "/";
    private static final String EXTENSION_CHARACTER = ".";
    private static final String DEFAULT_PAGE_URL = "/index.html";

    private final StartLine startLine;
    private final HttpHeader httpHeader;
    private final HttpRequestBody requestBody;

    private HttpRequest(StartLine startLine, HttpHeader httpHeader, HttpRequestBody requestBody) {
        this.startLine = startLine;
        this.httpHeader = httpHeader;
        this.requestBody = requestBody;
    }

    public static HttpRequest of(final String startLine, final HttpHeader header, final HttpRequestBody requestBody) {
        return new HttpRequest(StartLine.from(startLine), header, requestBody);
    }

    public static HttpRequest readRequest(final BufferedReader bufferedReader) throws IOException {
        final String httpStartLine = bufferedReader.readLine();
        final HttpHeader httpHeader = HttpHeader.readHttpHeaderLines(bufferedReader);
        final HttpRequestBody requestBody = HttpRequestBody.readRequestBody(bufferedReader, httpHeader);

        return HttpRequest.of(httpStartLine, httpHeader, requestBody);
    }

    public String getRequestPath() {
        String requestUrl = startLine.getRequestPath();
        requestUrl = makeDefaultRequestUrl(requestUrl);

        return requestUrl;
    }

    public HttpMethod getRequestMethod() {
        return startLine.getMethod();
    }

    public Cookies getCookies() {
        return httpHeader.getCookies();
    }

    public Optional<Cookie> getJSessionCookie() {
        return httpHeader.getJSessionCookie();
    }

    public QueryParams getQueryParams() {
        if (startLine.isSameMethod(GET)) {
            return startLine.getQueryParams();
        }
        return requestBody.getBodyWithQueryParam();
    }

    private String makeDefaultRequestUrl(String requestUrl) {
        if (requestUrl.equals(ROOT)) {
            return DEFAULT_PAGE_URL;
        }
        if (!requestUrl.contains(EXTENSION_CHARACTER)) {
            return addExtension(requestUrl);
        }
        return requestUrl;
    }

    private String addExtension(final String requestUrl) {
        final int index = requestUrl.indexOf(QUERY_START_CHARACTER);
        if (index != -1) {
            final String path = requestUrl.substring(0, index);
            final String queryString = requestUrl.substring(index + 1);
            return path + HTML_EXTENSION + QUERY_START_CHARACTER + queryString;
        }
        return requestUrl + HTML_EXTENSION;
    }
}
