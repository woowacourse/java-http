package org.apache.coyote.http11.request;

import java.util.List;

import org.apache.coyote.http11.Headers;

public class HttpRequest {
    private static final int REQUEST_LINE_INDEX = 0;
    private static final int HEADER_INDEX = 1;
    private static final String CONTENT_TYPE_KEY = "Accept";

    private final RequestLine requestLine;
    private final Headers headers;
    private final RequestBody requestBody;

    private HttpRequest(RequestLine requestLine, Headers headers, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public static HttpRequest of(List<String> requestHeader, RequestBody requestBody) {
        RequestLine requestLine = RequestLine.from(requestHeader.get(REQUEST_LINE_INDEX));
        List<String> requests = requestHeader.subList(HEADER_INDEX, requestHeader.size());
        Headers headers = Headers.from(requests);
        return new HttpRequest(requestLine, headers, requestBody);
    }

    public String getHeaderValue(String key) {
        return headers.getValue(key);
    }

    public ContentType contentType() {
        if (!headers.containsKey(CONTENT_TYPE_KEY)) {
            return ContentType.HTML;
        }
        return ContentType.from(headers.getValue(CONTENT_TYPE_KEY));
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public String getHeaderCookie() {
        return headers.getValue("Cookie");
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

}
