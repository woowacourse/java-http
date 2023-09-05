package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpResponse {

    private Map<String, String> headers;
    private HttpStatus httpStatus;
    private String body;
    private Map<String, String> cookies = new ConcurrentHashMap<>();


    public HttpResponse(Map<String, String> headers,
                        HttpStatus httpStatus, String body) {
        this.headers = headers;
        this.httpStatus = httpStatus;
        this.body = body;
    }

    public static HttpResponse ok(String responseBody, ContentType contentType) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", contentType.value + ";charset=utf-8");
        headers.put("Content-Length", String.valueOf(responseBody.getBytes().length));
        return new HttpResponse(headers, HttpStatus.OK, responseBody);
    }

    public static HttpResponse found(String location) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Length", "0");
        headers.put("Location", location);
        return new HttpResponse(headers, HttpStatus.FOUND, "");
    }


    public Map<String, String> getHeaders() {
        return headers;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getCookie() {
        return cookies;
    }

}
