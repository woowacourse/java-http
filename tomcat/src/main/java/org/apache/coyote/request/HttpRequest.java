package org.apache.coyote.request;

import java.util.Map;
import java.util.Optional;
import org.apache.coyote.cookie.Cookie;
import org.apache.coyote.cookie.Cookies;

public class HttpRequest {

    private static final String HTML_EXTENSION = ".html";
    private static final String QUERY_START_CHARACTER = "?";
    private static final String ROOT = "/";
    private static final String EXTENSION_CHARACTER = ".";
    private static final String DEFAULT_PAGE_URL = "/index.html";
    private static final String COOKIE_HEADER = "Cookie";

    private final StartLine startLine;
    private final Map<String, String> headers;
    private final Cookies cookies;
    private final String requestBody;

    public HttpRequest(StartLine startLine, Map<String, String> headers, Cookies cookies, String requestBody) {
        this.startLine = startLine;
        this.headers = headers;
        this.cookies = cookies;
        this.requestBody = requestBody;
    }

    public static HttpRequest of(final String startLine, final Map<String, String> headers, final String requestBody) {
        final String cookie = headers.get(COOKIE_HEADER);

        return new HttpRequest(StartLine.from(startLine), headers, Cookies.from(cookie), requestBody);
    }

    public String getRequestUrlWithoutQuery() {
        final String requestUrl = getRequestUrl();
        if (requestUrl.contains(QUERY_START_CHARACTER)) {
            final int index = requestUrl.indexOf(QUERY_START_CHARACTER);
            return requestUrl.substring(0, index);
        }
        return requestUrl;
    }

    public String getRequestUrl() {
        String requestUrl = startLine.getUri();
        requestUrl = makeDefaultRequestUrl(requestUrl);

        return requestUrl;
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

    public String getRequestBody() {
        return requestBody;
    }

    public HttpMethod getRequestMethod() {
        return startLine.getMethod();
    }

    public Cookies getCookies() {
        return cookies;
    }

    public Optional<Cookie> getJSessionCookie() {
        return cookies.getJSessionCookie();
    }
}
