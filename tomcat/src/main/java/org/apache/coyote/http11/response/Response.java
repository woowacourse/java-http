package org.apache.coyote.http11.response;

import java.util.Map;
import java.util.stream.Collectors;

public class Response {
    private HttpStatus status;
    private String contentType;
    private String responseBody;
    private String location;
    private Map<String, String> cookie;
    private boolean filtered;

    public byte[] getResponse() {
        return String.join("\r\n",
                        "HTTP/1.1 " + status + " ",
                        "Content-Type: text/" + contentType + ";charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        makeLocation() + makeCookie(),
                        responseBody)
                .getBytes();
    }

    private String makeLocation() {
        if (location == null) {
            return "";
        }
        return "location : " + location + "\r\n";
    }

    private String makeCookie() {
        if (cookie == null) {
            return "";
        }
        return "Set-Cookie :" + cookie.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("; "));
    }

    public boolean isFiltered() {
        return this.filtered;
    }

    public Response setFiltered(boolean filtered) {
        this.filtered = filtered;
        return this;
    }

    public Response setStatus(HttpStatus status) {
        this.status = status;
        return this;
    }

    public Response setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public Response setResponseBody(String responseBody) {
        this.responseBody = responseBody;
        return this;
    }

    public Response setLocation(String location) {
        this.location = location;
        return this;
    }

    public Response setCookie(Map<String, String> cookie) {
        this.cookie = cookie;
        return this;
    }

    public void badResponse(HttpStatus httpStatus, String responseBody, String location) {
        this.status = httpStatus;
        this.contentType = "html";
        this.responseBody = responseBody;
        this.location = location;
    }
}
