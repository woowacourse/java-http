package org.apache.catalina.servlets.http.response;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.catalina.servlets.http.Cookie;

public class HttpResponse {

    private static final String START_LiNE_FORMAT = "%s %s %s ";
    private static final String HEADER_FORMAT_TEMPLATE = "%s: %s \r\n";

    private HttpStatus httpStatus = HttpStatus.OK;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private String responseBody = "";

    public HttpResponse() {
    }

    public void sendRedirect(String s) {
        headers.put("Location", s);
        setStatus(302);
    }

    public void setStatus(int i) {
        this.httpStatus = HttpStatus.from(i);
    }

    public void setContentLength(int i) {
        headers.put("Content-Length", String.valueOf(i));
    }

    public void setContentType(String s) {
        headers.put("Content-Type", s);
    }

    public void addCookie(Cookie cookie) {
        headers.put("Set-Cookie", cookie.toString());
    }

    public String getResponse() {
        String startLine = START_LiNE_FORMAT.formatted("HTTP/1.1", httpStatus.getStatusCode(), httpStatus.name());
        StringBuilder sb = new StringBuilder(startLine).append("\r\n");
        headers.forEach((name, value) -> sb.append(HEADER_FORMAT_TEMPLATE.formatted(name, value)));
        sb.append("\r\n").append(responseBody);
        return sb.toString();
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }
}
