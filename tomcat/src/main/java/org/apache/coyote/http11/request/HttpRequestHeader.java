package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestHeader {

    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final int REQUEST_URI_INDEX = 1;

    private final String requestLine;
    private final RequestHeaders requestHeaders;

    private HttpRequestHeader(String requestLine, Map<String, String> headers) {
        this.requestLine = requestLine;
        this.requestHeaders = new RequestHeaders(headers, getRequestUri());
    }

    public static HttpRequestHeader of(String requestLine, Map<String, String> headers) {
        return new HttpRequestHeader(requestLine, headers);
    }

    public String getRequestUri() {
        List<String> httpRequestMethodInformation = Arrays.stream(requestLine
                .split(REQUEST_LINE_DELIMITER))
                .collect(Collectors.toList());
        return httpRequestMethodInformation.get(REQUEST_URI_INDEX);
    }

    public String getAcceptHeaderValue() {
        return requestHeaders.getAcceptHeaderValue();
    }

    private boolean isExistAccept() {
        return requestHeaders.isAcceptValueCss();
    }

    public boolean isAcceptValueCss() {
        return isExistAccept() && (getAcceptHeaderValue().contains("text/css"));
    }

    public String getRequestLine() {
        return requestLine;
    }

    public RequestHeaders getRequestHeaders() {
        return requestHeaders;
    }
}
