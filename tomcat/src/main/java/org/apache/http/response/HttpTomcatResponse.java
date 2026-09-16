package org.apache.http.response;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpTomcatResponse implements HttpResponse {

    private static final int DEFAULT_STATUS_CODE = 200;
    private static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";

    private int statusCode;
    private final Map<String, String> headers;
    private String body;

    private HttpTomcatResponse(
            int statusCode,
            Map<String, String> headers,
            String body
    ) {
        this.statusCode = statusCode;
        this.headers = new HashMap<>(headers);
        this.body = body;
    }

    public static HttpTomcatResponse createDefault() {
        return new HttpTomcatResponse(
                DEFAULT_STATUS_CODE,
                Map.of("Content-Type", DEFAULT_CONTENT_TYPE),
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
        return Collections.unmodifiableMap(headers);
    }

    @Override
    public String getBody() {
        return body;
    }

    @Override
    public void setBody(String body) {
        this.body = body;
    }
}
