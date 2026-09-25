package org.apache.coyote.http11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {

    private final static String HTTP_VERSION = "HTTP/1.1";
    private HttpStatus httpStatus;
    private final Map<String, String> headers;
    private String responseBody;

    public HttpResponse() {
        this.httpStatus = HttpStatus.OK;
        this.headers = new LinkedHashMap<>();
        this.responseBody = "";
    }

    public void addCookie(String jSessionId) {
        headers.put("Set-Cookie", "JSESSIONID=" + jSessionId);
    }

    public void sendRedirect(String location) {
        httpStatus = HttpStatus.FOUND;
        headers.put("Location", location);
    }

    public void forward(String path) throws IOException {
        String body = ResourceResolver.resolve(path);
        httpStatus = HttpStatus.OK;
        headers.put("Content-Type", ResourceResolver.resolveContentType(path) + ";charset=utf-8");
        headers.put("Content-Length", body.getBytes().length + "");
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
