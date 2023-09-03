package org.apache.catalina.servlet.response;

import org.apache.catalina.servlet.session.Cookies;

public class HttpResponse {

    private final StatusLine statusLine;
    private final ResponseHeaders headers;
    private final Cookies cookies = new Cookies();
    private final String messageBody;

    public HttpResponse(StatusLine statusLine, String messageBody) {
        this(statusLine, new ResponseHeaders(), messageBody);
    }

    public HttpResponse(StatusLine statusLine, ResponseHeaders headers, String messageBody) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.messageBody = messageBody;
        setContentLength();
    }

    private void setContentLength() {
        if (messageBody == null) {
            return;
        }
        headers.put("Content-Length", String.valueOf(messageBody.getBytes().length));
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public void addCookie(String name, String value) {
        cookies.put(name, value);
    }

    public StatusLine statusLine() {
        return statusLine;
    }

    public ResponseHeaders headers() {
        return headers;
    }

    public Cookies cookies() {
        return cookies;
    }

    public String messageBody() {
        return messageBody;
    }

    @Override
    public String toString() {
        return statusLine.toString()
                + cookieHeadersToString()
                + headers.toString()
                + ((messageBody == null) ? "" : messageBody);
    }

    private String cookieHeadersToString() {
        StringBuilder sb = new StringBuilder();
        for (String name : cookies.cookies().keySet()) {
            sb.append("Set-Cookie: ")
                    .append(name).append("=").append(cookies.get(name))
                    .append(" \r\n");
        }
        return sb.toString();
    }
}
