package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String CONTENT_TYPE_KEY = "Accept";

    private final RequestURI requestURI;
    private final HttpMethod httpMethod;
    private final Map<String, String> headers;
    private final RequestBody requestBody;

    private HttpRequest(
            RequestURI requestURI,
            HttpMethod httpMethod,
            Map<String, String> headers,
            RequestBody requestBody
    ) {
        this.requestURI = requestURI;
        this.httpMethod = httpMethod;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(List<String> requestHeader, RequestBody requestBody) {
        String uri = requestHeader.get(0).split(" ")[1];
        String method = requestHeader.get(0).split(" ")[0];
        Map<String, String> headers = parseHeaders(requestHeader);
        return new HttpRequest(RequestURI.from(uri), HttpMethod.from(method), headers, requestBody);
    }

    private static Map<String, String> parseHeaders(List<String> request) {
        Map<String, String> headers = new HashMap<>();
        for (String header : request.subList(1, request.size())) {
            String[] keyAndValue = header.split(": ", 2);
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

    public RequestURI getRequestUrl() {
        return requestURI;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

}
