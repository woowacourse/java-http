package org.apache.coyote.http11.httpresponse;

import org.apache.coyote.http11.FileReader;

import java.io.IOException;

public class HttpResponse {

    private final StatusLine statusLine;
    private final ResponseHeaders headers;
    private final String body;

    public HttpResponse(StatusLine statusLine, ResponseHeaders headers, String body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse of(String location, HttpStatusCode httpStatusCode) {
        try {
            StatusLine statusLine = new StatusLine("HTTP/1.1", httpStatusCode);
            String contentType = FileReader.probeContentType(location);
            String responseBody = FileReader.read(location);
            ResponseHeaders responseHeaders = new ResponseHeaders(contentType, responseBody);

            return new HttpResponse(statusLine, responseHeaders, responseBody);
        } catch (IOException e) {
            throw new IllegalArgumentException("파일을 읽을 수 없습니다.", e);
        }
    }

    public void addCookie(String cookie) {
        headers.setCookie("Set-Cookie", cookie);
    }

    public void sendRedirect(String location) {
        headers.setLocation(location);
    }

    public String getResponse() {
        return String.join("\r\n",
                String.format("%s", statusLine.getMessage()),
                String.format("%s", headers.getMessage()),
                "",
                body);
    }

    public void setJSessionCookie(String value) {
        headers.setCookie("Set-Cookie", "JSESSIONID=" + value);
    }
}
