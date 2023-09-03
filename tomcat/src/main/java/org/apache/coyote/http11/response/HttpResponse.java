package org.apache.coyote.http11.response;

public class HttpResponse {

    private final StatusLine statusLine;
    private final ResponseHeaders headers;
    private final String messageBody;

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

    @Override
    public String toString() {
        return statusLine.toString()
                + headers.toString()
                + ((messageBody == null) ? "" : messageBody);
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
