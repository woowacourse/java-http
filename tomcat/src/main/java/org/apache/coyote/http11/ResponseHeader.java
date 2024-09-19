package org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpResponse.KEY_VALUE_SEPARATOR;
import static org.apache.coyote.http11.HttpResponse.SESSION_ID_NAME;
import static org.apache.coyote.http11.HttpResponse.SET_COOKIE_PREFIX;
import static org.apache.coyote.http11.Status.FOUND;

public class ResponseHeader {

    private final StatusLine statusLine;
    private String contentType = "";
    private String contentLength = "";
    private String location = "";
    private String cookie = "";

    public ResponseHeader(String path, Status status, int bodyLength) {
        this.statusLine = StatusLine.from(status);
        setContentType(path);
        setContentLength(status, bodyLength);
        setLocation(path, status);
    }

    public void setCookieHeader(String sessionId) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(SET_COOKIE_PREFIX)
                .append(SESSION_ID_NAME)
                .append(KEY_VALUE_SEPARATOR)
                .append(sessionId)
                .append(" \r\n");
        cookie = stringBuilder.toString();
    }

    private void setContentLength(Status status, int length) {
        if (FOUND == status) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Content-Length: ")
                .append(length)
                .append(" ")
                .append("\r\n");
        contentLength = stringBuilder.toString();
    }

    private void setContentType(String path) {
        StringBuilder stringBuilder = new StringBuilder();
        String mimeType = "text/html";
        if (path.contains("/css")) {
            mimeType = "text/css";
        }
        stringBuilder.append("Content-Type: ").append(mimeType).append(";")
                .append("charset=utf-8 ")
                .append("\r\n");
        contentType = stringBuilder.toString();
    }

    private void setLocation(String path, Status status) {
        if (status != FOUND) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Location: ")
                .append(path)
                .append(" ")
                .append("\r\n");
        location = stringBuilder.toString();
    }

    public String getHeader() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(statusLine.getStatusLine())
                .append("\r\n")
                .append(contentType)
                .append(contentLength)
                .append(location)
                .append(cookie)
                .append("\r\n");
        return stringBuilder.toString();
    }
}
