package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final RequestURI requestURI;
    private final Map<String, String> headers;

    private HttpRequest(RequestURI requestURI, Map<String, String> headers) {
        this.requestURI = requestURI;
        this.headers = headers;
    }

    public static HttpRequest from(List<String> request) {
        String uri = request.get(0).split(" ")[1];
        return new HttpRequest(RequestURI.from(uri), parseHeaders(request));
    }

    private static Map<String, String> parseHeaders(List<String> request) {
        Map<String, String> headers = new HashMap<>();
        for (String header : request.subList(1, request.size())) {
            String[] keyAndValue = header.split(": ", 2);
            headers.put(keyAndValue[KEY_INDEX], keyAndValue[VALUE_INDEX]);
        }
        return headers;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public RequestURI getRequestUrl() {
        return requestURI;
    }

    public String getUrl() {
        return requestURI.getUri();
    }

}
