package org.apache.coyote.http11;

import org.apache.coyote.http11.ContentType.Charset;

public class Response {

    private static final String SPACE = " ";
    private static final String LINE_SEPARATOR = "\r\n";

    private int statusCode;
    private String sc;
    private ContentType contentType1;
    private int contentLength;
    private String location;
    private String sourceCode;
    private String cookie;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(final int statusCode) {
        this.statusCode = statusCode;
    }

    public String getSc() {
        return sc;
    }

    public void setSc(final String sc) {
        this.sc = sc;
    }

    public void setContentType1(final ContentType contentType1) {
        this.contentType1 = contentType1;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(final int contentLength) {
        this.contentLength = contentLength;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(final String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(final String cookie) {
        this.cookie = cookie;
    }

    public String toHttpResponse() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("HTTP/1.1 ").append(statusCode).append(" ")
                .append(sc).append(SPACE).append(LINE_SEPARATOR);
        stringBuilder.append(contentType1.getResponseText(Charset.UTF_8)).append(SPACE).append(LINE_SEPARATOR);
        stringBuilder.append("Content-Length: ").append(contentLength).append(SPACE).append(LINE_SEPARATOR);

        if (location != null) {
            stringBuilder.append("Location: ").append(location).append(SPACE).append(LINE_SEPARATOR);
        }
        if (cookie != null) {
            stringBuilder.append("Set-Cookie: ").append(cookie).append(SPACE).append(LINE_SEPARATOR);
        }
        stringBuilder.append(LINE_SEPARATOR);
        stringBuilder.append(sourceCode);

        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return "Response{" +
               "statusCode=" + statusCode +
               ", sc='" + sc + '\'' +
               ", contentType='" + contentType1 + '\'' +
               ", contentLength=" + contentLength +
               ", location='" + location + '\'' +
               '}';
    }
}
