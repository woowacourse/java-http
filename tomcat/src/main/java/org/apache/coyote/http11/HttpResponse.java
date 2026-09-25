package org.apache.coyote.http11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String CHARSET_UTF_8 = "charset=utf-8";
    private final Map<String, String> headers;
    private HttpStatus httpStatus;
    private String responseBody;

    public HttpResponse() {
        this.httpStatus = HttpStatus.OK;
        this.headers = new LinkedHashMap<>();
        this.responseBody = "";
    }

    public void addCookie(String jSessionId) {
        headers.put(SET_COOKIE, "JSESSIONID=" + jSessionId);
    }

    public void sendRedirect(String location) {
        httpStatus = HttpStatus.FOUND;
        headers.put(LOCATION, location);
    }

    public void forward(String path) throws IOException {
        String body = ResourceResolver.resolve(path);
        httpStatus = HttpStatus.OK;
        headers.put(CONTENT_TYPE, ResourceResolver.resolveContentType(path) + ";" + CHARSET_UTF_8);
        headers.put(CONTENT_LENGTH, body.getBytes().length + "");
        responseBody = body;
    }

    public byte[] getBytes() {
        List<String> lines = new ArrayList<>();
        lines.add(HTTP_VERSION + " " + httpStatus.getCode() + " " + httpStatus.getStatusMessage());
        headers.forEach((name, value) -> lines.add(name + ": " + value));
        lines.add("");
        lines.add(responseBody);
        return String.join("\r\n", lines).getBytes();
    }
}
