package nextstep.jwp.http.request;

import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.authentication.HttpCookie;

import java.util.Map;

public class HttpRequestHeader {
    public static final String REQUEST_LINE = "requestLine";
    public static final String CONTENT_LENGTH_HEADER = "Content-Length";
    public static final String COOKIE_HEADER = "Cookie";
    private static final String ZERO_STRING = "0";
    private static final String EMPTY_STRING = "";

    private final RequestLine requestLine;
    private final HttpCookie httpCookie;
    private final int contentLength;

    public HttpRequestHeader(final Map<String, String> requestHeaders) {
        if (requestHeaders.isEmpty()) {
            throw new IllegalStateException();
        }

        final String requestHttpCookie = requestHeaders.getOrDefault(COOKIE_HEADER, EMPTY_STRING);
        final int requestContentLength = Integer.parseInt(requestHeaders.getOrDefault(CONTENT_LENGTH_HEADER, ZERO_STRING));

        this.requestLine = new RequestLine(requestHeaders.get(REQUEST_LINE));
        this.httpCookie = new HttpCookie(requestHttpCookie);
        this.contentLength = requestContentLength;
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
