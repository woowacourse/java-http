package org.apache.coyote.http11.response;

import org.apache.coyote.http11.cookie.HttpCookie;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseHeaders {

    private final Map<String, String> headers;

    private ResponseHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static ResponseHeaders of(ResponseBody responseBody) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", responseBody.getContentType().getName());
        headers.put("Content-Length", String.valueOf(responseBody.getLength()));
        return new ResponseHeaders(headers);
    }

    public void addCookie(HttpCookie cookie) {
        headers.put("Set-Cookie", cookie.toString());
    }

    public void setLocation(String location) {
        headers.put("Location", location);
    }

    public String parse() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> headersEntry : headers.entrySet()) {
            stringBuilder.append(headersEntry.getKey()).append(": ").append(headersEntry.getValue()).append(" \r\n");
        }
        return stringBuilder.toString();
    }

    public void addContent(ResponseBody responseBody) {
        headers.put("Content-Type", responseBody.getContentType().getName());
        headers.put("Content-Length", String.valueOf(responseBody.getLength()));
    }
}
