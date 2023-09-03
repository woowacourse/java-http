package org.apache.catalina.servlet.response;

import java.io.BufferedWriter;
import org.apache.catalina.servlet.session.Cookies;

public class HttpResponse {

    private final Cookies cookies = new Cookies();
    private final ResponseHeaders headers = new ResponseHeaders();
    private StatusLine statusLine;
    private String messageBody;
    private BufferedWriter writer;

    public HttpResponse() {
    }

    public HttpResponse(BufferedWriter writer) {
        this.writer = writer;
    }

    private void setContentLength() {
        if (messageBody == null) {
            return;
        }
        headers.put("Content-Length", String.valueOf(messageBody.getBytes().length));
    }

    public void setStatusLine(StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
        setContentLength();
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

    public BufferedWriter writer() {
        return writer;
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
