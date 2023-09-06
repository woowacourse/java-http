package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {
    private static final int REQUEST_LINE_INDEX = 0;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String HEADER_DELIMITER = ": ";
    private static final String CONTENT_TYPE_KEY = "Accept";

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final RequestBody requestBody;

    private HttpRequest(RequestLine requestLine, Map<String, String> headers, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public static HttpRequest of(List<String> requestHeader, RequestBody requestBody) {
        Map<String, String> headers = parseHeaders(requestHeader);
        RequestLine requestLine = RequestLine.from(requestHeader.get(REQUEST_LINE_INDEX));
        return new HttpRequest(requestLine, headers, requestBody);
    }

    private static Map<String, String> parseHeaders(List<String> request) {
        Map<String, String> headers = new HashMap<>();
        for (String header : request.subList(1, request.size())) {
            String[] keyAndValue = header.split(HEADER_DELIMITER, 2);
            headers.put(keyAndValue[KEY_INDEX], keyAndValue[VALUE_INDEX]);
        }
        return headers;
    }

    public ContentType contentType() {
        if (!headers.containsKey(CONTENT_TYPE_KEY)) {
            return ContentType.HTML;
        }
        return ContentType.from(headers.get(CONTENT_TYPE_KEY));
    }

    public boolean isRequestBodyEmpty() {
        return requestBody.isEmpty();
    }

    public String getHeaderValue(String key) {
        return headers.getOrDefault(key, "");
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

}
