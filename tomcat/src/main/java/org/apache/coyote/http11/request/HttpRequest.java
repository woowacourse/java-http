package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.exception.ExceptionType;
import nextstep.jwp.exception.InvalidHttpRequestException;
import org.apache.coyote.http11.common.HttpMethod;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final RequestBody requestBody;

    private HttpRequest(final RequestLine requestLine, final RequestHeaders requestHeaders,
                        final RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(final BufferedReader reader) {
        try {
            final RequestLine requestLine = RequestLine.from(reader.readLine());
            final RequestHeaders requestHeaders = RequestHeaders.from(reader);
            final int contentLength = requestHeaders.getContentLength();

            return getRequest(reader, requestLine, requestHeaders, contentLength);
        } catch (IOException e) {
            throw new InvalidHttpRequestException(ExceptionType.INVALID_REQUEST_LINE_EXCEPTION);
        }
    }

    private static HttpRequest getRequest(final BufferedReader reader, final RequestLine requestLine,
                                          final RequestHeaders requestHeaders, final int contentLength) {
        if (contentLength > 0) {
            return new HttpRequest(requestLine, requestHeaders, RequestBody.of(reader, contentLength));
        }
        return new HttpRequest(requestLine, requestHeaders, RequestBody.from(new HashMap<>()));
    }

    public String getUrl() {
        return requestLine.getUrl();
    }

    public Map<String, String> getParams() {
        return requestLine.getParams();
    }

    public String getCookie() {
        String cookie = requestHeaders.findCookie();
        final int splitPoint = cookie.lastIndexOf("=");
        return cookie.substring(splitPoint + 1);
    }

    public boolean hasCookie() {
        return requestHeaders.hasCookie();
    }

    public boolean isGet() {
        return requestLine.isMatchHttpMethod(HttpMethod.GET);
    }

    public boolean isPost() {
        return requestLine.isMatchHttpMethod(HttpMethod.POST);
    }

    public Map<String, String> getRequestBody() {
        return requestBody.getRequestBody();
    }

}
