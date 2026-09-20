package org.apache.http.response;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpTomcatResponse implements HttpResponse {

    private static final int DEFAULT_STATUS_CODE = 200;
    private static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";
    private final Map<String, String> headers;
    private int statusCode;
    private String body;

    private HttpTomcatResponse(
            int statusCode,
            Map<String, String> headers,
            String body
    ) {
        this.statusCode = statusCode;
        this.headers = new LinkedHashMap<>(headers);
        this.body = body;
    }

    public static HttpTomcatResponse createDefault() {
        Map<String, String> defaultHeaders = new LinkedHashMap<>();
        defaultHeaders.put("Content-Type", DEFAULT_CONTENT_TYPE);
        defaultHeaders.put("Content-Length", "0");
        return new HttpTomcatResponse(
                DEFAULT_STATUS_CODE,
                defaultHeaders,
                ""
        );
    }

    @Override
    public int getStatus() {
        return statusCode;
    }

    @Override
    public void setStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    @Override
    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(headers));
    }

    @Override
    public String getBody() {
        return body;
    }

    @Override
    public void setBody(String body) {
        this.body = body;
        int contentLength = body
                .getBytes(StandardCharsets.UTF_8)
                .length;

        headers.put("Content-Length", String.valueOf(contentLength));
    }

    @Override
    public void setLocation(String path) {
        headers.put("Location", path);
    }

    @Override
    public void setCookie(String cookies) {
        headers.put("Set-Cookie", cookies);
    }

}
