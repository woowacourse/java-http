package nextstep.jwp.http.request;

import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.authentication.HttpCookie;

import java.util.List;
import java.util.Map;

public class HttpRequestHeader {
    private static final int REQUEST_LINE_INDEX = 0;
    private static final String CONTENT_LENGTH_HEADER = "Content-Length: ";
    private static final String COOKIE_HEADER = "Cookie: ";
    private static final String ZERO_STRING = "0";
    private static final String EMPTY_STRING = "";

    private final RequestLine requestLine;
    private final HttpCookie httpCookie;
    private final int contentLength;

    public HttpRequestHeader(final List<String> requestHeaders) {
        if (requestHeaders.isEmpty()) {
            throw new IllegalStateException();
        }

        this.requestLine = new RequestLine(requestHeaders.get(REQUEST_LINE_INDEX));
        this.httpCookie = new HttpCookie(parseHttpCookie(requestHeaders));
        this.contentLength = parseContentLength(requestHeaders);
    }

    // TODO: parsing 로직 중복 처리
    private String parseHttpCookie(final List<String> requestHeaders) {
        String httpCookieValue = requestHeaders.stream()
                .filter(header -> header.startsWith(COOKIE_HEADER))
                .findAny()
                .orElseGet(() -> EMPTY_STRING);
        if (!httpCookieValue.isEmpty()) {
            httpCookieValue = httpCookieValue.substring(COOKIE_HEADER.length());
        }
        return httpCookieValue;
    }

    private int parseContentLength(final List<String> requestHeaders) {
        String contentLengthHeader = requestHeaders.stream()
                .filter(header -> header.startsWith(CONTENT_LENGTH_HEADER))
                .findAny()
                .orElseGet(() -> ZERO_STRING);
        if (!contentLengthHeader.equals(ZERO_STRING)) {
            contentLengthHeader = contentLengthHeader.substring(CONTENT_LENGTH_HEADER.length());
        }
        return Integer.parseInt(contentLengthHeader);
    }

    public boolean doesNotHaveJSession() {
        return httpCookie.doesNotHaveJSession();
    }

    public boolean doesNotHaveQueryParameters() {
        return requestLine.doesNotHaveQueryParameters();
    }

    public String getJSession() {
        return httpCookie.getJSession();
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public String getProtocol() {
        return requestLine.getProtocol();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public Map<String, String> getQueryParameters() {
        return requestLine.getQueryParameters();
    }

    public int getContentLength() {
        return contentLength;
    }

    public HttpCookie getCookie() {
        return httpCookie;
    }
}
