package org.apache.coyote.http11.response;

import org.apache.coyote.http11.session.Cookies;

public class HttpResponse {

    private final StatusLine statusLine;
    private final ResponseHeaders headers;
    private final String messageBody;
    private final Cookies cookies = new Cookies();

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

    public void addCookie(String name, String value) {
        cookies.put(name, value);
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

    public StatusLine statusLine() {
        return statusLine;
    }

    public ResponseHeaders headers() {
        return headers;
    }

    public String messageBody() {
        return messageBody;
    }
}
