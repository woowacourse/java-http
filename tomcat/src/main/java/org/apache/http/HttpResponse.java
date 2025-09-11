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
        return response;
    }

    public void redirect(String redirectPath) {
        setHttpStatus(HttpStatus.FOUND);
        setLocationHeader(redirectPath);
    }

    public HttpCookie getHttpCookie() {
        return httpCookie;
    }

    public void setHttpCookie(final HttpCookie httpCookie) {
        this.httpCookie = httpCookie;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(final String responseBody) {
        this.responseBody = responseBody;
    }

    public String getLocationHeader() {
        return "Location: " + headers.get("Location") + "\r\n";
    }

    public void setLocationHeader(String redirectPath) {
        headers.put("Location", redirectPath);
    }
}
