package org.apache.coyote.http11.http.request.dto;

import java.util.Collections;
import java.util.Map;
import org.apache.coyote.http11.http.HttpHeaders;
import org.apache.coyote.http11.http.HttpMethod;
import org.apache.coyote.http11.http.HttpVersion;
import org.apache.coyote.http11.http.request.RequestLine;

public record HttpRequest(
        RequestLine requestLine,
        HttpHeaders headers,
        Map<String, String> queryParams,
        Map<String, String> bodyParams,
        Map<String, String> cookies
) {

    public HttpRequest {
        queryParams = queryParams != null ? queryParams : Collections.emptyMap();
        bodyParams = bodyParams != null ? bodyParams : Collections.emptyMap();
        cookies = cookies != null ? cookies : Collections.emptyMap();
    }

    public HttpMethod method() {
        return requestLine.method();
    }

    public String path() {
        return requestLine.path();
    }

    public HttpVersion version() {
        return requestLine.version();
    }

    public boolean hasGet() {
        return requestLine.isMethod(HttpMethod.GET);
    }

    public boolean hasPost() {
        return requestLine.isMethod(HttpMethod.POST);
    }

    public String getParam(String key) {
        if (bodyParams.containsKey(key)) {
            return bodyParams.get(key);
        }
        return queryParams.get(key);
    }
}
