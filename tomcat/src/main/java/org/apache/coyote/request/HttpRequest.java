package org.apache.coyote.request;

import static org.apache.coyote.request.startline.HttpMethod.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.cookie.Cookie;
import org.apache.coyote.cookie.Cookies;
import org.apache.coyote.request.query.QueryParams;
import org.apache.coyote.request.startline.HttpMethod;
import org.apache.coyote.request.startline.StartLine;

public class HttpRequest {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String HEADER_DELIMITER = ": ";
    private static final String HTML_EXTENSION = ".html";
    private static final String QUERY_START_CHARACTER = "?";
    private static final String ROOT = "/";
    private static final String EXTENSION_CHARACTER = ".";
    private static final String DEFAULT_PAGE_URL = "/index.html";
    private static final String COOKIE_HEADER = "Cookie";
    private static final String CONTENT_LENGTH = "Content-Length";

    private final StartLine startLine;
    private final HttpHeader headers;
    private final Cookies cookies;
    private final HttpRequestBody requestBody;

    private HttpRequest(StartLine startLine, HttpHeader headers, Cookies cookies, HttpRequestBody requestBody) {
        this.startLine = startLine;
        this.headers = headers;
        this.cookies = cookies;
        this.requestBody = requestBody;
    }

    public static HttpRequest of(final String startLine, final Map<String, String> headers, final String requestBody) {
        final String cookie = headers.get(COOKIE_HEADER);

        return new HttpRequest(StartLine.from(startLine), HttpHeader.from(headers), Cookies.from(cookie),
                HttpRequestBody.from(requestBody));
    }

    public static HttpRequest readRequest(final BufferedReader bufferedReader) throws IOException {
        final String httpStartLine = bufferedReader.readLine();
        final Map<String, String> httpHeaderLines = readHttpHeaderLines(bufferedReader);
        final String requestBody = readRequestBody(bufferedReader, httpHeaderLines);

        return HttpRequest.of(httpStartLine, httpHeaderLines, requestBody);
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
        return cookies;
    }

    public Optional<Cookie> getJSessionCookie() {
        return cookies.getJSessionCookie();
    }

    public QueryParams getQueryParams() {
        if (startLine.getMethod().equals(GET)) {
            return startLine.getQueryParams();
        }
        return requestBody.getBodyWithQueryParam();
    }

    private static Map<String, String> readHttpHeaderLines(BufferedReader bufferedReader) throws IOException {
        final Map<String, String> httpHeaderLines = new HashMap<>();
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            if (line.isBlank()) {
                break;
            }
            final String[] header = line.split(HEADER_DELIMITER);
            httpHeaderLines.put(header[KEY_INDEX], header[VALUE_INDEX].trim());
        }

        return httpHeaderLines;
    }

    private static String readRequestBody(BufferedReader bufferedReader, Map<String, String> httpHeaderLines)
            throws IOException {
        final String contentLengthHeader = httpHeaderLines.get(CONTENT_LENGTH);
        if (contentLengthHeader == null) {
            return "";
        }

        final int contentLength = Integer.parseInt(contentLengthHeader.trim());
        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);

        return new String(buffer);
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
