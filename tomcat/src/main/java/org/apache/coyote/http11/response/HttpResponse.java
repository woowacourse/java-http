package org.apache.coyote.http11.response;

public class HttpResponse {

    private final StatusLine statusLine;
    private final ResponseHeaders headers;
    private String body;

    public HttpResponse(HttpStatusCode httpStatusCode) {
        this.statusLine = new StatusLine(httpStatusCode);
        this.headers = new ResponseHeaders();
        this.body = null;
    }

    public boolean hasLocation() {
        return headers.hasLocation();
    }

    public void sendRedirect(String location) {
        headers.setLocation(location);
    }

    public void addCookie(String cookie) {
        headers.setCookie(cookie);
    }

    public void setContentType(String contentType) {
        headers.setContentType(contentType);
    }

    public String getLocation() {
        return headers.getLocation();
    }

    public void setContentLength(int length) {
        headers.setContentLength(length);
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getResponse() {
        return String.join("\r\n",
                String.format("%s", statusLine.getMessage()),
                String.format("%s", headers.getMessage()),
                "",
                body);
    }
}
