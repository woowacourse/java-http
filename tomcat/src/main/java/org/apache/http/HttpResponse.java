package org.apache.http;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private final String protocol;
    private final Map<String, String> headers = new HashMap<>();
    private HttpStatus httpStatus;
    private HttpCookie httpCookie;
    private String responseBody = "";

    public HttpResponse(final String protocol) {
        this.protocol = protocol;
    }

    public static HttpResponse notFound(HttpRequest httpRequest) {
        HttpResponse response = new HttpResponse(httpRequest.getProtocol());

        response.setHttpStatus(HttpStatus.NOT_FOUND);
        response.setHeader("Content-Type", "text/html; charset=utf-8");

        return response;
    }

    public void redirect(String redirectPath) {
        setHttpStatus(HttpStatus.FOUND);
        setHeader("Content-Type", "text/html; charset=utf-8");
        setLocationHeader(redirectPath);
    }

    public HttpCookie getHttpCookie() {
        return httpCookie;
    }

    public void setHttpCookie(final HttpCookie httpCookie) {
        this.httpCookie = httpCookie;
    }

    public String getProtocol() {
        return protocol;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(final String responseBody) {
        this.responseBody = responseBody;
    }

    public String getLocationHeader() {
        return headers.get("Location");
    }

    public void setLocationHeader(String redirectPath) {
        headers.put("Location", redirectPath);
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }
}
